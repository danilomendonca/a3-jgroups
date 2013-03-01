package node.selfAdaptationSystem.mapeManager.masterWithSlavesNodeFailureScenario;

import utilities.NodeID;
import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.coordination.protocols.pingEcho.PingEchoProtocolHandler;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.MonitorComputation;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class M2 extends MonitorComputation implements MasterWithSlavesNodeFailureComputation {
	
	public M2(MasterWithSlavesNodeFailureScenario scenario, SelfAdaptationModels models, 
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
	 * As a master node and subject of this particular Self-Healing Scenario:
	 * - respond to ping-messages from other scenario participants 
	 */
	public void executeOnSubjectMasterNode(){
		//Initialize CoordinationHandler (with TARGET role) if necessary
		if(this.pingEchoHandler == null)
			this.pingEchoHandler = new PingEchoProtocolHandler(this, this.getSelfAdaptationModels().getHostNode());
		
		this.pingEchoHandler.execute();
		
		//No need to move on to next computation as subject node
	}
	
	@Override
	/**
	 * As a master of an organization adjacent to the organization of this scenario's subject master node:
	 * - periodically send ping-messages to the subject
	 * - process echo-replies
	 * - log all active inter-organizational base-level communication with the subject
	 */
	public void executeOnNeighborMasterNode(){		
		this.defaultNonSubjectImplementation();
	}
	
	@Override
	/**
	 * As a slave to this scenario's subject master node:
	 * - periodically send ping-messages to the subject
	 * - process echo-replies
	 * - log all active intra-organizational base-level communication with the subject
	 */
	public void executeOnSlaveNode(){		
		this.defaultNonSubjectImplementation();
	}
	
	private void defaultNonSubjectImplementation(){

		//---HACK---
		//Problem: let the (new) master initialize its scenarios before sending ping-messages
		//TODO: Properly solve this!!
		if(this.waitingCyclesLeft > 0){
			this.waitingCyclesLeft--;
			
			//Stay in this computation for now
			return;
		}
		
		
		//Initialize CoordinationHandler (with SOURCE role) if necessary
		if(this.pingEchoHandler == null){
			NodeID pingTarget = this.getScenario().getSubject();
			int minimumEchoTimestampExecutionCycleAge = 100;
			
			this.pingEchoHandler = new PingEchoProtocolHandler(this, pingTarget, minimumEchoTimestampExecutionCycleAge);
		}			
		
		this.pingEchoHandler.execute();
		
		
		//Implementation-specific: logging of inter-org communication not strictly needed
		
		//Move on to next computation: A2
		// Provide PingEchoInformation from CoordinationHandler
		this.transition("A2", this.pingEchoHandler.getPingEchoInformation());
	}
	
	private int waitingCyclesLeft = 100;
	
	private PingEchoProtocolHandler pingEchoHandler;
	
	
	
	
	@Override
	/**
	 * As the newly elected master of the organization of which this scenario's subject was the original master:
	 * - Do nothing. Technically shouldn't occur (no new master has been elected yet)
	 * 
	 * @throws	ComputationExecutionException
	 * 			Always; this method should never be used.
	 */
	public void executeOnNewMasterNode() throws ComputationExecutionException {
		throw new ComputationExecutionException("M2 Computation on New Master Node");
	}	

}
