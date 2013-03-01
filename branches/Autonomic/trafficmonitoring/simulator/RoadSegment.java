package simulator;

import java.awt.Point;

import simulator.models.SimulatorModel;


public class RoadSegment {
	private Node begin, end;
		
	public RoadSegment(Node begin, Node end){
		this.begin = begin;
		begin.addOutgoing(this);
		this.end = end;
		end.addIncomming(this);		
		
		double x1 = (double)getBegin().getX();
		double y1 = (double)getBegin().getY();
		double x2 = (double)getEnd().getX();
		double y2 = (double)getEnd().getY();
		
		double dist = Math.sqrt((y2-y1)*(y2-y1) + (x2-x1)*(x2-x1));
		lengthInMetre = (int) (dist*5);		
	}
	
	public Node getBegin(){
		return begin;
	}
	
	public Node getEnd(){
		return end;
	}
	
	//TODO
	private int lengthInMetre;
	public int getLength(){
		return lengthInMetre;
	}
	
	
	/******************
	 * Simulator part *
	 ******************/
	
	// Read Only variables for display:

	// length of the road
	public int LENGTH;
	// Maximum speed
	public int MAXSPEED;
	  
	public Car[] cars;
	 	 
	// cell length
	private double deltaX;
	// simulator used
	private SimulatorModel sim;
	  
	public void init(double deltaX, SimulatorModel sim) {
		this.deltaX = deltaX;
		this.MAXSPEED = (int)(135.0/(deltaX*3.6)); // 135 km/h, dt=1s
		   
		//TODO
		this.LENGTH = (int)(getLength()/deltaX); // 2250 meter (300 sites with dx=7.5m)
		    		
		setSimulator (sim);
		
		// the road as an array of speeds
		cars = new Car[LENGTH];
	}
	  
	public void setSimulator (SimulatorModel sim) {
		this.sim = sim;
	}
	
	private int updateSpeed(int speed, int gap, double prob_slowdown, double lambda) {
		sim.init (MAXSPEED, LENGTH, deltaX);		  
	  
		speed = sim.updateSpeed (speed, gap, prob_slowdown, lambda);
    
  	  	if (speed > gap) {
  	  		speed = gap;
  	  	}
  	  	return speed;
	}
	
	public int getFirstCarIndex(){
		int i = 0;
		while(i<cars.length && cars[i] == null){
			i++;
		}
		return i;
	}
	
	private int getGapForCar(int carIndex){
		int i = carIndex + 1;
		while(i<cars.length && cars[i] == null){
			i++;
		}
		return i - carIndex - 1;
	}
	
	/*
  	private int choice_last;
  	private boolean last_waiting = false;
  	private final double chance_waiting_change_mind = 0.05;	  	
	  	
  	private void makeChoice(){
  		//Doe deze if als auto's blijven bij hun beslissing om ergens af te slaan!!!
  		//if(!last_waiting || Math.random()<this.chance_waiting_change_mind){
  		if(!last_waiting){
  			choice_last = this.getEnd().getRandomOutgoingSegment(Math.random());
  			last_waiting = true;
  		}
  	}
	  */
	  	
  	/**
  	 * Opmerking: speed kan nooit groter zijn dan gap!!
  	 * Dus enkel laatste voertuig kan mogelijk van segment veranderen!!!
  	 */	  
  	public void updateSpeeds(double prob_slowdown, double lambda) {
  		  		
  		int i = 0; 
  		while(i<this.cars.length){
  			if(cars[i] != null){
  				int gap = getGapForCar(i);
  				
  				if(i + gap >= cars.length - 1){
  					//last car	  
  								    				    				    		
		    		RoadSegment segment = this.getEnd().getOutgoing(cars[i].getChoice());
		    		if(segment != null){
		    			gap += segment.getFirstCarIndex();			    			
		    			if(gap >= cars.length)
		    				gap = cars.length - 1;			    			
		    		}else{
		    			gap = cars.length - 1;
		    		}	  					
  				}
  				cars[i].speed = updateSpeed (cars[i].speed, gap, prob_slowdown, lambda);
  				
  				if(i + cars[i].speed >= cars.length){
	    			//car will reach end of segment  					
  					this.getEnd().requestOutgoing(this);
  				}	  		
  			}	  			
  			i++;
  		}
  	}
	  	
  	public void moveVehicles(){
  		int i = 0; 
  		while(i<this.cars.length){
  			if(cars[i] != null){
  				int iNext = i + cars[i].speed;
  				if(iNext != i){
			    	if (iNext < cars.length){
			    		//speed[inext] zal altijd -1 zijn, dus daar zal nooit een auto staan!
			    		if(cars[iNext] != null) System.out.println("fout " + iNext);
			    		cars[iNext] = cars[i];
			    		cars[i] = null;
			    		i = iNext;
			    	}else{
			    		if(this.getEnd().canPass(this)){
			    			pass = true;
			    			last_i = i;
			    			last_inext = iNext;
			    			//System.out.println("car passed");
			    		}else{
			    			pass = false;
			    			Car temp = cars[i];
			    			temp.speed = cars.length - i - 1;
			    			cars[i] = null;
			    			cars[cars.length - 1] = temp;
			    		}
			    		break;
			    	}
		    	}	  				
  			}
  			i++;
  		}
  	
  	}
	  	
  	private int last_i;
  	private int last_inext;
  	private boolean pass = false;
	  	
  	public void transferVehicles(){
  		if(pass){
  			RoadSegment rs = this.getEnd().getOutgoing(cars[last_i].getChoice());
			if(rs != null){
				if(rs.cars[last_inext - cars.length] != null){
					System.out.println("dikke shit");
				}
				rs.cars[last_inext - cars.length] = cars[last_i];
				
			}//else entering sink!!!
			cars[last_i].removeChoice();
			cars[last_i] = null;
    		pass = false;
    		//last_waiting = false;
    		
  		}  		
  	}
	  	
	  	
	  	
	  	
////////////////////
	    // local measurement

	  	//TODO
	    /*
	  	public void measure() {
	    	 int mp = LENGTH-1;
			    // length of the measurement
			 int ml = LENGTH/3;
	    	
	    	
	    	int vsum=0;
	    	int rhoc=0;

	    	for(int i=mp;i>mp-ml;i--) {
	    		if (speed[i] >= 0) {
	    			vsum+=speed[i];
	    			rhoc++;
	    		}
	    	}
	    	double v = (double)(vsum)/(double)((rhoc > 0) ? rhoc : 1 );
	    	double dens =(double)(rhoc)/(double)(ml);
	    	double flow = v*dens;
	    }  	  */
}
