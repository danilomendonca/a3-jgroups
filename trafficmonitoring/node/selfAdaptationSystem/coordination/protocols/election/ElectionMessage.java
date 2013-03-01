package node.selfAdaptationSystem.coordination.protocols.election;

import node.selfAdaptationSystem.coordination.SelfAdaptationMessage;
import utilities.NodeID;

public class ElectionMessage<C extends Comparable<?>> extends SelfAdaptationMessage {
	
	public ElectionMessage(NodeID electionCandidate, C candidateCriterium){
		this.electionCandidate = electionCandidate;
		this.candidateCriterium = candidateCriterium;
	}
	
	public NodeID getElectionCandidate(){
		return this.electionCandidate;
	}
	
	private NodeID electionCandidate;
	
	
	public C getCandidateCriterium(){
		return this.candidateCriterium;
	}
	
	private C candidateCriterium;
	
}
