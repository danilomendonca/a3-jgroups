package node.organizationMiddleware.contextManager.syncmechanisms.distribution;

import node.agentMiddleware.communication.interfaces.SendInterface;
import node.organizationMiddleware.contextManager.contextDirectories.Organization;

import utilities.NodeID;
import utilities.MessageBuffer;

public class MasterIDReplier implements Runnable {
	
	private Organization personalOrganization;
	private MessageBuffer msgBuffer;
	private SendInterface agentLayer;
	
	public MasterIDReplier(
			Organization personalOrganization,
			MessageBuffer msgBuffer,
			SendInterface agentLayer){
		this.personalOrganization = personalOrganization;
		this.msgBuffer = msgBuffer;
		this.agentLayer = agentLayer;
		msgBuffer.subscribeOnMasterIDRequests();
	}
	
	public void run() {
		while(msgBuffer.hasMasterIDRequestAsNextMessage()){
			NodeID returnAdress = msgBuffer.receiveMasterIDRequest();
			//System.out.println("MasterIDReplier replies to " + returnAdress.toString() +" with "+personalOrganization.getMasterID());
			agentLayer.sendMasterIDReply(personalOrganization.getMasterID(), returnAdress.toString());
		}
	}
	
	public void stop(){
		msgBuffer.unsubscribeOnMasterIDRequests();
	}
	
	

}
