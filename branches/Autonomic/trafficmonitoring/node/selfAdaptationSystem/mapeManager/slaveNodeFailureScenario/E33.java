package node.selfAdaptationSystem.mapeManager.slaveNodeFailureScenario;

import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.ExecuteComputation;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class E33 extends ExecuteComputation implements SlaveNodeFailureComputation {

	public E33(SlaveNodeFailureScenario scenario, SelfAdaptationModels models,
				BaseLevelConnector baseLevel) {
		super(scenario, models, baseLevel);
	}
		
	
	/**************************	 
	 * 
	 *	Execution
	 *
	 **************************/		

	@Override
	/**
	 * As a master to this scenario's subject slave node:
	 * - Unpauze base-level operations
	 */
	public void executeOnMasterNode() throws ComputationExecutionException {
		this.getBaseLevelConnector().unblockMerging();
		this.getBaseLevelConnector().unblockSplitting();
		
		//Move on to the next computation: End3
		this.transition("End3");
	}	
	
	
	
	@Override
	/**
	 * As a slave node and subject of this particular Self-Healing Scenario:
	 * - Do nothing. Technically shouldn't occur (at this point in the scenario, 
	 * the subject slave node has failed)
	 * 
	 * @throws	ComputationExecutionException
	 * 			Always; this method should never be used.
	 */
	public void executeOnSubjectSlaveNode() throws ComputationExecutionException {
		throw new ComputationExecutionException("E33 Computation on Subject Slave Node");
	}

}
