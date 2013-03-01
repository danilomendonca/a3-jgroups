package simulator;

import java.util.Enumeration;
import java.util.Vector;

public class Sink extends Node{
	//all roadsegments requesting a sink
	private Vector<RoadSegment> sink_requests;
	//whether an incomming is allowed to pass
	private boolean[] canPass;
	
	public Sink(int x, int y){
		super(x, y);
		sink_requests = new Vector<RoadSegment>();
	}
	
	
	public double[] RoadChoises(){
		double[] choices = new double[getOutgoing().size()];
		for(int i = 0; i<choices.length; i++){
			choices[i]=1/choices.length;
		}
		return choices;
	}
		
	public void requestOutgoing(RoadSegment fromSegment){
		if(sink_requests == null)
			initRequests();
		sink_requests.add(fromSegment);
	}
	
	public void step(){
		this.letSink();
		this.initRequests();
	}
	
	private void letSink(){
		for(Enumeration<RoadSegment> e = sink_requests.elements(); e.hasMoreElements();){
			RoadSegment rs = e.nextElement();
			//zoek de index van het laatste voertuig
			int lastIndex = rs.cars.length-1;
			while (rs.cars[lastIndex] == null && lastIndex >= 0){
				lastIndex --;
			}
			
			rs.cars[lastIndex] = null;
			CarCounter.getCounter().sinkCar();
		}
	}
	
	private void initRequests(){
		sink_requests = new Vector<RoadSegment>();
	}
	
	public boolean canPass(RoadSegment from){
		return true;
	}
}
