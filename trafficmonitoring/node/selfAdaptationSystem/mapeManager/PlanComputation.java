package node.selfAdaptationSystem.mapeManager;

import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public abstract class PlanComputation extends MapeComputation {

	public PlanComputation(SelfAdaptationScenario<?> scenario, SelfAdaptationModels models) {
		super(scenario, models);
	}
}
