package simulator;

import java.util.Vector;

public class Crossing extends Node{
	//per outgoing a list of requests from incomming
	private Vector<RoadSegment> passingRequests;
	//whether an incomming is allowed to pass
	private boolean[] canPass;
	
	private boolean isBlocked = false;
	
	private int passToken;
	
	public Crossing(int x, int y){
		super(x, y);
	}
				
	public void requestOutgoing(RoadSegment fromSegment){
		if(passingRequests == null)
			initRequests();
		
		passingRequests.add(fromSegment);
	}
	
	public void step(){
		this.canPass = new boolean[getIncomming().size()];
		this.makeChoices();
		this.initRequests();
	}
	
	private void makeChoices(){
		if(passingRequests == null)
			initRequests();
		
		
		for(int i = passToken; i<this.getIncomming().size(); i++){
			if(this.passingRequests.contains(getIncomming().elementAt(i))){
				this.canPass[i] = true;
				break;
			}else if(i == this.getIncomming().size()-1){
				for(int j = 0; j<passToken; j++){
					if(this.passingRequests.contains(getIncomming().elementAt(j))){
						this.canPass[j] = true;
						break;
					}
				}
			}
		}
		
		passToken++;
		if(passToken>=this.getIncomming().size())
			passToken = 0;
	}
	
	private void initRequests(){
		passingRequests = new Vector<RoadSegment>();
	}
	
	public boolean canPass(RoadSegment from){
		return (!isBlocked && canPass[this.getIncomming().indexOf(from)]);
	}
	
	public void changeBlock(){
		this.isBlocked = !this.isBlocked;
		//System.out.println("bootleneck: "+this.isBlocked);
	}
}
