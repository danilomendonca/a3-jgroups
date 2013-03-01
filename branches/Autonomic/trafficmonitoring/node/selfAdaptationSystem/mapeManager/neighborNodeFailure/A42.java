package node.selfAdaptationSystem.mapeManager.neighborNodeFailure;

import java.util.ArrayList;
import java.util.List;

import utilities.NodeID;

import node.selfAdaptationSystem.coordination.protocols.pingEcho.PingEchoInformation;
import node.selfAdaptationSystem.mapeManager.AnalyzeComputation;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.neighborNodeFailure.messages.M42Input;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class A42 extends AnalyzeComputation implements NeighborNodeFailureComputation {
	
	public A42(NeighborNodeFailureScenario scenario, SelfAdaptationModels models){
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
	 * - advance to computation P4 if all currently considered replacement neighbors of the subject neighbor node
	 * have successfully replied to the ping-messages sent out earlier
	 * - if not, go back to computation M42 to wait for these echo-replies or, if one or more dead
	 * nodes have been encountered, to adjust to the list of potential neighbor nodes and to retry
	 */
	public void executeOnSubjectNeighborNode(){
		//Make sure this period is long enough so this computation doesn't decide that nodes are dead when they
		// are in fact just in earlier stages of this scenario
		int maxExecutionCycleTimestampAge = 200;
		
		//Retrieve information from computation transition message
		@SuppressWarnings("unchecked")
		List<PingEchoInformation> pingEchoInfos = 
			(List<PingEchoInformation>) this.getComputationTransitionMessage().getTargetComputationInput();		
		
		//Check to see if all currently considered potential neighbor nodes have sent an echo-reply
		boolean allCurrentNeighborsHaveReplied = true;
		for(PingEchoInformation pingEchoInfo : pingEchoInfos){
			if(!pingEchoInfo.echoReplyReceived())
				allCurrentNeighborsHaveReplied = false;
		}
		
		//If not all potential neighbor nodes have sent an echo-reply: return to computation M42
		if(!allCurrentNeighborsHaveReplied){
			//Go over all ping-echo information objects to see which potential neighbor nodes should be classified as dead
			ArrayList<NodeID> deadNodes = new ArrayList<NodeID>();
			for(PingEchoInformation pingEchoInfo : pingEchoInfos){
				if(!pingEchoInfo.echoReplyReceived() 
						&& !this.getSelfAdaptationModels().executionCycleTimestampIsYoungerThan(
								pingEchoInfo.getExecutionCycleTimestampLastSentPingMessage(), maxExecutionCycleTimestampAge) ){
					deadNodes.add(pingEchoInfo.getTargetNode());
					
					//Also record this information in the self-healing models:
					this.getSelfAdaptationModels().addPreviouslyFailedNeighborNode(pingEchoInfo.getTargetNode());
				}
			}
			
			//Send this list of dead neighbor nodes (a sublist of the current potential replacement neighbor nodes) back
			// to computation M42
			M42Input input = new M42Input();
			input.setDeadNeighborNodes(deadNodes);
			this.transition("M42", input);
		}
		else{
			//All valid neighbor nodes found!
			
			//If computation M42 hasn't been given control after having found all new neighbor nodes, go back
			// and let it do its thing
			if(!this.newNeighborNodesReported){
				M42Input input = new M42Input();
				input.setNewNeighborNodesFound();
				this.transition("M42", input);
				
				//Make sure this situation happens just once
				this.newNeighborNodesReported = true;
			}	
			else{				
				//M42 has just completed its last execution cycle; move on to computation P4
				// Send along the list of replacement neighbor nodes for the dead subject node
				ArrayList<NodeID> replacementNeighborNodes = new ArrayList<NodeID>();
				for(PingEchoInformation pingEchoInfo : pingEchoInfos){
					replacementNeighborNodes.add(pingEchoInfo.getTargetNode());
				}
				
				this.transition("P4", replacementNeighborNodes);
			}			
		}
	}
	
	private boolean newNeighborNodesReported = false;
	
	
	
	
	@Override
	/**
	 * As the subject node of this particular Self-Healing Scenario:
	 * - Nothing needs to be done (technically shouldn't occur: at this point in the scenario, the subject
	 * node is supposedly dead)
	 * 
	 * @throws	ComputationExecutionException
	 * 			Always; this method should never be used.		
	 */
	public void executeOnSubjectNode() throws ComputationExecutionException {
		throw new ComputationExecutionException("M42 Computation on Subject Node");
	}

}
