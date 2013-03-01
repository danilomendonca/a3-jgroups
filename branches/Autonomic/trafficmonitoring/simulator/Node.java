package simulator;

import java.util.Vector;

public abstract class Node {
	private int x, y;
	private Vector<RoadSegment> incomming, outgoing;
	
	public Node(int x, int y){
		this.x = x;
		this.y = y;
		
		incomming = new Vector<RoadSegment>();
		outgoing = new Vector<RoadSegment>();		
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public void addIncomming(RoadSegment segment){
		this.incomming.add(segment);
	}
	
	public void addOutgoing(RoadSegment segment){
		this.outgoing.add(segment);
	}
	
	protected Vector<RoadSegment> getOutgoing(){
		return outgoing;
	}
	
	protected Vector<RoadSegment> getIncomming(){
		return incomming;
	}
	
	//return null when node is a sink!
	public RoadSegment getOutgoing(int nb){
		if(nb< this.outgoing.size())
			return outgoing.get(nb);
		else return null;
	}
	
	public abstract void step();
	
	public abstract void requestOutgoing(RoadSegment fromSegment);
	
	public abstract boolean canPass(RoadSegment from);
	
}
