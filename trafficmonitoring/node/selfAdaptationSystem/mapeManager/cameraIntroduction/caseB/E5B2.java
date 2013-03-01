package node.selfAdaptationSystem.mapeManager.cameraIntroduction.caseB;

import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.ExecuteComputation;
import node.selfAdaptationSystem.mapeManager.cameraIntroduction.CameraIntroductionComputation;
import node.selfAdaptationSystem.mapeManager.cameraIntroduction.CameraIntroductionScenario;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class E5B2 extends ExecuteComputation implements CameraIntroductionComputation {
	
	public E5B2(CameraIntroductionScenario scenario, SelfAdaptationModels models, BaseLevelConnector baseLevel){
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
	 * - Adapt the base-level so all operations can be started
	 */
	public void executeOnAliveSubjectNode(){
		//In order to accommodate for the case C situation (see computation A52):
		// wait a couple of execution cycles so that this slave's master has definitely
		// completed this E5A2 computation and then trigger the sending of the current
		// traffic situation to this master. If case C is relevant and this local subject camera
		// does NOT sense traffic congestion, the organization will thereby be split through regular
		// base-level operations.		
		if(this.waitExecutionCycles-- > 0){
			return;
		}		
		this.getBaseLevelConnector().forceTrafficInfoEvent();
		
		
		
		//Move on to next computation: End5B
		this.transition("End5B");
	}
	
	private int waitExecutionCycles = 10;
	
	@Override
	/**
	 * As the new master of the scenario subject:
	 * - Adapt the base-level so all operations can be resumed
	 */
	public void executeOnNewSubjectMasterNode() throws ComputationExecutionException{
		this.getBaseLevelConnector().unblockMerging();
		this.getBaseLevelConnector().unblockSplitting();		
		
		//Move on to next computation: End5B
		this.transition("End5B");		
	}
	
	@Override
	/**
	 * As a master of an organization neighboring on the scenario subject camera, but not to be become the
	 * scenario subject's new master node:
	 * - Adapt the base-level so all operations can be resumed
	 */
	public void executeOnNeighborMasterNode() throws ComputationExecutionException{
		this.getBaseLevelConnector().unblockMerging();
		this.getBaseLevelConnector().unblockSplitting();
		
		//Move on to next computation: End5B
		this.transition("End5B");
	}

}
