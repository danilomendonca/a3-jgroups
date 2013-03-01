package node.organizationMiddleware.contextManager.syncmechanisms.distribution;

import java.util.ArrayList;
import java.util.HashSet;

import node.agentMiddleware.communication.interfaces.SendInterface;
import node.organizationMiddleware.contextManager.contextDirectories.Context;
import node.organizationMiddleware.contextManager.contextDirectories.Organization;
import node.organizationMiddleware.contextManager.contextDirectories.OrganizationBoundary;
import node.organizationMiddleware.contextManager.contextDirectories.RolePosition;


import utilities.NodeID;
import utilities.Event;
import utilities.MessageBuffer;
import utilities.Subscriber;

public class Neighbours_SyncMechanism implements Runnable, Subscriber{

	private Context orgsContext;
	private Organization personalOrganization;
	private SendInterface agentLayer;
	private MessageBuffer messageBuffer;
	private ArrayList<NodeID> cache;
	
	public Neighbours_SyncMechanism(Organization personalOrganization,
									SendInterface agentLayer,
									MessageBuffer msgBuffer,
									Context ctx){
		this.agentLayer = agentLayer;
		this.personalOrganization = personalOrganization;
		this.messageBuffer = msgBuffer;
		this.orgsContext = ctx;
		
		this.personalOrganization.subscribe("changeOrganizationBoundaries", this);
		this.messageBuffer.subscribeOnPings();
		this.messageBuffer.subscribeOnPongs();
		this.messageBuffer.subscribeOnMasterIDReplies();
		
		this.cache = new ArrayList<NodeID>();
		removeInvalidNeighbours();
		sendOutRequestsForMasterIDs();
	}
	
	private void removeInvalidNeighbours(){
		ArrayList<OrganizationBoundary> boundaries = this.personalOrganization.getOrganizationBoundaries();
		
		ArrayList<Organization> neighboursToBeRemoved = new ArrayList<Organization>();
		
		//remove organizations that are no longer neighbours
		for(Organization neighbour : orgsContext.getNeighbourOrgs()){
			boolean isNeighbour = false;
			for(OrganizationBoundary boundary : boundaries){
				if(neighbour.contains(boundary.getExternalAgent()))
					isNeighbour = true;
			}
			if(!isNeighbour)
				neighboursToBeRemoved.add(neighbour);
		}
		
		for(Organization toBeRemovedNeighour : neighboursToBeRemoved){
			orgsContext.removeNeighbourOrganization(toBeRemovedNeighour);			
		}
	}
	
	private void sendOutRequestsForMasterIDs(){
		ArrayList<OrganizationBoundary> boundaries = this.personalOrganization.getOrganizationBoundaries();
		
		//find new neighbours
		for(OrganizationBoundary boundary : boundaries){
			boolean mustBeSend = true;
			
			//System.out.println(orgsContext.getPersonalID() + " : cache contains "+boundary.getExternalAgent()+cache.contains(boundary.getExternalAgent()));
			if(cache.contains(boundary.getExternalAgent())){
				mustBeSend = false;
			}
			
			for(Organization neighbour : orgsContext.getNeighbourOrgs()){
				if(neighbour.contains(boundary.getExternalAgent())){
					mustBeSend = false;
				}
			}
			if(mustBeSend){
				NodeID neighbouringAgent = boundary.getExternalAgent();
				//System.out.println(orgsContext.getPersonalID() + " sends out request to "+neighbouringAgent);
				agentLayer.sendMasterIDRequest(personalOrganization.getMasterID(),neighbouringAgent.toString());
				//System.out.println(orgsContext.getPersonalID() + " add to cache "+neighbouringAgent);
				cache.add(neighbouringAgent);
			}
		}
	}

	public void run() {
		while(messageBuffer.hasMasterIDReplyAsNextMessage()){
			sendPingToMaster(messageBuffer.receiveMasterIDReply());
		}
		while(messageBuffer.hasPingAsNextMessage()){
			Organization newNeighbour = messageBuffer.receivePing();
			//System.out.println(orgsContext.getPersonalID() +" receives ping from " +newNeighbour.getMasterID());
			updateCache(newNeighbour);
			addNeighbour(newNeighbour);
			sendPongToMaster(newNeighbour.getMasterID());
		}
		while(messageBuffer.hasPongAsNextMessage()){
			Organization newNeighbour = messageBuffer.receivePong();
			//System.out.println(orgsContext.getPersonalID() +" receives pong from " +newNeighbour.getMasterID());
			updateCache(newNeighbour);
			addNeighbour(newNeighbour);			
		}
	}
	
