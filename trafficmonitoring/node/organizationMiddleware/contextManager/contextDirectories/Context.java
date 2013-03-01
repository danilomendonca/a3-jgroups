package node.organizationMiddleware.contextManager.contextDirectories;

import java.util.ArrayList;

import utilities.NodeID;
import utilities.Event;
import utilities.Publisher;

public class Context extends Publisher{
	
	private ArrayList<Organization> neighboringOrganizations;
	private Organization organizationActiveOnNode;
	//ID of the node to which this context belongs
	private NodeID nodeID;
	
	public Context(NodeID id){
		neighboringOrganizations = new ArrayList<Organization>();
		nodeID = id;
	}

	public NodeID getPersonalID(){
		return nodeID;
	}
	
	public ArrayList<Organization> getNeighbourOrgs() {
		return neighboringOrganizations;
	}

	public Organization getPersonalOrg() {
		return organizationActiveOnNode;
	}

	public void setNeighbourOrgs(ArrayList<Organization> neighbourOrgs) {
		this.neighboringOrganizations = neighbourOrgs;
		publish(new Event("setNeighbourOrgs"));
	}
	
	public void removePersonalOrg(){
		this.organizationActiveOnNode = null;
		publish(new Event("removePersonalOrg"));
	}
	
	public void setPersonalOrg(Organization personalOrg) {
		this.organizationActiveOnNode = personalOrg;
		publish(new Event("setPersonalOrg"));
	}
	
	//@Pieter
	public void addNeighbourOrganization(Organization org){	
		this.neighboringOrganizations.add(org);
		
		//Check whether the given organization is in fact an updated version of
		// one that is already registered here
		//NOTE: might not do anything on SlaveControllers (who might not receive boundary and agent information)
		for(OrganizationBoundary boundary : this.organizationActiveOnNode.getOrganizationBoundaries()){
			ArrayList<Organization> duplicates = new ArrayList<Organization>();
			for(Organization neighborOrg : this.neighboringOrganizations){
				if(neighborOrg.getAgents().contains(boundary.getExternalAgent()))
					duplicates.add(neighborOrg);
			}
			
			//If more than one organization contains the same node that is a neighbor of the
			// local organization: only retain the one with the highest organization ID
			int highestOrganizationID = 0;
			for(Organization duplicate : duplicates){
				if(duplicate.getId() > highestOrganizationID)
					highestOrganizationID = duplicate.getId();					
			}
			
			//Remove all organizations from the set of neighbor organizations with an id lower than the
			// highest
			for(Organization duplicate : duplicates){
				if(duplicate.getId() < highestOrganizationID)
					this.neighboringOrganizations.remove(duplicate);
			}
		}		
		
		publish(new Event("addNeighbourOrganization"));
	}
	
	public void removeNeighbourOrganization(Organization org){		
		this.neighboringOrganizations.remove(org);
		publish(new Event("removeNeighbourOrganization"));
	}
	
	public void removeNeighbours(ArrayList<Organization> neighbours){
		this.neighboringOrganizations.remove(neighbours);
		publish(new Event("removeNeighbours"));
	}
	
}
