package it.polimi.A3Behavior2;

import java.util.ArrayList;

import org.jgroups.Address;

import A3JGroups.A3JGMessage;
import A3JGroups.JGSupervisorRole;
import de.nec.nle.siafu.model.Agent;
import de.nec.nle.siafu.model.Place;

public class SubYellowSupervisor  extends JGSupervisorRole{
	
	private Agent agent;
	
	public SubYellowSupervisor(int resourceCost, String groupName) {
		super(resourceCost, groupName);
	}
	
	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}
	
	@Override
	public void run() {
		try {
			this.getNode().joinGroup(this.getChan().getClusterName().substring(3));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void messageFromFollower(A3JGMessage msg) {
		ArrayList<Address> ad = new ArrayList<Address>();
		ad.add((Address) msg.getContent());
		A3JGMessage mex = new A3JGMessage();
		ArrayList<Place> p = ((MixedNode) this.getNode()).getRed();
		mex.setContent(p.get((int) (Math.random()*(p.size()-1))));
		sendMessageToFollower(mex, ad);
		
	}

	@Override
	public void updateFromFollower(A3JGMessage msg) {
		
		
	}

	@Override
	public int fitnessFunc() {
		int fit =  Math.abs(agent.getPos().getCol() - agent.getDestination().getPos().getCol()) +  Math.abs(agent.getPos().getRow() - agent.getDestination().getPos().getRow());
		return fit;
	}

}

