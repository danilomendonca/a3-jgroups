package node.selfAdaptationSystem.mapeManager.masterWithSlavesNodeFailureScenario;

import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.SelfAdaptationScenarioComputation;

public interface MasterWithSlavesNodeFailureComputation extends SelfAdaptationScenarioComputation {

	/**
	 * 
	 * @throws		ComputationExecutionException
	 * 				If this node is not the master subject node of the self-healing scenario
	 */
	public void executeOnSubjectMasterNode() throws ComputationExecutionException;
	
	/**
	 * 
	 * @throws		ComputationExecutionException
	 * 				If this node is not a slave node in the organization of the subject master node
	 */
	public void executeOnSlaveNode() throws ComputationExecutionException;
	
	/**
	 * 
	 * @throws		ComputationExecutionException
	 * 				If this node is not the master node of an organization that is adjacent to the
	 * 				organization of the subject master node
	 */
	public void executeOnNeighborMasterNode() throws ComputationExecutionException;
		
	/**
	 * 
	 * @throws		ComputationExecutionException
	 * 				If this node is not the newly elected master of the organization of which the
	 * 				scenario's subject was the original master.
	 */
	public void executeOnNewMasterNode() throws ComputationExecutionException;
}
