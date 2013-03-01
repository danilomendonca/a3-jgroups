package tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import node.organizationMiddleware.contextManager.contextDirectories.Organization;
import node.selfAdaptationSystem.coordination.SelfAdaptationMessageProfiler;

import org.junit.Test;

import utilities.NodeID;

public class SelfConfigurationTests extends SelfAdaptationTests {
	
	@Test
	/**
	 * CameraIntroduction(Subject 4) - Case A (start new single master organization)
	 */
	public void testCameraIntroductionCaseA(){
		NodeID failAndIntroduceNode = new NodeID(4);
		
		//An execution step high enough just to be sure adaptation can succeed
		int testEndStep = 80;
		
		boolean failurePointReached = false;
		boolean revivalPointReached = false;
		
		while(this.currentBigStep < testEndStep){	
			
			this.simulationStep();		
			
			if(this.currentBigStep == 1){
				this.simulator.updateRate(20);
			}
			
			if((this.currentBigStep == 50) && !failurePointReached){
				this.simulator.failNode(failAndIntroduceNode.getId());
				failurePointReached = true;
			}
			
			if((this.currentBigStep == 60) && !revivalPointReached){
				this.simulator.bringNodeBackOnline(failAndIntroduceNode.getId());
				revivalPointReached = true;
			}
		}
		
		//Make sure no adaptation is still ongoing
		assertFalse(this.trafficSystemIsBusyHealing());
		
		//Check if there is a single member organization for camera 4
		ArrayList<NodeID> nodes = new ArrayList<NodeID>();
		nodes.add(failAndIntroduceNode);
		this.testConsistentOrganization(nodes);
		
		//Check if the organization of which camera 3 is a member is consistent
		NodeID camera3Master = this.getOrganizationContext(new NodeID(3)).getPersonalOrg().getMasterID();
		this.testOrganizationConsistency(camera3Master);
		
		//Check if the organization of which camera 5 is a member is consistent
		NodeID camera5Master = this.getOrganizationContext(new NodeID(5)).getPersonalOrg().getMasterID();
		this.testOrganizationConsistency(camera5Master);
	}
	
	@Test
	/**
	 * CameraIntroduction(Subject 4) - Case B (join neighbor organization as slave)
	 */
	public void testCameraIntroductionCaseB(){
		NodeID failAndIntroduceNode = new NodeID(4);

		//An execution step high enough just to be sure adaptation can succeed
		int testEndStep = 200;

		boolean failurePointReached = false;
		boolean revivalPointReached = false;

		while(this.currentBigStep < testEndStep){ 

			this.simulationStep();    

			if(this.currentBigStep == 1){
				this.simulator.switchBottleNeck(1);
				this.simulator.updateRate(20);
			}
			
			if(this.currentBigStep == 120){
				this.simulator.updateRate(0);
			}

			if((this.currentBigStep == 150) && !failurePointReached){
				this.simulator.failNode(failAndIntroduceNode.getId());
				failurePointReached = true;
			}

			if((this.currentBigStep == 170) && !revivalPointReached){
				this.simulator.bringNodeBackOnline(failAndIntroduceNode.getId());
				revivalPointReached = true;
			}
		}
		
		//Make sure no adaptation is still ongoing
		assertFalse(this.trafficSystemIsBusyHealing());
		
		//Check if there is a consistent organization with cameras 3 and 4 as members
		ArrayList<NodeID> nodes = new ArrayList<NodeID>();
		nodes.add(failAndIntroduceNode);
		nodes.add(new NodeID(3));
		this.testConsistentOrganization(nodes);
		//Check that in this organization, camera 3 is the master and 4 is the slave
		Organization camera3Org = this.getOrganizationContext(new NodeID(3)).getPersonalOrg();
		assertTrue(camera3Org.getMasterID().equals(new NodeID(3)));
		assertTrue((camera3Org.getAgents().size()==2) && camera3Org.getAgents().contains(failAndIntroduceNode) &&
				camera3Org.getAgents().contains(new NodeID(3)));		
		
		//Check if the organization of which camera 2 is a member is consistent
		NodeID camera2Master = this.getOrganizationContext(new NodeID(2)).getPersonalOrg().getMasterID();
		this.testOrganizationConsistency(camera2Master);
		
		//Check if the organization of which camera 5 is a member is consistent
		NodeID camera5Master = this.getOrganizationContext(new NodeID(5)).getPersonalOrg().getMasterID();
		this.testOrganizationConsistency(camera5Master);
	}	
	
