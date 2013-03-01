package node.selfAdaptationSystem.coordination.protocols.pingEcho;

import node.selfAdaptationSystem.coordination.SelfAdaptationMessage;

public class PingEchoMessage extends SelfAdaptationMessage {
	
	public PingEchoMessage(PingEchoMessageType type){		
		this.type = type;
	}
	
	public PingEchoMessageType getType(){
		return this.type;
	}
	
	private final PingEchoMessageType type;
	
	public enum PingEchoMessageType { PING, ECHO }
	
	@Override
	public PingEchoMessage clone(){
		return new PingEchoMessage(this.getType());
	}

}
