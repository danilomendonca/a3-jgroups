package node.selfAdaptationSystem.mapeManager;

import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;
import utilities.NodeID;

public abstract class SelfConfigurationScenario<R> extends SelfHealingScenario<R> {
	
	public SelfConfigurationScenario(NodeID scenarioSubject, BaseLevelConnector baseLevel, SelfAdaptationModels models){
		super(scenarioSubject, baseLevel, models);
	}

}
