package node.selfAdaptationSystem.mapeManager.masterWithSlavesNodeFailureScenario;

import java.util.ArrayList;

import utilities.NodeID;
import node.selfAdaptationSystem.coordination.protocols.election.ElectionInformation;
import node.selfAdaptationSystem.coordination.protocols.election.ElectionProtocolHandler;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.PlanComputation;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class P22 extends PlanComputation implements MasterWithSlavesNodeFailureComputation {
	
	public P22(MasterWithSlavesNodeFailureScenario scenario, SelfAdaptationModels models) {
		super(scenario, models);
	}
		
	
	/**************************	 
	 * 
	 *	Execution
	 *
	 **************************/
	
	@Override
	/**
	 * As a slave to this scenario's subject master node:
	 * - Coordinate with the other slave nodes of the organization to elect a new master
	 */
	public void executeOnSlaveNode(){		
		//Initialize CoordinationHandler if necessary
		if(this.electionHandler == null){
			//The handler should communicate with all remaining slave nodes of this organization
			// through the alive neighbors of this node
			NodeID localNode = this.getSelfAdaptationModels().getHostNode();
			ArrayList<NodeID> directCommunicationNodes = this.getSelfAdaptationModels().getAliveNeighborNodes();
			int electionElectionCycleTimeout = 50;
			
			//Note: the localNode is used as the election criterium as well 
			this.electionHandler = 
				new ElectionProtocolHandler<NodeID>(this, localNode, localNode, directCommunicationNodes, electionElectionCycleTimeout);
		}
			
		this.electionHandler.execute();
		
		if(this.electionHandler.hasCompleted()){
			//Prepare input for E22
			ElectionInformation input = this.electionHandler.getElectionResult();
			
			//Move on to next computation: E22
			this.transition("E22", input);
		}
		else{
			//Stay in this computation (for now)
			return;
		}
	}	
	
	private ElectionProtocolHandler<NodeID> electionHandler;
	
	
	
	@Override
	/**
	 * As a master of an organization adjacent to the organization of this scenario's subject master node:
	 * - Do nothing. Technically shouldn't occur (a master node of a neighboring organization is not involved
	 * in this particular computation)
	 */
	public void executeOnNeighborMasterNode() throws ComputationExecutionException {
		throw new ComputationExecutionException("P22 Computation on Master of Neighboring Organization");
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
		throw new ComputationExecutionException("P22 Computation on New Master Node");
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
		throw new ComputationExecutionException("P22 Computation on Subject Master Node");
	}

}
