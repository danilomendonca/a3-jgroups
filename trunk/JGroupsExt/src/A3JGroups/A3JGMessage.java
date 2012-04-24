package A3JGroups;


import org.jgroups.Message;

public class A3JGMessage extends Message{


	private Object content;
	//true if is an update message, false otherwise
	private boolean type;
	
	public A3JGMessage(Object content) {
		this.content = content;
	}

	public Object getContent() {
		return content;
	}

	public boolean getType() {
		return type;
	}

	public void setType(boolean type) {
		this.type = type;
	}
	
}
