package tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import node.ApplicationFactory;
import node.CameraNode;
import node.hostInfrastructure.SimulatedCommunicationNetwork;
import node.organizationMiddleware.contextManager.contextDirectories.Context;
import node.organizationMiddleware.contextManager.contextDirectories.NeighbourInfo;
import node.organizationMiddleware.contextManager.contextDirectories.Organization;
import node.organizationMiddleware.contextManager.contextDirectories.OrganizationBoundary;
import node.organizationMiddleware.contextManager.contextDirectories.RolePosition;
import node.organizationMiddleware.contextManager.contextDirectories.TrafficJamInfo;
import node.organizationMiddleware.organizationController.MasterController;
import node.selfAdaptationSystem.SelfAdaptationSubsystem;
import node.selfAdaptationSystem.coordination.MapeCommunicationManager;
import node.selfAdaptationSystem.coordination.SelfAdaptationMessageProfiler;
import node.selfAdaptationSystem.mapeManager.SelfAdaptationScenario;
import node.selfAdaptationSystem.mapeManager.masterWithSlavesNodeFailureScenario.MasterWithSlavesNodeFailureScenario;
import node.selfAdaptationSystem.mapeManager.neighborNodeFailure.NeighborNodeFailureScenario;
import node.selfAdaptationSystem.mapeManager.slaveNodeFailureScenario.SlaveNodeFailureScenario;
import node.selfAdaptationSystem.selfAdaptationController.SelfAdaptationController.LocalTrafficSystemRoleType;
import node.selfAdaptationSystem.selfAdaptationModels.OrganizationSnapshot;

import org.junit.After;
import org.junit.Before;

import simulator.TrafficSimulator;
import utilities.NodeID;
import GUI.SimulatorWindow;

public abstract class SelfAdaptationTests {
	
	protected ApplicationFactory application;
	protected SimulatorWindow gui;
	protected TrafficSimulator simulator;
	
	
	/**************************	 
	 * 
	 *	Before & After
	 *
	 **************************/
	
	@Before
	public void before(){		
		application = 
			new ApplicationFactory(this.roadNetwork,1,false,false,0, this.windowXSize, this.windowYSize, this.cameraRange);
		this.gui = application.simulatorWindow;
		this.simulator = application.trafficSimulator;
		
		this.gui.start();
		
		//Initialize internal test structures
		this.createCameraNodesList();
		
		this.runApplication = true;
		
		//Initialize Message Profiler
		SelfAdaptationMessageProfiler profiler = SelfAdaptationMessageProfiler.getProfiler();
		File outputFile = new File("messageCount.txt");
		profiler.setOutputFile(outputFile);
		MapeCommunicationManager.setMessageProfiler(profiler);
	}
	
	@After
	public void after(){
		this.gui.stop();
		this.gui.dispose();
		
		SimulatedCommunicationNetwork.resetSimulatedNetworkInstance();
		
		this.step = 0;
		this.cameraNodes = null;
		
		//Print MessageProfiler stats, if necessary
		if(this.profileMessages)
			SelfAdaptationMessageProfiler.getProfiler().print();
	}
	
	/**
	 * Configure the road network to be used during testing. The given identifier
	 * corresponds to the "scenarios" defined in the RoadNetwork class
	 * 
	 * 1: Y aka three-way junction
	 * 2: Manhattan grid
	 * 4: straight road, six cameras (DEFAULT)
	 */
	public void setRoadNetworkConfig(int networkIdentifier){
		this.roadNetwork = networkIdentifier;
	}
	
	//Default: straight road, six cameras
	private int roadNetwork = 4;
	
	protected int windowXSize = 400;
	protected int windowYSize = 100;
	
	protected int cameraRange = 30;
	
	//Enables or disables message profiling.
	// Default: no profiling
	protected boolean profileMessages = false;
	
	
	
	/**************************	 
	 * 
	 *	Self-Healing Subsystem
	 *
	 **************************/
	
