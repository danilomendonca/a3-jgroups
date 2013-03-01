package simulator;

import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Collections;
import java.util.Iterator;

import node.developer.sensors.*;
import utilities.NodeID;

public class RoadNetwork {
	private Vector<RoadSegment> segments;
	private Vector<Node> nodes;
	private Vector<Source> sources;
	private Vector<Crossing> crossings;
	private Vector<Camera> cameras; 
	private Vector<Vector<Camera>> camerasPerSegement;
	private Hashtable<String,String[]> neighbours;
		
	private final double defaultCarRate = 15;

	//@Pieter
	public RoadNetwork(int scenario){
//	public RoadNetwork(int scenario, test){
		sources = new Vector<Source>();
		segments = new Vector<RoadSegment>();
		nodes = new Vector<Node>();
		crossings = new Vector<Crossing>();
		
		//TODO
		if(scenario == 1)
			this.createNetwork1();
//			this.createNetwork1(test);		
		else if(scenario == 2)
			this.createNetwork2();
		else if(scenario == 3)
			this.createNetwork3();
		else this.createNetwork4();
		this.createPaths();
	}
	
	private void createPaths(){
		for(Enumeration<Source> e = sources.elements(); e.hasMoreElements();){
			Source s= e.nextElement();
			Vector<int[]> paths = new Vector<int[]>();
			paths.add(new int[0]);
			Vector<int[]> p = getPaths(s.getOutgoing(0).getEnd(), paths, new Vector<Node>());
			
			//TODO: NON DETERMINISTIC AFTER SHUFFELING
			Collections.shuffle(p);
			Collections.shuffle(p);
			s.setCarPaths(p);
		}
	}
	
