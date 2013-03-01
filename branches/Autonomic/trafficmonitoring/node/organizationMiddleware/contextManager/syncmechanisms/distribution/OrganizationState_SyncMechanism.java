package node.organizationMiddleware.contextManager.syncmechanisms.distribution;

import node.agentMiddleware.communication.interfaces.SendInterface;
import node.organizationMiddleware.contextManager.contextDirectories.Context;
import node.organizationMiddleware.contextManager.contextDirectories.Organization;
import node.organizationMiddleware.contextManager.contextDirectories.RolePosition;

import utilities.Event;
import utilities.MessageBuffer;
import utilities.Subscriber;

public class OrganizationState_SyncMechanism implements Subscriber, Runnable{

	private Context ctx;
	private MessageBuffer messageBuffer;
	private SendInterface agentLayer;
	
	public OrganizationState_SyncMechanism(Context ctx, 
									 SendInterface agentLayer,
									 MessageBuffer msgBuffer){
		this.ctx = ctx;
		this.messageBuffer = msgBuffer;
		this.messageBuffer.subscribeOnOrganizationInfoMessages();
		this.agentLayer = agentLayer;
		
		ctx.getPersonalOrg().subscribe("changeAgents", this);
		ctx.getPersonalOrg().subscribe("changeTrafficStateOfOrganization", this);

		sendInfoToAllOrganizations();
	}


	public void publish(Event e) {
		if(e.getEventType().equals("changeAgents") )
			agentsStateChange();
		else if(e.getEventType().equals("changeTrafficStateOfOrganization")) 
			trafficStateChange();
	}

	public void run() {
		while(messageBuffer.hasOrganizationInfoAsNextMessage()){
			Organization newNeighbourState = messageBuffer.receiveOrganizationInfo();
						
			//check if orgInfo is new data about an already existing neighbour 
			Organization toRemove = null;
			boolean remove = false;
			for(Organization neighbour : this.ctx.getNeighbourOrgs()){
				if(neighbour.getId() == newNeighbourState.getId()){
					toRemove = neighbour;
					remove = true;
					break;
				}
			}			
			if(remove){
				this.ctx.removeNeighbourOrganization(toRemove);
				this.ctx.addNeighbourOrganization(newNeighbourState);
			}
		}	
	}
	
	/********************
	 * 
	 *  helper functions
	 * 
	 ********************/
	
	private void trafficStateChange() {
		sendInfoToAllOrganizations();		
	}

	private void agentsStateChange() {
		sendInfoToAllOrganizations();
	}

	public void sendInfoToAllOrganizations(){		
		//create the necessary information to send
		Organization orgInfo = ctx.getPersonalOrg().clone();
		
		for(Organization neighbour : ctx.getNeighbourOrgs()){
			//System.out.println("OrganizationState_SyncMechanism -> sendOrganizationInfo | " + ctx.getPersonalID() + " sends organization info to " + neighbour.getMasterID());
			agentLayer.sendOrganizationInfo(orgInfo, neighbour.getMasterID().toString());		
			//System.out.println(ctx.getPersonalID() + " verstuurt Organization Data naar " +neighbour.getMasterID());
			//System.out.println("\t velocity : "+ temp_velocity + " ; density " + temp_density);
		}
	}

	public void stop() {
		ctx.getPersonalOrg().unsubscribe("changeAgents", this);
		ctx.getPersonalOrg().unsubscribe("changeTrafficStateOfOrganization", this);
		messageBuffer.unsubscribeOnOrganizationInfoMessages();
	}
}