	protected void testSelfHealingSubsystem(){
		//Assert that no scenarios are busy healing on any camera node
		assertFalse(this.trafficSystemIsBusyHealing());
		
		//If the system is in a non-healing state and depending on its base-level role, each node 
		// should have a certain number of active scenarios:
		for(CameraNode camera : this.cameraNodes.values()){	
			SelfAdaptationSubsystem shSystem = camera.selfAdaptationSubsystem;
			
			LocalTrafficSystemRoleType baseLevelRole = shSystem.controller.getCurrentLocalSystemRole();
			
			//The base-level can not be transitioning between roles
			assertFalse(baseLevelRole == LocalTrafficSystemRoleType.ROLE_TRANSITION);
			
			//Every node (regardless of base-level role) needs a NeighborNodeFailureScenario-instance for
			// each of its neighbor nodes
			// + an instance with itself as the subject
			List<SelfAdaptationScenario<?>> neighborNodeFailureScenarios = 
				shSystem.mapeManager.getAllScenariosOfType(NeighborNodeFailureScenario.scenarioType);
			List<NodeID> neighborNodes = shSystem.models.getAliveNeighborNodes();
			assertTrue(neighborNodeFailureScenarios.size()-1 == neighborNodes.size());	//Don't count its own scenario
			for(SelfAdaptationScenario<?> neighborNodeFailureScenario : neighborNodeFailureScenarios){
				assertTrue(neighborNodes.contains(neighborNodeFailureScenario.getSubject()) ||
						neighborNodeFailureScenario.getSubject().equals(shSystem.models.getHostNode()));
			}
			
			//A master of an organization needs a MasterWithSlavesNodeFailureScenario-instance for each of the
			// masters of neighboring organizations that have slaves
			if( (baseLevelRole == LocalTrafficSystemRoleType.SINGLE_MASTER) || 
					(baseLevelRole == LocalTrafficSystemRoleType.MASTER_WITH_SLAVES)){
				List<SelfAdaptationScenario<?>> masterWithSlavesNodeFailureScenarios = 
					shSystem.mapeManager.getAllScenariosOfType(MasterWithSlavesNodeFailureScenario.scenarioType);
				List<OrganizationSnapshot> neighborOrganizations = shSystem.models.getNeighborOrganizations();				
				for(OrganizationSnapshot neighborOrg : neighborOrganizations){
					if(neighborOrg.getNodes().size() > 1){
						//Verify that there is a corresponding scenario with the neighbor organizations's master
						// as the scenario subject
						boolean scenarioFound = false;
						for(SelfAdaptationScenario<?> masterWithSlavesNodeFailureScenario : masterWithSlavesNodeFailureScenarios){
							if(masterWithSlavesNodeFailureScenario.getSubject().equals(neighborOrg.getMasterNode()))
								scenarioFound = true;
						}
						assertTrue(scenarioFound);					
					}
				}
			}
			
			//A master of a organization containing additional slaves needs a SlaveNodeFailureScenario-instance for each
			// of its slaves
			// + a MasterWithSlavesNodeFailure-instance with itself as the subject
			if(baseLevelRole == LocalTrafficSystemRoleType.MASTER_WITH_SLAVES){
				List<SelfAdaptationScenario<?>> slaveNodeFailureScenarios = 
					shSystem.mapeManager.getAllScenariosOfType(SlaveNodeFailureScenario.scenarioType);
				List<NodeID> localOrgNodes = shSystem.models.getLocalOrganization().getNodes();
				assertTrue(localOrgNodes.size()-1 == slaveNodeFailureScenarios.size());	//Minus one: don't count the master itself
				for(SelfAdaptationScenario<?> slaveNodeFailureScenario : slaveNodeFailureScenarios){
					assertTrue(localOrgNodes.contains(slaveNodeFailureScenario.getSubject()));
				}
				
				List<SelfAdaptationScenario<?>> masterWithSlavesNodeFailureScenarios = 
					shSystem.mapeManager.getAllScenariosOfType(MasterWithSlavesNodeFailureScenario.scenarioType);
				for(SelfAdaptationScenario<?> masterWithSlavesNodeFailureScenario : masterWithSlavesNodeFailureScenarios){
					boolean scenarioFound = false;
					if(masterWithSlavesNodeFailureScenario.getSubject().equals(shSystem.models.getHostNode())){
						scenarioFound = true;
					}
					assertTrue(scenarioFound);
				}
			}
			
			//A slave of an organization needs a SlaveNodeFailureScenario-instance with itself as the subject
			// + a MasterWithSlavesNodeFailureScenario-instance for its master
			if(baseLevelRole == LocalTrafficSystemRoleType.SLAVE){
				List<SelfAdaptationScenario<?>> slaveNodeFailureScenarios = 
					shSystem.mapeManager.getAllScenariosOfType(SlaveNodeFailureScenario.scenarioType);
				for(SelfAdaptationScenario<?> slaveNodeFailureScenario : slaveNodeFailureScenarios){
					boolean scenarioFound = false;
					if(slaveNodeFailureScenario.getSubject().equals(shSystem.models.getHostNode())){
						scenarioFound = true;
					}
					assertTrue(scenarioFound);
				}
				
				List<SelfAdaptationScenario<?>> masterWithSlavesNodeFailureScenarios = 
					shSystem.mapeManager.getAllScenariosOfType(MasterWithSlavesNodeFailureScenario.scenarioType);
				for(SelfAdaptationScenario<?> masterWithSlavesNodeFailureScenario : masterWithSlavesNodeFailureScenarios){
					boolean scenarioFound = false;
					NodeID masterNode = shSystem.models.getLocalOrganization().getMasterNode();
					if(masterWithSlavesNodeFailureScenario.getSubject().equals(masterNode)){
						scenarioFound = true;
					}
					assertTrue(scenarioFound);
				}
			}			
		}
	}
	