	public void createCameras(int cameraRange){
		//@Pieter
//		//TODO
//		//Voor netwerk 1
//		int cameraRange = 15;
//		//voor netwerk 2
//		//int cameraRange = 30;
		
		
		//vector met voor elk segment een vector met de camera's op dat segment
		camerasPerSegement = new Vector<Vector<Camera>>();
				
		int cameraID = 1;
		
		//maak voor elk segment een stel cameras aan, en stel de buren per segment al goed
		for(Enumeration<RoadSegment> e = segments.elements(); e.hasMoreElements();){
			Vector<Camera> newCameras = new Vector<Camera>();
			RoadSegment segment = e.nextElement();
			int nbCameras = (int)(segment.cars.length/cameraRange);			
			int from = 0;
			int currentCamera = 1;
						
			while(from<segment.cars.length && currentCamera <= nbCameras){
				int to = from+cameraRange;
				if(currentCamera == nbCameras)
					to = segment.cars.length-1;
				
				Camera c = new Camera(new NodeID(cameraID++), segment, from, to);
				if(!newCameras.isEmpty()){
					newCameras.lastElement().addPhysicalNeighbour(c.getAgentID().toString());
					c.addPhysicalNeighbour(newCameras.lastElement().getAgentID().toString());
				}
				newCameras.add(c);
				from = to+1;
				currentCamera++;				
			}
			camerasPerSegement.add(newCameras);		
		}
		
		//voeg buren toe voor segmenten die grenzen aaneen op kruispunten (crossings)
		//zowel incomming als outgoing kunnen ook onderling aaneengrenwen nu!!!
		for(Enumeration<Crossing> e = crossings.elements(); e.hasMoreElements();){
			Crossing crossing = e.nextElement();
			//incomming
			for(Enumeration<RoadSegment> incom = crossing.getIncomming().elements(); incom.hasMoreElements(); ){
				//laatste camera op inkomend segment 
				Camera toAdd = camerasPerSegement.elementAt(this.segments.indexOf(incom.nextElement())).lastElement();
				//voeg camera toe aan alle aangrenzende cameras op uitgaande segmenten
				for(Enumeration<RoadSegment> out = crossing.getOutgoing().elements(); out.hasMoreElements(); ){
					//eerste camera op uitgaand
					Camera addTo = camerasPerSegement.elementAt(this.segments.indexOf(out.nextElement())).firstElement();
					addTo.addPhysicalNeighbour(toAdd.getAgentID().toString());					
				}
				//voeg camera toe aan alle aangrenzende cameras op inkomende segmenten
				for(Enumeration<RoadSegment> in = crossing.getIncomming().elements(); in.hasMoreElements(); ){
					//laatste camera op inkomend segment 
					Camera addTo = camerasPerSegement.elementAt(this.segments.indexOf(in.nextElement())).lastElement();
					addTo.addPhysicalNeighbour(toAdd.getAgentID().toString());					
				}
			}			
			//outgoing
			for(Enumeration<RoadSegment> outgo = crossing.getOutgoing().elements(); outgo.hasMoreElements(); ){
				//laatste camera op inkomend segment 
				Camera toAdd = camerasPerSegement.elementAt(this.segments.indexOf(outgo.nextElement())).firstElement();
				//voeg camera toe aan alle aangrenzende cameras op uitgaande segmenten
				for(Enumeration<RoadSegment> out = crossing.getOutgoing().elements(); out.hasMoreElements(); ){
					//eerste camera op uitgaand
					Camera addTo = camerasPerSegement.elementAt(this.segments.indexOf(out.nextElement())).firstElement();
					addTo.addPhysicalNeighbour(toAdd.getAgentID().toString());					
				}
				//voeg camera toe aan alle aangrenzende cameras op inkomende segmenten
				for(Enumeration<RoadSegment> in = crossing.getIncomming().elements(); in.hasMoreElements(); ){
					//laatste camera op inkomend segment 
					Camera addTo = camerasPerSegement.elementAt(this.segments.indexOf(in.nextElement())).lastElement();
					addTo.addPhysicalNeighbour(toAdd.getAgentID().toString());					
				}
			}
		}
		
		//print de cameras uit
		/*
		for(Enumeration<Vector<Camera>> e =  camerasPerSegement.elements(); e.hasMoreElements();){
			System.out.println("-- Segment:");
			for(Enumeration<Camera> ee = e.nextElement().elements(); ee.hasMoreElements();){
				Camera c = ee.nextElement();
				System.out.print("Cam "+ c.getID()+" : "+c.getFromIndex()+" - "+c.getToIndex()+" /");
				for(Enumeration<Camera> eee = c.getNeighbours().elements(); eee.hasMoreElements();){
					System.out.print(eee.nextElement().getID()+"/");
				}
				System.out.print("\n");
			}
		}*/
		
		cameras = new Vector<Camera>();
		for(Enumeration<Vector<Camera>> e =  camerasPerSegement.elements(); e.hasMoreElements();){
			cameras.addAll(e.nextElement());
		}
		
		//The hashmap neighbours contains the neighbours as they were on creation time
		//this is e.g. used by the self-healing manager, to have a topology of the cameras
		neighbours = new Hashtable<String,String[]>();
		for(Enumeration<Camera> e =  cameras.elements(); e.hasMoreElements();){
			Camera c = e.nextElement();
			c.getAliveNeighbourIDs();
			neighbours.put(c.getAgentID().toString(), c.getAliveNeighbourIDs());
		}
		
		/*
		for(Enumeration<String> e =  neighbours.keys(); e.hasMoreElements();){
			String node = e.nextElement();
			String result = "n "+node+": ";
			String[] n = neighbours.get(node);
			for(int i = 0; i<n.length;i++){
				result+=n[i]+", ";
			}
			System.out.println(result);
		}*/		
	}
	
	
	public Vector<Camera> getCameras(){
		return cameras;
	}
	
	public Vector<Vector<Camera>> getCamerasPerSegment(){
		return this.camerasPerSegement;
	} 
	
	public Vector<RoadSegment> getSegments(){
		return segments;
	}
	
	public Vector<Node> getNodes(){
		return nodes;
	}
	
