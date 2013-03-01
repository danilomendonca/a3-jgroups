package node.agentMiddleware.communication.interfaces;

import node.organizationMiddleware.contextManager.contextDirectories.TrafficJamInfo;

import utilities.NodeID;

public interface TrafficJamInfoReceiver {

	public void receiveTrafficJamInfo(TrafficJamInfo info, NodeID target);
	
}
