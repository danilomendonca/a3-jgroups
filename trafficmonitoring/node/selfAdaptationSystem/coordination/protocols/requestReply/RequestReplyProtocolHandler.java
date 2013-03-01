package node.selfAdaptationSystem.coordination.protocols.requestReply;

import utilities.NodeID;
import node.selfAdaptationSystem.coordination.protocols.CoordinationProtocolHandler;
import node.selfAdaptationSystem.coordination.protocols.requestReply.RequestReplyMessage.RequestReplyMessageType;
import node.selfAdaptationSystem.mapeManager.MapeComputation;

public class RequestReplyProtocolHandler<I>
				extends CoordinationProtocolHandler<RequestReplyProtocolHandler.RequestReplyProtocolRole> {
	
	public enum RequestReplyProtocolRole { REQUESTER, REPLIER }
	
	/**
	 * Create a handler in the role of REQUESTER, sending a request message to the REPLIER and expecting a reply
	 */
	public RequestReplyProtocolHandler(MapeComputation localMapeComputation, NodeID replierTargetNode){
		super(RequestReplyProtocolRole.REQUESTER, localMapeComputation);
		
		this.replierNode = replierTargetNode;
	}
	
	/**
	 * Create a handler in the role of REPLIER, responding to a request message with a reply
	 */
	public RequestReplyProtocolHandler(MapeComputation localMapeComputation){
		super(RequestReplyProtocolRole.REPLIER, localMapeComputation);
	}
	
	
	/**************************	 
	 * 
	 *	Execution
	 *
	 **************************/
	
	@Override
	/**
	 * If this handler is occupying the role of REQUESTER:
	 * - send a request message to the REPLIER
	 * - wait for a reply message
	 * 
	 * If this handler is occupying the role of REPLIER:
	 * - respond to a request message by replying, optionally with a certain message payload
	 */
	public void execute(){
		if(this.getProtocolRole() == RequestReplyProtocolRole.REQUESTER)
			this.executeOnRequester();
		else
			this.executeOnReplier();
	}	
	
	private I replyPlayload;
	
	
	/**************************	 
	 * 
	 *	Execution on REQUESTER
	 *
	 **************************/
	
	private void executeOnRequester(){
		if(!this.requestSent){
			this.sendRequestMessage(this.replierNode);
			
			this.requestSent = true;
		}		
		
		this.processReplyMessage();
	}
	
	public void reset(){
		this.requestSent = false;
		this.hasCompleted = false;
		this.replyPlayload = null;
	}
	
	public I getReplyPayload(){
		return this.replyPlayload;
	}
	
	private void processReplyMessage(){
		if(this.getMapeComputation().hasNextRemoteMessage()){
			//Look for specific remote messages, relevant to this protocol handler
			@SuppressWarnings("unchecked")
			RequestReplyMessage<I> reply = (RequestReplyMessage<I>) this.receiveEchoMessage();
			
			if(reply != null){
				this.replyPlayload = reply.getMessagePayload();
				
				this.hasCompleted = true;
			}			
		}
		//Else: reply not received yet; try again in next execution cycle
	}
	
	/*
	 * Send RequestReplyMessage: REQUEST type, no payload
	 */
	private void sendRequestMessage(NodeID replierTarget) {
		this.getMapeComputation().sendRemoteMessage(replierTarget, 
				new RequestReplyMessage<I>(RequestReplyMessageType.REQUEST, null));
	}
	
	public boolean hasCompleted(){
		return this.hasCompleted;
	}
	
	private NodeID replierNode;
	private boolean requestSent = false;
	private boolean hasCompleted = false;
	
	
	/**************************	 
	 * 
	 *	Execution on REPLIER
	 *
	 **************************/
	
	public void setReplyPayload(I payload){
		this.replyPlayload = payload;
	}

	private void executeOnReplier(){
		while(this.getMapeComputation().hasNextRemoteMessage()){
			@SuppressWarnings("unchecked")
			RequestReplyMessage<I> request = (RequestReplyMessage<I>) this.getMapeComputation().getNextRemoteMessage();
			
			this.sendReplyMessage(request.getSenderNode());
			
			this.hasCompleted = true;
			
//			System.out.println(this.getMapeComputation().getSelfAdaptationModels().getHostNode() + " reply sent to " + request.getSenderNode());
		}		
	}
	
	private void sendReplyMessage(NodeID requester){
		this.getMapeComputation().sendRemoteMessage(requester, 
				new RequestReplyMessage<I>(RequestReplyMessageType.REPLY, this.replyPlayload));
	}
	
	
	/**************************	 
	 * 
	 *		RECEIVE
	 *
	 **************************/
	
	protected RequestReplyMessage<?> receiveEchoMessage(){
		//Look only for ECHO-replies sent by the REPLIER node
		RequestReplyMessageSelector echoSelector = 
			new RequestReplyMessageSelector(this.replierNode, RequestReplyMessageType.REPLY);
		
		return ((RequestReplyMessage<?>) this.getMapeComputation().getNextRemoteMessage(echoSelector));
	}
	
}
