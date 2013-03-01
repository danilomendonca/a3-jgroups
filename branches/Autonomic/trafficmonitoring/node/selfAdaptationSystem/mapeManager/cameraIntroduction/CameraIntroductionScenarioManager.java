package node.selfAdaptationSystem.mapeManager.cameraIntroduction;

import java.util.ArrayList;
import java.util.List;

import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.coordination.SelfAdaptationMessage;
import node.selfAdaptationSystem.mapeManager.ScenarioIdentifier;
import node.selfAdaptationSystem.mapeManager.ScenarioManager;
import node.selfAdaptationSystem.selfAdaptationController.SelfAdaptationController.LocalTrafficSystemRoleType;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class CameraIntroductionScenarioManager extends ScenarioManager<CameraIntroductionScenario> {
	
	
	/**************************	 
	 * 
	 * Scenario Instantiation
	 *
	 **************************/
	
	@Override
	public CameraIntroductionScenario instantiateScenario(
			ScenarioIdentifier scenario, SelfAdaptationModels models, BaseLevelConnector baseLevel){
		
		return new CameraIntroductionScenario(scenario.getScenarioSubject(), models, baseLevel);
	}
	
	
	/**************************	 
	 * 
	 *	Anticipated Scenarios
	 *
	 **************************/
	
	@Override
	/**
	 * Provide a list of scenarios, characterized by ScenarioIdentifier-objects,
	 * of CameraIntroduction scenarios that currently need to be deployed, based on the given
	 * role of the base-level system and the up-to-date information contained within the
	 * self-healing models:
	 * 
	 * If the local camera is currently not integrated in the traffic monitoring system and is 
	 * therefore not occupying any role:
	 * - A CameraIntroductionScenario for itself
	 */
	public List<ScenarioIdentifier> determineAnticipatedScenarios(
													LocalTrafficSystemRoleType currentRole,
													SelfAdaptationModels models){
		ArrayList<ScenarioIdentifier> result = new ArrayList<ScenarioIdentifier>();
		
		if(currentRole == LocalTrafficSystemRoleType.NO_ROLE){
			//Own CameraIntroduction
			ScenarioIdentifier ownCameraIntroductionScenario =
				new ScenarioIdentifier(models.getHostNode(), CameraIntroductionScenario.scenarioType);
			result.add(ownCameraIntroductionScenario);
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
	 * If the local node is a master and the given self-adaptation message was sent within the context of a 
	 * SlaveNodeFailureScenario, specifically computation P5: return true.
	 * Return false otherwise.
	 */
	public boolean canAcceptUnanticipatedMessage(SelfAdaptationMessage unanticipatedMessage, SelfAdaptationModels models){
		if(!models.getLocalOrganization().getMasterNode().equals(models.getHostNode()))
			return false;
		
		if(!unanticipatedMessage.getScenario().getScenarioType().equals(CameraIntroductionScenario.scenarioType))
			return false;
		if(!unanticipatedMessage.getTargetMapeComputationID().equals("P5"))
			return false;
		
		return true;
	}

}
