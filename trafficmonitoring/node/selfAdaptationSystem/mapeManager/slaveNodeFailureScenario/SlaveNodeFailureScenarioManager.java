package node.selfAdaptationSystem.mapeManager.slaveNodeFailureScenario;

import java.util.ArrayList;
import java.util.List;

import utilities.NodeID;
import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.coordination.SelfAdaptationMessage;
import node.selfAdaptationSystem.mapeManager.ScenarioIdentifier;
import node.selfAdaptationSystem.mapeManager.ScenarioManager;
import node.selfAdaptationSystem.selfAdaptationController.SelfAdaptationController.LocalTrafficSystemRoleType;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class SlaveNodeFailureScenarioManager extends ScenarioManager<SlaveNodeFailureScenario> {

	/**************************	 
	 * 
	 * Scenario Instantiation
	 *
	 **************************/
	
	@Override
	public SlaveNodeFailureScenario instantiateScenario(
			ScenarioIdentifier scenario, SelfAdaptationModels models, BaseLevelConnector baseLevel){
		
		return new SlaveNodeFailureScenario(scenario.getScenarioSubject(), models, baseLevel);
	}
	
	
	/**************************	 
	 * 
	 *	Anticipated Scenarios
	 *
	 **************************/
	
	@Override
	/**
	 * Provide a list of scenarios, characterized by ScenarioIdentifier-objects,
	 * of SlaveNodeFailure scenarios that currently need to be deployed, based on the given
	 * role of the base-level system and the up-to-date information contained within the
	 * self-healing models:
	 * 
	 * If the local camera is currently a slave in an organization:
	 * - a SlaveNodeFailure scenario for itself
	 * 
	 * If the local camera is currently a master of an organization:
	 * - a SlaveNodeFailure scenario for each of its slaves
	 * 
	 * If the local camera is currently transitioning between traffic system roles:
	 * - None
	 * 
	 * If the local camera is currently not integrated in the traffic monitoring system and is 
	 * therefore not occupying any role:
	 * - None
	 */
	public List<ScenarioIdentifier> determineAnticipatedScenarios(
													LocalTrafficSystemRoleType currentRole,
													SelfAdaptationModels models){
		ArrayList<ScenarioIdentifier> result = new ArrayList<ScenarioIdentifier>();
		
		if( (currentRole == LocalTrafficSystemRoleType.ROLE_TRANSITION) ||
				(currentRole == LocalTrafficSystemRoleType.NO_ROLE) )
			return result;
		
		if(currentRole == LocalTrafficSystemRoleType.SLAVE){
			//Own SlaveNodeFailure
			ScenarioIdentifier ownSlaveScenario =
				new ScenarioIdentifier(models.getHostNode(), SlaveNodeFailureScenario.scenarioType);
			result.add(ownSlaveScenario);
		}
		else{
			//Here: 	currentRole == LocalTrafficSystemRoleType.SINGLE_MASTER
			//							or 	.MASTER_WITH_SLAVES
			
			if(currentRole == LocalTrafficSystemRoleType.MASTER_WITH_SLAVES){
				//SlaveNodeFailure for each slave
				for(NodeID node : models.getLocalOrganization().getNodes()){
					if(!node.equals(models.getHostNode())){
						ScenarioIdentifier slaveScenario =
							new ScenarioIdentifier(node, SlaveNodeFailureScenario.scenarioType);
						result.add(slaveScenario);
					}
				}
			}
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
	 * No unanticipated scenarios of this type; always returns false;
	 */
	public boolean canAcceptUnanticipatedMessage(SelfAdaptationMessage unanticipatedMessage, SelfAdaptationModels models){
		return false;
	}
}
