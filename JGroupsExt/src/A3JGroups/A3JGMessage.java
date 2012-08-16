package A3JGroups;

import java.io.Serializable;
import java.util.List;

import org.jgroups.Address;


/**
 * A3JGMessage identifies the type of messages used in A3 for the exchange of information 
 * between supervisors and followers.
 * 
 * A A3JGMessage instance is created using one of the two public constructors.
 * 
 * @author bett.marco88@gmail.com
 *
 */
public class A3JGMessage implements Serializable{

	private static final long serialVersionUID = 1L;
	//true if is an update message, false otherwise
	private boolean type;
	private Object content;
	public List<Address> dest = null;
	public String valueID;
	
	/**
	 * Constructor used for create an A3JGMessage, by defining the identifier and the content.
	 * 
	 * @param valueID 
	 * 			Is the string used to identify the message exchanged. More messages can have 
	 * 			the same valueID.
	 * @param content
	 * 			Is the Object exchanged between the supervisor and the followers.
	 */
	public A3JGMessage(String valueID, Object content) {
		super();
		this.valueID = valueID;
		this.content = content;
	}
	
	/**
	 * Constructor used for create an A3JGMessage, by defining the identifier.
	 * 
	 * @param valueID 
	 * 			Is the string used to identify the message exchanged. More messages can have 
	 * 			the same valueID.
	 */
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
