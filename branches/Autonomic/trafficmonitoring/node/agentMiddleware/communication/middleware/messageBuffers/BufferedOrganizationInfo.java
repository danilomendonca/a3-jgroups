package node.agentMiddleware.communication.middleware.messageBuffers;

import node.agentMiddleware.communication.middleware.CommunicationMiddleware;
import node.organizationMiddleware.contextManager.contextDirectories.Organization;


public class BufferedOrganizationInfo extends BufferedInfo{
	private Organization info;
	
	public BufferedOrganizationInfo(Organization info, int delay, String receiver){
		super(delay, receiver);
		this.info = info;
	}
	
	public Organization getInfo(){
		return info;
	}
	
	public void deliver(CommunicationMiddleware cmw){
		cmw.deliver(info);
	}
	
}
