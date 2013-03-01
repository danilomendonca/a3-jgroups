package node.selfAdaptationSystem.mapeManager.slaveNodeFailureScenario;

import utilities.NodeID;
import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.coordination.protocols.pingEcho.PingEchoProtocolHandler;
import node.selfAdaptationSystem.mapeManager.MonitorComputation;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class M3 extends MonitorComputation implements SlaveNodeFailureComputation {

	public M3(SlaveNodeFailureScenario scenario, SelfAdaptationModels models, 
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
	 * As a slave node and subject of this particular Self-Healing Scenario:
	 * - respond to ping-messages from this node's master node
	 */
	public void executeOnSubjectSlaveNode() {
		//Initialize CoordinationHandler (with TARGET role) if necessary
		if(this.pingEchoHandler == null)
			this.pingEchoHandler = new PingEchoProtocolHandler(this, this.getSelfAdaptationModels().getHostNode());
		
		this.pingEchoHandler.execute();
		
		//No need to move on to next computation as subject node
	}

	@Override
	/**
	 * As a master to this scenario's subject slave node
	 * - periodically send ping-messages to the subject
	 * - process echo-replies
	 */
	public void executeOnMasterNode() {

		//---HACK---
		//Problem: let the slave initialize its scenarios before sending ping-messages
		//TODO: Properly solve this!!
		if(this.waitingCyclesLeft > 0){
			this.waitingCyclesLeft--;
			
			//Stay in this computation for now
			return;
		}
		
		
		//Initialize CoordinationHandler (with SOURCE role) if necessary
		if(this.pingEchoHandler == null){
			NodeID pingTarget = this.getScenario().getSubject();
			int minimumEchoTimestampExecutionCycleAge = 30;
			
			this.pingEchoHandler = new PingEchoProtocolHandler(this, pingTarget, minimumEchoTimestampExecutionCycleAge);
		}		
		
		this.pingEchoHandler.execute();
		
		
		//Implementation-specific: logging of inter-org communication not strictly needed
		
		//Move on to next computation: A3
		// Provide PingEchoInformation from CoordinationHandler
		this.transition("A3", this.pingEchoHandler.getPingEchoInformation());
	}
	
	private int waitingCyclesLeft = 100;
	
	private PingEchoProtocolHandler pingEchoHandler;

}