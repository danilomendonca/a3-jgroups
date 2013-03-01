package node.organizationMiddleware.organizationController.evolutionLaws;

import java.util.ArrayList;

import node.organizationMiddleware.contextManager.contextDirectories.NeighbourInfo;
import node.organizationMiddleware.contextManager.contextDirectories.Organization;
import node.organizationMiddleware.contextManager.contextDirectories.RolePosition;
import node.organizationMiddleware.contextManager.contextDirectories.TrafficJamInfo;

import utilities.NodeID;
import utilities.GUIDManager;


public class MergeLaw{
	
	private GUIDManager guidManager;

	public MergeLaw(GUIDManager guidManager){
		this.guidManager = guidManager;
	}
	
	public Organization mergeOrganisations(Organization org1, Organization org2){
		ArrayList<NodeID> agents = new ArrayList<NodeID>();
		
		for(RolePosition rp : org1.getFilledRolePositions()){
			agents.add(rp.getAgentId());
		}
		for(RolePosition rp : org2.getFilledRolePositions()){
			agents.add(rp.getAgentId());
		}
		
//		ArrayList<Integer> history = new ArrayList<Integer>();
//		history.addAll(org1.getHistoryByRef());
//		history.addAll(org2.getHistoryByRef());
//		history.add(org1.getId());
//		history.add(org2.getId());
		
		//add the id; history; masterID; trafficJamTreshold
		//organization boundaries and everyAgentSeesCongestion should remain "uninitialised"
		Organization result = new Organization(org1.getTrafficJamTreshhold(),
											   this.guidManager.getNextID(), 
											   //history, 
											   org1.getMasterID());
		
		//add the agents
		result.changeAgents(agents);
		
		//create the rolepositions
		for(NodeID agent : agents){
			RolePosition rp = new RolePosition(new TrafficJamInfo(), new NeighbourInfo(),"dummy");
			//System.out.println("de merge regel voegt volgende agent toe aan de organizatie "+agent);
			rp.setAgentId(agent);
			result.addFilledRolePosition(rp);
		}
	
		return result;
	}
	
			
	/**
	 * @pre org1 and org2 are neighbours
	 * 
	 */
	public boolean mustBeMerged(Organization org1, Organization org2){
		//@Pieter
		if(this.mergingBlocked)
			return false;
		
		
		
		return org1.everyAgentSeesCongestion().isTrue() && org2.everyAgentSeesCongestion().isTrue();
	}
	
	
	
	
	/**************************	 
	 * 
	 *	Self-Healing 
	 *
	 **************************/	
	
	//@Pieter
	/**
	 * Block merging altogether
	 */
	public void blockMerging(){
		this.mergingBlocked = true;
	}
	public void unblockMerging(){
		this.mergingBlocked = false;
	}
	private boolean mergingBlocked = false;
	
}
