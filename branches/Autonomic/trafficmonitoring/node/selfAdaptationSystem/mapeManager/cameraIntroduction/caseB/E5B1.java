package node.selfAdaptationSystem.mapeManager.cameraIntroduction.caseB;

import java.util.ArrayList;

import utilities.NodeID;
import node.organizationMiddleware.contextManager.contextDirectories.Context;
import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.ExecuteComputation;
import node.selfAdaptationSystem.mapeManager.cameraIntroduction.CameraIntroductionComputation;
import node.selfAdaptationSystem.mapeManager.cameraIntroduction.CameraIntroductionScenario;
import node.selfAdaptationSystem.selfAdaptationModels.OrganizationSnapshot;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class E5B1 extends ExecuteComputation implements CameraIntroductionComputation {
	
	public E5B1(CameraIntroductionScenario scenario, SelfAdaptationModels models,
			BaseLevelConnector baseLevel) {
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
	 * - Adapt the base-level to reflect this scenario subject joining a neighboring organization as a slave
	 */
	public void executeOnAliveSubjectNode(){
		//Retrieve relevant computation input sent by previous computation
		NodeID newMaster = (NodeID) this.getComputationTransitionMessage().getTargetComputationInput();
		
		//Find id of this subject's new organization
		int newOrganizationID = Integer.MIN_VALUE;
		for(OrganizationSnapshot neighborOrg : this.getSelfAdaptationModels().getAllNeighborNodeOrganizationInformation()){
			if(neighborOrg.getMasterNode().equals(newMaster))
				newOrganizationID = neighborOrg.getOrganizationId();
		}
		
		this.getBaseLevelConnector().startLocalSlave(newMaster, newOrganizationID);
		
		//Transition to next computation P5B2
		this.transition("P5B2");
	}	
	
	@Override
	/**
	 * As the new master of the scenario subject:
	 * - Adapt the base-level to reflect the scenario subject joining the organization of this master node as a slave
	 */
	public void executeOnNewSubjectMasterNode(){
		//Retrieve relevant computation input sent by previous computation
		Context changeContext = 
			(Context) this.getComputationTransitionMessage().getTargetComputationInput();
		
		Context localContext = this.getBaseLevelConnector().getLocalOrganizationContext();
		
		//Add new agent
		ArrayList<NodeID> agents = localContext.getPersonalOrg().getAgents();
		agents.add(this.getScenario().getSubject());
		localContext.getPersonalOrg().changeAgents(agents);
		
		//Add new roleposition, sent by the subject in the previous computation. Assume only one roleposition-object was sent
		localContext.getPersonalOrg().addFilledRolePosition(changeContext.getPersonalOrg().getFilledRolePositions().get(0));
		
		//Change boundaries for the local organization, based on the information calculated in the previous computation
		localContext.getPersonalOrg().changeOrganizationBoundaries(changeContext.getPersonalOrg().getOrganizationBoundaries());
		
		//Note that, whether the subject slave will be located between other (current) members of this organization or whether it
		// will be at the edge of this master's organization, all current neighbor organizations remain valid; no adaptations needed.
		
		//Transition to next computation P5B2
		this.transition("P5B2");
	}
	
	
	
	@Override
	/**
	 * As a master of an organization neighboring on the scenario subject camera, but not to be become the
	 * scenario subject's new master node:
	 * - Do nothing. Technically shouldn't occur (This role does not participate in this computation)
	 * 
	 * @throws	ComputationExecutionException
	 * 			Always; this method should never be used.
	 */
	public void executeOnNeighborMasterNode() throws ComputationExecutionException {
		throw new ComputationExecutionException("E5B1 Computation on Other Neighbor Master Node");
	}

}
