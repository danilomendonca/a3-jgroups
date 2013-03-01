package node.selfAdaptationSystem.mapeManager.neighborNodeFailure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import utilities.NodeID;
import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.coordination.protocols.pingEcho.PingEchoMessage;
import node.selfAdaptationSystem.coordination.protocols.pingEcho.PingEchoProtocolHandler;
import node.selfAdaptationSystem.coordination.protocols.pingEcho.PingEchoMessage.PingEchoMessageType;
import node.selfAdaptationSystem.mapeManager.MonitorComputation;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class M41 extends MonitorComputation implements NeighborNodeFailureComputation {

	public M41(NeighborNodeFailureScenario scenario, SelfAdaptationModels models, 
				BaseLevelConnector baseLevel){
		super(scenario, models, baseLevel);
	}
	
	
	/**************************	 
	 * 
	 *	Execution
	 *
	 **************************/		
	
	@Override
	/**
	 * As the subject node of this particular Self-Healing Scenario:
	 * - respond to ping-messages from neighbor nodes with echo-replies
	 * - possibly react to the reintroduction of a previously failed neighbor node
	 * 	(this implies an unanticipated message/scenario from a physical neighbor of the local node)
	 */
	public void executeOnSubjectNode(){
		//Do not use the ProtocolHandler's implementation because the id of the sender
		// needs to be analyzed
		
		while(this.hasNextRemoteMessage()){
			PingEchoMessage msg = (PingEchoMessage) this.getNextRemoteMessage();
			
			//Send echo-reply
			if(msg.getType().equals(PingEchoMessageType.PING))
				this.sendRemoteMessage(msg.getSenderNode(), new PingEchoMessage(PingEchoMessageType.ECHO));
			
			//If the message was sent by a node that is registered in the self-adaptation models as having
			// failed previously: conclude that it wants to be reintroduced into the traffic monitoring
			// system and that the local node should also become a neighbor of this reintroduced camera again.
			//The base-level list of alive neighbor nodes will need to be adapted, as the introduction of this new
			// camera implies that cameras which were neighbor nodes will now no longer be ...
			//Note: In this case, this scenario was instantiated because of the received unanticipated message
			if(this.getSelfAdaptationModels().getPreviouslyFailedNeighborNodes().contains(msg.getSenderNode())){				
				//Start the (recursive) process from the local host node, using an empty set of previously checked nodes
				NodeID nodeToCheck = this.getSelfAdaptationModels().getHostNode();
				NodeID restartedNode = msg.getSenderNode();
				Set<NodeID> alreadyCheckedNodes = new HashSet<NodeID>();				
				
				Set<NodeID> newAliveAndFailedNeighbors =
					this.determineNewAliveAndFailedNeighborNodeList(nodeToCheck, restartedNode, alreadyCheckedNodes);
				
				//The new list of alive neighbor nodes, used by the local base-level, are all cameras in the newly determined
				// list that were not previously registered as having failed, plus the camera that is going to be reintroduced
				// into the traffic monitoring system				
				ArrayList<NodeID> newAliveNeighbors = new ArrayList<NodeID>();
				newAliveNeighbors.add(restartedNode);
				for(NodeID aliveOrFailedNeighbor : newAliveAndFailedNeighbors){
					if(!this.getSelfAdaptationModels().getPreviouslyFailedNeighborNodes().contains(aliveOrFailedNeighbor)){
						newAliveNeighbors.add(aliveOrFailedNeighbor);
					}
				}
				//HACK: Normally ONLY adapt the base-level in a separate Execute-computation ...
				this.getBaseLevelConnector().setAliveNeighbors(newAliveNeighbors);
				
				//The new list of previously failed neighbor nodes in the self-adaptation models is the determined list of alive
				// and failed neighbors, minus the new list of alive neighbors
				ArrayList<NodeID> newFailedNeighbors = new ArrayList<NodeID>();
				newFailedNeighbors.addAll(newAliveAndFailedNeighbors);
				newFailedNeighbors.removeAll(newAliveNeighbors);				
				this.getSelfAdaptationModels().setPreviouslyFailedNeighborNodeList(newFailedNeighbors);
				//Remove unneeded neighbor organization information from the self-adaptation models
				for(NodeID previousNeighbor : this.getSelfAdaptationModels().getAliveNeighborNodes()){
					if(!newAliveNeighbors.contains(previousNeighbor)){
						this.getSelfAdaptationModels().removeNeighborNodeOrganizationInformation(previousNeighbor);
					}
				}
			}
		}		
		
		//No need to move on to next computation as subject node
	}
	
	/*
	 * Returns the set of alive and failed neighbor nodes of the given nodeToCheck, to be used by the local self-adaptation 
	 * subsystem now that the given restartedNode has reappeared after failing previously. Use the given set of checked nodes
	 * to avoid unnecessary work.
	 */
	private Set<NodeID> determineNewAliveAndFailedNeighborNodeList(NodeID nodeToCheck, NodeID restartedNode, 
																		Set<NodeID> alreadyCheckedNodes){
		//In any case: the list of already check nodes is part of the result, 
		// plus the nodeToCheck (as this is going to be done here)
		HashSet<NodeID> result = new HashSet<NodeID>();
		result.addAll(alreadyCheckedNodes);
		result.add(nodeToCheck);
		
		//If the nodeToCheck is registered in the local self-adaptation models as having failed previously but is
		// not the given restarted node, or if it is the local host node: recursively check all physical neighbors 
		//of this node, unless this has been done already
		if((this.getSelfAdaptationModels().getPreviouslyFailedNeighborNodes().contains(nodeToCheck) &&
				!nodeToCheck.equals(restartedNode)) || nodeToCheck.equals(this.getSelfAdaptationModels().getHostNode())){
			for(NodeID physicalNeighbor : this.getSelfAdaptationModels().getPhysicalNeighborsFor(nodeToCheck)){
				if(!alreadyCheckedNodes.contains(physicalNeighbor)){
					//Send along the latest set of checked nodes
					result.addAll(this.determineNewAliveAndFailedNeighborNodeList(physicalNeighbor, restartedNode, result));
				}
			}
		}
		
		//Make sure that the returned list does not contain the local host node
		result.remove(this.getSelfAdaptationModels().getHostNode());
		
		return result;
	}

	@Override
	/**
	 * As a neighbor of the subject of this particular Self-Healing Scenario:
	 * - periodically send ping messages to the scenario's subject; a neighbor of this local node
	 * - process echo-replies from subject neighbor node
	 */
	public void executeOnSubjectNeighborNode(){
		//Initialize CoordinationHandler (with SOURCE role) if necessary
		if(this.pingEchoHandler == null){
			NodeID pingTarget = this.getScenario().getSubject();
			int minimumEchoTimestampExecutionCycleAge = 30;
			
			this.pingEchoHandler = 
				new PingEchoProtocolHandler(this, pingTarget, minimumEchoTimestampExecutionCycleAge);
		}			
		
		this.pingEchoHandler.execute();
		
		//Move on to next computation: A41
		// Provide PingEchoInformation from CoordinationHandler
		this.transition("A41", this.pingEchoHandler.getPingEchoInformation());
	}
	
	private PingEchoProtocolHandler pingEchoHandler;

}