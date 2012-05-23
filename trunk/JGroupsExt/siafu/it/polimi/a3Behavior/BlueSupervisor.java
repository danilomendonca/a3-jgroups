package it.polimi.a3Behavior;

import java.util.ArrayList;

import org.jgroups.Address;

import de.nec.nle.siafu.model.Agent;
import de.nec.nle.siafu.model.Place;

import A3JGroups.A3JGMessage;
import A3JGroups.JGSupervisorRole;


public class BlueSupervisor extends JGSupervisorRole {
	
	private Agent agent;
	private ArrayList<Place> pos;
	
	public BlueSupervisor(int resourceCost, String groupName) {
		super(resourceCost, groupName);
	}
	
	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}
	
	public void setPos(ArrayList<Place> pos) {
		this.pos = pos;
	}

	@Override
	public void run() {
		while (this.active) {
			A3JGMessage mex = new A3JGMessage();
			mex.setContent("NeedDestination?");
			sendMessageToFollower(mex, null);
		}
	}

	@Override
	public void messageFromFollower(A3JGMessage msg) {
		ArrayList<Address> ad = new ArrayList<Address>();
		ad.add((Address) msg.getContent());
		int posNum = (int) (Math.random()*4);
		A3JGMessage mex = new A3JGMessage();
		mex.setContent(pos.get(posNum));
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
