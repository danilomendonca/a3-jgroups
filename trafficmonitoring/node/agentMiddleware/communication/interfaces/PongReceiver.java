package node.agentMiddleware.communication.interfaces;

import node.organizationMiddleware.contextManager.contextDirectories.Organization;

public interface PongReceiver {

	public void receivePong(Organization data);
	
}
