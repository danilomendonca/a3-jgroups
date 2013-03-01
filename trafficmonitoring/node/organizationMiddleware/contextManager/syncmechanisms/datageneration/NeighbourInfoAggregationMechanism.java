package node.organizationMiddleware.contextManager.syncmechanisms.datageneration;

import java.util.ArrayList;

import node.organizationMiddleware.contextManager.contextDirectories.NeighbourInfo;
import node.organizationMiddleware.contextManager.contextDirectories.Organization;
import node.organizationMiddleware.contextManager.contextDirectories.OrganizationBoundary;
import node.organizationMiddleware.contextManager.contextDirectories.RolePosition;




import utilities.Event;
import utilities.Subscriber;
import utilities.NodeID;

public class NeighbourInfoAggregationMechanism implements Subscriber{

	private Organization organization;
	private ArrayList<NeighbourInfo> currentlySubscribedOn;
	
	public NeighbourInfoAggregationMechanism(Organization org){
		this.organization = org;
		this.currentlySubscribedOn = new ArrayList<NeighbourInfo>();
		recalculateOrganizationBoundaries();
		organization.subscribe("addFilledRolePosition", this);
		organization.subscribe("removeFilledRolePosition", this);
		subscribeOnNewNeighbourInfo();
	}
	
	public void stop(){
		organization.unsubscribe("addFilledRolePosition", this);
		organization.unsubscribe("removeFilledRolePosition", this);
		unsubscribeOnOldNeighbourInfo();
	}
	
	public void publish(Event e) {
		if(e.getEventType().equals("addFilledRolePosition") ||
				e.getEventType().equals("removeFilledRolePosition")){
			unsubscribeOnOldNeighbourInfo();
			subscribeOnNewNeighbourInfo();
			recalculateOrganizationBoundaries();
		}else if(e.getEventType().equals("addNeighbours")){
			//System.out.println("NeighbourInfoAggregationMech -> publish (addNeighbours is event type)");
			recalculateOrganizationBoundaries();
			
		}
						
	}
	
	/***************************
	 * 						   *
	 *   AggregationMechanism  *
	 *                         *
	 ***************************/
	
	private void recalculateOrganizationBoundaries() {
		
		ArrayList<RolePosition> filledRolePositions = organization.getFilledRolePositions();
		ArrayList<OrganizationBoundary> newOrganizationBoundaries = new ArrayList<OrganizationBoundary>();
		for(RolePosition filledRolePosition : filledRolePositions){
			NeighbourInfo neighbourInfo = filledRolePosition.getNeighbourInfo();
			for(NodeID neighbour : neighbourInfo.getNeighbours()){
				if(!organization.contains(neighbour)){
					OrganizationBoundary orgBoundary = new OrganizationBoundary();
					orgBoundary.setInternalAgent(filledRolePosition.getAgentId());
					orgBoundary.setExternalAgent(neighbour);
					newOrganizationBoundaries.add(orgBoundary);
				}	
			}
			
		}
		//System.out.println("NeighbourInfoAggregationMechanism -> recalculateOrganizationBoundaries on node " +this.organization.getMasterID());
		organization.changeOrganizationBoundaries(newOrganizationBoundaries);
	}
	
	/****************************
	 *                          *
	 * 		helper function     *
	 * 						    *
	 ****************************/
	
	private void subscribeOnNewNeighbourInfo(){
		ArrayList<RolePosition> filledRolePositions = organization.getFilledRolePositions();
		for(RolePosition filledRolePosition : filledRolePositions){
			NeighbourInfo neighbourInfo = filledRolePosition.getNeighbourInfo();
			neighbourInfo.subscribe("addNeighbours", this);
			currentlySubscribedOn.add(neighbourInfo);
		}
	}
	
	private void unsubscribeOnOldNeighbourInfo(){
		for(NeighbourInfo neighbourInfo : currentlySubscribedOn){
			neighbourInfo.unsubscribe("addNeighbours", this);
		}
		currentlySubscribedOn.clear();
		
	}
	
}
