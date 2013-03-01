package tests;

import node.organizationMiddleware.contextManager.contextDirectories.Organization;
import node.selfAdaptationSystem.coordination.SelfAdaptationMessageProfiler;

import org.junit.Test;

import utilities.NodeID;

public class YJunctionTests extends SelfAdaptationTests {
	
	public YJunctionTests(){
		//Configure the road network as a Y aka three-way junction
		this.setRoadNetworkConfig(1);
		
		//Set window dimensions
		this.windowXSize = 500;
		this.windowYSize = 200;
		
		//Set camera range lower, so that more cameras are needed (and placed) alongside 
		// each road segment
		this.cameraRange = 15;
		
		//Set message profiler execution cycle print interval
		SelfAdaptationMessageProfiler.getProfiler().setExecutionCyclePrintInterval(100);
	}
	
//	@Test
	public void test(){
		
		//TODO: not finished!!!
		
		//An execution step high enough just to be sure adaptation can succeed
		int testBigEndStep = 600;
		
		boolean failurePoint1Reached = false;
		int failurePoint1Step = Integer.MAX_VALUE/2;
		boolean failurePoint2Reached = false;
		int failurePoint2Step = Integer.MAX_VALUE/2;
		NodeID failedMaster = null;
		boolean revivalPointReached = false;
		
		while(this.currentBigStep < testBigEndStep){	
			
			this.simulationStep();		
			
			if(this.currentBigStep == 1){
				this.simulator.updateRate(30);
				this.simulator.switchBottleNeck(1);
			}
			
			//Check to see all roads (up until node 12) is blocked up
			if(this.getOrganizationContext(new NodeID(12)) != null && 
					this.getOrganizationContext(new NodeID(12)).getPersonalOrg() != null){
				NodeID master = this.getOrganizationContext(new NodeID(12)).getPersonalOrg().getMasterID();
				Organization masterOrg = this.getOrganizationContext(master).getPersonalOrg();			
				if((masterOrg.getAgents().size() == 12) && !failurePoint1Reached){
					//Fail slave, e.g. node 2
					this.simulator.failNode(2);
					failurePoint1Reached = true;
					failurePoint1Step = this.step;

					System.out.println("SlaveNodeFailure at step " + this.step);
				}

				if((this.step > failurePoint1Step + 100) && !failurePoint2Reached){
					//MasterNodeFailure
					this.simulator.failNode(master.getId());
					failedMaster = master;
					failurePoint2Reached = true;
					failurePoint2Step = this.step;

					System.out.println("MasterWithSlavesNodeFailure at step " + this.step);
				}

				if((this.step > failurePoint2Step + 500) && !revivalPointReached){
					//Camera reintroduction
					this.simulator.bringNodeBackOnline(failedMaster.getId());
					revivalPointReached = true;

					System.out.println("CameraIntroduction at step " + this.step);
				}
			}
			
			
		}
	}

}
