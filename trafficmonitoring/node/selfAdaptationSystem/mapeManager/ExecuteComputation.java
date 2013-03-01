package node.selfAdaptationSystem.mapeManager;

import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public abstract class ExecuteComputation extends MapeComputation {
										
	public ExecuteComputation(SelfAdaptationScenario<?> scenario, SelfAdaptationModels models, 
								BaseLevelConnector baseLevel) {
		super(scenario, models);
		
		this.baseLevel = baseLevel;
	}
	
	
	/**************************	 
	 * 
	 *	Base-Level Connector
	 *
	 **************************/
	
	public BaseLevelConnector getBaseLevelConnector(){
		return this.baseLevel;
	}
	
	private BaseLevelConnector baseLevel;

}
