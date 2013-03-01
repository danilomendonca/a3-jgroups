package node.organizationMiddleware.contextManager.contextDirectories;

import utilities.NodeID;
import utilities.Event;
import utilities.Publisher;

public class RolePosition extends Publisher{

	private String roleType;
	private boolean filled;
	private NodeID agentId;
	private TrafficJamInfo trafficInfo; 
	private NeighbourInfo neighbours; 
	
	public RolePosition(
			TrafficJamInfo trafficInfo,
			NeighbourInfo neighbours,
			String roleType){
		
		this.roleType = roleType;
		this.filled = false;
		this.trafficInfo = trafficInfo;
		this.neighbours = neighbours;
		
	}
	
	/****************
	 *              *
	 *    getters   *
	 *              *
	 ****************/
	
	public NodeID getAgentId() {
		return agentId;
	}
	public boolean isFilled() {
		return filled;
	}
	public NeighbourInfo getNeighbourInfo() {
		return neighbours;
	}
	public String getRoleType() {
		return roleType;
	}
	public TrafficJamInfo getTrafficJamInfo() {
		return trafficInfo;
	}

	/*********************
	 *                   *
	 *      modifiers    *
	 *                   *
	 *********************/
	
	public void setAgentId(NodeID agentId) {
		this.agentId = agentId;
		this.filled = true;
		publish(new Event("setAgentId"));
	}
	
	public void setNeighbours(NeighbourInfo neighbours) {
		this.neighbours = neighbours;
		publish(new Event("setNeighbours"));
	}

	public void setTrafficInfo(TrafficJamInfo trafficInfo) {
		this.trafficInfo = trafficInfo;
		publish(new Event("setTrafficInfo"));
	}

	public RolePosition copy() {
		RolePosition result = new RolePosition(new TrafficJamInfo(), new NeighbourInfo(), this.roleType);
		result.filled = this.filled;
		result.setAgentId(this.agentId.copy());
		return result;
	}	
	
	
}