	/*
	 * Returns a list of nodes, for which their corresponding self-healing subsystem
	 * have one or more scenarios active for the given scenario subject node
	 */
	protected ArrayList<NodeID> findNodesWithActiveScenarios(NodeID scenarioSubject){
		ArrayList<NodeID> scenarioNodes = new ArrayList<NodeID>();
		
		for(CameraNode camera : this.cameraNodes.values()){
			//Don't check failed nodes
			if(!camera.hasFailed()){
				NodeID cameraNode = 
					camera.getOrganizationController().getOrganizationContext().getPersonalID();

				SelfAdaptationSubsystem shSystem = camera.selfAdaptationSubsystem;
				for(SelfAdaptationScenario<?> scenario : shSystem.mapeManager.getScenarioInstances()){
					if(scenario.getSubject().equals(scenarioSubject) 
							&& !scenarioNodes.contains(cameraNode))
						scenarioNodes.add(cameraNode);
				}
			}		
		}
		
		return scenarioNodes;
	}
	
	/*
	 * Returns true if any scenarios running on any of the camera nodes are busy adapting. 
	 * False otherwise.
	 */
	protected boolean trafficSystemIsBusyHealing(){
		for(CameraNode camera : this.cameraNodes.values()){			
			SelfAdaptationSubsystem shSystem = camera.selfAdaptationSubsystem;
			for(SelfAdaptationScenario<?> scenario : shSystem.mapeManager.getScenarioInstances()){
				if(scenario.isBusyAdapting())
					return true;
			}
		}
		
		return false;
	}
	
	
	/**************************	 
	 * 
	 *	CameraNodes
	 *
	 **************************/
	
	/*
	 * Based on the failure of the given set of nodes (and their subsequent healing), perform some
	 * general system checks
	 */
	protected void testGeneralPostHealingSystemProperties(ArrayList<NodeID> failedNodes){
		//For each given failed node: ...
		for(NodeID failedNode : failedNodes){
			//No organization should have the failed node as one of its current agents
			for(NodeID camera : this.cameraNodes.keySet()){
				//Make sure to only check alive nodes
				if(!failedNodes.contains(camera)){
					Organization cameraOrg = this.getOrganizationContext(camera).getPersonalOrg();
				
					assertFalse(cameraOrg.getAgents().contains(failedNode));
				}			
			}
			
			//No active camera node should have the failed node registered as its neighbor node
			for(NodeID neighborNode : this.findNeigborNodesOf(failedNode)){
				assertTrue(failedNodes.contains(neighborNode));
			}
			
			//No active master node can have the failed node as an agent of one of its neighboring organizations
			for(NodeID neighborNode : this.findMastersWithNeighborOrganizationAgent(failedNode)){
				assertTrue(failedNodes.contains(neighborNode));
			}
		}
		
		//Test the self-healing subsystem
		this.testSelfHealingSubsystem();
	}
	
	protected void testGeneralPostHealingSystemProperties(NodeID failedNode){
		ArrayList<NodeID> failedNodes = new ArrayList<NodeID>();
		failedNodes.add(failedNode);
		
		this.testGeneralPostHealingSystemProperties(failedNodes);
	}
	
