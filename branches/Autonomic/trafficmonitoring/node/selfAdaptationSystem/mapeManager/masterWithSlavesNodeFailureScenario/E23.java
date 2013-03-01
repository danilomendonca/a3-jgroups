package node.selfAdaptationSystem.mapeManager.masterWithSlavesNodeFailureScenario;

import node.organizationMiddleware.contextManager.contextDirectories.Context;

import node.organizationMiddleware.contextManager.contextDirectories.Organization;
import node.organizationMiddleware.contextManager.contextDirectories.RolePosition;
import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.ExecuteComputation;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class E23 extends ExecuteComputation implements MasterWithSlavesNodeFailureComputation {

	public E23(MasterWithSlavesNodeFailureScenario scenario, SelfAdaptationModels models,
				BaseLevelConnector baseLevel) {
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
	 * As a slave to this scenario's subject master node:
	 * - adapt the base-level organization context
	 */
	public void executeOnSlaveNode() throws ComputationExecutionException{
		//Retrieve relevant computation input sent by previous computation
		Context newLocalContext = (Context) this.getComputationTransitionMessage().getTargetComputationInput();
		
		//Adapt the base-level using the new local organization context
		this.adaptBaseLevelUsingNewContext(newLocalContext);
		
		//Re-initialize base-level perception and sync mechanisms 
		// (responsible for providing up-to-date neighbor and traffic info)
		this.getBaseLevelConnector().initializePerceptionMechanismsOnSlaveNode();

		//Move on to next computation: P24
		this.transition("P24");
	}	
	
	@Override
	/**
	 * As the newly elected master of the organization of which this scenario's subject was the original master:
	 * - - adapt the base-level organization context
	 */
	public void executeOnNewMasterNode() throws ComputationExecutionException {
		//Retrieve relevant computation input sent by previous computation
		Context newLocalContext = (Context) this.getComputationTransitionMessage().getTargetComputationInput();
		
		//Adapt the base-level using the new local organization context
		this.adaptBaseLevelUsingNewContext(newLocalContext);
		
		//Re-initialize base-level perception and sync mechanisms 
		// (responsible for providing up-to-date neighbor and traffic info)
		this.getBaseLevelConnector().initializePerceptionMechanismsOnMasterNode();
		
		//Move on to next computation: P24
		this.transition("P24");
	}
	
	private void adaptBaseLevelUsingNewContext(Context newLocalContext){
		//Implementation-specific: perform several operations separately instead of all at once in order to trigger
		// some base-level effects (Publish/Subscribe related)
		
		Context localContext = this.getBaseLevelConnector().getLocalOrganizationContext();
		
		//Set neighbor organizations
		localContext.setNeighbourOrgs(newLocalContext.getNeighbourOrgs());
		
		Organization newPersonalOrg = newLocalContext.getPersonalOrg();
		
		//Set new organization master
		localContext.getPersonalOrg().changeMaster(newLocalContext.getPersonalOrg().getMasterID());
		
		//Set organization agents (master + any slaves)
		localContext.getPersonalOrg().changeAgents(newPersonalOrg.getAgents());
		
		//Set personal organization boundaries
		localContext.getPersonalOrg().changeOrganizationBoundaries(newPersonalOrg.getOrganizationBoundaries());
		
		//Set filled role positions
		localContext.getPersonalOrg().clearFilledRolePositions();
		for(RolePosition rp : newPersonalOrg.getFilledRolePositions())
			localContext.getPersonalOrg().addFilledRolePosition(rp);		
	}
	
	
	
	
	@Override
	/**
	 * As a master of an organization adjacent to the organization of this scenario's subject master node:
	 * - Do nothing. Technically shouldn't occur (a master node of a neighboring organization is not involved
	 * in this particular computation)
	 */
	public void executeOnNeighborMasterNode() throws ComputationExecutionException {
		throw new ComputationExecutionException("E23 Computation on Master of Neighboring Organization");
	}
	
	@Override
	/**
	 * As a master node and subject of this particular Self-Healing Scenario:
	 * - Do nothing. Technically shouldn't occur (at this stage in the scenario, the subject node has failed)
	 * 
	 * @throws	ComputationExecutionException
	 * 			Always; this method should never be used.
	 */
	public void executeOnSubjectMasterNode() throws ComputationExecutionException {
		throw new ComputationExecutionException("E23 Computation on Subject Master Node"); 
	}
	
}
