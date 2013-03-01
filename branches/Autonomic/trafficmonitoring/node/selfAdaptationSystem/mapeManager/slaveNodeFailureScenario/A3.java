package node.selfAdaptationSystem.mapeManager.slaveNodeFailureScenario;

import node.selfAdaptationSystem.coordination.protocols.pingEcho.PingEchoInformation;
import node.selfAdaptationSystem.mapeManager.AnalyzeComputation;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class A3 extends AnalyzeComputation implements SlaveNodeFailureComputation {

	public A3(SlaveNodeFailureScenario scenario, SelfAdaptationModels models){
		super(scenario, models);
	}
	

	/**************************	 
	 * 
	 *	Execution
	 *
	 **************************/		

	@Override
	/**
	 * As a master to this scenario's subject slave node:
	 * - Check the local timestamp for the scenario's subject and decide whether to execute the actual
	 * self-healing process
	 */
	public void executeOnMasterNode(){		
		int maxExecutionCycleTimestampAge = 10;
		
		if(scenarioSubjectFailed(maxExecutionCycleTimestampAge))
			//Initiate self-healing process; move on to next computation P31
			this.transition("P31");
		else
			//No problem detected; go back to computation A3
			this.transition("M3");
	}

	private boolean scenarioSubjectFailed(int maxExecutionCycleTimestampAge){
		//Retrieve information from computation transition message
		PingEchoInformation pingEchoInfo = 
			(PingEchoInformation) this.getComputationTransitionMessage().getTargetComputationInput();
		
		//Return false if no echo-reply has been received, but the original ping-message has been sent
		// out more than the given amount of execution cycles
		return !pingEchoInfo.echoReplyReceived() 
					&& !this.getSelfAdaptationModels().executionCycleTimestampIsYoungerThan(
							pingEchoInfo.getExecutionCycleTimestampLastSentPingMessage(), maxExecutionCycleTimestampAge);
	}
	

	
	@Override
	/**
	 * As a slave node and subject of this particular Self-Healing Scenario:
	 * - Do nothing. Technically shouldn't occur (at this point in the scenario, 
	 * the subject slave node has failed)
	 * 
	 * @throws	ComputationExecutionException
	 * 			Always; this method should never be used.
	 */
	public void executeOnSubjectSlaveNode() throws ComputationExecutionException {
		throw new ComputationExecutionException("A3 Computation on Subject Slave Node");
	}

}