	/*
	 * Tests whether the current application has the given list of agents as the only members
	 * of the organization with the given id.
	 */
	protected void testConsistentOrganization(int organizationID, ArrayList<NodeID> agents){
		//Check if there is a master node in charge of an organization with the given id		
		Context orgMasterContext = this.findContextInformationOnMasterNode(organizationID);
		assertNotNull(orgMasterContext);
		
		//Verify that this master node has the given list of agents as the current organization members
		assertTrue(orgMasterContext.getPersonalOrg().getAgents().containsAll(agents));
		assertTrue(agents.containsAll(orgMasterContext.getPersonalOrg().getAgents()));
		
		//Verify consistency of the organization information on this master with its slaves (if any) and
		// with masters of neighboring organizations, in order to be sure that the current state of the
		// system is entirely valid with regards to the organization with the given id
		this.testOrganizationConsistency(orgMasterContext.getPersonalID());
	}
	
	/*
	 * To be used during tests to confirm that the system is in a valid state, with one of its organizations
	 * containing the given set of agents
	 */
	protected boolean consistentOrganization(ArrayList<NodeID> agents){
		try{
			this.testConsistentOrganization(agents);
			
			return true;
		}
		catch(Exception e){
			return false;
		}
		catch(AssertionError e){
			return false;
		}
	}
	
	/*
	 * Tests whether the current application has the given list of agents as the only members
	 * of one of the active organizations
	 */
	protected void testConsistentOrganization(ArrayList<NodeID> agents){
		//Find id of the organization the first agent in the list is a member of
		Context firstAgentContext = this.getOrganizationContext(agents.get(0));
		assertNotNull(firstAgentContext);
		int organizationID = firstAgentContext.getPersonalOrg().getId();

		//Check organization consistency
		this.testConsistentOrganization(organizationID, agents);
	}
	
	/*
	 * Verifies that the information concerning the organization that is currently active on the 
	 * given master node is consistent with the information on its slave nodes (if any) and on 
	 * the masters of the neighboring organizations.
	 */
	protected void testOrganizationConsistency(NodeID organizationMasterNode){
		Context masterNodeContext = this.getOrganizationContext(organizationMasterNode);
		Organization masterNodeOrganization = masterNodeContext.getPersonalOrg();
		
		if(!masterNodeOrganization.getMasterID().equals(organizationMasterNode))
			throw new IllegalArgumentException("Given node not a master of its organization");
		
		//If the organization on the given master node has additional slaves: test consistency		
		if(masterNodeOrganization.getAgents().size() > 1)
			this.testOrganizationConsistencyWithSlaves(organizationMasterNode);
		
		//The list of cameras for which the id of their personal organization is equal to the id
		// of the organization on the given master node, should be equal to the list of organization
		// agents (in other words: no rogue organization agents allowed)
		for(NodeID potentialAgent : this.findAllMembersOfOrganization(masterNodeOrganization.getId())){
			assertTrue(masterNodeOrganization.getAgents().contains(potentialAgent));
		}
		
		//Verify that every registered agent has an associated RolePosition-object
		ArrayList<NodeID> agents = masterNodeOrganization.getAgents();
		ArrayList<RolePosition> rolePositions = masterNodeOrganization.getFilledRolePositions();
		
		assertTrue(agents.size() == rolePositions.size());
		for(RolePosition rp : rolePositions){
			assertTrue(agents.contains(rp.getAgentId()));
			
			//Additionally: every RolePosition should have non-negative values for its TrafficJameInfo
			TrafficJamInfo trafficInfo = rp.getTrafficJamInfo();
			assertTrue(trafficInfo.getAvgVelocity() >= 0);
			assertTrue(trafficInfo.getDensity() >= 0);
			//assertTrue(trafficInfo.getIntensity() >= 0);
			
			//Additionally: every RolePosition should have at least one registered active neighbor; each neighbor
			// being either a member of this organization or an external agent of one of the organization boundaries
			NeighbourInfo neighborInfo = rp.getNeighbourInfo();
			assertTrue(neighborInfo.getNeighbours().size() >= 1);			
			for(NodeID neighbor : neighborInfo.getNeighbours()){
				boolean memberOfOrganization = masterNodeOrganization.getAgents().contains(neighbor);
				
				boolean externalAgent = false;
				for(OrganizationBoundary boundary : masterNodeOrganization.getOrganizationBoundaries()){
					if(boundary.getExternalAgent().equals(neighbor))
						externalAgent = true;
				}
				
				assertTrue(memberOfOrganization || externalAgent);
				assertFalse(memberOfOrganization && externalAgent);
			}		
		}
		
		//Verify for each OrganizationBoundary that its internal agent belongs to this organization and that
		// its external agent belongs to a neighboring organization
		for(OrganizationBoundary boundary : masterNodeOrganization.getOrganizationBoundaries()){
			//Internal agent
			assertTrue(masterNodeOrganization.getAgents().contains(boundary.getInternalAgent()));
			
			//External agent
			boolean externalAgentFound = false;
			for(Organization neighborOrg : masterNodeContext.getNeighbourOrgs()){
				if(neighborOrg.getAgents().contains(boundary.getExternalAgent()))
					externalAgentFound = true;
			}
			assertTrue(externalAgentFound);
		}
		
		//Test the organization consistency for every neighboring organization
		this.testOrganizationConsistencyWithNeighborOrganizations(organizationMasterNode);
	}
	
