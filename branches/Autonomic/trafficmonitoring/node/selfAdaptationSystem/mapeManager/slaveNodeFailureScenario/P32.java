package node.selfAdaptationSystem.mapeManager.slaveNodeFailureScenario;

import node.organizationMiddleware.contextManager.contextDirectories.RolePosition;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.PlanComputation;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class P32 extends PlanComputation implements SlaveNodeFailureComputation {
	
	public P32(SlaveNodeFailureScenario scenario, SelfAdaptationModels models){
		super(scenario, models);
	}
			
	
	/**************************	 
	 * 
	 *	Execution
	 *
	 **************************/		

	@Override
	/**
	 * As a master to this scenario's subject slave node:
	 * - Prepare a new version of the the local organization structure that reflects the slave node failure.
	 */
	public void executeOnMasterNode() {
		//Implementation-specific: wait until the base-level system has received up-to-date neighbor information
		// (contained within RolePosition-objects from the slaves that were also direct alive neighbor nodes of
		// the failed slave node.
		for(RolePosition rp : this.getSelfAdaptationModels().getFullLocalOrganizationInformation().getFilledRolePositions()){
			if(rp.getNeighbourInfo().getNeighbours().contains(this.getScenario().getSubject())){
				//A RolePosition-object on the local master node still mentions the failed slave node as a neighbor; at
				// least one slave has not yet sent its updated neighbor information. Try again in the next execution cycle.
				return;
			}
		}
		
		//Move on to next computation: E32
		this.transition("E32");
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
		throw new ComputationExecutionException("P32 Computation on Subject Slave Node");
	}
	
}