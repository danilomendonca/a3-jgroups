package it.polimi.a3Behavior;

import de.nec.nle.siafu.exceptions.NothingNearException;
import de.nec.nle.siafu.model.Agent;
import de.nec.nle.siafu.model.Place;
import de.nec.nle.siafu.model.World;
import A3JGroups.A3JGMessage;
import A3JGroups.JGFollowerRole;

public class YellowFollower extends JGFollowerRole{
	
	private Agent agent;
	private World world;
	private boolean pos;
	private Place screen;
	
	public YellowFollower(int resourceCost, String groupName) {
		super(resourceCost, groupName);
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
		pos=true;
		while (this.active) {
			try {
				if(!world.findAllAgentsNear(screen.getPos(), 80, true).contains(agent))
					this.node.terminate("yellow");
			} catch (NothingNearException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void messageFromSupervisor(A3JGMessage msg) {
		if(msg.getContent().equals("NeedDestination?") && pos){
			A3JGMessage mex = new A3JGMessage();
			mex.setContent(this.getChan().getAddress());
			sendMessageToSupervisor(mex);
		}else if (pos){
			agent.setDestination((Place) msg.getContent());
			pos=false;
		}
	}

}