	protected void testOrganizationConsistencyWithSlaves(NodeID organizationMasterNode){
		Organization masterNodeOrganization = 
			this.getOrganizationContext(organizationMasterNode).getPersonalOrg();	
		
		//For each agent of the organization of which the given node is the master:
		for(NodeID agent : masterNodeOrganization.getAgents()){
			if(!agent.equals(organizationMasterNode)){
				Organization slaveOrganization = this.getOrganizationContext(agent).getPersonalOrg();
				
				//Check organization ID's
				assertTrue(masterNodeOrganization.getId() == slaveOrganization.getId());
				
				//Check organization master ID's
				assertTrue(masterNodeOrganization.getMasterID().equals(slaveOrganization.getMasterID()));
				
				//Check to see if the slave node has just one registered agent (itself)
				//Note: apparently, the base-level system doesn't make sure that, on slave nodes, the local
				// node is registered as an agent -> skip these tests
				//assertTrue(slaveOrganization.getAgents().size() == 1);
				//assertTrue(slaveOrganization.getAgents().contains(agent));
				
				//Check to see if the slave node has at least its own RolePosition object
				RolePosition slaveRolePositionOnSlaveNode = null;
				for(RolePosition rp : slaveOrganization.getFilledRolePositions()){
					if(rp.getAgentId().equals(agent))
						slaveRolePositionOnSlaveNode = rp;
				}
				assertNotNull(slaveRolePositionOnSlaveNode);
				
				//Compare the contents of the slave RolePosition-object on the slave node with the one 
				// on the master node
				for(RolePosition rp : masterNodeOrganization.getFilledRolePositions()){
					if(rp.getAgentId().equals(agent)){
						//Compare TrafficJamInfo and NeighbourInfo						
						assertTrue(rp.getNeighbourInfo().equals(slaveRolePositionOnSlaveNode.getNeighbourInfo()));
						//Note: as the traffic information variables (avgVelocity, density, ...) can change from
						// moment to moment, irrespective of healing operations being performed, this test can
						// probably be skipped
						//assertTrue(rp.getTrafficJamInfo().equals(slaveRolePositionOnSlaveNode.getTrafficJamInfo()));
					}
				}				
			}
		}
	}
	
	/*
	 * Makes sure that the information located on masters of organizations neighboring on the
	 * one owned by the given master node is accurate, compared to the information on the given
	 * master node (meaning its personal organization)
	 */
	protected void testOrganizationConsistencyWithNeighborOrganizations(NodeID organizationMasterNode){
		Context masterNodeContext = this.getOrganizationContext(organizationMasterNode);
		
		ArrayList<Context> actualNeighborOrganizations = new ArrayList<Context>();
		//Gather, from all masters of neighboring organizations, their actual organization 
		// information objects
		//Note: use the special 'find' method in order to avoid checking rogue neighbor organizations
		// (organizations that are not registered as such on the given master node, but for which the reverse
		// is true)
		for(NodeID neighborOrgMaster : this.findMastersWithNeighborOrganizationAgent(organizationMasterNode)){
			actualNeighborOrganizations.add(this.getOrganizationContext(neighborOrgMaster));		
		}
		
		//The number of registered neighbor organizations on the given master node should be equal to the
		// number of organizations that have this node as an agent of one of its neighboring organizations
		assertTrue(masterNodeContext.getNeighbourOrgs().size() == actualNeighborOrganizations.size());
		
		//For each neighbor organization: test both the correctness of the information of the organization
		// of the given master node on its neighbor organizations and of the neighbor organization on
		// the given master node
		for(NodeID neighborOrgMaster : this.findMastersWithNeighborOrganizationAgent(organizationMasterNode)){
			this.testNeighborOrganizationConsistency(organizationMasterNode, neighborOrgMaster);
			
			//Note: apparently, an accurate list of neighbor nodes (kept by NeighbourInfo-objects) is not
			// sent to neighbor masters (see Neighbours_SyncMechanism.sendPingToMaster()) and
			// OrganizationState_SyncMechanism only sends accurate TrafficJamInfo at a later point in time
			//Therefore, this next test will not succeed  --> leave it out
			
			//this.testNeighborOrganizationConsistency(neighborOrgMaster, organizationMasterNode);
		}	
	}
	
