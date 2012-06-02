package it.polimi.A3Behavior2;

import it.polimi.Hospital2.RoutingManager;
import it.polimi.Hospital2.WorldModel;

import java.util.ArrayList;

import org.jgroups.Address;

import de.nec.nle.siafu.model.Place;

import A3JGroups.A3JGMessage;
import A3JGroups.JGSupervisorRole;

public class ScreenSupervisor extends JGSupervisorRole {

	WorldModel wm;
	RoutingManager rm;
	
	public ScreenSupervisor(int resourceCost, String groupName, WorldModel wm) {
		super(resourceCost, groupName);
		this.wm = wm;
		rm = new RoutingManager(wm);
	}

	@Override
	public void run() {
		
	}

	@Override
	public void messageFromFollower(A3JGMessage msg) {
		ArrayList<Address> ad = new ArrayList<Address>();
		Content c = ((Content) msg.getContent());
		ad.add(c.getAd());
		A3JGMessage mex = new A3JGMessage();
		
		if(rm.getNextDestination(c.getI(), c.getColour())!=null){
			ArrayList<Place> pos = new ArrayList<Place>();
			pos.add(rm.getNextDestination(c.getI(), c.getColour()));
			c.setPos(pos);
		}else if(c.getColour().equals("red")){
				c.setPos(wm.getLab());
		}else if(c.getColour().equals("blue")){
			c.setPos(wm.getRadiology());
		}else if(c.getColour().equals("yellow")){
			c.setPos(wm.getPhysiotherapy());
		}
		
		c.setDirection(rm.getIndicatorDirection(c.getI(), c.getColour()));
		mex.setContent(c);
		sendMessageToFollower(mex, ad);
	}

	@Override
	public void updateFromFollower(A3JGMessage msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int fitnessFunc() {
		// TODO Auto-generated method stub
		return 0;
	}

}
