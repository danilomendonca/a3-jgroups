package node.selfAdaptationSystem.mapeManager.selfMonitoring.cameraMonitoring;

import java.util.ArrayList;
import java.util.HashMap;

import utilities.NodeID;

import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.coordination.protocols.requestReply.RequestReplyProtocolHandler;
import node.selfAdaptationSystem.mapeManager.MonitorComputation;
import node.selfAdaptationSystem.selfAdaptationModels.OrganizationSnapshot;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class M92 extends MonitorComputation implements CameraMonitoringComputation {

	public M92(CameraMonitoringScenario scenario, SelfAdaptationModels models, 
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
	 * As the subject node of this particular Self-Monitoring Scenario:
	 * - request information from alive neighbors of this local subject camera so that
	 * the local self-adaptation models can be kept up-to-date
	 */
	public void executeOnSubjectNode(){
		//Verify that for each of the current alive neighbor nodes a separate protocol handler has been created
		ArrayList<NodeID> neighborNodes = this.getSelfAdaptationModels().getAliveNeighborNodes();
		for(NodeID neighborNode : neighborNodes){
			if(!this.requestProtocolHandlers.containsKey(neighborNode)){
				this.requestProtocolHandlers.put(neighborNode, new RequestReplyProtocolHandler<OrganizationSnapshot>(this, neighborNode));
			}
		}
		
		//Remove protocol handlers for nodes that are no longer neighbors of this subject camera
		ArrayList<NodeID> protocolHandlerKeyset = new ArrayList<NodeID>();
		protocolHandlerKeyset.addAll(this.requestProtocolHandlers.keySet());
		for(NodeID protocolHandlerKey : protocolHandlerKeyset){
			if(!neighborNodes.contains(protocolHandlerKey)){
				this.requestProtocolHandlers.remove(protocolHandlerKey);
			}
		}
		
		//Remove neighbor organization information in the self-adaptation models (if any),
		// linked with every encountered failed neighbor node
		for(NodeID failedNeighbor : this.getSelfAdaptationModels().getPreviouslyFailedNeighborNodes())
			this.getSelfAdaptationModels().removeNeighborNodeOrganizationInformation(failedNeighbor);
		
		//Execute each current protocol handler
		for(RequestReplyProtocolHandler<OrganizationSnapshot> protocolHandler : this.requestProtocolHandlers.values()){
			protocolHandler.execute();
		}
		
		//Up-date the self-adaptation models with the payload-data, if any, received from the current alive neighbor nodes,
		// but only if the neighbor node belongs to a different organization than
		for(NodeID neighborNode : this.requestProtocolHandlers.keySet()){
			RequestReplyProtocolHandler<OrganizationSnapshot> protocolHandler = this.requestProtocolHandlers.get(neighborNode);			
			
			if(protocolHandler.hasCompleted()){
				OrganizationSnapshot replyPayload = protocolHandler.getReplyPayload();
				
				//Extra case: if the local camera is being introduced in a working traffic system and therefore does not have
				// a local organization yet, add neighbor organization information anyway
				if( (this.getSelfAdaptationModels().getLocalOrganization() == null) ||
						(replyPayload.getOrganizationId() != this.getSelfAdaptationModels().getLocalOrganization().getOrganizationId()) ){
					this.getSelfAdaptationModels().addNeighborNodeOrganizationInformation(neighborNode, replyPayload);
				}
				else{
					//If the local node and the currently considered neighbor node belong to the same organization: make sure
					// that the self-adaptation models don't contain old organization information
					this.getSelfAdaptationModels().removeNeighborNodeOrganizationInformation(neighborNode);
				}
			}
		}
		
		if(!this.getSelfAdaptationModels().
				executionCycleTimestampIsYoungerThan(this.lastRequestExecutionCycleTimestamp, this.executionCyclesBetweenRequests)){			
			//Check if enough execution cycles have passed to send a new round of requests for updated neighbor information
			for(RequestReplyProtocolHandler<OrganizationSnapshot> protocolHandler : this.requestProtocolHandlers.values()){
				//Reset each the protocol handler, preparing it for a fresh execution in the next cycle
				protocolHandler.reset();
			}
			
			this.lastRequestExecutionCycleTimestamp = this.getSelfAdaptationModels().getCurrentExecutionCycle();
		}
		
		
		
		//Note: no transition needed since up-to-date information is needed during each execution cycle
	}
	
	//Contains the dedicated protocol handles for all alive neighbor cameras for the scenario subject
	private HashMap<NodeID, RequestReplyProtocolHandler<OrganizationSnapshot>> requestProtocolHandlers = 
		new HashMap<NodeID, RequestReplyProtocolHandler<OrganizationSnapshot>>();
	private int lastRequestExecutionCycleTimestamp = Integer.MIN_VALUE;
	private int executionCyclesBetweenRequests = 50;
	
	
	@Override
	/**
	 * As a neighbor of the subject of this particular Self-Monitoring Scenario:
	 * - respond to information requests 
	 */
	public void executeOnSubjectNeighborNode(){
		//Make sure the payload information is up-to-date
		//If possible, i.e. this camera is an active part of the traffic monitoring system and is not being introduced:
		// reply with the id and master of the local organization
		OrganizationSnapshot currentOrg = this.getSelfAdaptationModels().getLocalOrganization();
		if(currentOrg != null){
			this.replierProtocolHandler.setReplyPayload(
					new OrganizationSnapshot(currentOrg.getOrganizationId(), currentOrg.getMasterNode(), null));
		}
		
		this.replierProtocolHandler.execute();
		
		//Note: no transition needed since up-to-date information is needed during each execution cycle
	}
	
	//The protocol  handler for the subject neighbor
	RequestReplyProtocolHandler<OrganizationSnapshot> replierProtocolHandler = 
		new RequestReplyProtocolHandler<OrganizationSnapshot>(this);
}
