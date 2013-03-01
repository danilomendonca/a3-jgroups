package node.selfAdaptationSystem.mapeManager.masterWithSlavesNodeFailureScenario;

import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.ExecuteComputation;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class E21 extends ExecuteComputation implements MasterWithSlavesNodeFailureComputation {

	public E21(MasterWithSlavesNodeFailureScenario scenario, SelfAdaptationModels models,
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
	 * As a master of an organization adjacent to the organization of this scenario's subject master node:
	 * - Disable inter-organizational communication with failed subject node
	 * 
	 * @throws	ComputationExecutionException
	 * 			This node is not a master node
	 */
	public void executeOnNeighborMasterNode() throws ComputationExecutionException {
//		//Implementation-specific: disabling MergeLaw is sufficient		
//		//Restrict the local organization from merging with the organization for which the failed scenario subject
//		// was the master
//		for(OrganizationSnapshot neighborOrg : this.getModelRepository().getNeighborOrganizations()){
//			if(neighborOrg.getMasterNode().equals(this.getScenario().getSubject()))
//				this.getBaseLevelConnector().disableMergeLaw(neighborOrg.getOrganizationId());
//		}		
		
		
		//Block merging and splitting completely while neighbor organization is healing
		//TODO: Just restrict merging with organization of failing master
		this.getBaseLevelConnector().blockMerging();
		this.getBaseLevelConnector().blockSplitting();
		
		
		//Move on to next computation: P24
		this.transition("P24");
	}
	
	
	@Override
	/**
	 * As a slave to this scenario's subject master node:
	 * - Disable intra-organizational communication with failed subject node
	 */
	public void executeOnSlaveNode() {	
		//Implementation-specific: nothing needs to be done
		
		//Move on to next computation: P22
		this.transition("P22");
	}

	
	
	
	@Override
	/**
	 * As the newly elected master of the organization of which this scenario's subject was the original master:
	 * - Do nothing. Technically shouldn't occur (no new master has been elected yet)
	 * 
	 * @throws	ComputationExecutionException
	 * 			Always; this method should never be used.
	 */
	public void executeOnNewMasterNode() throws ComputationExecutionException {
		throw new ComputationExecutionException("E21 Computation on New Master Node");
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
		throw new ComputationExecutionException("E21 Computation on Subject Master Node");
	}
	
}
