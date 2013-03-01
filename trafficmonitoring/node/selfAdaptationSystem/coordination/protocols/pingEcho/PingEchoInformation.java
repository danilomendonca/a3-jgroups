package node.selfAdaptationSystem.coordination.protocols.pingEcho;

import utilities.NodeID;

public class PingEchoInformation {
	
	public PingEchoInformation(NodeID targetNode){
		this.targetNode = targetNode;
	}
	
	private final NodeID targetNode;
	
	public NodeID getTargetNode(){
		return this.targetNode;
	}
		
	
	/**************************	 
	 * 
	 *		Ping
	 *
	 **************************/
	
	public void updateExecutionCycleTimestampForSentPingMessage(int currentExecutionCycle){
		this.executionCycletimestampLastSentPingMessage = currentExecutionCycle;
	}
	
	public int getExecutionCycleTimestampLastSentPingMessage(){
		return this.executionCycletimestampLastSentPingMessage;
	}
	
	public boolean pingMessageSent(){
		return (this.executionCycletimestampLastSentPingMessage > 0);
	}
	
	private int executionCycletimestampLastSentPingMessage = Integer.MIN_VALUE;
	
	
	/**************************	 
	 * 
	 *		Echo
	 *
	 **************************/
	
	public void updateExecutionCycleTimestampForReceivedEchoReply(int currentExecutionCycle){
		this.executionCycleTimestampLastReceivedEchoReply = currentExecutionCycle;
	}
	
	public void resetExecutionCycleTimestampReceivedEchoReply(){
		this.executionCycleTimestampLastReceivedEchoReply = Integer.MIN_VALUE;
	}
	
	public int getExecutionCycleTimestampLastReceivedEchoReply(){
		return this.executionCycleTimestampLastReceivedEchoReply;
	}
	
	public boolean echoReplyReceived(){
		return (this.executionCycleTimestampLastReceivedEchoReply > 0);
	}
	
	private int executionCycleTimestampLastReceivedEchoReply = Integer.MIN_VALUE;	

}
