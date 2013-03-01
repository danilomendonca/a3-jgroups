package node.selfAdaptationSystem.coordination;
import java.util.LinkedList;
import java.util.Queue;

import utilities.NodeID;

import node.selfAdaptationSystem.mapeManager.MapeComputation;
import node.selfAdaptationSystem.mapeManager.ScenarioIdentifier;

public class MapeCoordinationPoint {
		
	public MapeCoordinationPoint(MapeCommunicationManager communicationManager, NodeID hostNode){
		this.communicationManager = communicationManager;		
		this.hostNode = hostNode;
		
		this.remoteMessageBuffer = new LinkedList<SelfAdaptationMessage>();
		this.transitionMessageBuffer = new LinkedList<ComputationTransitionMessage<?>>();
	}
	
	
	/**************************	 
	 * 
	 *	Reflective Computation
	 *
	 **************************/	
	
	public void setReflectiveComputation(MapeComputation computation){
		this.computation = computation;
	}
	
	public MapeComputation getReflectiveComputation(){
		return this.computation;
	}
	
	private MapeComputation computation;
	
	
	/**************************	 
	 * 
	 *	Communication Manager
	 *
	 **************************/	
	
	/**
	 * @pre		This point should accept the given message		
	 * 			acceptsSelfHealingMessage(...)
	 */
	public synchronized void deliver(SelfAdaptationMessage message){
		if(message instanceof ComputationTransitionMessage<?>)
			this.transitionMessageBuffer.add((ComputationTransitionMessage<?>) message);
		else
			this.remoteMessageBuffer.add(message);
	}
	
	/**
	 * Note: if the computation for this point is part of a scenario that has ended, new
	 * self-healing messages will not be accepted
	 */
	public boolean acceptsSelfHealingMessage(ScenarioIdentifier scenario, String mapeID){
		if(this.computation.getScenario().hasEnded())
			return false;
		
		return (this.computation.getComputationID().equals(mapeID) 
					&& this.computation.getScenario().getIdentifier().equals(scenario));
	}
	
	
	/**************************	 
	 * 
	 *	Receive Remote 
	 *    Messages
	 *
	 **************************/		
	
	public boolean hasNextRemoteMessage() {
		return (this.remoteMessageBuffer.size() > 0);
	}

	/**
	 * Returns the next inter-loop coordination message, received from a remote
	 * node
	 * 
	 * @throws	NoSuchElementException 			
	 */
	public SelfAdaptationMessage getNextRemoteMessage() {
		return this.remoteMessageBuffer.remove();
	}
	
	/**
	 * Returns the first self-healing message currently stored in this coordination point's
	 * message buffer that is selected by the given message selector.
	 * If no such message is found, the null-reference is returned;
	 */
	public SelfAdaptationMessage getNextRemoteMessage(SelfAdaptationMessageSelector messageSelector){
		SelfAdaptationMessage messageToReturn = null;
		
		for(SelfAdaptationMessage message : this.remoteMessageBuffer){
			if(messageSelector.select(message))
				messageToReturn = message;
		}
		
		if(messageToReturn != null){
			this.remoteMessageBuffer.remove(messageToReturn);
		}
		
		return messageToReturn;
	}
	
	private Queue<SelfAdaptationMessage> remoteMessageBuffer;	
	
	
	/**************************	 
	 * 
	 *	Receive Transition
	 *       Messages
	 *
	 **************************/		
	
	public boolean hasNextTransitionMessage() {
		return (this.transitionMessageBuffer.size() > 0);
	}

	/**
	 * Returns the next intra-loop coordination message (computation transition)
	 * 
	 * @throws	NoSuchElementException 			
	 */
	public ComputationTransitionMessage<?> getNextTransitionMessage() {
		return this.transitionMessageBuffer.remove();
	}
	
	private Queue<ComputationTransitionMessage<?>> transitionMessageBuffer;	
	
	
	
	/**************************	 
	 * 
	 *	Send Messages
	 *
	 **************************/	
	
	/**
	 * Should NOT be used directly by ReflectiveComputation-subclasses!
	 */
	public synchronized void send(NodeID destinationNode, SelfAdaptationMessage message) {
		//Set needed information
		message.setSenderNode(this.hostNode);
		
		//For use in message profiling/reporting
		if(this.getReflectiveComputation().getScenario().isBusyAdapting()){
			message.setSentDuringAdaptation();
		}
		
		this.communicationManager.sendSelfAdaptationMessage(destinationNode, message);
	}
	
	public NodeID getLocalNode(){
		return this.hostNode;
	}
	
	private final MapeCommunicationManager communicationManager;
	private final NodeID hostNode;
}
