package node.selfAdaptationSystem.coordination.protocols.requestReply;

import node.selfAdaptationSystem.coordination.SelfAdaptationMessage;

public class RequestReplyMessage<I> extends SelfAdaptationMessage {
	
	public RequestReplyMessage(RequestReplyMessageType type, I messagePayload){
		this.type = type;
		this.messagePayload = messagePayload;		
	}
	
	public I getMessagePayload(){
		return this.messagePayload;
	}
	
	private final I messagePayload;
	
	public RequestReplyMessageType getType(){
		return this.type;
	}
	
	private final RequestReplyMessageType type;
	
	public enum RequestReplyMessageType { REQUEST, REPLY };

}
