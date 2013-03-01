package node.selfAdaptationSystem.coordination;

import utilities.NodeID;
import node.selfAdaptationSystem.mapeManager.ScenarioIdentifier;

public class ComputationTransitionMessage<A> extends SelfAdaptationMessage {
	
	public ComputationTransitionMessage(NodeID senderNode,
										ScenarioIdentifier scenario,
										String sourceMapeComputationID,
										String targetMapeComputationID,
										A targetComputationInput){
		this.setSenderNode(senderNode);
		this.setScenario(scenario);
		
		this.setTargetMapeComputationID(targetMapeComputationID);
		this.sourceMapeComponentID = sourceMapeComputationID;
		this.targetComputationInput = targetComputationInput;		
	}
	
	//Identifier for the specific scenario computation this transition was sent from
	// Examples computations: E22, M2, ...
	private String sourceMapeComponentID;
	
	public String getSourceMapeComponentID() {
		return sourceMapeComponentID;
	}

	public void setSourceMapeComponentID(String sourceMapeComponentID) {
		this.sourceMapeComponentID = sourceMapeComponentID;
	}
	
	
	
	public A getTargetComputationInput(){
		return this.targetComputationInput;
	}
	
	private A targetComputationInput;

	@Override
	public ComputationTransitionMessage<A> clone() {
		// TODO Auto-generated method stub
		return null;
	}

}
