package node.selfAdaptationSystem.mapeManager.cameraIntroduction;

import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.SelfAdaptationScenarioComputation;

public interface CameraIntroductionComputation extends SelfAdaptationScenarioComputation {
	
	/**
	 * 
	 * @throws		ComputationExecutionException
	 * 				If this node is not the node that has recently come back online and is the
	 * 				subject of this self-configuration scenario
	 */
	public void executeOnAliveSubjectNode() throws ComputationExecutionException;
	
	/**
	 * 
	 * @throws		ComputationExecutionException
	 * 				If this node is not the master node of an organization that is adjacent to the
	 * 				subject node. In other words: one of the master node's slaves is a direct alive
	 * 				neighbor node of the subject node.
	 */
	public void executeOnNeighborMasterNode() throws ComputationExecutionException;
	
	/**
	 * 
	 * @throws		ComputationExecutionException
	 * 				If this node is not (to become) the new master to the scenario's subject.
	 */
	public void executeOnNewSubjectMasterNode() throws ComputationExecutionException;

}
