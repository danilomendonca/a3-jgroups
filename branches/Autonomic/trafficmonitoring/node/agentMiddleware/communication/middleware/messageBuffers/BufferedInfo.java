package node.agentMiddleware.communication.middleware.messageBuffers;

import node.agentMiddleware.communication.middleware.CommunicationMiddleware;

public abstract class BufferedInfo {
	private int delay;
	
	//Node-id
	private String receiverID;
	
	public BufferedInfo(int delay, String receiverID){
		this.delay = delay;
		this.receiverID = receiverID;
	}
	
	public String getReceiverID(){
		return this.receiverID;
	}
	
	public boolean hasDelay(){
		return delay>0;
	}
	
	public void advanceTime(){
		delay--;
	}
	
	public abstract void deliver(CommunicationMiddleware cmw);
}
