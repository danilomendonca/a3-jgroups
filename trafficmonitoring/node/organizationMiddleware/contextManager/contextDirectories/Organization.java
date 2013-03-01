package node.organizationMiddleware.contextManager.contextDirectories;

import java.util.ArrayList;
import utilities.NodeID;
import utilities.Event;
import utilities.Publisher;
import utilities.ThreevaluedLogic;


public class Organization extends Publisher {

	private ArrayList<RolePosition> openRolePositions;
	private ArrayList<RolePosition> filledRolePositions;
	//one agent per node, so nodeID refers to the agent
	private ArrayList<NodeID> agents;
	private int id;
	private NodeID masterControllerID;
	
	//application specific
	private ArrayList<OrganizationBoundary> organizationBoundaries; 
	private ThreevaluedLogic everyAgentSeesCongestion;
	private float trafficJamTreshold;
	
	public Organization(float trafficJamTreshold, int id, /*ArrayList<Integer> history,*/ NodeID masterID ){
		this.agents = new ArrayList<NodeID>();
		this.openRolePositions = new ArrayList<RolePosition>();
		this.filledRolePositions = new ArrayList<RolePosition>();
		this.organizationBoundaries = new ArrayList<OrganizationBoundary>();
		this.trafficJamTreshold = trafficJamTreshold;
		this.id = id;
		//this.organizationHistory = history;
		this.masterControllerID = masterID;
		this.everyAgentSeesCongestion = new ThreevaluedLogic();
	}

	/****************
	 *              *
	 *    getters   *
	 *              *
	 ****************/
	
	public NodeID getMasterID(){
		return masterControllerID;
	}
	
	public int getId(){
		return id;
	}
	
	public ArrayList<NodeID> getAgents(){
		return agents;
	}
	
	public ArrayList<RolePosition> getFilledRolePositions(){
		return filledRolePositions;
	}

	public ArrayList<RolePosition> getOpenRolePositions() {
		return openRolePositions;
	}

	public ArrayList<OrganizationBoundary> getOrganizationBoundaries() {
		return organizationBoundaries;
	}
	
	public ThreevaluedLogic everyAgentSeesCongestion() {
		return everyAgentSeesCongestion;
	}
			
	public float getTrafficJamTreshhold() {
		return trafficJamTreshold;
	}
	
//	public ArrayList<Integer> getHistory(){
//		return (ArrayList<Integer>) this.organizationHistory.clone();
//	}
//	
//	public ArrayList<Integer> getHistoryByRef(){
//		return (ArrayList<Integer>) this.organizationHistory;
//	}
	
	/*****************
	 *               *
	 *    modifiers  * 
	 *               *
	 *****************/
	
	//@Pieter
	public synchronized void changeMaster(NodeID newMaster){
		this.masterControllerID = newMaster;
	}
	
	public synchronized void changeAgents(ArrayList<NodeID> agents){		
		this.agents = agents;
		publish(new Event("changeAgents"));
	}
	
	public synchronized void addOpenRolePosition(RolePosition rolePosition){
		openRolePositions.add(rolePosition);
		publish(new Event("addOpenRolePosition"));
	}
	
	public synchronized void removeOpenRolePosition(RolePosition rolePosition){
		openRolePositions.remove(rolePosition);
		publish(new Event("removeOpenRolePosition"));
	}
	
	public synchronized void addFilledRolePosition(RolePosition rolePosition){
		filledRolePositions.add(rolePosition);
		publish(new Event("addFilledRolePosition"));
	}
	
	public synchronized void removeFilledRolePosition(RolePosition rolePosition){
		filledRolePositions.remove(rolePosition);
		publish(new Event("removeFilledRolePosition"));
	}
	
	//@Pieter
	public synchronized void clearFilledRolePositions(){
		this.filledRolePositions.clear();
	}

	public synchronized void changeTrafficStateOfOrganization(
			ThreevaluedLogic everyAgentSeesCongestion) {
		this.everyAgentSeesCongestion = everyAgentSeesCongestion;
		publish(new Event("changeTrafficStateOfOrganization"));
	}

	public synchronized void changeOrganizationBoundaries(
			ArrayList<OrganizationBoundary> boundaries) {
		//System.out.println("Organisation -> changeOrganizationBoundaries");
		this.organizationBoundaries = boundaries;
		publish(new Event("changeOrganizationBoundaries"));
	}
	
	/**************
	 *            *
	 *    other   *
	 *            *
	 **************/

	public boolean contains(NodeID agent) {
		if(agent == null)
			throw new IllegalArgumentException("Organization.contains(AgentID agent) -> passed argument " +
					"was a null reference");
		for(NodeID agentId : agents){
			if(agentId.getId() == agent.getId())
				return true;
		}
		return false;
	}
	
//	//TODO test!
//	public boolean isAncestorOf(Organization neighbour) {
//		ArrayList<Integer> history = neighbour.getHistoryByRef();
//		for(int id : history){
//			if(getId() == id)
//				return true;
//		}
//		return false;
//	}
	
	public ArrayList<NodeID> copyAgents() {
		ArrayList<NodeID> result = new ArrayList<NodeID>();
		for(NodeID agent : agents){
			NodeID newAgent = agent.copy();
			result.add(newAgent);
		}
		return result;
	}

	public ArrayList<OrganizationBoundary> copyOrganizationBoundaries() {
		ArrayList<OrganizationBoundary> result = new ArrayList<OrganizationBoundary>();
		for(OrganizationBoundary boundary : organizationBoundaries){
			OrganizationBoundary newBoundary = boundary.copy();
			result.add(newBoundary);
		}
		return result;
	}
	
	//@Pieter
	public Organization clone(){
		Organization clone = new Organization(this.getTrafficJamTreshhold(), this.getId(), this.getMasterID());
		clone.changeAgents(this.copyAgents());
		clone.changeTrafficStateOfOrganization(this.everyAgentSeesCongestion());
		clone.changeOrganizationBoundaries(this.copyOrganizationBoundaries());		
		
		for(RolePosition rp : this.getFilledRolePositions()){
			RolePosition rpCopy = rp.copy();
			
			//Set traffic jam info
			rpCopy.getTrafficJamInfo().setAvgVelocity(rp.getTrafficJamInfo().getAvgVelocity());
			rpCopy.getTrafficJamInfo().setDensity(rp.getTrafficJamInfo().getDensity());
			rpCopy.getTrafficJamInfo().setIntensity(rp.getTrafficJamInfo().getIntensity());
			
			//Set neighbor info
			rpCopy.setNeighbours(rp.getNeighbourInfo().copy());			
			
			clone.addFilledRolePosition(rpCopy);
		}
		
		return clone;
	}
	
}
