package node.selfAdaptationSystem.mapeManager.selfMonitoring;

import utilities.NodeID;
import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.mapeManager.SelfAdaptationScenario;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public abstract class SelfMonitoringScenario<R> extends SelfAdaptationScenario<R> {

	public SelfMonitoringScenario(NodeID scenarioSubject, BaseLevelConnector baseLevel, SelfAdaptationModels models){
		super(scenarioSubject, baseLevel, models);
	}
	
}
