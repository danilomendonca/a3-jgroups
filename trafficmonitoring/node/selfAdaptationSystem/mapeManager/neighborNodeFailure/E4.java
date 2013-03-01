package node.selfAdaptationSystem.mapeManager.neighborNodeFailure;

import java.util.ArrayList;

import utilities.NodeID;

import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.ExecuteComputation;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class E4 extends ExecuteComputation implements NeighborNodeFailureComputation {

	public E4(NeighborNodeFailureScenario scenario, SelfAdaptationModels models,
				BaseLevelConnector baseLevel) {
		super(scenario, models, baseLevel);
	}


	/**************************	 
	 * 
	 *	Execution
	 *
	 **************************/		

	@SuppressWarnings("unchecked")
	@Override
	/**
	 * As a neighbor of the subject of this particular Self-Healing Scenario:
	 * - Adapt the base-level to the reflect the failing of a neighbor node
	 * - Adapt the self-healing models to reflect the failing of a neighbor node
	 */
	public void executeOnSubjectNeighborNode(){		
		//Set alive neighboring cameras in base-level
		// Retrieve this list from the computation transition message
		this.getBaseLevelConnector().setAliveNeighbors(
				(ArrayList<NodeID>) this.getComputationTransitionMessage().getTargetComputationInput());
		
//		System.out.println("old neighbor node list for node " + this.getSelfAdaptationModels().getHostNode() +
//				": " + this.getSelfAdaptationModels().getAliveNeighborNodes());		
//		System.out.println("new neighbor node list for node " + this.getSelfAdaptationModels().getHostNode() +
//				": " + (ArrayList<NodeID>) this.getComputationTransitionMessage().getTargetComputationInput());
		
		//Move on to next computation: End4
		this.transition("End4");
	}	
	
	
	
	/**
	 * As the subject node of this particular Self-Healing Scenario:
	 * - Nothing needs to be done (technically shouldn't be used: at this point in the scenario, the subject
	 * node is supposedly dead)
	 * 
	 * @throws	ComputationExecutionException
	 * 			Always; this method should never be used.		
	 */
	@Override
	public void executeOnSubjectNode() throws ComputationExecutionException {
		throw new ComputationExecutionException("E4 Computation on Subject Node");
	}

}
