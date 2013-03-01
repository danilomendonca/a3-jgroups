package node.organizationMiddleware.contextManager.syncmechanisms.datageneration;

import java.util.ArrayList;

import node.agentMiddleware.perception.interfaces.NeighbourPerceive;
import node.agentMiddleware.perception.interfaces.Perception;
import node.organizationMiddleware.contextManager.contextDirectories.NeighbourInfo;


import utilities.NodeID;

public class NeighbourInfoPerceptionMechanism implements NeighbourPerceive{
	
	private NeighbourInfo neighbours;
	private Perception perceptionLayer;
	
	public NeighbourInfoPerceptionMechanism(Perception perceptionLayer, NeighbourInfo neighbours){
		this.neighbours = neighbours;	
		this.perceptionLayer = perceptionLayer;
		perceptionLayer.senseNeighbours(this);
	}
	
	public void changeNeighbours(ArrayList<NodeID> neighbours) {
		//@Pieter
		this.neighbours.removeNeighbours();
		
		
		
		this.neighbours.addNeighbours(neighbours);
	}

	public void stop() {
		perceptionLayer.stopNeighbourSensing(this);
		
	}

}
