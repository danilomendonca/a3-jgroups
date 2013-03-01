package node.selfAdaptationSystem.mapeManager.slaveNodeFailureScenario;

import java.util.ArrayList;

import utilities.NodeID;
import node.organizationMiddleware.contextManager.contextDirectories.Organization;
import node.organizationMiddleware.contextManager.contextDirectories.RolePosition;
import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.ExecuteComputation;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class E32 extends ExecuteComputation implements SlaveNodeFailureComputation {

	public E32(SlaveNodeFailureScenario scenario, SelfAdaptationModels models,
				BaseLevelConnector baseLevel) {
		super(scenario, models, baseLevel);
	}
		
	
	/**************************	 
	 * 
	 *	Execution
	 *
	 **************************/		

	@Override
	/**
	 * As a master to this scenario's subject slave node:
	 * - Adapt the local organization structure in the base-level to reflect the subject slave node failure
	 */
	public void executeOnMasterNode() {
		Organization localOrg = this.getBaseLevelConnector().getLocalOrganizationContext().getPersonalOrg();
		NodeID failedSlaveSubject = this.getScenario().getSubject();
		
		//In base-level: change the local organization's agents
		ArrayList<NodeID> newAgents = new ArrayList<NodeID>();
		for(NodeID agent : localOrg.getAgents()){
			if(!agent.equals(failedSlaveSubject))
				newAgents.add(agent);
		}
		localOrg.changeAgents(newAgents);
		
		//In base-level: change the local organization's filled rolepositions
		RolePosition failedSlaveRP = null;
		for(RolePosition rp : localOrg.getFilledRolePositions()){
			if(rp.getAgentId().equals(failedSlaveSubject))
				failedSlaveRP = rp;
		}
		localOrg.removeFilledRolePosition(failedSlaveRP);
		
		//Move on the next computation: P33
		this.transition("P33");
	}	
	
	
	
	@Override
	/**
	 * As a slave node and subject of this particular Self-Healing Scenario:
	 * - Do nothing. Technically shouldn't occur (at this point in the scenario, 
	 * the subject slave node has failed)
	 * 
	 * @throws	ComputationExecutionException
	 * 			Always; this method should never be used.
	 */
	public void executeOnSubjectSlaveNode() throws ComputationExecutionException {
		throw new ComputationExecutionException("E32 Computation on Subject Slave Node");
	}

}
