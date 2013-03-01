package node.selfAdaptationSystem.coordination.protocols.election;

import java.util.ArrayList;

import utilities.NodeID;

public class ElectionInformation {

	public ElectionInformation(NodeID newlyElectedMaster, ArrayList<NodeID> otherRemainingSlaves){
		this.newlyElectedMaster = newlyElectedMaster;
		this.otherRemainingSlaves = otherRemainingSlaves;
	}
	
	public NodeID getNewlyElectedMaster(){
		return this.newlyElectedMaster;
	}
	
	public ArrayList<NodeID> getOtherRemainingSlaves(){
		return this.otherRemainingSlaves;
	}
	
	private NodeID newlyElectedMaster;
	
	private ArrayList<NodeID> otherRemainingSlaves;
}
