package node.selfAdaptationSystem.mapeManager.masterWithSlavesNodeFailureScenario;

import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.ControlLoopEnd;

public class End2 extends ControlLoopEnd implements MasterWithSlavesNodeFailureComputation {
	
	public End2(MasterWithSlavesNodeFailureScenario scenario){
		super(scenario);
	}
	
	
	/**************************	 
	 * 
	 *	Execution
	 *
	 **************************/

	@Override
	public void executeOnSubjectMasterNode() throws ComputationExecutionException {
		//Should never be used
		throw new ComputationExecutionException("The MasterWithSlavesNodeFailure scenario loop has ended");
	}

	@Override
	public void executeOnSlaveNode() throws ComputationExecutionException {
		//Should never be used
		throw new ComputationExecutionException("The MasterWithSlavesNodeFailure scenario loop has ended");
	}

	@Override
	public void executeOnNeighborMasterNode() throws ComputationExecutionException {
		//Should never be used
		throw new ComputationExecutionException("The MasterWithSlavesNodeFailure scenario has ended");
	}

	@Override
	public void executeOnNewMasterNode() throws ComputationExecutionException {
		//Should never be used
		throw new ComputationExecutionException("The MasterWithSlavesNodeFailure scenario has ended");
	}

}