	@Test
	/**
	 * CameraIntroduction(Subject 3) - Case C (join spanning organization as slave)
	 */
	public void testCameraIntroductionCaseC(){
		NodeID failAndIntroduceNode = new NodeID(3);
		
		//An execution step high enough just to be sure adaptation can succeed
		int testEndStep = 150;
		
		boolean failurePointReached = false;
		boolean revivalPointReached = false;
		
		while(this.currentBigStep < testEndStep){	
			
			this.simulationStep();		
			
			//Make sure camera 4 sees congestion
			if(this.currentBigStep == 1){
				this.simulator.switchBottleNeck(1);
				this.simulator.updateRate(20);
			}			
			//Make sure camera 2 sees congestion
			if(this.currentBigStep == 35){
				this.simulator.switchBottleNeck(0);
			}
			if(this.currentBigStep == 65){
				this.simulator.updateRate(0);
			}
			
			if((this.currentBigStep == 100) && !failurePointReached){
				this.simulator.failNode(failAndIntroduceNode.getId());
				failurePointReached = true;
				
				this.simulator.updateRate(0);
			}
			
			if((this.currentBigStep == 130) && !revivalPointReached){
				this.simulator.bringNodeBackOnline(failAndIntroduceNode.getId());
				revivalPointReached = true;
			}
		}
		
		//Make sure no adaptation is still ongoing
		assertFalse(this.trafficSystemIsBusyHealing());
		
		//Check if there is a single member organization for camera 3
		ArrayList<NodeID> nodes = new ArrayList<NodeID>();
		nodes.add(failAndIntroduceNode);
		this.testConsistentOrganization(nodes);
		
		//Check if there is a single member organization for camera 2
		nodes.clear();
		nodes.add(new NodeID(2));
		this.testConsistentOrganization(nodes);
		
		//Check if there is a single member organization for camera 4
		nodes.clear();
		nodes.add(new NodeID(4));
		this.testConsistentOrganization(nodes);
	}
	
	@Test
	/**
	 * Verify that the new single member organization on the reintroduced camera 4
	 * is able to merge with a neighboring organization
	 */
	public void testMergeAfterCaseAIntroduction(){		
		NodeID failAndIntroduceNode = new NodeID(4);

		//An execution step high enough just to be sure adaptation can succeed
		int testEndStep = 180;

		boolean failurePointReached = false;
		boolean revivalPointReached = false;

		while(this.currentBigStep < testEndStep){ 

			this.simulationStep();    

			if(this.currentBigStep == 1){
				this.simulator.updateRate(20);
			}
			
			if((this.currentBigStep == 50) && !failurePointReached){
				this.simulator.failNode(failAndIntroduceNode.getId());
				failurePointReached = true;
			}
			
			if((this.currentBigStep == 60) && !revivalPointReached){
				this.simulator.bringNodeBackOnline(failAndIntroduceNode.getId());
				revivalPointReached = true;
			}
			
			//Create congestion so merging will occur
			if(this.currentBigStep == 70){
				this.simulator.switchBottleNeck(1);
			}
		}
		
		//Check if there is an organization with cameras 3 and 4 as members
		ArrayList<NodeID> nodes = new ArrayList<NodeID>();
		nodes.add(failAndIntroduceNode);
		nodes.add(new NodeID(3));
		this.testConsistentOrganization(nodes);
	}
	
	@Test
	/**
	 * Verify that the organization in which the reintroduced camera 4 is a slave
	 * is able to merge with a neighboring organization
	 */
	public void testMergeAfterCaseBIntroduction(){
		NodeID failAndIntroduceNode = new NodeID(4);

		//An execution step high enough just to be sure adaptation can succeed
		int testEndStep = 200;

		boolean failurePointReached = false;
		boolean revivalPointReached = false;

		while(this.currentBigStep < testEndStep){ 

			this.simulationStep();    

			if(this.currentBigStep == 1){
				this.simulator.switchBottleNeck(1);
				this.simulator.updateRate(20);
			}

			if((this.currentBigStep == 150) && !failurePointReached){
				this.simulator.failNode(failAndIntroduceNode.getId());
				failurePointReached = true;
			}

			if((this.currentBigStep == 170) && !revivalPointReached){
				this.simulator.bringNodeBackOnline(failAndIntroduceNode.getId());
				revivalPointReached = true;
			}
		}
		//Check if there is a consistent organization with cameras 2, 3 and 4 as members
		ArrayList<NodeID> nodes = new ArrayList<NodeID>();
		nodes.add(failAndIntroduceNode);
		nodes.add(new NodeID(2));
		nodes.add(new NodeID(3));
		this.testConsistentOrganization(nodes);
	}
	
