package node.organizationMiddleware.contextManager.syncmechanisms.distribution;

import java.util.ArrayList;

import node.organizationMiddleware.contextManager.contextDirectories.Context;
import node.organizationMiddleware.contextManager.contextDirectories.RolePosition;
import node.organizationMiddleware.contextManager.contextDirectories.TrafficJamInfo;

import utilities.NodeID;
import utilities.MessageBuffer;


public class TrafficJamInfoSyncMechanism_Remote implements Runnable {

	private Context ctx;
	private MessageBuffer messageBuffer;
	
	public TrafficJamInfoSyncMechanism_Remote(Context ctx,
											  MessageBuffer msgBuffer){
		this.ctx = ctx;
		this.messageBuffer = msgBuffer;
		messageBuffer.subscribeOnTrafficJamInfoMessages();
	}
	
	public void run() {
		while(messageBuffer.hasReceiveTrafficJamInfoAsNextMessage()){
			ArrayList<Object> data = messageBuffer.receiveTrafficJamInfo();
			TrafficJamInfo info = (TrafficJamInfo) data.get(0);
			NodeID target = (NodeID) data.get(1);
			for(RolePosition rp : ctx.getPersonalOrg().getFilledRolePositions()){
				if(rp.getAgentId().equals(target)){
					rp.getTrafficJamInfo().setAvgVelocityAndDensity(info.getAvgVelocity(), info.getDensity());
					//rp.getTrafficJamInfo().setAvgVelocity(info.getAvgVelocity());
					//rp.getTrafficJamInfo().setDensity(info.getDensity());
				}
			}
		}
	}

	public void stop() {
		messageBuffer.unsubscribeOnTrafficJamInfoMessages();
	}

}
