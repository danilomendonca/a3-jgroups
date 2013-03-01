package node.selfAdaptationSystem.mapeManager.selfMonitoring.cameraMonitoring;

import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.SelfAdaptationScenarioComputation;

public interface CameraMonitoringComputation extends SelfAdaptationScenarioComputation {
	
	/**
	 * 
	 * @throws		ComputationExecutionException
	 * 				If this node is not the subject node of the self-monitoring scenario
	 */
	public void executeOnSubjectNode() throws ComputationExecutionException;

	/**
	 * 
	 * @throws		ComputationExecutionException
	 * 				If this node is not a neighbor of the subject node of the self-monitoring scenario
	 */
	public void executeOnSubjectNeighborNode() throws ComputationExecutionException;

}
