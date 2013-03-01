package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import node.organizationMiddleware.contextManager.contextDirectories.Context;
import node.organizationMiddleware.contextManager.contextDirectories.Organization;

import org.junit.Test;
import utilities.NodeID;

public class SingleFailureTests extends SelfAdaptationTests{	
	
	
	@Test
	/**
	 * MasterWithSlavesNodeFailure(Subject 4) for organization with nodes 2, 3 and 4
	 */
	public void testMasterWithSlavesNodeFailure(){		
		NodeID failNode = new NodeID(4);
		
		Organization preFailureOrganization = null;
		ArrayList<Organization> preFailureNeighborOrganizations = new ArrayList<Organization>();
		
		//An execution step high enough just to be sure healing can succeed
		int testEndStep = 250;
		
		boolean failurePointReached = false;
		
		while(this.runApplication){	
			
			this.simulationStep();		
			
			if(this.currentBigStep == 1){
				this.simulator.switchBottleNeck(1);
				this.simulator.updateRate(20);
			}			
			
			//Store pre-failure information			
			// Look for a valid system state where there's an organization with nodes 2, 3 and 4
			ArrayList<NodeID> nodes = new ArrayList<NodeID>();
			nodes.add(new NodeID(2));
			nodes.add(new NodeID(3));
			nodes.add(new NodeID(4));
			boolean preFailureSystemState = this.consistentOrganization(nodes);
			if(preFailureSystemState && !failurePointReached){
				//Needs to be cloned!				
				Context failNodeContext = this.getOrganizationContext(failNode);
				preFailureOrganization = failNodeContext.getPersonalOrg().clone();
				for(Organization neighborOrg : failNodeContext.getNeighbourOrgs()){
					preFailureNeighborOrganizations.add(neighborOrg.clone());
				}			
								
				//MasterWithSlavesNodeFailure
				this.simulator.failNode(failNode.getId());				
				failurePointReached = true;				
			}
			
			//Stop application after healing
			// Look for a system state where no scenarios with the failed node as a subject are still running
			if(this.findNodesWithActiveScenarios(failNode).size() == 0){
				this.runApplication = false;
			}

			//Make sure, if no consistent and expected post-failure system state can be reached, not
			// to run forever
			assertTrue(this.currentBigStep <= testEndStep);
		}
		
		this.testGeneralPostHealingSystemProperties(failNode);
		
		//Check for a valid system state where there's an organization with just node 2 and 3
		ArrayList<NodeID> nodes = new ArrayList<NodeID>();
		nodes.add(new NodeID(2));
		nodes.add(new NodeID(3));
		this.testConsistentOrganization(nodes);
		
		//Lookup information on newly elected master of the original organization
		int organizationID = preFailureOrganization.getId();
		Context postFailureContext = this.findContextInformationOnMasterNode(organizationID);
		
		//The members of this organization should be the original members, minus the failed master
		ArrayList<NodeID> newAgents = postFailureContext.getPersonalOrg().getAgents();
		for(NodeID oldAgent : preFailureOrganization.getAgents()){
			if(!oldAgent.equals(failNode))
				assertTrue(newAgents.contains(oldAgent));
			else
				assertFalse(newAgents.contains(oldAgent));
		}	
		
		//Check whether the same set of neighbor organizations are active before and after the
		// failure
		for(Organization preFailureNeighborOrg : preFailureNeighborOrganizations){
			boolean correspondingOrgFound = false;
			for(Organization postFailureNeighborOrg : postFailureContext.getNeighbourOrgs()){
				if(preFailureNeighborOrg.getId() == postFailureNeighborOrg.getId()){
					//Corresponding neighbor organization found; test some things
					assertTrue(preFailureNeighborOrg.getMasterID().
							equals(postFailureNeighborOrg.getMasterID()));
					assertTrue(preFailureNeighborOrg.getAgents().size() == 
						postFailureNeighborOrg.getAgents().size());
					
					correspondingOrgFound = true;
				}
			}
			
			//Make sure the corresponding organization was indeed found
			assertTrue(correspondingOrgFound);
		}			
		
		//Test organization consistency
		NodeID newMaster = postFailureContext.getPersonalOrg().getMasterID();
		this.testOrganizationConsistency(newMaster);
	}
	
