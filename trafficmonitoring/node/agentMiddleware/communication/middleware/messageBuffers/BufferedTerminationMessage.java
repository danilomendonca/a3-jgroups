package node.agentMiddleware.communication.middleware.messageBuffers;

import node.agentMiddleware.communication.middleware.CommunicationMiddleware;


public class BufferedTerminationMessage extends BufferedInfo{
	
	public BufferedTerminationMessage(int delay, String receiver){
		super(delay, receiver);
	}
	
	public void deliver(CommunicationMiddleware cmw){
		cmw.deliver(this);
	}
}