	public Vector<Source> getSources(){
		return sources;
	}
	public Vector<Crossing> getCrossings(){
		return crossings;
	}
	
	public Hashtable<String,String[]> getNeighbours(){
		return this.neighbours;
	}
	
	private void printPaths(Vector<int[]> paths){
		for(Enumeration<int[]> e = paths.elements(); e.hasMoreElements();){
			int[] p = e.nextElement();
			String path = "";
			for(int i = 0; i<p.length; i++){
				path+=p[i]+" ";
			}
			System.out.println("path: "+path);
		}
	}
	
	//@Pieter
//	//Test Main
//	public static void main(String[] args){
//		RoadNetwork n = new RoadNetwork(2,1);
//		n.createCameras();
//	}
	
	//@Pieter
	public void createNetwork1(){	
//	public void createNetwork1(int test){
//		if(test == 1 || test == 4){
			Source so1 = new Source(50, 50, defaultCarRate);
			Source so2 = new Source(50, 150, defaultCarRate);
			
			sources.add(so1);
			sources.add(so2);		
			
			Crossing cr1 = new Crossing(150, 100);
			Crossing cr2 = new Crossing(250, 100);
						
			crossings.add(cr1);
			crossings.add(cr2);	
						
			Sink si1 = new Sink(400, 100);
							
			segments.add(new RoadSegment(so1, cr1));
			segments.add(new RoadSegment(so2, cr1));
			segments.add(new RoadSegment(cr1, cr2));
			segments.add(new RoadSegment(cr2, si1));
								
			nodes.add(so1);
			nodes.add(so2);
			nodes.add(cr1);
			nodes.add(cr2);
			nodes.add(si1);
//		}else{
//			Source so1 = new Source(50, 50, defaultCarRate);
//			Source so2 = new Source(50, 150, defaultCarRate);
//			
//			sources.add(so1);
//			sources.add(so2);		
//			
//			Crossing cr1 = new Crossing(150, 100);
//			Crossing cr2 = new Crossing(250, 100);
//			Crossing cr3 = new Crossing(750, 100);
//			
//			crossings.add(cr1);
//			crossings.add(cr2);	
//			crossings.add(cr3);
//			
//			
//			Sink si1 = new Sink(800, 100);
//							
//			segments.add(new RoadSegment(so1, cr1));
//			segments.add(new RoadSegment(so2, cr1));
//			segments.add(new RoadSegment(cr1, cr2));
//			segments.add(new RoadSegment(cr2, cr3));
//			segments.add(new RoadSegment(cr3, si1));
//					
//			nodes.add(so1);
//			nodes.add(so2);
//			nodes.add(cr1);
//			nodes.add(cr2);
//			nodes.add(cr3);
//			nodes.add(si1);
//					
//		}
				 
	}
	
	public void createNetwork3(){
		
		Source so1 = new Source(50, 50, defaultCarRate);
		Source so2 = new Source(50, 150, defaultCarRate);
		
		sources.add(so1);
		sources.add(so2);		
		
		Crossing cr1 = new Crossing(150, 100);
		Crossing cr2 = new Crossing(250, 100);
					
		crossings.add(cr1);
		crossings.add(cr2);	
					
		Sink si1 = new Sink(400, 100);
						
		segments.add(new RoadSegment(so1, cr1));
		segments.add(new RoadSegment(so2, cr1));
		segments.add(new RoadSegment(cr1, cr2));
		segments.add(new RoadSegment(cr2, si1));
							
		nodes.add(so1);
		nodes.add(so2);
		nodes.add(cr1);
		nodes.add(cr2);
		nodes.add(si1);
				 
	}
	
