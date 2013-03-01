package node.organizationMiddleware.contextManager.syncmechanisms.distribution;

import java.util.ArrayList;

import node.organizationMiddleware.contextManager.contextDirectories.Context;
import node.organizationMiddleware.contextManager.contextDirectories.NeighbourInfo;
import node.organizationMiddleware.contextManager.contextDirectories.RolePosition;

import utilities.NodeID;
import utilities.MessageBuffer;

public class NeighbourInfoSyncMechanism_Remote implements Runnable{
	
	private MessageBuffer messageBuffer;
	private Context ctx;
	
	public NeighbourInfoSyncMechanism_Remote(MessageBuffer msgBuffer,
											 Context ctx){
		this.messageBuffer = msgBuffer;
		this.ctx = ctx;
		messageBuffer.subscribeOnNeighbourInfoMessages();
	}


	public void run() {
		while(messageBuffer.hasReceiveNeighbourInfoAsNextMessage()){
			ArrayList<Object> data = messageBuffer.receiveNeighbourInfo();
			NeighbourInfo info = (NeighbourInfo) data.get(0);
			NodeID target = (NodeID) data.get(1);
			for(RolePosition rp : ctx.getPersonalOrg().getFilledRolePositions()){
				if(rp.getAgentId().equals(target)){
					rp.getNeighbourInfo().addNeighbours(info.getNeighbours());
				}
			}
		}
	}

	public void stop() {
		messageBuffer.unsubscribeOnNeighbourInfoMessages();
		
	}


}