	@Test
	/**
	 * SlaveNodeFailure(Subject 3) for organization with nodes 2, 3 and 4
	 */
	public void testSlaveNodeFailure(){
		NodeID failNode = new NodeID(3);
		
		Organization preFailureOrganization = null;
		
		//An execution step high enough just to be sure healing can succeed
		int testEndStep = 250;
		
		boolean failurePointReached = false;	
		
		while(this.runApplication){			
			
			this.simulationStep();
			
			if(this.currentBigStep == 1){
				this.simulator.switchBottleNeck(1);
				this.simulator.updateRate(20);
			}
			
			//Store pre-failure information			
			// Look for a valid system state where there's an organization with nodes 2, 3 and 4
			ArrayList<NodeID> nodes = new ArrayList<NodeID>();
			nodes.add(new NodeID(2));
			nodes.add(new NodeID(3));
			nodes.add(new NodeID(4));
			boolean preFailureSystemState = this.consistentOrganization(nodes);
			if(preFailureSystemState && !failurePointReached){
				//Needs to be cloned!
				Context failNodeContext = this.getOrganizationContext(failNode);
				preFailureOrganization = failNodeContext.getPersonalOrg().clone();	
				
				//SlaveNodeFailure
				this.simulator.failNode(failNode.getId());				
				failurePointReached = true;
			}
			
			//Stop application after healing
			// Look for a system state where no scenarios with the failed node as a subject are still running
			if(this.findNodesWithActiveScenarios(failNode).size() == 0){
				this.runApplication = false;
			}
			
			//Make sure, if no consistent and expected post-failure system state can be reached, not
			// to run forever
			assertTrue(this.currentBigStep <= testEndStep);
		}
		
		this.testGeneralPostHealingSystemProperties(failNode);
		
		//Check for a valid system state where there's an organization with just nodes 2 and 4
		ArrayList<NodeID> nodes = new ArrayList<NodeID>();
		nodes.add(new NodeID(2));
		nodes.add(new NodeID(4));
		this.testConsistentOrganization(nodes);
		
		//Lookup information on the master of the organization of the failed slave
		int organizationID = preFailureOrganization.getId();
		Context postFailureContext = this.findContextInformationOnMasterNode(organizationID);
		
		//The members of this organization should be the original members, minus the failed slave
		ArrayList<NodeID> newAgents = postFailureContext.getPersonalOrg().getAgents();
		for(NodeID oldAgent : preFailureOrganization.getAgents()){
			if(!oldAgent.equals(failNode))
				assertTrue(newAgents.contains(oldAgent));
		}
		
		//Test organization consistency of the organization the failed slave was a member of
		NodeID organizationMaster = postFailureContext.getPersonalOrg().getMasterID();
		this.testOrganizationConsistency(organizationMaster);
	}
	
