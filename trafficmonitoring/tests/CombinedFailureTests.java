package tests;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import utilities.NodeID;

public class CombinedFailureTests extends SelfAdaptationTests {
	
	@Test
	/**
	 * Test when several adjacent nodes fail one after another
	 * - Failure 1: Node 3
	 * - Failure 2: Node 4
	 * - Failure 3: Node 2 
	 */
	public void testAdjacentNodeFailures(){
		boolean failureNode3PointReached = false;
		boolean failureNode4PointReached = false;
		boolean failureNode2PointReached = false;
		
		//An execution step high enough just to be sure healing can succeed
		int testEndStep = 400;	
		
		while(this.runApplication){	
			
			this.simulationStep();		
			
			if(this.currentBigStep == 1){
				this.simulator.switchBottleNeck(1);
				this.simulator.updateRate(20);
			}
			
			//Failure 1: node 3
			if((this.currentBigStep == 30) && !failureNode3PointReached){				
				this.simulator.failNode(3);
				failureNode3PointReached = true;
			}
			
			//Failure 2: node 4
			// Look for a valid system state where there's an organization with nodes 2 and 4
			ArrayList<NodeID> nodes = new ArrayList<NodeID>();
			nodes.add(new NodeID(2));
			nodes.add(new NodeID(4));
			boolean preNode4FailureSystemState = this.consistentOrganization(nodes);
			if(preNode4FailureSystemState && !failureNode4PointReached){				
				this.simulator.failNode(4);				
				failureNode4PointReached = true;				
			}
			
			//Failure 3: node 2
			// Look for a valid system state where there's an organization with nodes 1 and 2
			nodes = new ArrayList<NodeID>();
			nodes.add(new NodeID(1));
			nodes.add(new NodeID(2));
			boolean preNode2FailureSystemState = this.consistentOrganization(nodes);
			if(preNode2FailureSystemState && !failureNode2PointReached){				
				this.simulator.failNode(2);				
				failureNode2PointReached = true;				
			}
			
			//Stop application after final healing (of node 2)
			if(this.findNodesWithActiveScenarios(new NodeID(2)).size() == 0){	
				this.runApplication = false;
			}

			//Make sure, if no consistent and expected post-failure system state can be reached, not
			// to run forever
			assertTrue(this.currentBigStep <= testEndStep);
		}
		
		ArrayList<NodeID> failedNodes = new ArrayList<NodeID>();
		failedNodes.add(new NodeID(3));
		failedNodes.add(new NodeID(4));
		failedNodes.add(new NodeID(2));
		
		//Check for a valid post-failure system state where there's an organization with just node 1 and
		// and organization with just node 5
		ArrayList<NodeID> nodes = new ArrayList<NodeID>();
		nodes.add(new NodeID(1));
		this.testConsistentOrganization(nodes);
		nodes.clear();
		nodes.add(new NodeID(5));
		this.testConsistentOrganization(nodes);
		
		//Check general post-healing system properties for all failed nodes
		this.testGeneralPostHealingSystemProperties(failedNodes);
		
		//Test organization consistency for the remaining nodes
		for(NodeID camera : this.cameraNodes.keySet()){
			if(!failedNodes.contains(camera)){
				this.testOrganizationConsistency(camera);
			}
		}
		
		//All scenarios with any of the failed nodes as the scenario subject should be done
		for(NodeID failedNode : failedNodes){
			assertTrue(this.findNodesWithActiveScenarios(failedNode).size() == 0);
		}		
	}

}
