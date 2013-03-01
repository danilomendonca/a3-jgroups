package node.selfAdaptationSystem.mapeManager.neighborNodeFailure;

import node.selfAdaptationSystem.coordination.protocols.pingEcho.PingEchoInformation;
import node.selfAdaptationSystem.mapeManager.AnalyzeComputation;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class A41 extends AnalyzeComputation implements NeighborNodeFailureComputation {

	public A41(NeighborNodeFailureScenario scenario, SelfAdaptationModels models){
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
	 * - Check the local timestamp for the scenario's subject and decide whether to execute the
	 * actual self-healing process
	 */
	public void executeOnSubjectNeighborNode(){
		int maxExecutionCycleTimestampAge = 10;
		
		if(scenarioSubjectFailed(maxExecutionCycleTimestampAge)){
			//Record this information in the self-healing models
			this.getSelfAdaptationModels().addPreviouslyFailedNeighborNode(this.getScenario().getSubject());
			
			//Initiate self-healing process; move on to next computation M42
			this.transition("M42");
		}			
		else
			//No problem detected; restart control loop; return to computation M41
			this.transition("M41");
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
	 * As the subject node of this particular Self-Healing Scenario:
	 * - Nothing needs to be done (technically shouldn't be used: subject node should just stay in M4)
	 * 
	 * @throws	ComputationExecutionException
	 * 			Always; this method should never be used.		
	 */
	public void executeOnSubjectNode() throws ComputationExecutionException {
		throw new ComputationExecutionException("A41 Computation on Subject Node");
	}
	
}
