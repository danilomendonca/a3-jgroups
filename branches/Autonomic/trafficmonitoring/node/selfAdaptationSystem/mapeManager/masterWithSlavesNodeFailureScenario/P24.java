package node.selfAdaptationSystem.mapeManager.masterWithSlavesNodeFailureScenario;

import java.util.ArrayList;

import utilities.NodeID;
import node.organizationMiddleware.contextManager.contextDirectories.Organization;
import node.organizationMiddleware.contextManager.contextDirectories.OrganizationBoundary;
import node.selfAdaptationSystem.coordination.protocols.notification.NotificationProtocolHandler;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.PlanComputation;
import node.selfAdaptationSystem.selfAdaptationModels.OrganizationSnapshot;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class P24 extends PlanComputation implements MasterWithSlavesNodeFailureComputation {

	public P24(MasterWithSlavesNodeFailureScenario scenario, SelfAdaptationModels models) {
		super(scenario, models);
	}
	
	
	/**************************	 
	 * 
	 *	Execution
	 *
	 **************************/	
	
	@Override
	/**
	 * As a slave to this scenario's subject master node:
	 * - await information from new master node on when to restart base-level operations
	 */
	public void executeOnSlaveNode(){
		//Initialize CoordinationHandler (role of NOTIFICATION_RECEIVER) if necessary
		if(this.notificationHandler == null)
			this.notificationHandler = new NotificationProtocolHandler<Organization>(this);
		
		this.notificationHandler.execute();		
		
		//Check if notification message has been received;
		if(this.notificationHandler.hasCompleted()){
			//Move on to next computation: E24
			this.transition("E24");
		}
		else {
			//Stay in this computation for now
			return;
		}
	}
	
	@Override
	/**
	 * As the newly elected master of the organization of which this scenario's subject was the original master:
	 * - send information to slaves of this organization and to the master of all neighboring organizations to
	 * notify them of the successful healing of this organization: base-level operations can therefore be resumed.
	 */
	public void executeOnNewMasterNode(){		
		//Implementation-specific: Wait for the base-level system to gather information for all neighboring organizations,
		// following the adaptation of the organization boundaries in E23.
		for(OrganizationBoundary orgBoundary : 
				this.getSelfAdaptationModels().getFullLocalOrganizationInformation().getOrganizationBoundaries()){
			
			boolean neighborOrgFound = false;
			for(OrganizationSnapshot neighborOrg : this.getSelfAdaptationModels().getNeighborOrganizations()){
				if(neighborOrg.getNodes().contains(orgBoundary.getExternalAgent())){
					neighborOrgFound = true;
				}
			}
			
			if(!neighborOrgFound){
				//Up-to-date organization information on this particular neighbor organization has not been 
				// received yet; check again in the next execution cycle
				return;
			}			
		}		
		
		if(this.getSelfAdaptationModels().getNeighborOrganizations().size() == 0){
			//If no such information has been received yet by the base-level system: retry in the next execution cycle
			return;
		}
		
		//Prepare list of notification receivers
		ArrayList<NodeID> notificationReceivers = new ArrayList<NodeID>();
		// Add all slave nodes of this organization
		for(NodeID organizationAgent : this.getSelfAdaptationModels().getLocalOrganization().getNodes()){
			if(!organizationAgent.equals(this.getSelfAdaptationModels().getHostNode()))
				notificationReceivers.add(organizationAgent);
		}
		// Add masters of all neighboring organizations
		for(OrganizationSnapshot neighborOrg : this.getSelfAdaptationModels().getNeighborOrganizations()){			
			notificationReceivers.add(neighborOrg.getMasterNode());
		}
		
		//Notify all receivers. Send along the updated organization information of this newly elected master
		// as the notification payload
		Organization payload = this.getSelfAdaptationModels().getFullLocalOrganizationInformation();
		this.notificationHandler = new NotificationProtocolHandler<Organization>(this, notificationReceivers, payload);		
		
		this.notificationHandler.execute();
		
		//Move on to next computation: E24
		this.transition("E24");
	}
	
	@Override
	/**
	 * As a master of an organization adjacent to the organization of this scenario's subject master node:
	 * - await information from new master node on when to restart base-level operations
	 */
	public void executeOnNeighborMasterNode() throws ComputationExecutionException{
		//Initialize CoordinationHandler (role of NOTIFICATION_RECEIVER) if necessary
		if(this.notificationHandler == null)
			this.notificationHandler = new NotificationProtocolHandler<Organization>(this);
		
		this.notificationHandler.execute();		
		
		//Check for notification message
		if(this.notificationHandler.hasCompleted()){
			//Move on to next computation: E24
			// Provide this computation with updated information
			// of the neighboring organization (sent as notification payload)
			//Note: the vital information inside this Organization information for the new neighbor
			// organization consists of the list of agents, plus the up-to-date traffic congestion information
			Organization updatedNeighborOrgInfo = this.notificationHandler.getNotificationPayload();
			
			//Move on to next computation: E24
			this.transition("E24", updatedNeighborOrgInfo);
		}
		else {			
			//Stay in this computation for now
			return;
		}
	}
	
	private NotificationProtocolHandler<Organization> notificationHandler;
	
	
	
	@Override
	/**
	 * As a master node and subject of this particular Self-Healing Scenario:
	 * - Do nothing. Technically shouldn't occur (at this stage in the scenario, the subject node has failed)
	 * 
	 * @throws	ComputationExecutionException
	 * 			Always; this method should never be used.
	 */
	public void executeOnSubjectMasterNode() throws ComputationExecutionException{
		throw new ComputationExecutionException("P24 Computation on Subject Master Node");
	}
	
}
