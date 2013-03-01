package node.selfAdaptationSystem.mapeManager;

import utilities.NodeID;

public class ScenarioIdentifier {
	
	public ScenarioIdentifier(NodeID subject, String scenarioType){
		this.scenarioSubject = subject;
		this.scenarioType = scenarioType;
	}
	
	private NodeID scenarioSubject;
	//ScenarioType examples: MasterNodeFailure, ...
	private String scenarioType;
	
	
	public NodeID getScenarioSubject() {
		return scenarioSubject;
	}
	public String getScenarioType() {
		return scenarioType;
	}
	
	
	@Override
	public boolean equals(Object other){
		if(!(other instanceof ScenarioIdentifier))
			return false;
		
		ScenarioIdentifier otherScenario = (ScenarioIdentifier) other;		
		if(!this.scenarioSubject.equals(otherScenario.getScenarioSubject()))
			return false;
		if(!this.getScenarioType().equals(otherScenario.getScenarioType()))
			return false;
		
		return true;
	}
	
	@Override
	public int hashCode(){
		return (this.scenarioSubject.hashCode() ^ this.scenarioType.hashCode());
	}
	
	public String toString(){
		return "ScenarioType: " + this.scenarioType + " - ScenarioSubject " + this.scenarioSubject;
	}

}
