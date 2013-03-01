package node.selfAdaptationSystem.mapeManager.masterWithSlavesNodeFailureScenario;

import utilities.NodeID;
import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.coordination.protocols.election.ElectionInformation;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.ExecuteComputation;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class E22 extends ExecuteComputation implements MasterWithSlavesNodeFailureComputation {
		
	public E22(MasterWithSlavesNodeFailureScenario scenario, SelfAdaptationModels models,
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
	 * - If the slave on this node has been previously elected as the new master: adapt the base-level to
	 * this new role
	 * - Otherwise: stay in the same base-level role, but change this slave's master
	 */
	public void executeOnSlaveNode(){	
		//Retrieve relevant computation input sent by previous computation
		ElectionInformation input = (ElectionInformation) this.getComputationTransitionMessage().getTargetComputationInput();
		NodeID newlyElectedMaster = input.getNewlyElectedMaster();
		
		if(newlyElectedMaster.equals(this.getSelfAdaptationModels().getHostNode())){
			//Change to master role			
			this.getBaseLevelConnector().changeSlaveToMaster();
		}
		else{
			//Just change base-level to reflect new master

			//Implementation-specific: do nothing, everything will happen in E23			
		}		
		
		//Move on to next computation: P23
		// Note: just forward the computation input received by the previous computation to the next one
		this.transition("P23", this.getComputationTransitionMessage().getTargetComputationInput());
	}
	
	
	@Override
	/**
	 * As a master of an organization adjacent to the organization of this scenario's subject master node:
	 * - Do nothing. Technically shouldn't occur (a master node of a neighboring organization is not involved
	 * in this particular computation)
	 */
	public void executeOnNeighborMasterNode() throws ComputationExecutionException {
		throw new ComputationExecutionException("E22 Computation on Master of Neighboring Organization");
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
		throw new ComputationExecutionException("E22 Computation on New Master Node");
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
		throw new ComputationExecutionException("E22 Computation on Subject Master Node");
	}
		
}