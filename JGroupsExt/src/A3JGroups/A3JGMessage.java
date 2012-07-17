package A3JGroups;

import java.io.Serializable;
import java.util.List;

import org.jgroups.Address;


public class A3JGMessage implements Serializable{

	private static final long serialVersionUID = 1L;
	//true if is an update message, false otherwise
	private boolean type;
	private Object content;
	public List<Address> dest = null;
	public String valueID;
	
	public A3JGMessage(String valueID, Object content) {
		super();
		this.valueID = valueID;
		this.content = content;
	}
	
	public A3JGMessage(String valueID) {
		super();
		this.valueID = valueID;
	}
	
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

	public String getValueID() {
		return valueID;
	}

	public void setValueID(String valueID) {
		this.valueID = valueID;
	}

	public void setDest(List<Address> dest) {
		this.dest = dest;
	}

	@Override
	public String toString() {
		return "[content=" + content + "]";
	}

}
