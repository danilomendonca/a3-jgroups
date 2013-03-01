package node.selfAdaptationSystem.mapeManager.slaveNodeFailureScenario;

import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.SelfAdaptationScenarioComputation;

public interface SlaveNodeFailureComputation extends SelfAdaptationScenarioComputation {
	
	/**
	 * 
	 * @throws		ComputationExecutionException
	 * 				If this node is not the subject slave node of the self-healing scenario
	 */
	public void executeOnSubjectSlaveNode() throws ComputationExecutionException;

	/**
	 * 
	 * @throws		ComputationExecutionException
	 * 				If this node is not the master node to which the subject of the self-healing scenario answers
	 */
	public void executeOnMasterNode() throws ComputationExecutionException;

}