	public void createNetwork4(){
		
		Source so1 = new Source(50, 50, defaultCarRate);
		
		sources.add(so1);
		
		Crossing cr1 = new Crossing(150, 50);
		crossings.add(cr1);
		Crossing cr2 = new Crossing(250, 50);
		crossings.add(cr2);
		
		Sink si1 = new Sink(350, 50);
						
		segments.add(new RoadSegment(so1, cr1));
		segments.add(new RoadSegment(cr1, cr2));
		segments.add(new RoadSegment(cr2, si1));
				
		nodes.add(so1);
		nodes.add(cr1);
		nodes.add(cr2);
		nodes.add(si1);
				 
	}
	
	public void createNetwork2(){		
		
		Node[][] network = new Node[5][5];
		int x0 = 50;
		int y0 = 50;
		int dx = 110;
		int dy = 110;
		
		for(int i = 1; i<network.length-1; i++){
			for(int j = 1; j<network[i].length-1; j++){
				network[i][j] = new Crossing(x0+j*dx, y0+i*dy);
				crossings.add((Crossing)network[i][j]);
			}
		}
		
		for(int j = 1; j<network[0].length-1; j++){
			if(j%2 == 0){
				network[0][j] = new Source(x0+j*dx, y0, defaultCarRate);
				sources.add((Source)network[0][j]);
				network[network.length-1][j] = new Sink(x0+j*dx, y0+(network.length-1)*dy);
			}else{
				network[0][j] = new Sink(x0+j*dx, y0);
				network[network.length-1][j] = new Source(x0+j*dx, y0+(network.length-1)*dy, defaultCarRate);
				sources.add((Source)network[network.length-1][j]);
			}
		}
			
		for(int i = 1; i<network.length-1; i++){
			if(i%2 == 0){
				network[i][0] = new Sink(x0, y0+i*dy);
				network[i][network[i].length-1] = new Source(x0+(network[i].length-1)*dx, y0+i*dy, defaultCarRate);
				sources.add((Source)network[i][network[i].length-1]);
			}else{
				network[i][0] = new Source(x0, y0+i*dy, defaultCarRate);
				network[i][network[i].length-1] = new Sink(x0+(network[i].length-1)*dx, y0+i*dy);
				sources.add((Source)network[i][0]);
			}
		}	
		
		for(int j = 1; j<network[0].length-1; j++){
			for(int i = 0; i<network.length-1; i++){
				if(j%2 == 0)
					segments.add(new RoadSegment(network[i][j], network[i+1][j]));
				else
					segments.add(new RoadSegment(network[i+1][j], network[i][j]));
			}
		}
		
		for(int i = 1; i<network.length-1; i++){
			for(int j = 0; j<network[0].length-1; j++){
				if(i%2 == 0)
					segments.add(new RoadSegment(network[i][j+1], network[i][j]));
				else
					segments.add(new RoadSegment(network[i][j], network[i][j+1]));
			}
		}
		
		for(int i = 0; i<network.length; i++){
			for(int j = 0; j<network[0].length; j++){
				if(network[i][j]!= null)
					nodes.add(network[i][j]);
			}
		}		 
	}
	
	private Vector<int[]> getPaths(Node source, Vector<int[]> paths, Vector<Node> visited){
		Vector<int[]> results = new Vector<int[]>();
		
		if(visited.contains(source))
			return results;
		
		Vector<Node> nVisited = new Vector<Node>();
		nVisited.addAll(visited);
		nVisited.add(source);
				
		for(int i = 0; i< source.getOutgoing().size(); i++){
			Vector<int[]> tempResults = new Vector<int[]>();
			for(Enumeration<int[]> e = paths.elements(); e.hasMoreElements(); ){
				int[] path = e.nextElement();
				int[] newPath = new int[path.length+1];
				for(int j = 0; j<path.length; j++){
					newPath[j] = path[j];
				}
				newPath[path.length] = i;
				tempResults.add(newPath);
			}			
			results.addAll(getPaths(source.getOutgoing(i).getEnd(), tempResults, nVisited));
		}		
		if(source.getOutgoing().size() == 0)
			return paths;
		return results;
	}
	
	public void switchBottleNeck(int node){
		crossings.elementAt(node).changeBlock();
	}
}
