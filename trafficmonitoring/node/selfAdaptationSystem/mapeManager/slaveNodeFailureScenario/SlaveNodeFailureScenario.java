package node.selfAdaptationSystem.mapeManager.slaveNodeFailureScenario;

import utilities.NodeID;
import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.ControlLoopEnd;
import node.selfAdaptationSystem.mapeManager.MapeComputation;
import node.selfAdaptationSystem.mapeManager.SelfHealingScenario;
import node.selfAdaptationSystem.selfAdaptationController.SelfAdaptationController.LocalTrafficSystemRoleType;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class SlaveNodeFailureScenario 
						extends SelfHealingScenario<SlaveNodeFailureScenario.SlaveNodeFailureRole> {

	/**
	 * Create a new instance of a SlaveNodeFailure-scenario in order to monitor given slave node
	 * and adapt to its node failure (if necessary)
	 */
	SlaveNodeFailureScenario(NodeID masterNodeSubject, SelfAdaptationModels models, 
															BaseLevelConnector baseLevel){
		super(masterNodeSubject, baseLevel, models);	
		
		//Set up Reflective Computations		
		//Scenario Part 1		
		M3 m3 = new M3(this, models, baseLevel);		
		A3 a3 = new A3(this, models);
		//Scenario Part 2	
		P31 p31 = new P31(this, models);
		E31 e31 = new E31(this, models, baseLevel);
		//Scenario Part 3	
		P32 p32 = new P32(this, models);
		E32 e32 = new E32(this, models, baseLevel);
		//Scenario Part 4	
		P33 p33 = new P33(this, models);
		E33 e33 = new E33(this, models, baseLevel);
		//Scenario End
		End3 end = new End3(this);
		
		//Add computations to scenario
		this.addReflectiveComputation(m3);
		this.addReflectiveComputation(a3);
		this.addReflectiveComputation(p31);
		this.addReflectiveComputation(e31);
		this.addReflectiveComputation(p32);
		this.addReflectiveComputation(e32);
		this.addReflectiveComputation(p33);
		this.addReflectiveComputation(e33);
		this.addReflectiveComputation(end);
		
		//Set up start computation
		this.setStartComputation(m3);
	}
	
	
	/**************************	 
	 * 
	 *	Scenario Identifier
	 *
	 **************************/
	
	@Override
	public String getScenarioType() {
		return SlaveNodeFailureScenario.scenarioType;
	}
	
	public static final String scenarioType = "SlaveNodeFailure";
	
	
	/**************************	 
	 * 
	 *	Scenario Roles
	 *
	 **************************/
	
	public enum SlaveNodeFailureRole { SUBJECT, MASTER_OF_SUBJECT }	
	
	@Override
	/**
	 * Makes sure, given the current local traffic system role, that the appropriate scenario
	 * role for the local camera within the context of this scenario is set.
	 * 
	 * - If this node is the subject node of this scenario, the SlaveNodeFailureRole is SUBJECT
	 * - If this node is a master to the scenario subject, the SlaveNodeFailureRole is MASTER_OF_SUBJECT
	 */
	protected void updateCurrentScenarioRole(LocalTrafficSystemRoleType currentRole){
		//Determine next SlaveNodeFailure role
		SlaveNodeFailureRole newRole;
		if(getSubject().equals(this.getSelfAdaptationModels().getHostNode()))
			newRole = SlaveNodeFailureRole.SUBJECT;
		else
			newRole = SlaveNodeFailureRole.MASTER_OF_SUBJECT;
		
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
		SlaveNodeFailureComputation executionComp = (SlaveNodeFailureComputation) computation;
		
		//Execute computation based on the current role of the self-healing subsystem within the context of this scenario
		if(this.getRole() == SlaveNodeFailureRole.SUBJECT)
			executionComp.executeOnSubjectSlaveNode();
		if(this.getRole() == SlaveNodeFailureRole.MASTER_OF_SUBJECT)
			executionComp.executeOnMasterNode();
	}
	
	@Override
	protected boolean isBusyAdapting(MapeComputation activeComputation){
		//If the scenario is at the end of the control loop
		if(activeComputation instanceof ControlLoopEnd)
			return false;
		
		if(activeComputation instanceof M3)
			return false;
		if(activeComputation instanceof A3)
			return false;
		
		return true;
	}
	
	
	@Override
	public boolean canHaveAsComputation(MapeComputation computation){
		return (computation instanceof SlaveNodeFailureComputation);
	}

}
