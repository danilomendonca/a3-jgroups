package node.selfAdaptationSystem.coordination.protocols.election;

import java.util.ArrayList;
import java.util.List;

import utilities.NodeID;
import node.selfAdaptationSystem.coordination.protocols.CoordinationProtocolHandler;
import node.selfAdaptationSystem.mapeManager.MapeComputation;

//Note on Comparable and Generics: 	http://jroller.com/dhall/date/20050615
public class ElectionProtocolHandler<C extends Comparable<? super C>> 
						extends CoordinationProtocolHandler<ElectionProtocolHandler.ElectionProtocolRole> {

	public enum ElectionProtocolRole { CANDIDATE }
	
	/**
	 * Create a handler, taking part in an election protocol with other CANDIDATEs. This handler will communicate
	 * with all other candidates by sending and forwarding messages to each node in the given list of direct
	 * communication nodes. The given criterium will be used to compare and determine which of the candidates 
	 * will be the winner of the election. The given timeout period determines how long this handler needs to wait 
	 * for remaining election messages before deeming the election to be over (and one of the candidates to have 
	 * been elected)
	 */
	public ElectionProtocolHandler(MapeComputation localMapeComputation, NodeID localNode, 
									C candidateCriterium, List<NodeID> directCommunicationNodes, int electionExecutionCycleTimeout){
		super(ElectionProtocolRole.CANDIDATE, localMapeComputation);
		
		this.localNode = localNode;
		this.localCandidateCriterium = candidateCriterium;
		this.directCommunicationNodes = directCommunicationNodes;
		this.electionExecutionCycleTimeout = electionExecutionCycleTimeout;
	}
	
	
	/**************************	 
	 * 
	 *	Execution
	 *
	 **************************/
	
	private final List<NodeID> directCommunicationNodes;
	private final NodeID localNode;
	private C localCandidateCriterium;
	private final int electionExecutionCycleTimeout;
	
	@Override
	/**
	 * First, this handler will send out its own election message to all direct communication nodes.
	 * Afterwards, it will keep receiving and forwarding election messages from other candidates to its direct
	 * communication nodes.
	 * If this handler will not have received any election message for a period longer than the election timeout period,
	 * it will conclude that the election has ended and will therefore produce ElectionInformation on the election
	 * winner and all other candidates.
	 */
	public void execute(){
		int currentExecutionCycle = this.getMapeComputation().getSelfAdaptationModels().getCurrentExecutionCycle();
		
		//Send election message to all direct communication nodes (if this hasn't been done already in a previous execution cycle)
		if(!this.ownElectionMessagesSent){			
			//Send election messages to ALL direct communication nodes. Expect only nodes interested in the election (their
			// identities are now still unknown at this point) to respond
			for(NodeID neighbor : directCommunicationNodes){
				this.sendOwnElectionMessage(neighbor);
			}
			
			//By default (in case of an election with just one candidate): this local node will be the winner
			this.preliminaryElectedNode = this.localNode;
			this.preliminaryElectedNodeCriterium = this.localCandidateCriterium;
			
			this.ownElectionMessagesSent = true;
			
			//Record time
			this.executionCycleTimestampLastElectionMessage = currentExecutionCycle;
		}
		
		//Process election messages from other candidates (may or may not be direct communication nodes)
		while(this.getMapeComputation().hasNextRemoteMessage()){		
			@SuppressWarnings("unchecked")
			ElectionMessage<C> message = (ElectionMessage<C>) this.getMapeComputation().getNextRemoteMessage();
			
			this.executionCycleTimestampLastElectionMessage = currentExecutionCycle;
			
			//Record other remaining candidates for later use
			if(!this.localNode.equals(message.getElectionCandidate()))
				this.otherRemainingCandidates.add(message.getElectionCandidate());
			
			//Use the candidate criterium to determine if the preliminary election winner needs to change
			if(message.getCandidateCriterium().compareTo(this.preliminaryElectedNodeCriterium) > 0){
				//The remote node has a higher election criterium than the current preliminary elected node: 
				// is therefore the new preliminary winner
				this.preliminaryElectedNode = message.getElectionCandidate();
				this.preliminaryElectedNodeCriterium = message.getCandidateCriterium();
			}
			
			//Forward the election message to direct communication node (apart from the message sender)
			// Reason: so that all election candidates receive all election messages
			for(NodeID communicationNode : this.directCommunicationNodes){
				if(!communicationNode.equals(message.getSenderNode())){
					this.forwardElectionMessage(communicationNode, message);	
				}
			}
		}
		
		//If no new election messages have been received for a certain amount of time: election has ended
		if(!this.getMapeComputation().getSelfAdaptationModels().executionCycleTimestampIsYoungerThan(
				this.executionCycleTimestampLastElectionMessage, this.electionExecutionCycleTimeout)){		
			//Election finished, record result
			this.electionResult =  new ElectionInformation(this.preliminaryElectedNode, this.otherRemainingCandidates);
		}
	}
	
	private boolean ownElectionMessagesSent = false;
	private ArrayList<NodeID> otherRemainingCandidates = new ArrayList<NodeID>();
	private NodeID preliminaryElectedNode;
	private C preliminaryElectedNodeCriterium;
	private int executionCycleTimestampLastElectionMessage;	
	
	@Override
	public boolean hasCompleted(){
		return (this.electionResult != null);
	}
	
	public ElectionInformation getElectionResult(){
		return this.electionResult;
	}
	
	private ElectionInformation electionResult;
	
	
	/**************************	 
	 * 
	 *		SEND
	 *
	 **************************/	
	
	private void sendOwnElectionMessage(NodeID destinationNode) {
		this.getMapeComputation().sendRemoteMessage(destinationNode, 
				new ElectionMessage<C>(this.localNode, this.localCandidateCriterium));
	}
	
	private void forwardElectionMessage(NodeID destinationNode, ElectionMessage<C> message) {
		this.getMapeComputation().sendRemoteMessage(destinationNode, 
				new ElectionMessage<C>(message.getElectionCandidate(), message.getCandidateCriterium()));
	}
	
}
