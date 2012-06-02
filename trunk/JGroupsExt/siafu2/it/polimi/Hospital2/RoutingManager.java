package it.polimi.Hospital2;

import java.util.ArrayList;

import de.nec.nle.siafu.model.Place;

public class RoutingManager {

	private WorldModel wm;
	
	private Configuration config;
	private ArrayList<Configuration> configs = new ArrayList<Configuration>();
			
	public RoutingManager(WorldModel worldM) {
		this.wm = worldM;
		createDefaultConfig();
		createBackupConfig();
		config = configs.get(0);
		
	}

	private void createDefaultConfig() {
		// TODO Auto-generated method stub
		Configuration c = new Configuration();
		
		c.setRedIndicator(0,6,2,0,0,6);
		c.setBlueIndicator(4,4,4,6,0,6);
		c.setYellowIndicator(2,4,2,0,0,2);
		c.setGreenIndicator(4,4,6,4,2,6);
		c.setRedDestination(null, wm.getScreen(0), wm.getScreen(0), wm.getScreen(1), wm.getScreen(3), wm.getScreen(2));
		c.setBlueDestination(wm.getScreen(2), wm.getScreen(3), null, wm.getScreen(5), wm.getScreen(3), null);
		c.setYellowDestination(wm.getScreen(1), null, wm.getScreen(0), null, wm.getScreen(3), wm.getScreen(3));
		c.setGreenDestination(wm.getScreen(2), wm.getScreen(3), wm.getLeftEntrance(), wm.getScreen(4), wm.getRightEntrance(), wm.getScreen(2));
		
		configs.add(c);
		
	}
	
	private void createBackupConfig() {
		// TODO Auto-generated method stub
		Configuration c = new Configuration();
		
		c.setRedIndicator(0,4,2,6,0,6);
		c.setBlueIndicator(4,4,4,6,0,6);
		c.setYellowIndicator(4,4,4,0,0,2);
		c.setGreenIndicator(4,4,6,4,2,6);
		c.setRedDestination(null, wm.getScreen(3), wm.getScreen(0), wm.getScreen(5), wm.getScreen(3), wm.getScreen(2));
		c.setBlueDestination(wm.getScreen(2), wm.getScreen(3), null, wm.getScreen(5), wm.getScreen(3), null);
		c.setYellowDestination(wm.getScreen(2), null, wm.getScreen(5), null, wm.getScreen(3), wm.getScreen(3));
		c.setGreenDestination(wm.getScreen(2), wm.getScreen(3), wm.getLeftEntrance(), wm.getScreen(4), wm.getRightEntrance(), wm.getScreen(2));
		
		configs.add(c);		
	}


	public void setConfiguration(int num) {
		config = configs.get(num);
	}
	
	public Place getNextDestination(int index, String colour) {
		if(colour.equals("red")){
			if(index==0)
				return wm.getLab((int)(Math.random()*5));
				
			return config.redDestination[index];
		}else if(colour.equals("green")){
			return config.greenDestination[index];
		}else if(colour.equals("blue")){
			if(index==2 || index ==5)
				return wm.getRadiology((int)(Math.random()*5));
			return config.blueDestination[index];
		}else{
			if(index==1 || index ==3)
				return wm.getPhysiotherapy((int)(Math.random()*5));
			return config.yellowDestination[index];
		}
	}

	public int getIndicatorDirection(int index, String colour) {
		if(colour.equals("red"))
			return config.redIndicator[index];
		else if(colour.equals("green"))
			return config.greenIndicator[index];
		else if(colour.equals("blue"))
			return config.blueIndicator[index];
		else
			return config.yellowIndicator[index];
	}

}
