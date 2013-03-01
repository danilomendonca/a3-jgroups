package node.selfAdaptationSystem.mapeManager.selfMonitoring.cameraMonitoring;

import java.util.ArrayList;

import node.organizationMiddleware.contextManager.contextDirectories.Context;
import node.organizationMiddleware.contextManager.contextDirectories.Organization;
import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.MonitorComputation;
import node.selfAdaptationSystem.selfAdaptationController.SelfAdaptationController.LocalTrafficSystemRoleType;
import node.selfAdaptationSystem.selfAdaptationModels.OrganizationSnapshot;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class M91 extends MonitorComputation implements CameraMonitoringComputation {
	
	public M91(CameraMonitoringScenario scenario, SelfAdaptationModels models, 
				BaseLevelConnector baseLevel){
		super(scenario, models, baseLevel);
	}
	
	/**************************	 
	 * 
	 *	Execution
	 *
	 **************************/		
	
	@Override
	/**
	 * As the subject node of this particular Self-Monitoring Scenario:
	 * - update the self-adaptation models with information (common for every camera, regardless
	 * of its current role) from the local traffic system
	 * 
	 * Note: this computation temporarily combines logic of the scenarios for each traffic system role.
	 * This should be separated into different scenario types ...
	 */
	public void executeOnSubjectNode(){
		//COMMON Information
		this.getSelfAdaptationModels().setHostNode(this.getBaseLevelConnector().getHostNodeID());
		this.getSelfAdaptationModels().setAliveNeighborNodes(this.getBaseLevelConnector().getAliveNeighbors());
		
		
		
		//TODO: properly separate the following into SingleMasterMonitoring, MasterWithSlavesMonitoring and SlaveMonitoring scenarios
		LocalTrafficSystemRoleType currentLocalSystemRole = this.getSelfAdaptationModels().getCurrentTrafficRole();
		
		//If the local traffic monitoring system is transitioning between system roles or if
		// it is currently not occupying any role in the traffic monitoring system:
		// don't try to update the self-adaptation models any further
		if( (currentLocalSystemRole == LocalTrafficSystemRoleType.ROLE_TRANSITION) ||
				(currentLocalSystemRole == LocalTrafficSystemRoleType.NO_ROLE) )
			return;
		
		Context localOrg = this.getBaseLevelConnector().getLocalOrganizationContext();
		this.getSelfAdaptationModels().setTrafficJamTreshold(localOrg.getPersonalOrg().getTrafficJamTreshhold());		
		
		// Only update the role position if it's been made available by the local base-level
		if(this.getBaseLevelConnector().getLocalRolePositionObject() != null)
			this.getSelfAdaptationModels().setLocalRoleposition(this.getBaseLevelConnector().getLocalRolePositionObject());
		
		
		if(currentLocalSystemRole == LocalTrafficSystemRoleType.SINGLE_MASTER){
			//Single Masters have up-to-date information on the masters (and possible slaves) of its
			// neighbor organizations
			
			this.updateModelForNeighboringOrganizations();
			
			this.updateModelForOwnOrganization();
		}
		else if(currentLocalSystemRole == LocalTrafficSystemRoleType.MASTER_WITH_SLAVES){
			//Masters with Slaves have up-to-date information on their own slaves and on 
			//the masters (and possible slaves) of its neighbor organizations
			
			this.updateModelForNeighboringOrganizations();
			
			this.updateModelForOwnOrganization();
		}
		else if(currentLocalSystemRole == LocalTrafficSystemRoleType.SLAVE){
			//Slaves only have up-to-date information on the master of their organization, not on other slave
			// nodes or on neighbor organizations
			
			this.getSelfAdaptationModels().setLocalOrganization(
					new OrganizationSnapshot(localOrg.getPersonalOrg().getId(), 
												localOrg.getPersonalOrg().getMasterID(), null));
		}
		
		
		
		//Note: no transition needed since up-to-date information is needed during each execution cycle
	}
	
	private void updateModelForNeighboringOrganizations(){
		Context localOrg = this.getBaseLevelConnector().getLocalOrganizationContext();
		
		ArrayList<OrganizationSnapshot> neighborOrgs = new ArrayList<OrganizationSnapshot>();
		for(Organization neighborOrg : localOrg.getNeighbourOrgs()){			
			OrganizationSnapshot newOrg = 
				new OrganizationSnapshot(neighborOrg.getId(), neighborOrg.getMasterID(), neighborOrg.getAgents());
			
			neighborOrgs.add(newOrg);
		}
		
		this.getSelfAdaptationModels().setNeighborOrganizations(neighborOrgs);
	}
	
	private void updateModelForOwnOrganization(){
		Organization localOrg = this.getBaseLevelConnector().getLocalOrganizationContext().getPersonalOrg();
		
		//Add all nodes: master + slaves (if any)
		this.getSelfAdaptationModels().setLocalOrganization(
				new OrganizationSnapshot(localOrg.getId(), localOrg.getMasterID(), localOrg.getAgents()));
		
		//In addition: store entire local organization information
		this.getSelfAdaptationModels().setFullLocalOrganizationInformation(localOrg.clone());
	}
	
	
	
	
	@Override
	/**
	 * As a neighbor of the subject of this particular Self-Monitoring Scenario:
	 * - Do nothing. Technically shouldn't occur (neighbor nodes are not involved in this computation
	 * 
	 * @throws	ComputationExecutionException
	 * 			Always; this method should never be used.
	 */
	public void executeOnSubjectNeighborNode()throws ComputationExecutionException {
		throw new ComputationExecutionException("M91 Computation on Neighbor Node");
	}

}
