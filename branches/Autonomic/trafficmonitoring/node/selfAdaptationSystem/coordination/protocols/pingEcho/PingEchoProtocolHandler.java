package node.selfAdaptationSystem.coordination.protocols.pingEcho;

import java.util.ArrayList;

import utilities.NodeID;
import node.selfAdaptationSystem.coordination.protocols.CoordinationProtocolHandler;
import node.selfAdaptationSystem.coordination.protocols.pingEcho.PingEchoMessage.PingEchoMessageType;
import node.selfAdaptationSystem.mapeManager.MapeComputation;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class PingEchoProtocolHandler 
				extends CoordinationProtocolHandler<PingEchoProtocolHandler.PingEchoProtocolRole> {

	public enum PingEchoProtocolRole { TARGET, SOURCE }
	
	/**
	 * Create a handler in the role of TARGET, responding to ping-messages from remote nodes
	 * with echo-replies
	 */
	public PingEchoProtocolHandler(MapeComputation localMapeComputation, NodeID localNode){
		super(PingEchoProtocolRole.TARGET, localMapeComputation);
		
		//The local node is the target node
		this.targetNode = localNode;
		this.pingEchoInfo = new PingEchoInformation(localNode);
		
		this.sourceContacts = new ArrayList<NodeID>();
	}
	
	/**
	 * Create a handler in the role of SOURCE, sending ping-messages to the given target node and
	 * processing its echo-replies. The handler will wait the given amount of execution cycles to contact the target
	 * node again after having received the previous echo-reply.
	 */
	public PingEchoProtocolHandler(MapeComputation localMapeComputation, NodeID targetNode, int minEchoTimestampExecutionCycleAge){
		super(PingEchoProtocolRole.SOURCE, localMapeComputation);
		
		this.targetNode = targetNode;
		this.minEchoTimestampExecutionCycleAge = minEchoTimestampExecutionCycleAge;
		
		this.pingEchoInfo = new PingEchoInformation(targetNode);
	}
	
	
	/**************************	 
	 * 
	 *	Execution
	 *
	 **************************/
	
	@Override
	/**
	 * If this handler is occupying the PingEchoProtocolRole of SOURCE:
	 * - It will send out a ping message to the target node if none has been sent already or
	 * if the minimum timestamp age of the last echo-reply is reached
	 * - It will respond to an echo-reply by updating its PingEchoInformation
	 * 
	 * If this handler is occupying the PingEchoProtocolRole of TARGEt:
	 * - It will respond to ping messages from target nodes by sending out echo-replies
	 */
	public void execute() {
		if(this.getProtocolRole() == PingEchoProtocolRole.SOURCE)
			this.executeOnSource();
		else
			this.executeOnTarget();
	}
	
	@Override
	/**
	 * Since monitoring a target node is a continuous process, this method always returns false.
	 */
	public boolean hasCompleted() {
		return false;
	}
	
	/**
	 * Note: only of use to SOURCE nodes
	 */
	public PingEchoInformation getPingEchoInformation(){
		return this.pingEchoInfo;
	}
	
	public NodeID getTargetNode(){
		return this.targetNode;
	}
	
	/**
	 * Returns a list of nodes that, in the SOURCE role, have contacted this local TARGET node
	 * with ping-requests (to which this node has successfully replied)
	 *
	 * Note: only of use on TARGET nodes
	 */
	public ArrayList<NodeID> getSourceContacts(){
		return this.sourceContacts;
	}
	
	private NodeID targetNode;
	private int minEchoTimestampExecutionCycleAge;
	private ArrayList<NodeID> sourceContacts;
	
	
	/**************************	 
	 * 
	 *	Execution on SOURCE
	 *
	 **************************/
	
	private void executeOnSource(){
		this.pingTargetNode();
		
		this.processEchoReply();	
	}
	
	private void pingTargetNode(){
		SelfAdaptationModels models = this.getMapeComputation().getSelfAdaptationModels();
		
		//Send ping message if none has been sent already or if the minimum timestamp age of the last echo-reply is reached
		if( !this.pingEchoInfo.pingMessageSent()
				|| (this.pingEchoInfo.echoReplyReceived() && !models.executionCycleTimestampIsYoungerThan(
							pingEchoInfo.getExecutionCycleTimestampLastReceivedEchoReply(), this.minEchoTimestampExecutionCycleAge) ) ){
			//Send message
			this.sendPingMessage(this.targetNode);
			
//			System.out.println("cycle " + this.getMapeComputation().getSelfAdaptationModels().getCurrentExecutionCycle() + 
//					" | on node "+this.getMapeComputation().getSelfAdaptationModels().getHostNode()+": ping sent to "+targetNode);
			
			//Record ping timestamp
			this.pingEchoInfo.updateExecutionCycleTimestampForSentPingMessage(models.getCurrentExecutionCycle());
			//Reset echo-reply timestamp
			this.pingEchoInfo.resetExecutionCycleTimestampReceivedEchoReply();
		}
	}
	
	protected void sendPingMessage(NodeID target) {
		this.getMapeComputation().sendRemoteMessage(target, new PingEchoMessage(PingEchoMessageType.PING));
	}
	
	private void processEchoReply(){
		PingEchoMessage echoReply = this.receiveEchoMessage();
		
		if(echoReply != null){
			//Record echo reply timestamp
			this.pingEchoInfo.updateExecutionCycleTimestampForReceivedEchoReply(this.getCurrentExecutionCycle());
			
//			System.out.println("cycle " + this.getMapeComputation().getSelfAdaptationModels().getCurrentExecutionCycle() + 
//					" | on node "+this.getMapeComputation().getSelfAdaptationModels().getHostNode()+": echo reply received from "+targetNode);
		}		
		//Else: no echo-reply received at the moment; do nothing
	}
	
	private PingEchoInformation pingEchoInfo;
	
	
	/**************************	 
	 * 
	 *	Execution on TARGET
	 *
	 **************************/
	
	/*
	 * Respond to ping-messages from remote nodes
	 */
	private void executeOnTarget(){
		while(this.getMapeComputation().hasNextRemoteMessage()){
			PingEchoMessage msg = (PingEchoMessage) this.getMapeComputation().getNextRemoteMessage();
			
//			System.out.println("cycle " + this.getMapeComputation().getSelfHealingModels().getCurrentExecutionCycle() + 
//					" | on node "+ this.getMapeComputation().getSelfHealingModels().getHostNode() + ": ping received from " + msg.getSenderNode());
			
			//Store source agent
			if(!sourceContacts.contains(msg.getSenderNode()))
				this.sourceContacts.add(msg.getSenderNode());
			
			if(msg.getType().equals(PingEchoMessageType.PING))
				this.sendEchoMessage(msg.getSenderNode());
		}
	}
	
	protected void sendEchoMessage(NodeID source) {
		this.getMapeComputation().sendRemoteMessage(source, new PingEchoMessage(PingEchoMessageType.ECHO));
	}	
	
	
	/**************************	 
	 * 
	 *		RECEIVE
	 *
	 **************************/
	
	protected PingEchoMessage receiveEchoMessage(){
		//Look only for ECHO-replies sent by the target node
		PingEchoMessageSelector echoSelector = 
			new PingEchoMessageSelector(PingEchoMessageType.ECHO, this.targetNode);
		
		return ((PingEchoMessage) this.getMapeComputation().getNextRemoteMessage(echoSelector));
	}

}
