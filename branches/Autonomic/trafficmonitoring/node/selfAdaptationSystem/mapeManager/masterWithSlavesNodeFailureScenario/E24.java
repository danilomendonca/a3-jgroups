package node.selfAdaptationSystem.mapeManager.masterWithSlavesNodeFailureScenario;

import node.organizationMiddleware.contextManager.contextDirectories.Organization;
import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.ExecuteComputation;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class E24 extends ExecuteComputation implements MasterWithSlavesNodeFailureComputation {

	public E24(MasterWithSlavesNodeFailureScenario scenario, SelfAdaptationModels models,
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
	 * As a slave to this scenario's subject master node:
	 * - Resume intra-organizational computations
	 */
	public void executeOnSlaveNode(){
		//Implementation-specific: nothing needs to be done		
		
		//Move on to next computation: End2
		this.transition("End2");
	}	
	
	@Override
	/**
	 * As a master of an organization adjacent to the organization of this scenario's subject master node:
	 * - Resume inter-organizational computation with the organization that just got a new master node
	 * - adapt base-level to reflect the changed neighbor organization (new master, agents, ...)
	 */
	public void executeOnNeighborMasterNode() throws ComputationExecutionException{
		//Retrieve relevant computation input sent by previous computation
		Organization updatedNeighborOrgInfo = (Organization) this.getComputationTransitionMessage().getTargetComputationInput();
		
		//Update the base-level with the new neighbor organization information
		this.getBaseLevelConnector().changeNeighborOrganization(updatedNeighborOrgInfo);
		
		//TODO: Just restrict merging with organization of failing master
		this.getBaseLevelConnector().unblockMerging();
		this.getBaseLevelConnector().unblockSplitting();
		
		//Send out current traffic info in case merging/splitting needs to take place
		this.getBaseLevelConnector().forceTrafficInfoEvent();
		
		//Move on to next computation: End2
		this.transition("End2");
	}
	
	@Override
	/**
	 * As the newly elected master of the organization of which this scenario's subject was the original master:
	 * - resume base-level operations
	 */
	public void executeOnNewMasterNode(){
		//Implementation-specific: nothing needs to be done to resume base-level operations
		
		//Send out current traffic info in case merging/splitting needs to take place
		this.getBaseLevelConnector().forceTrafficInfoEvent();
		
		//Move on to next computation: End2
		this.transition("End2");
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
		throw new ComputationExecutionException("E24 Computation on Subject Master Node"); 
	}	
}