	@Test
	/**
	 * SingleMasterNodeFailure(Subject 3)
	 */
	public void testSingleMasterNodeFailure(){
		NodeID failNode = new NodeID(3);
		
		Organization preFailureOrganization = null;
		
		//An execution step high enough just to be sure healing can succeed
		int testEndStep = 50;
		
		boolean failurePointReached = false;
		
		while(this.runApplication){
			
			//Note: because the original single member organization is supposed to disappear,
			// just define test moments based on currentBigStep (definition based on a consistent
			// post-healing organization state won't work as easily)
			//TODO: introduce explicit SingleMasterNodeFailureScenario to remedy this (see other
			// single failure tests for better end conditions)
			
			this.simulationStep();
			
			//Store pre-failure information
			if((this.currentBigStep == 30) && !failurePointReached){
				//Needs to be cloned!
				Context failNodeContext = this.getOrganizationContext(failNode);
				preFailureOrganization = failNodeContext.getPersonalOrg().clone();	
				
				//SingleMasterNodeFailure
				this.simulator.failNode(failNode.getId());
				failurePointReached = true;
			}
			
			//Stop application after healing
			if(this.currentBigStep == 40){
				this.runApplication = false;
			}
			
			//Make sure, if no consistent and expected post-failure system state can be reached, not
			// to run forever
			assertTrue(this.currentBigStep <= testEndStep);
		}
		
		this.testGeneralPostHealingSystemProperties(failNode);
		
		//Verify that no active node can be part of an organization with an id equal to one governed
		// by the failed node
		int organizationID = preFailureOrganization.getId();
		ArrayList<NodeID> failedOrganizationAgents = this.findAllMembersOfOrganization(organizationID);
		for(NodeID camera : this.cameraNodes.keySet()){
			if(!camera.equals(failNode))
				assertFalse(failedOrganizationAgents.contains(camera));
		}
		
		//Test organization consistency of both node 4's and node 2's organization
		this.testOrganizationConsistency(new NodeID(4));
		this.testOrganizationConsistency(new NodeID(2));
		
		//All scenarios with the failed node as the scenario subject should be done
		//TODO: rework scenarios to make sure this test can pass
//		assertTrue(this.findNodesWithActiveScenarios(failNode).size() == 0);
	}
	
	@Test
	/**
	 * Test of system ability to merge with a post-healing organization: merge single master organization (with node 1)
	 * with master with slaves organization (with nodes 2, 3 and 1 after the failure of master node 4)
	 */
	public void testMergeAfterMasterWithSlavesNodeFailure(){
		NodeID failNode = new NodeID(4);
		
//		Organization preFailureOrganization = null;
//		ArrayList<Organization> preFailureNeighborOrganizations = new ArrayList<Organization>();
		
		//An execution step high enough just to be sure healing can succeed
		int testEndStep = 250;
		
		boolean failurePointReached = false;		
		
		while(this.runApplication){
			
			this.simulationStep();		
			
			if(this.currentBigStep == 1){
				this.simulator.switchBottleNeck(1);
				this.simulator.updateRate(30);
			}
			
			//Store pre-failure information	
			// Look for a valid system state where there's an organization with nodes 2, 3 and 4
			ArrayList<NodeID> nodes = new ArrayList<NodeID>();
			nodes.add(new NodeID(2));
			nodes.add(new NodeID(3));
			nodes.add(new NodeID(4));
			boolean preFailureSystemState = this.consistentOrganization(nodes);
			if(preFailureSystemState && ! failurePointReached){
//				//Needs to be cloned!				
//				Context failNodeContext = this.getOrganizationContext(failNode);
//				preFailureOrganization = failNodeContext.getPersonalOrg().clone();
//				for(Organization neighborOrg : failNodeContext.getNeighbourOrgs()){
//					preFailureNeighborOrganizations.add(neighborOrg.clone());
//				}
				
				//MasterWithSlavesNodeFailure
				this.simulator.failNode(failNode.getId());				
				failurePointReached = true;		
			}
			
			//Stop application
			// Look for a valid system state where there's an organization with just nodes 1, 2 and 3
			nodes = new ArrayList<NodeID>();
			nodes.add(new NodeID(1));
			nodes.add(new NodeID(2));
			nodes.add(new NodeID(3));
			boolean postFailureSystemState = this.consistentOrganization(nodes);
			if(postFailureSystemState){	
				this.runApplication = false;
			}

			//Make sure, if no consistent and expected post-failure system state can be reached, not
			// to run forever
			assertTrue(this.currentBigStep <= testEndStep);
		}
		
		//Look up organization context for new master
		int newOrgID = this.getOrganizationContext(new NodeID(1)).getPersonalOrg().getId();
		Context postFailureContext = this.findContextInformationOnMasterNode(newOrgID);
		
		//Test organization consistency
		NodeID newMaster = postFailureContext.getPersonalOrg().getMasterID();
		this.testOrganizationConsistency(newMaster);
		
		this.testGeneralPostHealingSystemProperties(failNode);
	}
	
}
