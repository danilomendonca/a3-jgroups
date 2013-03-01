package node.agentMiddleware.communication.interfaces;

import node.organizationMiddleware.contextManager.contextDirectories.Organization;

public interface PingReceiver {

	public void receivePing(Organization data);
	
}
