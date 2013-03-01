package node.selfAdaptationSystem.mapeManager.cameraIntroduction.caseA;

import java.util.ArrayList;

import utilities.ThreevaluedLogic;

import node.organizationMiddleware.contextManager.contextDirectories.OrganizationBoundary;
import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.ExecuteComputation;
import node.selfAdaptationSystem.mapeManager.cameraIntroduction.CameraIntroductionComputation;
import node.selfAdaptationSystem.mapeManager.cameraIntroduction.CameraIntroductionScenario;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class E5A1 extends ExecuteComputation implements CameraIntroductionComputation {
	
	public E5A1(CameraIntroductionScenario scenario, SelfAdaptationModels models, BaseLevelConnector baseLevel){
		super(scenario, models, baseLevel);
	}

	
	/**************************	 
	 * 
	 *	Execution
	 * @throws ComputationExecutionException 
	 *
	 **************************/
	
	@Override
	/**
	 * As a camera node that has recently come back online after having failed earlier:
	 * - Start a new single member organization on this local camera, thereby making it a new master node.
	 */
	public void executeOnAliveSubjectNode() throws ComputationExecutionException{
		//Create new single master organization for the subject camera
		this.getBaseLevelConnector().createNewSingleMasterOrganization();		
		this.getBaseLevelConnector().initializePerceptionMechanismsOnMasterNode();
		
		//Set organization boundaries
		ArrayList<OrganizationBoundary> boundaries = this.getSelfAdaptationModels().calculateLocalOrganizationBoundaries();
		this.getBaseLevelConnector().getLocalOrganizationContext().getPersonalOrg().changeOrganizationBoundaries(boundaries);
		
		//Set traffic information
		ThreevaluedLogic trafficState = new ThreevaluedLogic();
		trafficState.set(this.getBaseLevelConnector().localCameraSeesTrafficCongestion());
		this.getBaseLevelConnector().getLocalOrganizationContext().getPersonalOrg().changeTrafficStateOfOrganization(trafficState);
		
		//Transition to computation P5A2
		this.transition("P5A2");
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
		throw new ComputationExecutionException("E5A1 Computation on Neighbor Master Node");
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
		throw new ComputationExecutionException("E5A1 Computation on New Subject Master Node");
	}

}
