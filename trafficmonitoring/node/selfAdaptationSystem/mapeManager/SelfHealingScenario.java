package node.selfAdaptationSystem.mapeManager;

import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;
import utilities.NodeID;

public abstract class SelfHealingScenario<R> extends SelfAdaptationScenario<R> {
	
	public SelfHealingScenario(NodeID scenarioSubject, BaseLevelConnector baseLevel, SelfAdaptationModels models){
		super(scenarioSubject, baseLevel, models);
	}

}
