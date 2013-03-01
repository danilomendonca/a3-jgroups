package node.agentMiddleware.communication.interfaces;

import node.organizationMiddleware.contextManager.contextDirectories.Organization;


public interface LifecycleMessagesReceiver {
	
	public void receiveStartAsSlaveMessage(Organization orgInfo);
	public void receiveStartAsOrganizationManagerMessage(Organization orgInfo);
}
