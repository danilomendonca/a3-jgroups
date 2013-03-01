package node.agentMiddleware.communication.middleware.messageBuffers;

import node.agentMiddleware.communication.middleware.CommunicationMiddleware;
import utilities.NodeID;


public class BufferedMasterIDReply extends BufferedInfo{
	private NodeID fromMasterID;
	
	public BufferedMasterIDReply(int delay, NodeID fromMasterID, String receiver){
		super(delay, receiver);
		this.fromMasterID = fromMasterID;
	}
	
	public NodeID getMasterID(){
		return fromMasterID;
	}
	
	public void deliver(CommunicationMiddleware cmw){
		cmw.deliver(this);
	}
}