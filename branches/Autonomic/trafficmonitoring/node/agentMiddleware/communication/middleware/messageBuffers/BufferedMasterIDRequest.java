package node.agentMiddleware.communication.middleware.messageBuffers;

import node.agentMiddleware.communication.middleware.CommunicationMiddleware;
import utilities.NodeID;


public class BufferedMasterIDRequest extends BufferedInfo{
	private NodeID returnID;
	
	public BufferedMasterIDRequest(int delay, NodeID returnIdentifier, String receiver){
		super(delay, receiver);
		this.returnID = returnIdentifier;
	}
	
	public NodeID getReturnID(){
		return returnID;
	}
	
	public void deliver(CommunicationMiddleware cmw){
		cmw.deliver(this);
	}
}