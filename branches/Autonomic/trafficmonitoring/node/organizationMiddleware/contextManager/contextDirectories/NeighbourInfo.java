package node.organizationMiddleware.contextManager.contextDirectories;

import java.util.ArrayList;

import utilities.NodeID;
import utilities.Event;
import utilities.Publisher;

public class NeighbourInfo extends Publisher{

	private ArrayList<NodeID> neighbours;
	
	public NeighbourInfo(){
		neighbours = new ArrayList<NodeID>();
	}
	
	/******************
	 *                *
	 *    getters     *
	 *                *
	 ******************/

	public ArrayList<NodeID> getNeighbours() {
		return neighbours;
	}



	/****************** 
	 *                * 
	 *    modifiers   *
	 *                *
	 ******************/
	
	/**
	 * deep copy
	 */
	public NeighbourInfo copy() {
		NeighbourInfo result = new NeighbourInfo();
		ArrayList<NodeID> newAgents = new ArrayList<NodeID>();
		for(NodeID neighbour : getNeighbours()){
			newAgents.add(neighbour.copy());
		}
		result.addNeighbours(newAgents);
		return result;
	}


	public void addNeighbours(ArrayList<NodeID> neighbours) {
		this.neighbours = neighbours;
		publish(new Event("addNeighbours"));	
	}

	public void removeNeighbours() {
		this.neighbours.clear();
		publish(new Event("removeNeighbours"));	
	}


	/**
	 * easymock noodzaak
	 * @return
	 */
	public boolean equals(Object o){
		try{
			NeighbourInfo info = (NeighbourInfo) o;
			ArrayList<NodeID> neighbours = info.neighbours; 
			if(neighbours.size() != this.neighbours.size())
				return false;
			for(NodeID neighbour : neighbours){
				if(!hasAsNeighbour(neighbour))
					return false;
			}
		}catch (ClassCastException e){
			return false;
		}
		return true;
	}
		
	private boolean hasAsNeighbour(NodeID neighbour){
		return getNeighbours().contains(neighbour);
	}
	
}
