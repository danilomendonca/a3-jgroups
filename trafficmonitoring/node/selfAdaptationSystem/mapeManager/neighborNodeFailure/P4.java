package node.selfAdaptationSystem.mapeManager.neighborNodeFailure;

import java.util.ArrayList;

import utilities.NodeID;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.PlanComputation;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class P4 extends PlanComputation implements NeighborNodeFailureComputation {

	public P4(NeighborNodeFailureScenario scenario, SelfAdaptationModels models){
		super(scenario, models);
	}


	/**************************	 
	 * 
	 *	Execution
	 *
	 **************************/		

	@Override
	/**
	 * As a neighbor of the subject of this particular Self-Healing Scenario:
	 * - Gather information in order to adapt the base-level to the failing of a neighbor node
	 */
	public void executeOnSubjectNeighborNode(){	
		//The new list of alive neighbor nodes is going to be the old list, minus the dead subject
		// node, plus the received list of replacement nodes (determined in the previous computations)
		ArrayList<NodeID> newNeighborNodes = new ArrayList<NodeID>();
		newNeighborNodes.addAll(this.getSelfAdaptationModels().getAliveNeighborNodes());
		newNeighborNodes.remove(this.getScenario().getSubject());
		
		@SuppressWarnings("unchecked")
		ArrayList<NodeID> replacements = 
			(ArrayList<NodeID>) this.getComputationTransitionMessage().getTargetComputationInput();
		for(NodeID replacementNode : replacements){
			if(!newNeighborNodes.contains(replacementNode))
				newNeighborNodes.add(replacementNode);
		}
		
		//Forward this list to the next computation (E4) and move on
		this.transition("E4", newNeighborNodes);
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
		throw new ComputationExecutionException("P4 Computation on Subject Node");
	}
	
}
