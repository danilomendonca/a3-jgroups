package it.polimi.Hospital2;

import de.nec.nle.siafu.model.Place;

public class Configuration {

	public int[] redIndicator = new int[6]; 
	public int[] blueIndicator = new int[6];
	public int[] yellowIndicator = new int[6];
	public int[] greenIndicator = new int[6];
	public Place[] redDestination = new Place[6];
	public Place[] blueDestination = new Place[6];
	public Place[] greenDestination = new Place[6];
	public Place[] yellowDestination = new Place[6];
	
	
	public void setRedIndicator(int v1, int v2, int v3, int v4, int v5, int v6){
		redIndicator[0] = v1;
		redIndicator[1] = v2;
		redIndicator[2] = v3;
		redIndicator[3] = v4;
		redIndicator[4] = v5;
		redIndicator[5] = v6;
	}
	
	public void setBlueIndicator(int v1, int v2, int v3, int v4, int v5, int v6){
		blueIndicator[0] = v1;
		blueIndicator[1] = v2;
		blueIndicator[2] = v3;
		blueIndicator[3] = v4;
		blueIndicator[4] = v5;
		blueIndicator[5] = v6;
	}
	
	public void setGreenIndicator(int v1, int v2, int v3, int v4, int v5, int v6){
		greenIndicator[0] = v1;
		greenIndicator[1] = v2;
		greenIndicator[2] = v3;
		greenIndicator[3] = v4;
		greenIndicator[4] = v5;
		greenIndicator[5] = v6;
	}
	
	public void setYellowIndicator(int v1, int v2, int v3, int v4, int v5, int v6){
		yellowIndicator[0] = v1;
		yellowIndicator[1] = v2;
		yellowIndicator[2] = v3;
		yellowIndicator[3] = v4;
		yellowIndicator[4] = v5;
		yellowIndicator[5] = v6;
	}
	
	public void setRedDestination(Place v1, Place v2, Place v3, Place v4, Place v5, Place v6){
		redDestination[0] = v1;
		redDestination[1] = v2;
		redDestination[2] = v3;
		redDestination[3] = v4;
		redDestination[4] = v5;
		redDestination[5] = v6;
	}
	
	public void setBlueDestination(Place v1, Place v2, Place v3, Place v4, Place v5, Place v6){
		blueDestination[0] = v1;
		blueDestination[1] = v2;
		blueDestination[2] = v3;
		blueDestination[3] = v4;
		blueDestination[4] = v5;
		blueDestination[5] = v6;
	}
	
	public void setGreenDestination(Place v1, Place v2, Place v3, Place v4, Place v5, Place v6){
		greenDestination[0] = v1;
		greenDestination[1] = v2;
		greenDestination[2] = v3;
		greenDestination[3] = v4;
		greenDestination[4] = v5;
		greenDestination[5] = v6;
	}
	
	public void setYellowDestination(Place v1, Place v2, Place v3, Place v4, Place v5, Place v6){
		yellowDestination[0] = v1;
		yellowDestination[1] = v2;
		yellowDestination[2] = v3;
		yellowDestination[3] = v4;
		yellowDestination[4] = v5;
		yellowDestination[5] = v6;
	}
}
