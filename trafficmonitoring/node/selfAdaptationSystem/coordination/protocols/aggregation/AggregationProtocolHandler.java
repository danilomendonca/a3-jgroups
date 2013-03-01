package node.selfAdaptationSystem.coordination.protocols.aggregation;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import utilities.NodeID;

import node.selfAdaptationSystem.coordination.protocols.CoordinationProtocolHandler;
import node.selfAdaptationSystem.mapeManager.MapeComputation;

public class AggregationProtocolHandler<I> 
				extends CoordinationProtocolHandler<AggregationProtocolHandler.AggregationProtocolRole> {
	
	public enum AggregationProtocolRole { SENDER, AGGREGATOR }
	
	/**
	 * Create a handler, taking up the role of SENDER in an aggregation context: it will send the given
	 * information-object to the given aggregator node.
	 */
	public AggregationProtocolHandler(MapeComputation mapeComputation, NodeID aggregator, I informationToSend){
		super(AggregationProtocolRole.SENDER, mapeComputation);
		
		this.aggregator = aggregator;
		this.informationToSend = informationToSend;
	}
	
	/**
	 * Create a handler, taking up the role of AGGREGATOR in an aggregation context: it will collect
	 * the information sent by the given list of sender nodes.
	 */
	public AggregationProtocolHandler(MapeComputation mapeComputation, List<NodeID> senderList){
		super(AggregationProtocolRole.AGGREGATOR, mapeComputation);
		
		this.senderList = senderList;
	}
	
	
	/**************************	 
	 * 
	 *	Execution
	 *
	 **************************/
	
	@Override
	/**
	 * If this handler is occupying the AggregationProtocolRole of SENDER:
	 * - it will send out the given information to the given aggregator, after which its part
	 * of the job is done.
	 * 
	 * If this handler is occupying the AggregationProtocolRole of AGGREGATOR:
	 * - it will receive information from all senders
	 * - when each node in the given list of senders has successfully provided the needed information,
	 * it will produce a list of everything it has received, after which its task is completed.
	 */
	public void execute() {
		if(this.getProtocolRole() == AggregationProtocolRole.SENDER)
			this.executeOnSender();
		else
			this.executeOnAggregator();
	}
	
	@Override
	/**
	 * If this handler is occupying the AggregationProtocolRole of SENDER:
	 * - it has completed after sending the required information to the aggregator
	 * 
	 * If this handler is occupying the AggregationProtocolRole of AGGREGATOR:
	 * - it has completed if it has received the required information from all senders
	 */
	public boolean hasCompleted() {
		return this.hasCompleted;
	}
	
	public boolean hasCompleted = false;
	
	public Collection<I> getAggregatedInformation(){
		return this.aggregatedInformation.values();
	}
	
	
	/**************************	 
	 * 
	 *	Execution on SENDER
	 *
	 **************************/
	
	private void executeOnSender(){
		AggregationMessage<I> message = new AggregationMessage<I>(this.informationToSend);
		
		//Transmit information
		this.getMapeComputation().sendRemoteMessage(this.aggregator, message);
		
		//Job as sender is done
		this.hasCompleted = true;
	}
	
	private NodeID aggregator;
	private I informationToSend;
	
	
	/**************************	 
	 * 
	 *	Execution on AGGREGATOR
	 *
	 **************************/
	
	private void executeOnAggregator(){
		//Collect current aggregation messages
		while(this.getMapeComputation().hasNextRemoteMessage()){
			@SuppressWarnings("unchecked")
			AggregationMessage<I> message = (AggregationMessage<I>) this.getMapeComputation().getNextRemoteMessage();
			
			//Save the sent information
			this.aggregatedInformation.put(message.getSenderNode(), message.getInformationToBeAggregated());
		}
		
		//Check to see if any sender still has to do its bit
		for(NodeID sender : this.senderList){
			if(!this.aggregatedInformation.containsKey(sender))
				//Still information left to receive: return in next execution cycle
				return;
		}
		
		//At this point: every sender has completed its job
		this.hasCompleted = true;
	}
	
	private HashMap<NodeID, I> aggregatedInformation = new HashMap<NodeID, I>();
	private List<NodeID> senderList;

}
