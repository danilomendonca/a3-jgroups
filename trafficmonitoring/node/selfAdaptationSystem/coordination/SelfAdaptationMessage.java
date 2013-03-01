package node.selfAdaptationSystem.coordination;

import node.selfAdaptationSystem.mapeManager.ScenarioIdentifier;

import utilities.NodeID;

public abstract class SelfAdaptationMessage {			
	
	//Identifier for the specific self-adaptation scenario to indicate the context within
	// which this message is being sent
	private ScenarioIdentifier scenario;
	
	public ScenarioIdentifier getScenario() {
		return scenario;
	}

	public void setScenario(ScenarioIdentifier scenario) {
		this.scenario = scenario;
	}


	//Identifier for the specific scenario computation this message is meant for
	// Examples computations: E22, M2, ...
	private String mapeComputationID;
	
	public String getTargetMapeComputationID() {
		return mapeComputationID;
	}

	public void setTargetMapeComputationID(String mapeComputationID) {
		this.mapeComputationID = mapeComputationID;
	}
	

	//NodeIdentifier of the sender of this message
	private NodeID senderNode;
	
	public void setSenderNode(NodeID sender){
		this.senderNode = sender;
	}
	
	public NodeID getSenderNode(){
		return this.senderNode;
	}
	
	//public abstract Object clone();
	
	
	
	
	protected void setSentDuringAdaptation(){
		this.sentDuringAdaptation = true;
	}
	
	public boolean sentDuringAdaptation(){
		return this.sentDuringAdaptation;
	}
	
	//Of use only for profiling/graphing/reporting
	private boolean sentDuringAdaptation = false;

}