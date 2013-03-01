package node.selfAdaptationSystem.mapeManager.neighborNodeFailure;

import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.ControlLoopEnd;

public class End4 extends ControlLoopEnd implements NeighborNodeFailureComputation {
	
	public End4(NeighborNodeFailureScenario scenario){
		super(scenario);
	}
	
	
	/**************************	 
	 * 
	 *	Execution
	 *
	 **************************/
	
	@Override
	public void executeOnSubjectNode() throws ComputationExecutionException {
		//Should never be used
		throw new ComputationExecutionException("The NeighborNodeFailure scenario loop has ended");
	}

	@Override
	public void executeOnSubjectNeighborNode() throws ComputationExecutionException {
		//Should never be used
		throw new ComputationExecutionException("The NeighborNodeFailure scenario loop has ended");
	}
	
}
