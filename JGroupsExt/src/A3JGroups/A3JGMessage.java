package A3JGroups;


import java.io.Serializable;

public class A3JGMessage implements Serializable{

	private static final long serialVersionUID = 1L;
	//true if is an update message, false otherwise
	private boolean type;
	private Object content;

	public boolean getType() {
		return type;
	}

	public void setType(boolean type) {
		this.type = type;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

}
