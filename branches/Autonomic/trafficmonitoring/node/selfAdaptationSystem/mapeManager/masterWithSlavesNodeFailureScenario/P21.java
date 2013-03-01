package node.selfAdaptationSystem.mapeManager.masterWithSlavesNodeFailureScenario;

import java.util.ArrayList;

import utilities.NodeID;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.PlanComputation;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class P21 extends PlanComputation implements MasterWithSlavesNodeFailureComputation {

	public P21(MasterWithSlavesNodeFailureScenario scenario, SelfAdaptationModels models) {
		super(scenario, models);
	}
		
	
	/**************************	 
	 * 
	 *	Execution
	 *
	 **************************/	
	
	@Override
	/**
	 * As a master of an organization adjacent to the organization of this scenario's subject master node:
	 * - Look up all ongoing inter-organizational interactions with the subject master node
	 */
	public void executeOnNeighborMasterNode(){
		//Implementation-specific: nothing needs to be done
		
		//Move on to next computation: E21
		this.transition("E21");
	}
	
	@Override
	/**
	 * As a master of an organization adjacent to the organization of this scenario's subject master node:
	 * - Look up all ongoing intra-organizational interactions with the subject master node
	 */
	public void executeOnSlaveNode() {
		//Wait until the NeighborNodeFailure scenario has updated the list of alive neighbor nodes
		//TODO: properly synchronize this
		ArrayList<NodeID> aliveNeighborNodes = this.getSelfAdaptationModels().getAliveNeighborNodes();
		if(aliveNeighborNodes.contains(this.getScenario().getSubject())){
			return;
		}
	
		//Implementation-specific: nothing needs to be done		
		
		//Move on to next computation: E21
		this.transition("E21");
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
		throw new ComputationExecutionException("P21 Computation on New Master Node");
	}
	
	@Override
	/**
	 * As a master node and subject of this particular Self-Healing Scenario:
	 * - Do nothing. Technically shouldn't occur (at this stage in the scenario, the subject node has failed)
	 * 
	 * @throws	ComputationExecutionException
	 * 			Always; this method should never be used.
	 */
	public void executeOnSubjectMasterNode() throws ComputationExecutionException{
		throw new ComputationExecutionException("P21 Computation on Subject Master Node");
	}

}
