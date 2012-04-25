package A3JGroups;


import org.jgroups.Message;

public class A3JGMessage extends Message{


	//true if is an update message, false otherwise
	private boolean type;
	private boolean sender;

	public boolean getType() {
		return type;
	}

	public void setType(boolean type) {
		this.type = type;
	}

	public boolean getSender() {
		return sender;
	}

	public void setSender(boolean sender) {
		this.sender = sender;
	}
	
}
