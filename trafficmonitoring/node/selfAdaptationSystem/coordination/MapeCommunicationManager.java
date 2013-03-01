package node.selfAdaptationSystem.coordination;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import utilities.NodeID;

import node.agentMiddleware.communication.middleware.CommunicationMiddleware;

public class MapeCommunicationManager {
	
	private CommunicationMiddleware cmw;
	
	public MapeCommunicationManager(NodeID localNode, CommunicationMiddleware cmw){
		this.localNode = localNode;
		this.cmw = cmw;	
		
		this.unanticipatedMessages = new LinkedList<SelfAdaptationMessage>();
		
		//Register this object as a self-adaptation messages receiver at the local communication middleware
		this.cmw.receiveSelfHealingMessages(this);	
	}
	
	/**
	 * Used by the underlying base-level CommunicationMiddleware to deliver Self-Adaptation messages
	 * sent from other nodes
	 */
	public synchronized void receiveSelfAdaptationMessage(SelfAdaptationMessage message) {
		for(MapeCoordinationPoint point : this.coordinationPoints){
			if(point.acceptsSelfHealingMessage(message.getScenario(), message.getTargetMapeComputationID())){
				point.deliver(message);
				
				return;
			}
		}
		
		//No coordination point found at the moment, store message
		this.unanticipatedMessages.add(message);
	}
	
	/**
	 * Uses the underlying base-level CommunicationMiddleware to send Self-Adaptation messages to
	 * other nodes
	 * 
	 * If the destination node is in fact the local node, deliver the message to its proper local
	 * Coordination Point.
	 */
	synchronized void sendSelfAdaptationMessage(NodeID destinationNode, SelfAdaptationMessage message){
		if(destinationNode.equals(this.localNode)){
			//Local recipient
			this.receiveSelfAdaptationMessage(message);
			
			//Intra-Loop Message counting, if necessary
			if(profiler != null){
				profiler.registerIntraLoopMessage(message);
			}
		}
		else{
			//Remote recipient
			this.cmw.sendSelfHealingMessage(message, destinationNode.toString());
			
			//Inter-Loop Message counting, if necessary
			if(profiler != null){
				profiler.registerInterLoopMessage(message);
			}
		}	
	}	
	
	private final NodeID localNode;
	
	
	/**************************	 
	 * 
	 *	Message Profiler
	 *
	 **************************/
	
	public static void setMessageProfiler(SelfAdaptationMessageProfiler profiler){
		MapeCommunicationManager.profiler = profiler;
	}
	
	private static SelfAdaptationMessageProfiler profiler;
	
	
	/**************************	 
	 * 
	 *	Unanticipated Messages
	 *
	 **************************/
	
	public boolean hasNextUnanticipatedMessage(){
		return (this.unanticipatedMessages.size() > 0);
	}
	
	/**
	 * Returns a list of all unanticipated messages, but doesn't remove them from the internal
	 * message buffer
	 */
	public synchronized List<SelfAdaptationMessage> previewAllUnanticipatedMessages(){
		ArrayList<SelfAdaptationMessage> result = new ArrayList<SelfAdaptationMessage>();
		result.addAll(this.unanticipatedMessages);
		
		return result;
	}
	
	/**
	 * Returns the next unanticipated message, but doesn't remove it from the internal
	 * message buffer
	 */
	public synchronized SelfAdaptationMessage previewNextUnanticipatedMessage(){
		return this.unanticipatedMessages.element();
	}
	
	public synchronized void removeUnanticipatedMessage(SelfAdaptationMessage unanticipatedMessage) 
				throws IllegalArgumentException{
		if(!this.unanticipatedMessages.contains(unanticipatedMessage))
			throw new IllegalArgumentException("Unanticipated Message not found!");
		
		this.unanticipatedMessages.remove(unanticipatedMessage);
	}
	
	/**
	 * Removes the unanticipated messages from the front of the message queue
	 */
	public synchronized void removeNextUnanticipatedMessage(){
		this.removeUnanticipatedMessage(this.unanticipatedMessages.peek());
	}
	
	/**
	 * Re-try to deliver the given unanticipated self-adaptation message
	 */
	public synchronized void deliverUnanticipatedMessage(SelfAdaptationMessage unanticipatedMessage) 
				throws IllegalArgumentException{
		if(!this.unanticipatedMessages.contains(unanticipatedMessage))
			throw new IllegalArgumentException("Unanticipated Message not found!");
		
		this.unanticipatedMessages.remove(unanticipatedMessage);
		this.receiveSelfAdaptationMessage(unanticipatedMessage);
		
		//If delivery once again wasn't possible: discard the message
		this.unanticipatedMessages.remove(unanticipatedMessage);
	}	
	
	public synchronized void deliverNextUnanticipatedMessage(){	
		this.deliverUnanticipatedMessage(this.unanticipatedMessages.peek());
	}
	
	private Queue<SelfAdaptationMessage> unanticipatedMessages;
	
	
	/**************************	 
	 * 
	 *	Coordination Points
	 *
	 **************************/	
	
	public synchronized void registerCoordinationPoint(MapeCoordinationPoint point){
		this.coordinationPoints.add(point);
	}
	
	public synchronized void unregisterCoordinationPoint(MapeCoordinationPoint point){
		this.coordinationPoints.remove(point);
	}
	
	private ArrayList<MapeCoordinationPoint> coordinationPoints = new ArrayList<MapeCoordinationPoint>();

}
