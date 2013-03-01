package node.selfAdaptationSystem.coordination.protocols.requestReply;

import utilities.NodeID;
import node.selfAdaptationSystem.coordination.SelfAdaptationMessage;
import node.selfAdaptationSystem.coordination.SelfAdaptationMessageSelector;
import node.selfAdaptationSystem.coordination.protocols.requestReply.RequestReplyMessage.RequestReplyMessageType;

public class RequestReplyMessageSelector extends SelfAdaptationMessageSelector {
	
	public RequestReplyMessageSelector(NodeID replier, RequestReplyMessageType type){
		this.replier = replier;
		this.messageType = type;
	}
	
	private final NodeID replier;
	private final RequestReplyMessageType messageType;

	@Override
	/**
	 * Returns true if the given message is a RequestReplyMessage,
	 * if it's the same as the type this selector is meant to look for and if the given
	 * message was sent by the node this selector is meant to look for
	 */
	public boolean select(SelfAdaptationMessage message) {
		if(!(message instanceof RequestReplyMessage<?>))
			return false;
		
		RequestReplyMessage<?> requestReplyMessage = (RequestReplyMessage<?>) message;
		return (requestReplyMessage.getType() == this.messageType)
					&& requestReplyMessage.getSenderNode().equals(this.replier);
	}

}
