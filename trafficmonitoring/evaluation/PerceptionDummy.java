package evaluation;

import java.util.ArrayList;

import node.agentMiddleware.perception.interfaces.NeighbourPerceive;
import node.agentMiddleware.perception.interfaces.Perception;
import node.agentMiddleware.perception.interfaces.TrafficJamInfoPerceive;

import utilities.NodeID;


public class PerceptionDummy implements Perception {

	private int nb;
	private ArrayList<NodeID> neighbours;

	
	public PerceptionDummy(NodeID id){
		int nodeID = Integer.parseInt(id.toString());
		neighbours = new ArrayList<NodeID>();
		NodeID leftNeighbour = new NodeID(nodeID-1);
		NodeID rightNeighbour = new NodeID(nodeID+1);
		neighbours.add(leftNeighbour);
		neighbours.add(rightNeighbour);
	}
	
	public void senseNeighbours(NeighbourPerceive callback) {
		nb++;
		if(nb == 2){
			//initial subscription is done;
			callback.changeNeighbours(neighbours);
		}
	}

	public void senseTrafficJamInfo(TrafficJamInfoPerceive callback) {
		//do nothing
	}

	public void stopNeighbourSensing(NeighbourPerceive callback) {
		//do nothing

	}

	public void stopTrafficJamInfoSensing(TrafficJamInfoPerceive callback) {
		//do nothing

	}

}
