package node.selfAdaptationSystem.mapeManager.cameraIntroduction.caseA;

import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.ExecuteComputation;
import node.selfAdaptationSystem.mapeManager.cameraIntroduction.CameraIntroductionComputation;
import node.selfAdaptationSystem.mapeManager.cameraIntroduction.CameraIntroductionScenario;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class E5A2 extends ExecuteComputation implements CameraIntroductionComputation {
	
	public E5A2(CameraIntroductionScenario scenario, SelfAdaptationModels models, BaseLevelConnector baseLevel){
		super(scenario, models, baseLevel);
	}
	
	
	/**************************	 
	 * 
	 *	Execution
	 *
	 **************************/
	
	@Override
	/**
	 * As a camera node that has recently come back online after having failed earlier:
	 * - Signal the local base-level to start normal operations
	 */
	public void executeOnAliveSubjectNode(){
		//Implementation-specific: nothing needs to be done	
		
		//Move on to next computation: End5A
		this.transition("End5A");
	}
	
	@Override
	/**
	 * As a master of an organization neighboring on the scenario subject camera:
	 * - Unpause base-level dynamics (i.e. splitting & merging)
	 */
	public void executeOnNeighborMasterNode() throws ComputationExecutionException{
		this.getBaseLevelConnector().unblockMerging();
		this.getBaseLevelConnector().unblockSplitting();
		
		//Move on to next computation: End5A
		this.transition("End5A");
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
		throw new ComputationExecutionException("E5A2 Computation on New Subject Master Node");
	}
}
