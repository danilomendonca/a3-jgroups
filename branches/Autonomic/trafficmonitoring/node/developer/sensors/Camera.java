package node.developer.sensors;
import simulator.RoadSegment;
import simulator.Car;
import java.util.*;

import node.organizationMiddleware.contextManager.contextDirectories.Context;


import utilities.NodeID;

public class Camera{
	private int fromIndex;
	private int toIndex;
	private Car[] viewingRange;
	//@Pieter
	private Vector<String> aliveNeighbours = new Vector<String>();
	private Vector<String> physicalNeighbors = new Vector<String>();
	
	private NodeID ID;
	
	//measurements
	private double density;
	private double avg_velocity;
	
	//used by the GUI
	private int x;
	private int y;
	
	//used by the GUI
	private double dx;
	private double dy;
	
	public Camera(NodeID ID, RoadSegment segment, int fromIndex, int toIndex){
		//System.out.println("Camera: "+ID.toString()+" from: "+fromIndex+" to: "+toIndex);
		
		this.ID = ID;
		this.fromIndex = fromIndex;
		this.toIndex = toIndex;
		this.viewingRange = segment.cars;
		aliveNeighbours = new Vector<String>();
		calculateGUIPosition(segment, fromIndex, toIndex);
	}
	
	//@Pieter
	public synchronized void addPhysicalNeighbour(String neighbour){
		if(!neighbour.equals(this.ID.toString()) && !this.physicalNeighbors.contains(neighbour)){
			this.physicalNeighbors.add(neighbour);
		}
		
		//@Pieter
		if(!neighbour.equals(this.ID.toString()) && !this.aliveNeighbours.contains(neighbour)){
			this.aliveNeighbours.add(neighbour);
		}
	}
		
	public NodeID getAgentID(){
		return ID;
	}
	
	public int getFromIndex(){
		return this.fromIndex;
	}
	
	public int getToIndex(){
		return this.toIndex;
	}
		
	public String[] getAliveNeighbourIDs(){
		return this.aliveNeighbours.toArray(new String[0]);
	}
	
	public ArrayList<NodeID> getAliveNeighbourIDlist(){
		ArrayList<NodeID> result = new ArrayList<NodeID>();
		for(Enumeration<String> e = this.aliveNeighbours.elements(); e.hasMoreElements();){
			result.add(new NodeID(Integer.parseInt(e.nextElement())));
		}
		return result;
	}
	
	public Vector<String> getAliveNeighbourIdVector(){
		return this.aliveNeighbours;
	}
	
	//@Pieter
	public void setAliveNeighbours(ArrayList<NodeID> aliveNeighbourList){
		this.aliveNeighbours.clear();
		
		for(NodeID neighbor : aliveNeighbourList)
			this.aliveNeighbours.add(neighbor.toString());
	}
	
	//@Pieter
	public void resetAliveNeighbourList(){
		this.aliveNeighbours.clear();
		this.aliveNeighbours.addAll(this.physicalNeighbors);
	}
	
	
		
	public void measure(){
		avg_velocity = 0;
		density = 0;
	    int rhoc = 0;
	    int vsum = 0;
	    
		for(int i = fromIndex; i < toIndex; i++) {
	    	if (viewingRange[i] != null && viewingRange[i].speed >= 0) {
	    		vsum+=viewingRange[i].speed;
	    		rhoc++;
	    	}
	    }
	    avg_velocity = (double)(vsum)/(double)((rhoc > 0) ? rhoc : 1 );
	    density =(double)(rhoc)/(double)(toIndex-fromIndex);
	}
	
	/**
	 * Kan gaan van 0 tot 1
	 */
	public double getDensity(){
		measure();
		return density;
	}
	
	/**
	 * kan gaan van 0 tot 5 ongeveer
	 */
	public double getIntensity(){
		measure();
		return getDensity()*getAvgVelocity();
	}
	
	public double getAvgVelocity(){
		measure();
		return avg_velocity;
		//return 2;
	}
	
	/*************
	 * GUI STUFF *
	 *************/
	
	private void calculateGUIPosition(RoadSegment segment, int fromIndex, int toIndex){
		double x1 = (double)segment.getBegin().getX();
		double y1 = (double)segment.getBegin().getY();
		double x2 = (double)segment.getEnd().getX();
		double y2 = (double)segment.getEnd().getY();
		
		double deltaX = (x2-x1)/segment.LENGTH;
		double deltaY = (y2-y1)/segment.LENGTH;
		this.dx = deltaX;
		this.dy = deltaY;
		
		double z = fromIndex + (toIndex-fromIndex)/2;
		
		double alpha = Math.atan((y1-y2)/(x2-x1));
		
		this.x = (int)(x1+deltaX*z - 12*Math.sin(alpha));
		this.y = (int)(y1+deltaY*z - 12*Math.cos(alpha));		
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public double getDx(){
		return dx;
	}
	
	public double getDy(){
		return dy;
	}
	
	/*****************
	 *               * 
	 * GUI HACKS ... *
	 *               *
	 *****************/
	
	//link to nodes context, to be able to show the current state of the node in the gui
	
	private Context myNodesContext;
	
	public void setOrganizationContext(Context ctx){
		this.myNodesContext = ctx;
	}
	
	public Context getNodesContext(){
		return this.myNodesContext;
	}
	
	private boolean isFailed = false;
	
	public void fail(){
		this.isFailed = true;
	}
	
	//@Pieter
	public void bringBackOnline(){
		this.isFailed = false;
		
		//Reset out-of-date list of alive neighbor nodes
		this.resetAliveNeighbourList();
		
		//Remove out-of-date organization context
		this.myNodesContext = null;
		
		//Reset measurements
		this.density = 0;
		this.avg_velocity = 0;
	}
	
	public boolean isFailed(){
		return this.isFailed;
	}
}
