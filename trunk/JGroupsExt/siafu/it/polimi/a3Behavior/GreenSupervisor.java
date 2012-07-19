package it.polimi.a3Behavior;

import java.util.ArrayList;

import org.jgroups.Address;

import de.nec.nle.siafu.model.Agent;
import de.nec.nle.siafu.model.Place;

import A3JGroups.A3JGMessage;
import A3JGroups.JGSupervisorRole;


public class GreenSupervisor extends JGSupervisorRole {

	private Agent agent;
	private ArrayList<Place> pos;
	
	public GreenSupervisor(int resourceCost) {
		super(resourceCost);
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
	
	}

	@Override
	public void messageFromFollower(A3JGMessage msg) {
		ArrayList<Address> ad = new ArrayList<Address>();
		ad.add((Address) msg.getContent());
		int posNum = (int) (Math.random()*4);
		A3JGMessage mex = new A3JGMessage("info");
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
