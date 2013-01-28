package testGreenhouse2;

import java.io.Serializable;

import org.jgroups.Address;

@SuppressWarnings("serial")
public class Request implements Serializable {

	private String nodeID;
	private Address src;
	private int representedNodesNumber;

	public Request(String nodeID, Address src, int representedNodesNumber) {
		this.nodeID = nodeID;
		this.src = src;
		this.representedNodesNumber = representedNodesNumber;
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

	public int getRepresentedNodesNumber() {
		return representedNodesNumber;
	}

	public void setRepresentedNodesNumber(int representedNodesNumber) {
		this.representedNodesNumber = representedNodesNumber;
	}

}
