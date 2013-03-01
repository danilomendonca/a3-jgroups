package node.selfAdaptationSystem.mapeManager.masterWithSlavesNodeFailureScenario;

import utilities.NodeID;
import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.ControlLoopEnd;
import node.selfAdaptationSystem.mapeManager.MapeComputation;
import node.selfAdaptationSystem.mapeManager.SelfHealingScenario;
import node.selfAdaptationSystem.selfAdaptationController.SelfAdaptationController.LocalTrafficSystemRoleType;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class MasterWithSlavesNodeFailureScenario 
					extends SelfHealingScenario<MasterWithSlavesNodeFailureScenario.MasterWithSlavesNodeFailureRole> {
	
	/**
	 * Create a new instance of a MasterWithSlavesNodeFailure-scenario in order to monitor given master node
	 * and adapt to its node failure (if necessary)
	 */
	MasterWithSlavesNodeFailureScenario(NodeID masterNodeSubject, SelfAdaptationModels models, 
																BaseLevelConnector baseLevel){
		super(masterNodeSubject, baseLevel, models);	
		
		//Set up Reflective Computations		
		//Scenario Part 1		
		M2 m2 = new M2(this, models, baseLevel);		
		A2 a2 = new A2(this, models);
		//Scenario Part 2		
		P21 p21 = new P21(this, models);
		E21 e21 = new E21(this, models, baseLevel);
		//Scenario Part 3		
		P22 p22 = new P22(this, models);
		E22 e22 = new E22(this, models, baseLevel);
		//Scenario Part 4	
		P23 p23 = new P23(this, models);
		E23 e23 = new E23(this, models, baseLevel);
		//Scenario Part 5	
		P24 p24 = new P24(this, models);
		E24 e24 = new E24(this, models, baseLevel);
		//Scenario End
		End2 end = new End2(this);
		
		//Add computations to scenario
		this.addReflectiveComputation(m2);
		this.addReflectiveComputation(a2);
		this.addReflectiveComputation(p21);
		this.addReflectiveComputation(e21);
		this.addReflectiveComputation(p22);
		this.addReflectiveComputation(e22);
		this.addReflectiveComputation(p23);
		this.addReflectiveComputation(e23);
		this.addReflectiveComputation(p24);
		this.addReflectiveComputation(e24);
		this.addReflectiveComputation(end);
		
		//Set up start computation
		this.setStartComputation(m2);
	}
	
	
	
	/**************************	 
	 * 
	 *	Scenario Identifier
	 *
	 **************************/
	
	@Override
	public String getScenarioType() {
		return MasterWithSlavesNodeFailureScenario.scenarioType;
	}
	
	public static final String scenarioType = "MasterWithSlavesNodeFailure";
	
	
	
	/**************************	 
	 * 
	 *	Scenario Roles
	 *
	 **************************/
	
	public enum MasterWithSlavesNodeFailureRole { 	SUBJECT, 
													SLAVE_TO_SUBJECT, 
													MASTER_OF_NEIGHBOR_ORGANIZATION, 
													NEW_MASTER }
	
	@Override
	/**
	 * Makes sure, given the current local traffic system role, that the appropriate scenario
	 * role for the local camera within the context of this scenario is set.
	 * 
	 * - If this node is the subject node of this scenario, the MasterWithSlavesNodeFailureRole is SUBJECT
	 * - If this node is a slave to the scenario subject, the MasterWithSlavesNodeFailureRole is SLAVE_TO_SUBJECT
	 * - If this node is a master of an organization that is adjacent to the organization of the
	 * scenario subject, the MasterWithSlavesNodeFailureRole is MASTER_OF_NEIGHBOR_ORGANIZATION
	 * - If this node is a master, but during the previous execution cycle its scenario role was SLAVE_TO_SUBJECT,
	 * this local node is the newly elected master. The MasterWithSlavesNodeFailureRole is therefore NEW_MASTER
	 * - If this node is a master and during the previous execution cycle its scenario role was NEW_MASTER,
	 * the MasterWithSlavesNodeFailureRole stays the same
	 */
	protected void updateCurrentScenarioRole(LocalTrafficSystemRoleType currentRole){
		//Determine new MasterWithSlavesNodeFailure role
		MasterWithSlavesNodeFailureRole newRole;		
		if(this.getSubject().equals(this.getSelfAdaptationModels().getHostNode()))
			newRole = MasterWithSlavesNodeFailureRole.SUBJECT;
		else if(currentRole == LocalTrafficSystemRoleType.SLAVE)
			newRole = MasterWithSlavesNodeFailureRole.SLAVE_TO_SUBJECT;
		else 
			newRole = MasterWithSlavesNodeFailureRole.MASTER_OF_NEIGHBOR_ORGANIZATION;
		//Note: NEW_MASTER is not a default role; comes later in the (already deployed) scenario (see below)
		
		
		//Check if the role needs to change mid-execution of this scenario:		
		//If the previous MasterWithSlavesNodeFailure scenario role was SLAVE_TO_SUBJECT and the
		// current local traffic system role is not SLAVE: the local node is the newly elected master
		if(this.getRole() == MasterWithSlavesNodeFailureRole.SLAVE_TO_SUBJECT 
												&& currentRole != LocalTrafficSystemRoleType.SLAVE){
			newRole = MasterWithSlavesNodeFailureRole.NEW_MASTER;
		}		
		//If the current role is NEW_MASTER: keep it that way
		if(this.getRole() == MasterWithSlavesNodeFailureRole.NEW_MASTER)
			newRole = MasterWithSlavesNodeFailureRole.NEW_MASTER;	
		
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
		MasterWithSlavesNodeFailureComputation executionComp = (MasterWithSlavesNodeFailureComputation) computation;

		//Execute computation based on the current role of the self-healing subsystem within the context of this scenario
		if(this.getRole() == MasterWithSlavesNodeFailureRole.SUBJECT)
			executionComp.executeOnSubjectMasterNode();
		if(this.getRole() == MasterWithSlavesNodeFailureRole.SLAVE_TO_SUBJECT)
			executionComp.executeOnSlaveNode();
		if(this.getRole() == MasterWithSlavesNodeFailureRole.MASTER_OF_NEIGHBOR_ORGANIZATION)
			executionComp.executeOnNeighborMasterNode();
		if(this.getRole() == MasterWithSlavesNodeFailureRole.NEW_MASTER)
			executionComp.executeOnNewMasterNode();
	}
	
	@Override
	protected boolean isBusyAdapting(MapeComputation activeComputation){
		if(activeComputation instanceof ControlLoopEnd)
			return false;
		
		if(activeComputation instanceof M2)
			return false;
		if(activeComputation instanceof A2)
			return false;
	
		return true;
	}
	
	
	@Override
	public boolean canHaveAsComputation(MapeComputation computation){
		return (computation instanceof MasterWithSlavesNodeFailureComputation);
	}
	
}
