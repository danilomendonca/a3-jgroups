package node.selfAdaptationSystem.coordination.protocols.aggregation;

import node.selfAdaptationSystem.coordination.SelfAdaptationMessage;

public class AggregationMessage<I> extends SelfAdaptationMessage {
	
	public AggregationMessage(I informationToBeAggregated){
		this.informationToBeAggregated = informationToBeAggregated;
	}
	
	public I getInformationToBeAggregated(){
		return this.informationToBeAggregated;
	}
	
	private I informationToBeAggregated;
	
	

}
