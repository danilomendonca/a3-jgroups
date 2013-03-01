package node.selfAdaptationSystem.coordination.protocols.pingEcho;

import utilities.NodeID;
import node.selfAdaptationSystem.coordination.SelfAdaptationMessage;
import node.selfAdaptationSystem.coordination.SelfAdaptationMessageSelector;
import node.selfAdaptationSystem.coordination.protocols.pingEcho.PingEchoMessage.PingEchoMessageType;

public class PingEchoMessageSelector extends SelfAdaptationMessageSelector {
	
	public PingEchoMessageSelector(PingEchoMessageType messageType, NodeID senderNode){
		this.messageType  = messageType;
		this.senderNode = senderNode;
	}
	
	private final PingEchoMessageType messageType;
	private final NodeID senderNode;
	

	@Override
	/**
	 * Returns true if the given message is a PingEchoMessage,
	 * if it's the same as the type this selector is meant to look for and if the given
	 * message was sent by the node this selector is meant to look for
	 */
	public boolean select(SelfAdaptationMessage message) {
		if(!(message instanceof PingEchoMessage))
			return false;
		
		PingEchoMessage pingEchoMessage = (PingEchoMessage) message;
		return (pingEchoMessage.getType() == this.messageType)
					&& pingEchoMessage.getSenderNode().equals(this.senderNode);
	}

}
