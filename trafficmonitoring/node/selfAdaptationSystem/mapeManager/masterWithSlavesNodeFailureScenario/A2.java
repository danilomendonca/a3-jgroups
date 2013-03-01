package node.selfAdaptationSystem.mapeManager.masterWithSlavesNodeFailureScenario;

import node.selfAdaptationSystem.coordination.protocols.pingEcho.PingEchoInformation;
import node.selfAdaptationSystem.mapeManager.AnalyzeComputation;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class A2 extends AnalyzeComputation implements MasterWithSlavesNodeFailureComputation {
		
	public A2(MasterWithSlavesNodeFailureScenario scenario, SelfAdaptationModels models){
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
	 * - Check the local timestamp for the scenario's subject and decide whether to execute the actual
	 * self-healing process
	 */
	public void executeOnNeighborMasterNode(){
		this.defaultNonSubjectImplementation();
	}
	
	@Override
	/**
	 * As a slave to this scenario's subject master node:
	 * - Check the local timestamp for the scenario's subject and decide whether to execute the actual
	 * self-healing process
	 */
	public void executeOnSlaveNode(){
		this.defaultNonSubjectImplementation();
	}
	
	private void defaultNonSubjectImplementation(){	
		int maxExecutionCycleTimestampAge = 10;
		
		if(scenarioSubjectFailed(maxExecutionCycleTimestampAge))
			//Initiate self-healing process: transition to P21
			this.transition("P21");
		else
			//No problem detected; restart control loop: transition to M2
			this.transition("M2");
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
	 * As the newly elected master of the organization of which this scenario's subject was the original master:
	 * - Do nothing. Technically shouldn't occur (no new master has been elected yet)
	 * 
	 * @throws	ComputationExecutionException
	 * 			Always; this method should never be used.
	 */
	public void executeOnNewMasterNode() throws ComputationExecutionException {
		throw new ComputationExecutionException("A2 Computation on New Master Node");
	}
	
	@Override
	/**
	 * As a master node and subject of this particular Self-Healing Scenario:
	 * - Nothing needs to be done (technically shouldn't be used: subject node should just stay in M2)
	 * 
	 * @throws	ComputationExecutionException
	 * 			Always; this method should never be used.		
	 */
	public void executeOnSubjectMasterNode() throws ComputationExecutionException{
		throw new ComputationExecutionException("A2 Computation on Subject Master Node");
	}	
	
}
