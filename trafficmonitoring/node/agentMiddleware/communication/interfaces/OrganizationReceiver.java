package node.agentMiddleware.communication.interfaces;

import node.organizationMiddleware.contextManager.contextDirectories.Organization;


public interface OrganizationReceiver {

	public void receiveOrganizationInfo(Organization orgInfo);
	
}