	private void updateCache(Organization newNeighbour) {
		for(NodeID agent : newNeighbour.getAgents()){
			//System.out.println(orgsContext.getPersonalID() + " removes from cache "+agent);
			this.cache.remove(agent);
		}
	}

	private void sendPingToMaster(NodeID masterID){
		Organization orgInfo = new Organization(personalOrganization.getTrafficJamTreshhold(),
													personalOrganization.getId(),
													//personalOrganization.getHistory(),
													personalOrganization.getMasterID().copy());
		orgInfo.changeAgents(copy(personalOrganization.getAgents()));
		orgInfo.changeTrafficStateOfOrganization(personalOrganization.everyAgentSeesCongestion());
		
		for(RolePosition rp : personalOrganization.getFilledRolePositions()){
			orgInfo.addFilledRolePosition(rp.copy());
		}
		//orgInfo.changeOrganizationBoundaries(personalOrganization.getOrganizationBoundaries());
		agentLayer.sendPing(orgInfo, masterID.toString());
	}
	
	private void sendPongToMaster(NodeID masterID){
		Organization orgInfo = new Organization(personalOrganization.getTrafficJamTreshhold(),
													personalOrganization.getId(),
													//personalOrganization.getHistory(),
													personalOrganization.getMasterID().copy());
		orgInfo.changeAgents(copy(personalOrganization.getAgents()));
		orgInfo.changeTrafficStateOfOrganization(personalOrganization.everyAgentSeesCongestion());
		for(RolePosition rp : personalOrganization.getFilledRolePositions()){
			orgInfo.addFilledRolePosition(rp.copy());
		}
		//orgInfo.changeOrganizationBoundaries(personalOrganization.getOrganizationBoundaries());
		agentLayer.sendPong(orgInfo, masterID.toString());
	}

	private void addNeighbour(Organization newNeighbour) {
		if(isValidNeighbour(newNeighbour)){
			//check if newNeighbour is already present in the collection of neighbours
			//if so nothing should happen
			//System.out.println("\t"+orgsContext.getPersonalID()+" executes addNeighbour for "+newNeighbour.getMasterID());
			
			for(Organization neighbour : orgsContext.getNeighbourOrgs()){
				if(neighbour.getId() == newNeighbour.getId()){
					return;
				}
			}			
			//System.out.println("\t"+orgsContext.getPersonalID()+" adds "+newNeighbour.getMasterID());
			orgsContext.addNeighbourOrganization(newNeighbour);
		}
	}

	private boolean isValidNeighbour(Organization newNeighbour) {
		ArrayList<OrganizationBoundary> boundaries = this.personalOrganization.getOrganizationBoundaries();

		for(OrganizationBoundary boundary : boundaries){
			if(newNeighbour.contains(boundary.getExternalAgent()))
					return true;
		}
		
		return false;
	}

	private ArrayList<NodeID> copy(ArrayList<NodeID> agents) {
		ArrayList<NodeID> result = new ArrayList<NodeID>();
		for(NodeID agent : agents){
			NodeID newAgent = agent.copy();
			result.add(newAgent);
		}
		return result;
	}

	public void publish(Event e) {
		
		if(e.getEventType().equals("changeOrganizationBoundaries")){
			//System.out.println("Neighbours_SyncMechanism -> publish aangeroepen op node : "+orgsContext.getPersonalID());
			removeInvalidNeighbours();
			sendOutRequestsForMasterIDs();
		}
//		else if(e.getEventType().equals("addNeighbourOrganization"))	
//			removeInvalidNeighbours();
	}
	
	public void stop(){
		this.personalOrganization.unsubscribe("changeOrganizationBoundaries", this);
		this.messageBuffer.unsubscribeOnMasterIDReplies();
		this.messageBuffer.unsubscribeOnPings();
		this.messageBuffer.unsubscribeOnPongs();
	}
	
	
	
}
