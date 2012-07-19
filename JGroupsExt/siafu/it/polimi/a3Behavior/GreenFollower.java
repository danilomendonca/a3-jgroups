package it.polimi.a3Behavior;

import de.nec.nle.siafu.exceptions.NothingNearException;
import de.nec.nle.siafu.model.Agent;
import de.nec.nle.siafu.model.Place;
import de.nec.nle.siafu.model.World;
import A3JGroups.A3JGMessage;
import A3JGroups.JGFollowerRole;



public class GreenFollower extends JGFollowerRole {
	
	private Agent agent;
	private World world;
	private Place screen;
	
	public GreenFollower(int resourceCost) {
		super(resourceCost);
	}
	
	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}
	
	public void setWorld(World world) {
		this.world = world;
	}
	
	public void setScreen(Place screen) {
		this.screen = screen;
	}

	@Override
	public void run() {
		A3JGMessage mex = new A3JGMessage("info");
		mex.setContent(this.getChan().getAddress());
		sendMessageToSupervisor(mex);
		while (this.active) {
			try {
				if(!world.findAllAgentsNear(screen.getPos(), 80, true).contains(agent))
					this.getNode().terminate("green");
			} catch (NothingNearException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void messageFromSupervisor(A3JGMessage msg) {
		agent.setDestination((Place) msg.getContent());
	}

}
