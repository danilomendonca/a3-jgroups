package node.agentMiddleware.communication.middleware.messageBuffers;

import node.agentMiddleware.communication.middleware.CommunicationMiddleware;
import node.organizationMiddleware.contextManager.contextDirectories.Organization;



public class BufferedPing extends BufferedInfo{
	private Organization organization;
	
	public BufferedPing(Organization org, int delay, String receiver){
		super(delay, receiver);
		this.organization = org;
	}
	
	public Organization getOrganization(){
		return organization;
	}
	
	public void deliver(CommunicationMiddleware cmw){
		cmw.deliver(this);
	}
	
}
