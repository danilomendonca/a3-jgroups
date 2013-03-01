package node.selfAdaptationSystem.mapeManager.neighborNodeFailure.messages;

import java.util.ArrayList;

import utilities.NodeID;

public class M42Input {
	
	
	public void setDeadNeighborNodes(ArrayList<NodeID> deadNeighbors){
		this.deadNeighborNodes = deadNeighbors;
	}
	
	public ArrayList<NodeID> getDeadNeighborNodes(){
		return this.deadNeighborNodes;
	}

	ArrayList<NodeID> deadNeighborNodes = new ArrayList<NodeID>();
	
	
	
	public void setNewNeighborNodesFound(){
		this.newNeighborNodesFound = true;
	}
	
	public boolean newNeighborNodesFound(){
		return this.newNeighborNodesFound;
	}
	
	private boolean newNeighborNodesFound = false;
	
}
