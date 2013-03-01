package node.agentMiddleware.communication.middleware.messageBuffers;

import node.agentMiddleware.communication.middleware.CommunicationMiddleware;
import node.organizationMiddleware.contextManager.contextDirectories.NeighbourInfo;
import utilities.NodeID;


public class BufferedNeighbourInfo extends BufferedInfo{
	private NeighbourInfo info;
	private NodeID target;
	
	public BufferedNeighbourInfo(NeighbourInfo info, NodeID target, int delay, String receiver){
		super(delay, receiver);
		this.info = info;
		this.target = target;
	}
	
	public NeighbourInfo getInfo(){
		return info;
	}
	
	public NodeID getTarget(){
		return target;
	}
	
	public void deliver(CommunicationMiddleware cmw){
		cmw.deliver(this);
	}

}
