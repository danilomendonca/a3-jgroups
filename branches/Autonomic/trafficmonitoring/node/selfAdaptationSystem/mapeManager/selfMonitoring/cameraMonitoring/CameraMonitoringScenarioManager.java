package node.selfAdaptationSystem.mapeManager.selfMonitoring.cameraMonitoring;

import java.util.ArrayList;
import java.util.List;

import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.coordination.SelfAdaptationMessage;
import node.selfAdaptationSystem.mapeManager.ScenarioIdentifier;
import node.selfAdaptationSystem.mapeManager.ScenarioManager;
import node.selfAdaptationSystem.selfAdaptationController.SelfAdaptationController.LocalTrafficSystemRoleType;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class CameraMonitoringScenarioManager extends ScenarioManager<CameraMonitoringScenario> {
	
	/**************************	 
	 * 
	 * Scenario Instantiation
	 *
	 **************************/
	
	@Override
	public CameraMonitoringScenario instantiateScenario(
			ScenarioIdentifier scenario, SelfAdaptationModels models, BaseLevelConnector baseLevel){
		
		return new CameraMonitoringScenario(scenario.getScenarioSubject(), models, baseLevel);
	}
	
	
	/**************************	 
	 * 
	 *	Anticipated Scenarios
	 *
	 **************************/
	
	@Override
	/**
	 * Provide a list of scenarios, characterized by ScenarioIdentifier-objects,
	 * of CameraMonitoring scenarios that currently need to be deployed, based on the given
	 * role of the base-level system and the up-to-date information contained within the
	 * self-adaptations models:
	 * 
	 * For all roles of the local traffic monitoring system 
	 * (SINGLE_MASTER, MASTER_WITH_SLAVES, SLAVE, ROLE_TRANSITION, NO_ROLE): 
	 * a CameraMonitoringScenario for itself
	 */
	public List<ScenarioIdentifier> determineAnticipatedScenarios(
													LocalTrafficSystemRoleType currentRole,
													SelfAdaptationModels models){
		ArrayList<ScenarioIdentifier> result = new ArrayList<ScenarioIdentifier>();
		
		result.add(new ScenarioIdentifier(models.getHostNode(), CameraMonitoringScenario.scenarioType));
		
		return result;
	}
	
	
	/**************************	 
	 * 
	 * Unanticipated Scenarios
	 *
	 **************************/
	
	@Override
	/**
	 * If this given self-adaptation message was sent in the context of a CameraMonitoringScenario-instance, specifically
	 * computation M92, and the local camera is an active part of the traffic monitoring system: return true.
	 * False otherwise.
	 */
	public boolean canAcceptUnanticipatedMessage(SelfAdaptationMessage unanticipatedMessage, SelfAdaptationModels models){
		if(models.getCurrentTrafficRole() == LocalTrafficSystemRoleType.NO_ROLE)
			return false;
		
		return( unanticipatedMessage.getScenario().getScenarioType().equals(CameraMonitoringScenario.scenarioType) &&
				unanticipatedMessage.getTargetMapeComputationID().equals("M92") );
	}
}
