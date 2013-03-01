package node.selfAdaptationSystem.coordination;

import node.agentMiddleware.communication.middleware.CommunicationMiddleware;
import node.agentMiddleware.communication.middleware.messageBuffers.BufferedInfo;

public class BufferedSelfAdaptationMessage extends BufferedInfo {

	private SelfAdaptationMessage message;
	
	public BufferedSelfAdaptationMessage(SelfAdaptationMessage message, int delay, String receiverID) {
		super(delay, receiverID);
		this.message = message;
	}
	
	public SelfAdaptationMessage getMessage(){
		return this.message;
	}
	
	

	@Override
	public void deliver(CommunicationMiddleware cmw) {
		cmw.deliver(this);
	}
}
