package node.selfAdaptationSystem.mapeManager.cameraIntroduction;

import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.MonitorComputation;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class M51 extends MonitorComputation implements CameraIntroductionComputation {
	
	public M51(CameraIntroductionScenario scenario, SelfAdaptationModels models, BaseLevelConnector baseLevel){
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
	 * - Gather information on the local camera in order to check whether this node has fully come
	 * back online and could be re-introduced into the traffic monitoring system
	 */
	public void executeOnAliveSubjectNode(){
		//Implementation-specific: just check a local switch
		boolean isBackOnline = this.getBaseLevelConnector().isBackOnline();
		
		//Move on to next computation: A51
		// Provide info on whether this local subject camera is back online
		this.transition("A51", isBackOnline);
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
		throw new ComputationExecutionException("M51 Computation on Neighbor Master Node");
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
		throw new ComputationExecutionException("M51 Computation on New Subject Master Node");
	}
}
