package node.agentMiddleware.communication.middleware.messageBuffers;

import node.agentMiddleware.communication.middleware.CommunicationMiddleware;
import node.organizationMiddleware.contextManager.contextDirectories.TrafficJamInfo;
import utilities.NodeID;


public class BufferedTrafficJamInfo extends BufferedInfo{
	private TrafficJamInfo info;
	private NodeID target;
	
	public BufferedTrafficJamInfo(TrafficJamInfo info, NodeID target, int delay, String receiver){
		super(delay, receiver);
		this.info = info;
		this.target = target;
	}
	
	public TrafficJamInfo getInfo(){
		return info;
	}
	
	public NodeID getTarget(){
		return target;
	}
	
	public void deliver(CommunicationMiddleware cmw){
		cmw.deliver(this);
	}
}
