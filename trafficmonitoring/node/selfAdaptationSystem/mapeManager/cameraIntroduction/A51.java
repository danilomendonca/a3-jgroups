package node.selfAdaptationSystem.mapeManager.cameraIntroduction;

import node.selfAdaptationSystem.mapeManager.AnalyzeComputation;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class A51 extends AnalyzeComputation implements CameraIntroductionComputation {
	
	public A51(CameraIntroductionScenario scenario, SelfAdaptationModels models){
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
	 * - Analyze the received information and determine whether the local camera is fully ready to
	 * be re-introduced into the traffic monitoring system
	 */
	public void executeOnAliveSubjectNode(){
		//Implementation-specific: just verify the given transition data
		
		boolean isBackOnline = (Boolean) this.getComputationTransitionMessage().getTargetComputationInput();
		
		if(isBackOnline){
			//Move on to next computation: P5
			this.transition("P5");
		}
		else{
			//Return to the previous computation M51 to retry
			this.transition("M51");
		}
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
		throw new ComputationExecutionException("A51 Computation on Neighbor Master Node");
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
		throw new ComputationExecutionException("A51 Computation on New Subject Master Node");
	}

}
