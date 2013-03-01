package node.selfAdaptationSystem.mapeManager.neighborNodeFailure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import utilities.NodeID;
import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.coordination.protocols.pingEcho.PingEchoInformation;
import node.selfAdaptationSystem.coordination.protocols.pingEcho.PingEchoMessage;
import node.selfAdaptationSystem.coordination.protocols.pingEcho.PingEchoProtocolHandler;
import node.selfAdaptationSystem.coordination.protocols.pingEcho.PingEchoMessage.PingEchoMessageType;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.MonitorComputation;
import node.selfAdaptationSystem.mapeManager.neighborNodeFailure.messages.M42Input;
import node.selfAdaptationSystem.selfAdaptationController.SelfAdaptationController.LocalTrafficSystemRoleType;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class M42 extends MonitorComputation implements NeighborNodeFailureComputation {
	
	public M42(NeighborNodeFailureScenario scenario, SelfAdaptationModels models, 
			BaseLevelConnector baseLevel){
		super(scenario, models, baseLevel);
	}
	
	/**************************	 
	 * 
	 *	Execution
	 *
	 **************************/
	
	/*
	 * Handles ping-requests sent by neighbor nodes of the scenario subject
	 */
	private void handlePingRequests(){
		//Initialize CoordinationHandler (with TARGET role) if necessary
		if(this.pingHandler == null)
			this.pingHandler = new PingEchoProtocolHandler(this, this.getSelfAdaptationModels().getHostNode());
		
		this.pingHandler.execute();	
	}
	
	private PingEchoProtocolHandler pingHandler;
	
	@Override
	/**
	 * As a neighbor of the subject of this particular Self-Healing Scenario:
	 * - ping potential all potential new neighbor nodes, meant to replace the dead subject node
	 * - reply to ping messages from other neighbor nodes looking to do the same thing
	 * - possibly react to the reintroduction of a previously failed neighbor node
	 * 	(this implies an unanticipated message from a node that is not a direct physical neighbor of the local node)
	 * 
	 * Note: the purpose if this computation M42 and of A42 is for all alive neighbors of the now dead subject
	 * node to 'meet up', as they will now all become direct neighbor nodes of each other.
	 */
	public void executeOnSubjectNeighborNode(){
		//Handle the case of a previously failed neighbor camera wanting to be reintroduced into the traffic monitoring
		// system.
		//This is the case if this scenario was instantiated after an unanticipated message, implying the absence of a
		// computation transition message
		//Note: this is normally not the case!
		if(this.getComputationTransitionMessage() == null){
			this.handleNeighborCameraIntroductionSituation();
		}
		//Else: execute the normal computation logic, i.e. pinging all potential new neighbor nodes, meant to replace
		// the dead subject node
		else{
			this.regularM42ComputationLogic();
		}	
	}
	
	private void regularM42ComputationLogic(){
		List<NodeID> deadNeighborNodes = null;	
		
		if(this.getComputationTransitionMessage().getSourceMapeComponentID().equals("A41")){
			//If the transition message was sent by the "A41" computation (i.e. this is the very first execution cycle of this
			// computation): initialize the list of dead neighbor nodes a list containing just the dead subject node
			deadNeighborNodes = new ArrayList<NodeID>();
			deadNeighborNodes.add(this.getScenario().getSubject());
		}
		else{
			//Else: this is not the first execution cycle for this computation, and the previous computation was "A42", meaning
			// that this Analyze computation either hasn't completed its analysis, that one or more dead neighbor nodes have
			// been encountered in the current list of potential neighbors, or that all new neighbor nodes have been found and
			// this computation just has to reply to additional ping-requests from neighbors of the dead subject node
			
			M42Input input  = (M42Input) this.getComputationTransitionMessage().getTargetComputationInput();
			
			//Retrieve the sublist of the list of current neighbor nodes, signifying those that were deemed - by the previous
			// computation A42 - to have failed
			deadNeighborNodes = input.getDeadNeighborNodes();
			//Also determine whether all new neighbor nodes have been found (true if an echo-reply has been received for all
			// neighbor nodes currently under consideration)
			this.allReplacementNeighborNodesFound = input.newNeighborNodesFound();
		}	
		
		//Update the current list of pingEchoHandlers
		this.updatePingEchoHandlerList(deadNeighborNodes);
		
		//Execute all current pingEchoHandlers
		for(PingEchoProtocolHandler handler : this.pingEchoHandlers.values())
			handler.execute();
		
		//Handle ping-requests from other neighbor nodes of the scenario subject (which are interested in the status
		// of this local node, as both are now meant to become direct neighbors of each other)
		this.handlePingRequests();
		
		//If computation A42 has previously decided on the new neighbor nodes meant to replace the dead subject node, 
		// do not continue until each of these new neighbor nodes has contacted the local node (so that all these other
		// nodes are aware of the fact that this local node is a new neighbor of them as well).
		//Do transition to the next computation however if this local node is currently being reintroduced into the 
		// traffic monitoring system, as in that case all neighbor nodes do not need to make contact (they can determine
		// their new alive neighbor node list without inter-loop coordination)
		if(this.allReplacementNeighborNodesFound){
			if(this.pingHandler.getSourceContacts().containsAll(this.pingEchoHandlers.keySet()) ||
					(this.getSelfAdaptationModels().getCurrentTrafficRole() == LocalTrafficSystemRoleType.NO_ROLE)){				
				//Move on to computation A42 (not to return)
				// Provide PingEchoInformation for each PingEchoProtocolHandler
				this.transitionToComputationA42();
			}
			//Else: Stay in this computation, at least for the next execution cycle
		}
		else{
			//Move on to next computation: A42 (possibly to return)
			// Provide PingEchoInformation for each PingEchoProtocolHandler
			this.transitionToComputationA42();
		}
	}
	
	private boolean allReplacementNeighborNodesFound = false;
	
	/*
	 * Sends a computation transition message in order to move on to computation A42.
	 * Provides PingEchoInformation for each current PingEchoProtocolHandler
	 */
	private void transitionToComputationA42(){
		ArrayList<PingEchoInformation> pingEchoInfoList = new ArrayList<PingEchoInformation>();
		for(PingEchoProtocolHandler handler : this.pingEchoHandlers.values())
			pingEchoInfoList.add(handler.getPingEchoInformation());

		this.transition("A42", pingEchoInfoList);
	}
	
	/*
	 * Update the list of PingEchoProtocolHandler-objects, used to ping the list of potential neighbor nodes meant
	 * to replace the dead subject node
	 * The updated list is equal to the current list, minus the ones in the given list of dead neighbors, plus
	 * all neighbors of each given dead neighbor node.
	 */
	private void updatePingEchoHandlerList(List<NodeID> deadNeighbors){
		//Add new handler-objects for the physical neighbors of each dead neighbor, avoiding to reconsider nodes that have
		// already been declared dead in previous execution cycles (if any), or (obviously) the local node
		NodeID localNode = this.getSelfAdaptationModels().getHostNode();
		for(NodeID deadNode : deadNeighbors){
			for(NodeID neighborOfDeadNode : this.getSelfAdaptationModels().getPhysicalNeighborsFor(deadNode)){
				if(!neighborOfDeadNode.equals(localNode) 
						&& !this.getSelfAdaptationModels().getPreviouslyFailedNeighborNodes().contains(neighborOfDeadNode)
						&& !this.pingEchoHandlers.containsKey(neighborOfDeadNode)){
					//Create new PingEchoProtocolHandler-object
					PingEchoProtocolHandler handler = 
						new PingEchoProtocolHandler(this, neighborOfDeadNode, this.minimumEchoTimestampExecutionCycleAge);
					
					this.pingEchoHandlers.put(neighborOfDeadNode, handler);					
				}				
			}
			
			//Remove its associated handler
			this.pingEchoHandlers.remove(deadNode);
		}
	}
	
	private HashMap<NodeID, PingEchoProtocolHandler> pingEchoHandlers = new HashMap<NodeID, PingEchoProtocolHandler>();
	//Take an excessively long timestampAge, so nodes that have replied with an echo-message are not bothered again
	private int minimumEchoTimestampExecutionCycleAge = 5000;
	
	
	/*
	 * The computation logic for specific to the case when a node, which previously was a neighbor node of
	 * this local camera needs to be reintroduced into the traffic monitoring system, sends a message to this
	 * SubjectNeighborNode
	 */
	private void handleNeighborCameraIntroductionSituation(){
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
		
		//In this case, as this computation is part of the adaptation-phases of the scenario, but no additional
		// actions are needed with respect to the alive neighbor node list: transition to the scenario end computation
		this.transition("End4");
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
	 * As the subject node of this particular Self-Healing Scenario:
	 * - Nothing needs to be done (technically shouldn't occur: at this point in the scenario, the subject
	 * node is supposedly dead)
	 * 
	 * @throws	ComputationExecutionException
	 * 			Always; this method should never be used.		
	 */
	public void executeOnSubjectNode() throws ComputationExecutionException {
		throw new ComputationExecutionException("M42 Computation on Subject Node");
	}

}
