package node.selfAdaptationSystem.mapeManager.cameraIntroduction.caseB;

import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.ControlLoopEnd;
import node.selfAdaptationSystem.mapeManager.cameraIntroduction.CameraIntroductionComputation;
import node.selfAdaptationSystem.mapeManager.cameraIntroduction.CameraIntroductionScenario;

public class End5B extends ControlLoopEnd implements CameraIntroductionComputation {
	
	public End5B(CameraIntroductionScenario scenario){
		super(scenario);
	}
	
	
	/**************************	 
	 * 
	 *	Execution
	 *
	 **************************/
	
	@Override
	public void executeOnAliveSubjectNode() throws ComputationExecutionException {
		//Should never be used
		throw new ComputationExecutionException("The CameraIntroduction scenario loop has ended");
	}
	
	@Override
	public void executeOnNeighborMasterNode() throws ComputationExecutionException {
		//Should never be used
		throw new ComputationExecutionException("The CameraIntroduction scenario loop has ended");
	}
	
	@Override
	public void executeOnNewSubjectMasterNode() throws ComputationExecutionException {
		//Should never be used
		throw new ComputationExecutionException("The CameraIntroduction scenario loop has ended");
	}

}