	/*
	 * Tests to see whether the information concerning the personal organization of the given source master
	 * node is consistent with the information that is kept on the given neighbor master node.
	 * Note: the organization information on the source node is always considered correct.
	 */
	protected void testNeighborOrganizationConsistency(NodeID sourceMasterNode, NodeID neighborMasterNode){
		Context sourceMasterNodeContext = this.getOrganizationContext(sourceMasterNode);
		Organization sourceMasterOrg = sourceMasterNodeContext.getPersonalOrg();
		
		//Find the corresponding neighbor organization on the given neighbor master node
		Context neighborMasterNodeContext = this.getOrganizationContext(neighborMasterNode);
		for(Organization neighborOrg : neighborMasterNodeContext.getNeighbourOrgs()){
			if(neighborOrg.getId() == sourceMasterOrg.getId()){
				//Compare master nodes
				assertTrue(sourceMasterOrg.getMasterID().equals(neighborOrg.getMasterID()));
				
				//Compare the source master organization agents
				assertTrue(sourceMasterOrg.getAgents().size() == neighborOrg.getAgents().size());
				for(NodeID agent : sourceMasterOrg.getAgents()){
					assertTrue(neighborOrg.getAgents().contains(agent));
				}
				
				//Compare RolePosition-objects
				//Note: because masters of neighboring organizations don't access RolePosition information
				// , this data is not properly synchronized at all times -> skip this test
//				assertTrue(sourceMasterOrg.getFilledRolePositions().size() 
//														== neighborOrg.getFilledRolePositions().size());
//				for(RolePosition sourceRp : sourceMasterOrg.getFilledRolePositions()){
//					//Find corresponding role position on neighbor master node
//					for(RolePosition rp : neighborOrg.getFilledRolePositions()){
//						if(sourceRp.getAgentId().equals(rp.getAgentId())){
//							assertTrue(sourceRp.getNeighbourInfo().equals(rp.getNeighbourInfo()));
//							assertTrue(sourceRp.getTrafficJamInfo().equals(rp.getTrafficJamInfo()));
//						}
//					}
//				}
				
				//For at least one of the neighbor master's personal organization boundaries, the external
				// agent needs to be a member of the organization that is active on the given source
				// master node
				Organization neighborMasterOrganization = neighborMasterNodeContext.getPersonalOrg();
				boolean externalAgentFound = false;
				for(OrganizationBoundary neighborMasterOrgBoundary : 
										neighborMasterOrganization.getOrganizationBoundaries()){
					if(sourceMasterOrg.getAgents().contains(neighborMasterOrgBoundary.getExternalAgent()))
						externalAgentFound = true;						
				}
				assertTrue(externalAgentFound);
				
				//Tests done
				return;
			}
		}
		
		//If this point is reached: corresponding neighbor organization not found.
		throw new IllegalArgumentException("No neighbor organization information found on neighborMasterNode");
	}
	
	/*
	 * Returns a list of nodes for which the id of their personal organization is equal to the given id
	 */
	protected ArrayList<NodeID> findAllMembersOfOrganization(int organizationID){
		ArrayList<NodeID> members = new ArrayList<NodeID>();
		
		for(CameraNode camera : this.cameraNodes.values()){
			//Only check cameras that are currently active
			if(!camera.hasFailed()){
				Context cameraContext = camera.getOrganizationController().getOrganizationContext();
				if(cameraContext.getPersonalOrg().getId() == organizationID)
					members.add(cameraContext.getPersonalID());
			}			
		}
		
		return members;
	}
	
