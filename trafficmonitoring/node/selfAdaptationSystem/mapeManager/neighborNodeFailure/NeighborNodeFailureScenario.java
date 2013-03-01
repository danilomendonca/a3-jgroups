package node.selfAdaptationSystem.mapeManager.neighborNodeFailure;

import utilities.NodeID;
import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.ControlLoopEnd;
import node.selfAdaptationSystem.mapeManager.MapeComputation;
import node.selfAdaptationSystem.mapeManager.SelfHealingScenario;
import node.selfAdaptationSystem.selfAdaptationController.SelfAdaptationController.LocalTrafficSystemRoleType;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

/**
 * 
 * 
 * Objectives:
 * 1) Keep parts of the self-healing models up-to-date:
 * - NeighborOrganizationsReceivedThroughNeighbors
 * - LocalBoundaries
 * 2) Keep base-level up-to-date
 * - AliveNeighbors of the local camera
 */
public class NeighborNodeFailureScenario 
					extends SelfHealingScenario<NeighborNodeFailureScenario.NeighborNodeFailureRole> {
	
	/**
	 * Create a new instance of a NeighborNodeFailure-scenario in order to monitor given node
	 * and adapt to its node failure (if necessary)
	 */
	public NeighborNodeFailureScenario(NodeID masterNodeSubject, SelfAdaptationModels models, 
																	BaseLevelConnector baseLevel){
		super(masterNodeSubject, baseLevel, models);
		
		//Set up Reflective Computations		
		//Scenario Part 1		
		M41 m41 = new M41(this, models, baseLevel);		
		A41 a41 = new A41(this, models);
		//Scenario Part 2		
		M42 m42 = new M42(this, models, baseLevel);		
		A42 a42 = new A42(this, models);
		//Scenario Part 3		
		P4 p4 = new P4(this, models);
		E4 e4 = new E4(this, models, baseLevel);
		//Scenario End
		End4 end = new End4(this);
		
		//Add computations to scenario
		this.addReflectiveComputation(m41);
		this.addReflectiveComputation(a41);
		this.addReflectiveComputation(m42);
		this.addReflectiveComputation(a42);
		this.addReflectiveComputation(p4);
		this.addReflectiveComputation(e4);
		this.addReflectiveComputation(end);
		
		//Set up start computation
		this.setStartComputation(m41);
	}
	
	
	/**************************	 
	 * 
	 *	Scenario Identifier
	 *
	 **************************/
	
	@Override
	public String getScenarioType() {
		return NeighborNodeFailureScenario.scenarioType;
	}
	
	public static final String scenarioType = "NeighborNodeFailure";
	
	
	/**************************	 
	 * 
	 *	Scenario Roles
	 *
	 **************************/
	
	public enum NeighborNodeFailureRole { SUBJECT, SUBJECT_NEIGHBOR }	
	
	@Override
	/**
	 * Makes sure, given the current local traffic system role, that the appropriate scenario
	 * role for the local camera within the context of this scenario is set.
	 * 
	 * - If this node is the subject node of this scenario, the NeighborNodeFailureRole is SUBJECT
	 * - If this node is a direct alive neighbor node of the subject, the NeighborNodeFailureRole is SUBJECT_NEIGHBOR
	 */
	protected void updateCurrentScenarioRole(LocalTrafficSystemRoleType currentRole){
		//Determine new NeighborNodeFailure role
		NeighborNodeFailureRole newRole;
		if(getSubject().equals(this.getSelfAdaptationModels().getHostNode()))
			newRole = NeighborNodeFailureRole.SUBJECT;
		else
			newRole = NeighborNodeFailureRole.SUBJECT_NEIGHBOR;
		
		this.setRole(newRole);
	}
	
	
	/**************************	 
	 * 
	 *	Execution
	 *
	 **************************/
	
	@Override
	/**
	 * Execute the given computation in the context of this scenario instance
	 * 
	 * @throws	ComputationExecutionException
	 */
	protected void execute(MapeComputation computation) throws ComputationExecutionException {		
		NeighborNodeFailureComputation executionComp = (NeighborNodeFailureComputation) computation;

		//Execute computation based on the current role of the self-healing subsystem within the context of this scenario
		if(this.getRole() == NeighborNodeFailureRole.SUBJECT)
			executionComp.executeOnSubjectNode();
		if(this.getRole() == NeighborNodeFailureRole.SUBJECT_NEIGHBOR)
			executionComp.executeOnSubjectNeighborNode();
	}
	
	@Override
	protected boolean isBusyAdapting(MapeComputation activeComputation){
		//If the scenario is at the end of the control loop
		if(activeComputation instanceof ControlLoopEnd)
			return false;
		
		if(activeComputation instanceof M41)
			return false;
		if(activeComputation instanceof A41)
			return false;
		
		return true;
	}
	
	
	@Override
	public boolean canHaveAsComputation(MapeComputation computation){
		return (computation instanceof NeighborNodeFailureComputation);
	}

}
