package node.selfAdaptationSystem.mapeManager.neighborNodeFailure;

import java.util.ArrayList;
import java.util.List;

import utilities.NodeID;
import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.coordination.SelfAdaptationMessage;
import node.selfAdaptationSystem.mapeManager.ScenarioIdentifier;
import node.selfAdaptationSystem.mapeManager.ScenarioManager;
import node.selfAdaptationSystem.selfAdaptationController.SelfAdaptationController.LocalTrafficSystemRoleType;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class NeighborNodeFailureScenarioManager extends ScenarioManager<NeighborNodeFailureScenario> {

	/**************************	 
	 * 
	 * Scenario Instantiation
	 *
	 **************************/
	
	@Override
	public NeighborNodeFailureScenario instantiateScenario(
			ScenarioIdentifier scenario, SelfAdaptationModels models, BaseLevelConnector baseLevel){
		
		return new NeighborNodeFailureScenario(scenario.getScenarioSubject(), models, baseLevel);
	}
	
	
	/**************************	 
	 * 
	 *	Anticipated Scenarios
	 *
	 **************************/
	
	@Override
	/**
	 * Provide a list of scenarios, characterized by ScenarioIdentifier-objects,
	 * of NeighborNodeFailure scenarios that currently need to be deployed, based on the given
	 * role of the base-level system and the up-to-date information contained within the
	 * self-healing models:
	 * 
	 * - a NeighborNodeFailure for itself
	 * - a NeighborNodeFailure for all its alive neighbor nodes
	 * 
	 * Note: these scenarios are necessary regardless of the local traffic system role
	 */
	public List<ScenarioIdentifier> determineAnticipatedScenarios(
													LocalTrafficSystemRoleType currentRole,
													SelfAdaptationModels models){
		ArrayList<ScenarioIdentifier> result = new ArrayList<ScenarioIdentifier>();
		
		//Own NeighborNodeFailure
		ScenarioIdentifier ownNeighborScenario =
			new ScenarioIdentifier(models.getHostNode(), NeighborNodeFailureScenario.scenarioType);
		result.add(ownNeighborScenario);	
		
		//NeighborNodeFailure for all neighbor nodes		
		for(NodeID neighbor : models.getAliveNeighborNodes()){
			ScenarioIdentifier neighborScenario =
				new ScenarioIdentifier(neighbor, NeighborNodeFailureScenario.scenarioType);
			result.add(neighborScenario);
		}
		
		return result;
	}
	
	
	/**************************	 
	 * 
	 * Unanticipated Scenarios
	 *
	 **************************/
	
	@Override
	/**
	 * If the given unanticipated message was sent in the context of a NeighborNodeFailureScenaio, specifically
	 * computation M42, and the message was sent by a camera that is registered in the given self-adaptation
	 * models as having failed previously: return true. False otherwise.
	 */
	public boolean canAcceptUnanticipatedMessage(SelfAdaptationMessage unanticipatedMessage, SelfAdaptationModels models){
		if(!unanticipatedMessage.getScenario().getScenarioType().equals(NeighborNodeFailureScenario.scenarioType))
			return false;
		if(!unanticipatedMessage.getTargetMapeComputationID().equals("M42"))
			return false;		
		if(!models.getPreviouslyFailedNeighborNodes().contains(unanticipatedMessage.getSenderNode()))
			return false;
		
		return true;
	}
	
}
