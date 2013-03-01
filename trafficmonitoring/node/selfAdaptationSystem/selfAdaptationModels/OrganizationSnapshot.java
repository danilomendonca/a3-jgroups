package node.selfAdaptationSystem.selfAdaptationModels;

import java.util.ArrayList;

import utilities.NodeID;

public class OrganizationSnapshot {
	
	private NodeID masterNode;
	private ArrayList<NodeID> nodes;
	private int organizationId;
	
	public OrganizationSnapshot(int organizationId, NodeID masterNode, ArrayList<NodeID> nodes) {
		this.organizationId = organizationId;
		this.masterNode = masterNode;
		this.nodes = nodes;
	}
	
	public NodeID getMasterNode() {
		return masterNode;
	}
	
	public void setMasterNode(NodeID newMaster){
		this.masterNode = newMaster;
	}
	
	public ArrayList<NodeID> getNodes() {
		return nodes;
	}
	
	public int getOrganizationId() {
		return organizationId;
	}
}
