package node.selfAdaptationSystem.mapeManager.slaveNodeFailureScenario;

import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.ExecuteComputation;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class E31 extends ExecuteComputation implements SlaveNodeFailureComputation {

	public E31(SlaveNodeFailureScenario scenario, SelfAdaptationModels models,
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
	 * - Pauze base-level operations temporarily
	 */
	public void executeOnMasterNode() throws ComputationExecutionException {
		this.getBaseLevelConnector().blockMerging();
		this.getBaseLevelConnector().blockSplitting();
		
		//Move on to the next computation: P32
		this.transition("P32");
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
		throw new ComputationExecutionException("E31 Computation on Subject Slave Node");
	}

}
