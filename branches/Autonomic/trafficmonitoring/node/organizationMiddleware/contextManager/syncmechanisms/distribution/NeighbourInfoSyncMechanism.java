package node.organizationMiddleware.contextManager.syncmechanisms.distribution;

import node.agentMiddleware.communication.interfaces.SendInterface;
import node.organizationMiddleware.contextManager.contextDirectories.Context;
import node.organizationMiddleware.contextManager.contextDirectories.NeighbourInfo;
import utilities.Event;
import utilities.Subscriber;

public class NeighbourInfoSyncMechanism implements Subscriber {

	private Context ctx;
	private SendInterface agentLayer;
	private NeighbourInfo neighbourInfo;
	
	public NeighbourInfoSyncMechanism(NeighbourInfo neighbourInfo,
									  Context ctx,
									  SendInterface agentLayer){
		this.ctx = ctx;
		this.agentLayer = agentLayer;
		this.neighbourInfo = neighbourInfo;
		neighbourInfo.subscribe("addNeighbours", this);
		sendOutInfo();
	}
	
	private void sendOutInfo() {
		String nodeID = ctx.getPersonalOrg().getMasterID().toString();
		agentLayer.sendNeighbourInfo(neighbourInfo.copy(), ctx.getPersonalID(), nodeID);
	}

	public void publish(Event e) {
		sendOutInfo();		
	}

	public void stop() {
		neighbourInfo.unsubscribe("addNeighbours", this);		
	}
	
	
}
