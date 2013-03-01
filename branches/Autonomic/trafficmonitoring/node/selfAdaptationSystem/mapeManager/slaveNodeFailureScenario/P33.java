package node.selfAdaptationSystem.mapeManager.slaveNodeFailureScenario;

import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.PlanComputation;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class P33 extends PlanComputation implements SlaveNodeFailureComputation {
	
	public P33(SlaveNodeFailureScenario scenario, SelfAdaptationModels models){
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
	 * - Prepare to unpauze base-level operations (merging & splitting) after healing
	 */
	public void executeOnMasterNode() {
		//Implementation-specific: nothing needs to be done
		
		//Move on to next computation: E33
		this.transition("E33");
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
		throw new ComputationExecutionException("P33 Computation on Subject Slave Node");
	}
	
}