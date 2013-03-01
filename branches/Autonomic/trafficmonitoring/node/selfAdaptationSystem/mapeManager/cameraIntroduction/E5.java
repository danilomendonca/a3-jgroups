package node.selfAdaptationSystem.mapeManager.cameraIntroduction;

import node.organizationMiddleware.contextManager.contextDirectories.NeighbourInfo;
import node.organizationMiddleware.contextManager.contextDirectories.TrafficJamInfo;
import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.ExecuteComputation;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class E5 extends ExecuteComputation implements CameraIntroductionComputation {
	
	public E5(CameraIntroductionScenario scenario, SelfAdaptationModels models, BaseLevelConnector baseLevel){
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
	 * - Make sure the base-level does not initiate anything on its own 
	 * - Initialize temporary sensing of local NeighbourInfo and TrafficJamInfo in case this is needed
	 * later on in the scenario (e.g. computation P5B1)
	 */
	public void executeOnAliveSubjectNode(){
		//Initialize and store Neighbor- and Traffic Info
		NeighbourInfo neighborInfo = new NeighbourInfo();
		this.getSelfAdaptationModels().setTempNeighborInfo(neighborInfo);
		TrafficJamInfo trafficInfo = new TrafficJamInfo();
		this.getSelfAdaptationModels().setTempTrafficInfo(trafficInfo);
		
		//Register these objects with the local perception middleware so that they will receive up-to-date info
		this.getBaseLevelConnector().registerTempNeighborAndTrafficSensing(trafficInfo, neighborInfo);
		
		//Move on to next computation: M52
		this.transition("M52");
	}	
	
	@Override
	/**
	 * As a master of an organization neighboring on the scenario subject camera:
	 * - Temporarily pause all base-level dynamics (splitting & merging) until the neighboring camera
	 * has been properly introduced into the traffic monitoring system
	 */
	public void executeOnNeighborMasterNode() throws ComputationExecutionException{
		//Block merging and splitting completely until camera introduction scenario has completed
		this.getBaseLevelConnector().blockMerging();
		this.getBaseLevelConnector().blockSplitting();
		
		//Move on to next computation: M52
		this.transition("M52");
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
		throw new ComputationExecutionException("E5 Computation on New Subject Master Node");
	}

}
