package node.selfAdaptationSystem.mapeManager.cameraIntroduction.caseA;

import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.ControlLoopEnd;
import node.selfAdaptationSystem.mapeManager.cameraIntroduction.CameraIntroductionComputation;
import node.selfAdaptationSystem.mapeManager.cameraIntroduction.CameraIntroductionScenario;

public class End5A extends ControlLoopEnd implements CameraIntroductionComputation {
	
	public End5A(CameraIntroductionScenario scenario){
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
	/**
	 * As the new master of the scenario subject:
	 *- Do nothing. Technically shouldn't occur (This role does not participate in this computation)
	 * 
	 * @throws	ComputationExecutionException
	 * 			Always; this method should never be used.
	 */
	public void executeOnNewSubjectMasterNode() throws ComputationExecutionException {
		throw new ComputationExecutionException("End5A Computation on New Subject Master Node");
	}

}
