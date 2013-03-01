package simulator;

import java.util.Enumeration;
import java.util.Vector;

public class Source extends Node{
	
	private double rate;
	
	private Vector<int[]> carPaths;
	private int pathIndex;
	
	public Source(int x, int y, double rate){
		super(x, y);
		this.rate = 20 - rate;
	}
	
	public void setCarPaths(Vector<int[]> paths){
		this.carPaths = paths;
		pathIndex = 0;
	}
	
	public void updateRate(double newRate){
		rate = 20 - newRate;
		if(rate == 20)
			rate = 10000;
	}
			
	public void requestOutgoing(RoadSegment fromSegment){}
	
	public void step(){
		this.createCars();
	}
	
	private int counter = 0;
	
	private void createCars(){
		if(on)
			if(counter>rate){
				for(Enumeration<RoadSegment> e = this.getOutgoing().elements(); e.hasMoreElements();){
					RoadSegment rs = e.nextElement();
				
					int gapNew = 0;
					while(gapNew < rs.cars.length && rs.cars[gapNew] == null){
						gapNew ++;
					}
					
					if(gapNew>1){
						rs.cars[0]=new Car(1, this.carPaths.elementAt(this.pathIndex));
						CarCounter.getCounter().sourceCar();
						pathIndex++;
						if(pathIndex>=this.carPaths.size())
							pathIndex = 0;
						
					}		
				}
				counter = 0;
			} else counter ++;
		
	}
	
	public boolean canPass(RoadSegment from){
		return false;
	}
	
	
	public boolean on = true;
	public void switchOnOff(){
		on = !on;
	}
}
