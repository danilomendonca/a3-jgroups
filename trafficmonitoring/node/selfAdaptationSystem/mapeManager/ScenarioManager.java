package node.selfAdaptationSystem.mapeManager;

import java.util.List;

import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.coordination.SelfAdaptationMessage;
import node.selfAdaptationSystem.selfAdaptationController.SelfAdaptationController.LocalTrafficSystemRoleType;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public abstract class ScenarioManager<S extends SelfAdaptationScenario<?>> {
	
	/**************************	 
	 * 
	 * Scenario Instantiation
	 *
	 **************************/
	
	/**
	 * Instantiate a new scenario that will begin executing with the very first computation of its control loop
	 */
	public abstract S instantiateScenario(ScenarioIdentifier scenario, SelfAdaptationModels models, BaseLevelConnector baseLevel);
	
	/**
	 * Instantiate a new scenario that will begin executing with the computation of its control loop with the given id
	 */
	public S instantiateScenario(ScenarioIdentifier scenario, String mapeComputationID,
			SelfAdaptationModels models, BaseLevelConnector baseLevel){		
		S result = this.instantiateScenario(scenario, models, baseLevel);
		
		//Set the computation with the given id as the scenario's active computation
		result.setOnlyActiveComputation(mapeComputationID);
		
		return result;
	}
	
	/**
	 * Returns true if this scenario manager creates scenario instances of the given type. False otherwise.
	 */
	public <T extends SelfAdaptationScenario<?>> boolean instantiatesScenariosOfType(Class<T> scenarioType){		
		//Implementation: create a new dummy scenario and verify its type
		
		ScenarioIdentifier dummyIdentifier = new ScenarioIdentifier(null, null);
		S dummyScenario = this.instantiateScenario(dummyIdentifier, null, null);
		
		return (scenarioType.isInstance(dummyScenario));
	}

	
	/**************************	 
	 * 
	 * Anticipated Scenarios
	 *
	 **************************/
	
	public abstract List<ScenarioIdentifier> determineAnticipatedScenarios(
			LocalTrafficSystemRoleType currentRole,	SelfAdaptationModels models);
	
	
	/**************************	 
	 * 
	 * Unanticipated Scenarios
	 *
	 **************************/
	
	public abstract boolean canAcceptUnanticipatedMessage(SelfAdaptationMessage unanticipatedMessage, SelfAdaptationModels models);

}
