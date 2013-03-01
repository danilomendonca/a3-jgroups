package node.agentMiddleware.communication.interfaces;

import node.organizationMiddleware.contextManager.contextDirectories.NeighbourInfo;

import utilities.NodeID;

public interface NeighbourInfoReceiver {

	public void receiveNeighbourInfo(NeighbourInfo info, NodeID target);
	
}