	@Test
	/**
	 * Test Case A camera introduction after multiple failures of adjacent cameras, useful for verifying
	 * some of the logic in NeighborNodeFailureScenario's M41 and M42 computations
	 */
	public void testIntroductionAfterMultipleAdjacentFailures(){	
		boolean failureNode3PointReached = false;
		boolean failureNode4PointReached = false;
		boolean revivalNode4PointReached = false;
		
		//An execution step high enough just to be sure healing can succeed
		int testEndStep = 90;	
		
		while(this.currentBigStep < testEndStep){	
			
			this.simulationStep();		
			
			if(this.currentBigStep == 1){
				this.simulator.updateRate(20);
			}
			
			//Failure 1: node 3
			if((this.currentBigStep == 10) && !failureNode3PointReached){				
				this.simulator.failNode(3);
				failureNode3PointReached = true;
			}
			
			//Failure 2: node 4
			if((this.currentBigStep == 30) && !failureNode4PointReached){				
				this.simulator.failNode(4);				
				failureNode4PointReached = true;				
			}
			
			//Camera Introduction, but only of node 4
			if((this.currentBigStep == 50) && !revivalNode4PointReached){				
				this.simulator.bringNodeBackOnline(4);		
				revivalNode4PointReached = true;				
			}
		}
		
		//Make sure no adaptation is still ongoing
		assertFalse(this.trafficSystemIsBusyHealing());
		
		//Check if there is a single member organization for camera 2
		ArrayList<NodeID> nodes = new ArrayList<NodeID>();
		nodes.add(new NodeID(2));
		this.testConsistentOrganization(nodes);
		
		//Check if there is a single member organization for camera 4
		nodes.clear();
		nodes.add(new NodeID(4));
		this.testConsistentOrganization(nodes);
		
		//Check if there is a single member organization for camera 5
		nodes.clear();
		nodes.add(new NodeID(5));
		this.testConsistentOrganization(nodes);
		
		//Verify that the neighbors of camera 2 are cameras 1 and 4
		// 2 should be registered as a neighbor camera on 1 and 4
		ArrayList<NodeID> neighborsOf2 = this.findNeigborNodesOf(new NodeID(2));
		assertTrue((neighborsOf2.size()==2) && 
				(neighborsOf2.contains(new NodeID(1))) && (neighborsOf2.contains(new NodeID(4))));
		// 2 should have 1 and 4 registered as its neighbor cameras
		ArrayList<NodeID> neighborsOn2 = this.findNeigborNodesOn(new NodeID(2));
		assertTrue((neighborsOn2.size()==2) && 
				(neighborsOn2.contains(new NodeID(1))) && (neighborsOn2.contains(new NodeID(4))));		
		
		//Verify that the neighbors of camera 4 are cameras 2 and 5
		// 4 should be registered as a neighbor camera on 2 and 5
		ArrayList<NodeID> neighborsOf4 = this.findNeigborNodesOf(new NodeID(4));
		assertTrue((neighborsOf4.size()==2) && 
				(neighborsOf4.contains(new NodeID(2))) && (neighborsOf4.contains(new NodeID(5))));
		// 4 should have 2 and 5 registered as its neighbor cameras
		ArrayList<NodeID> neighborsOn4 = this.findNeigborNodesOn(new NodeID(4));
		assertTrue((neighborsOn4.size()==2) && 
				(neighborsOn4.contains(new NodeID(2))) && (neighborsOn4.contains(new NodeID(5))));		
		
		//Verify that the neighbors of camera 5 are cameras 4 and 6
		// 5 should be registered as a neighbor camera on 4 and 6
		ArrayList<NodeID> neighborsOf5 = this.findNeigborNodesOf(new NodeID(5));
		assertTrue((neighborsOf5.size()==2) && 
				(neighborsOf5.contains(new NodeID(4))) && (neighborsOf5.contains(new NodeID(6))));
		// 5 should have 4 and 6 registered as its neighbor cameras
		ArrayList<NodeID> neighborsOn5 = this.findNeigborNodesOn(new NodeID(5));
		assertTrue((neighborsOn5.size()==2) && 
				(neighborsOn5.contains(new NodeID(4))) && (neighborsOn5.contains(new NodeID(6))));	
	}
	
	
	@Test
	/** 
	 * In order:
	 * - MasterWithSlavesNodeFailure(Subject 4)
	 * - CameraIntroduction(Subject 4) - Case B (join neighbor organization as slave)
	 */
	public void testMessageProfiling(){
		//Enable message profiling
		this.profileMessages = true;
		
		//Set message profiler execution cycle print interval
		SelfAdaptationMessageProfiler.getProfiler().setExecutionCyclePrintInterval(100);
//		SelfAdaptationMessageProfiler.getProfiler().setExecutionCyclePrintInterval(0);
		
		//Register all message
		SelfAdaptationMessageProfiler.getProfiler().setAdaptationProfilingOnly(false);
		
		NodeID failAndIntroduceNode = new NodeID(4);
		
		//An execution step high enough just to be sure adaptation can succeed
		int testEndStep = 350;
		
		boolean failurePointReached = false;
		boolean revivalPointReached = false;
		
		while(this.currentBigStep < testEndStep){	
			
			this.simulationStep();		
			
			if(this.currentBigStep == 1){
				this.simulator.switchBottleNeck(1);
				this.simulator.updateRate(20);
			}
			
			if(this.currentBigStep == 100){
				this.simulator.updateRate(0);
			}
			
			if((this.currentBigStep == 170) && !failurePointReached){
				this.simulator.failNode(failAndIntroduceNode.getId());
				failurePointReached = true;
				
				System.out.println("masterwithslavesnodefailure at cycle " + this.step);
			}
			
			if((this.currentBigStep == 210) && !revivalPointReached){
				this.simulator.bringNodeBackOnline(failAndIntroduceNode.getId());
				revivalPointReached = true;
				
				System.out.println("camera introduction case B at cycle " + this.step);
			}
			
			if(this.currentBigStep == 210){
				this.simulator.updateRate(20);
			}
		}
	}	

}