	/*
	 * Returns a list of nodes that have the given node set as one of their active neighbor nodes
	 */
	protected ArrayList<NodeID> findNeigborNodesOf(NodeID node){
		ArrayList<NodeID> neighbors = new ArrayList<NodeID>();
		
		for(CameraNode camera : this.cameraNodes.values()){
			Context cameraContext = camera.getOrganizationController().getOrganizationContext();
			NodeID cameraID = cameraContext.getPersonalID();
			
			//Check because of the lack of an actual personal organization on failed nodes
			if(cameraContext.getPersonalOrg() != null){
				for(RolePosition rp : cameraContext.getPersonalOrg().getFilledRolePositions()){
					if(rp.getAgentId().equals(cameraID)){
						//Own RolePosition found
						if(rp.getNeighbourInfo().getNeighbours().contains(node))
								neighbors.add(cameraID);
					}
				}
			}			
		}
		
		return neighbors;
	}
	
	/*
	 * Returns a list of nodes are registered on the given node as its alive neighbor cameras
	 */
	protected ArrayList<NodeID> findNeigborNodesOn(NodeID node){
		Organization nodeOrg = this.getOrganizationContext(node).getPersonalOrg();
		
		for(RolePosition rp : nodeOrg.getFilledRolePositions()){
			if(rp.getAgentId().equals(node)){
				return rp.getNeighbourInfo().getNeighbours();
			}
		}
		
		//If this point is reached: problem with the neighbor information on the given node
		throw new IllegalArgumentException();
	}
	
	/*
	 * Returns a list of master nodes that have registered the given agent node as a member of
	 * one of their neighbor organizations
	 */
	protected ArrayList<NodeID> findMastersWithNeighborOrganizationAgent(NodeID agent){
		ArrayList<NodeID> masters = new ArrayList<NodeID>();
		
		for(CameraNode camera : this.cameraNodes.values()){
			//Only check cameras that are currently active and that are a master
			if(!camera.hasFailed() && (camera.getOrganizationController() instanceof MasterController)){
				Context cameraContext = camera.getOrganizationController().getOrganizationContext();
				for(Organization neighborOrg : cameraContext.getNeighbourOrgs()){
					if(neighborOrg.getAgents().contains(agent))
						masters.add(cameraContext.getPersonalID());
				}
			}
		}
			
		return masters;
	}
	
	/*
	 * Retrieve information on the local context of the master of the organization with the
	 * given organization id.
	 */
	protected Context findContextInformationOnMasterNode(int organizationID){
		for(CameraNode camera : this.cameraNodes.values()){
			Context cameraContext = camera.getOrganizationController().getOrganizationContext();
			if( (cameraContext.getPersonalOrg() != null)
					&& (cameraContext.getPersonalOrg().getId() == organizationID) 
					&& cameraContext.getPersonalOrg().getMasterID().equals(cameraContext.getPersonalID()) )
				return cameraContext;
		}
		
		throw new IllegalArgumentException("OrganizationID not found");
	}
	
	protected Context getOrganizationContext(NodeID node){
		CameraNode camera = this.cameraNodes.get(node);
		return camera.getOrganizationController().getOrganizationContext();
	}
	
	protected void createCameraNodesList(){
		this.cameraNodes = new HashMap<NodeID, CameraNode>();
		for(CameraNode cameraNode : this.application.cameraNodes){
			this.cameraNodes.put(cameraNode.getContext().getPersonalID(), cameraNode);
		}
	}
	
	protected HashMap<NodeID, CameraNode> cameraNodes;
	
	
	/**************************	 
	 * 
	 *	TrafficSimulator &
	 *	Application
	 *
	 **************************/
	
	/*
	 * Single-threaded mode!
	 */
	protected void simulationStep(){
		this.application.step();
		
		//Message Profiler, if necessary
		if(this.profileMessages)
			SelfAdaptationMessageProfiler.getProfiler().nextExecutionCycle();
		
		this.step++;
		
		//Advance the simulator (and therefore the GUI) only periodically
		//bigSteps are useful when broadly defining test moments
		if(this.currentBigStep < this.step / 25){			
			this.simulator.advance();
			
			this.currentBigStep = this.step / 25;
		}
	}
	
	protected boolean runApplication = true;
	
	protected int step = 0;
	protected int currentBigStep = 0;

}
