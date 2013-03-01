package node.selfAdaptationSystem.mapeManager.slaveNodeFailureScenario;

import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.PlanComputation;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class P31 extends PlanComputation implements SlaveNodeFailureComputation {
	
	public P31(SlaveNodeFailureScenario scenario, SelfAdaptationModels models){
		super(scenario, models);
	}
			
	
	/**************************	 
	 * 
	 *	Execution
	 *
	 **************************/		

	@Override
	/**
	 * As a master to this scenario's subject slave node:
	 * - Plan to pause base-level operations (merging & splitting)
	 */
	public void executeOnMasterNode() {
		//Implementation-specific: nothing needs to be done
		
		//Move on to next computation: E31
		this.transition("E31");
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
		throw new ComputationExecutionException("P31 Computation on Subject Slave Node");
	}
	
}