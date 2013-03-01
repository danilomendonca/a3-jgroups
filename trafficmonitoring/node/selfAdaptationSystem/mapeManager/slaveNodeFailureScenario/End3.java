package node.selfAdaptationSystem.mapeManager.slaveNodeFailureScenario;

import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.ControlLoopEnd;

public class End3 extends ControlLoopEnd implements SlaveNodeFailureComputation {
	
	public End3(SlaveNodeFailureScenario scenario){
		super(scenario);
	}
	
	
	/**************************	 
	 * 
	 *	Execution
	 *
	 **************************/		

	@Override
	public void executeOnSubjectSlaveNode() throws ComputationExecutionException {
		//Should never be used
		throw new ComputationExecutionException("The SlaveNodeFailure scenario loop has ended");
	}

	@Override
	public void executeOnMasterNode() throws ComputationExecutionException {
		//Should never be used
		throw new ComputationExecutionException("The SlaveNodeFailure scenario loop has ended");
	}

}
