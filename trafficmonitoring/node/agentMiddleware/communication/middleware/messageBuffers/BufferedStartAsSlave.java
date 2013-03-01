package node.agentMiddleware.communication.middleware.messageBuffers;

import node.agentMiddleware.communication.middleware.CommunicationMiddleware;
import node.organizationMiddleware.contextManager.contextDirectories.Organization;




public class BufferedStartAsSlave extends BufferedInfo{
	private Organization organization;
	
	public BufferedStartAsSlave(Organization organization, int delay, String receiver){
		super(delay, receiver);
		this.organization = organization;
	}
	
	public Organization getOrganization(){
		return organization;
	}
	
	public void deliver(CommunicationMiddleware cmw){
		cmw.deliver(this);
	}
}
