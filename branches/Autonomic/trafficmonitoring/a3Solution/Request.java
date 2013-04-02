package a3Solution;

import java.io.Serializable;
import java.util.List;

import org.jgroups.Address;

@SuppressWarnings("serial")
public class Request implements Serializable {

	private String nodeID;
	private Address src;
	private List<String> physicalNeighbour;

	public Request(String nodeID, Address src, List<String> physicalNeighbour) {
		this.nodeID = nodeID;
		this.src = src;
		this.physicalNeighbour = physicalNeighbour;
	}

	public String getNodeID() {
		return nodeID;
	}

	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}

	public Address getSrc() {
		return src;
	}

	public void setSrc(Address src) {
		this.src = src;
	}

	public List<String> getPhysicalNeighbour() {
		return physicalNeighbour;
	}

	public void setPhysicalNeighbour(List<String> physicalNeighbour) {
		this.physicalNeighbour = physicalNeighbour;
	}

}
