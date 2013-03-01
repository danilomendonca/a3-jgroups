package node.agentMiddleware.communication.middleware.messageBuffers;

import node.agentMiddleware.communication.middleware.CommunicationMiddleware;
import node.organizationMiddleware.contextManager.contextDirectories.Organization;


public class BufferedStartAsMaster extends BufferedInfo{
	private Organization organization;
	
	public BufferedStartAsMaster(Organization organization, int delay, String receiver){
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
