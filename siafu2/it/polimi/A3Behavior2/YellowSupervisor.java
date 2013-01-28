package it.polimi.A3Behavior2;

import java.util.ArrayList;

import org.jgroups.Address;


import de.nec.nle.siafu.model.Agent;
import de.nec.nle.siafu.model.Place;
import A3JGroups.A3JGMessage;
import A3JGroups.A3JGSupervisorRole;

public class YellowSupervisor extends A3JGSupervisorRole {
	
	private Agent agent;
	
	public YellowSupervisor(int resourceCost) {
		super(resourceCost);
	}
	
	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	@Override
	public void run() {
		
	}

	@Override
	public void messageFromFollower(A3JGMessage msg) {
		ArrayList<Address> ad = new ArrayList<Address>();
		ad.add((Address) msg.getContent());
		A3JGMessage mex = new A3JGMessage("info");
		ArrayList<Place> p = ((MixedNode) this.getNode()).getYellow();
		mex.setContent(p.get((int) (Math.random()*(p.size()-1))));
		sendMessageToFollower(mex, ad);
	}

	@Override
	public void updateFromFollower(A3JGMessage msg) {
		
	}

	@Override
	public int fitnessFunc() {
		return 1;
	}

	
}
