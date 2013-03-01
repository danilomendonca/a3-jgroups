package node.selfAdaptationSystem.mapeManager.masterWithSlavesNodeFailureScenario;

import java.util.ArrayList;
import java.util.List;

import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.coordination.SelfAdaptationMessage;
import node.selfAdaptationSystem.mapeManager.ScenarioIdentifier;
import node.selfAdaptationSystem.mapeManager.ScenarioManager;
import node.selfAdaptationSystem.selfAdaptationController.SelfAdaptationController.LocalTrafficSystemRoleType;
import node.selfAdaptationSystem.selfAdaptationModels.OrganizationSnapshot;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class MasterWithSlavesNodeFailureScenarioManager 
											extends	ScenarioManager<MasterWithSlavesNodeFailureScenario> {
	
	/**************************	 
	 * 
	 * Scenario Instantiation
	 *
	 **************************/
	
	@Override
	public MasterWithSlavesNodeFailureScenario instantiateScenario(
			ScenarioIdentifier scenario, SelfAdaptationModels models, BaseLevelConnector baseLevel){
		
		return new MasterWithSlavesNodeFailureScenario(scenario.getScenarioSubject(), models, baseLevel);
	}
	
	
	/**************************	 
	 * 
	 *	Anticipated Scenarios
	 *
	 **************************/
	
	@Override
	/**
	 * Provide a list of scenarios, characterized by ScenarioIdentifier-objects,
	 * of MasterWithSlavesNodeFailure scenarios that currently need to be deployed, based on the given
	 * role of the base-level system and the up-to-date information contained within the
	 * self-healing models:
	 * 
	 * If the local camera is currently a slave in an organization:
	 * - a MasterWithSlavesNodeFailure scenario for the organziation's master
	 * 
	 * If the local camera is currently a master of an organization:
	 * - a MasterWithSlavesNodeFailure scenario for itself, if its organization has slaves too
	 * - a MasterWithSlavesNodeFailure scenario for the masters of all neighboring organizations that,
	 * apart from a master, also have one or more slave nodes
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
			//MasterWithSlavesNodeFailure for this slave's master
			ScenarioIdentifier masterScenario = 
				new ScenarioIdentifier(models.getLocalOrganization().getMasterNode(), 
										MasterWithSlavesNodeFailureScenario.scenarioType);
			result.add(masterScenario);
		}
		else {
			//Here: 	currentRole == LocalTrafficSystemRoleType.SINGLE_MASTER
			//							or 	.MASTER_WITH_SLAVES
			
			if(currentRole == LocalTrafficSystemRoleType.MASTER_WITH_SLAVES){
				//Own MasterWithSlavesNodeFailure
				ScenarioIdentifier ownMasterScenario = 
					new ScenarioIdentifier(models.getHostNode(), MasterWithSlavesNodeFailureScenario.scenarioType);
				result.add(ownMasterScenario);
			}		
			
			//MasterWithSlavesNodeFailure for the masters of all neighboring organizations that have one or more slaves
			for(OrganizationSnapshot neighborOrg : models.getNeighborOrganizations()){
				if(neighborOrg.getNodes().size() > 1){				
					ScenarioIdentifier neighborMasterScenario = 
						new ScenarioIdentifier(neighborOrg.getMasterNode(), MasterWithSlavesNodeFailureScenario.scenarioType);
					result.add(neighborMasterScenario);
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
	 * For all coordination within the context of MasterWithSlaveNodeFailureScenario-instances, the
	 * messages will never be unanticipated; return false.
	 */
	public boolean canAcceptUnanticipatedMessage(SelfAdaptationMessage unanticipatedMessage, SelfAdaptationModels models){
		return false;
	}

}
