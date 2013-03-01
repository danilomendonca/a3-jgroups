package node.selfAdaptationSystem.mapeManager.cameraIntroduction.caseA;

import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.PlanComputation;
import node.selfAdaptationSystem.mapeManager.cameraIntroduction.CameraIntroductionComputation;
import node.selfAdaptationSystem.mapeManager.cameraIntroduction.CameraIntroductionScenario;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class P5A1 extends PlanComputation implements CameraIntroductionComputation {
	
	public P5A1(CameraIntroductionScenario scenario, SelfAdaptationModels models){
		super(scenario, models);
	}

	
	/**************************	 
	 * 
	 *	Execution
	 *
	 **************************/
	
	@Override
	/**
	 * As a camera node that has recently come back online after having failed earlier:
	 * - Plan to start a new single member organization on this local camera, thereby making it
	 * a new master node.
	 */
	public void executeOnAliveSubjectNode(){
		//Implementation-specific: nothing to be done
		
		//Transition to computation E5A1
		this.transition("E5A1");
	}
	
	
	
	
	@Override
	/**
	 * As a master of an organization neighboring on the scenario subject camera:
	 *- Do nothing. Technically shouldn't occur (This role does not participate in this computation)
	 * 
	 * @throws	ComputationExecutionException
	 * 			Always; this method should never be used.
	 */
	public void executeOnNeighborMasterNode() throws ComputationExecutionException {
		throw new ComputationExecutionException("P5A1 Computation on Neighbor Master Node");
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
		throw new ComputationExecutionException("P5A1 Computation on New Subject Master Node");
	}
}
