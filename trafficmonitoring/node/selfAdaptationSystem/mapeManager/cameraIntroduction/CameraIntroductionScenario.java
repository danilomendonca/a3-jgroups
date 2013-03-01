package node.selfAdaptationSystem.mapeManager.cameraIntroduction;

import utilities.NodeID;
import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.ControlLoopEnd;
import node.selfAdaptationSystem.mapeManager.MapeComputation;
import node.selfAdaptationSystem.mapeManager.SelfConfigurationScenario;
import node.selfAdaptationSystem.mapeManager.cameraIntroduction.caseA.*;
import node.selfAdaptationSystem.mapeManager.cameraIntroduction.caseB.*;
import node.selfAdaptationSystem.selfAdaptationController.SelfAdaptationController.LocalTrafficSystemRoleType;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class CameraIntroductionScenario 
				extends SelfConfigurationScenario<CameraIntroductionScenario.CameraIntroductionRole> {
	
	/**
	 * Create a new instance of a CameraIntroduction-scenario in order to monitor and respond to
	 * the given subject node coming online and needing to be introduced into a working traffic system
	 */
	public CameraIntroductionScenario(NodeID aliveSubject, SelfAdaptationModels models, 
																	BaseLevelConnector baseLevel){
		super(aliveSubject, baseLevel, models);
		
		//Set up Reflective Computations		
		//Scenario Part 1		
		M51 m51 = new M51(this, models, baseLevel);
		A51 a51 = new A51(this, models);
		//Scenario Part 2		
		P5 p5 = new P5(this, models);
		E5 e5 = new E5(this, models, baseLevel);
		//Scenario Part 3		
		M52 m52 = new M52(this, models, baseLevel);
		A52 a52 = new A52(this, models);
		//Scenario Case A - Part 1	
		P5A1 p5a1 = new P5A1(this, models);
		E5A1 e5a1 = new E5A1(this, models, baseLevel);
		//Scenario Case A - Part 2
		P5A2 p5a2 = new P5A2(this, models);
		E5A2 e5a2 = new E5A2(this, models, baseLevel);
		//Scenario Case A - End
		End5A endA = new End5A(this);
		//Scenario Case B - Part 1	
		P5B1 p5b1 = new P5B1(this, models);
		E5B1 e5b1 = new E5B1(this, models, baseLevel);
		//Scenario Case B - Part 2
		P5B2 p5b2 = new P5B2(this, models);
		E5B2 e5b2 = new E5B2(this, models, baseLevel);
		//Scenario Case B - End
		End5B endB = new End5B(this);
		
		
		//Add computations to scenario
		this.addReflectiveComputation(m51);
		this.addReflectiveComputation(a51);
		this.addReflectiveComputation(p5);
		this.addReflectiveComputation(e5);
		this.addReflectiveComputation(m52);
		this.addReflectiveComputation(a52);
		this.addReflectiveComputation(p5a1);
		this.addReflectiveComputation(e5a1);
		this.addReflectiveComputation(p5a2);
		this.addReflectiveComputation(e5a2);
		this.addReflectiveComputation(endA);
		this.addReflectiveComputation(p5b1);
		this.addReflectiveComputation(e5b1);
		this.addReflectiveComputation(p5b2);
		this.addReflectiveComputation(e5b2);
		this.addReflectiveComputation(endB);
		
		
		//Set up start computation
		this.setStartComputation(m51);
	}
	
	/**************************	 
	 * 
	 *	Scenario Identifier
	 *
	 **************************/
	
	@Override
	public String getScenarioType() {
		return CameraIntroductionScenario.scenarioType;
	}
	
	public static final String scenarioType = "CameraIntroduction";
	
	
	/**************************	 
	 * 
	 *	Scenario Roles
	 *
	 **************************/
	
	public enum CameraIntroductionRole { SUBJECT, MASTER_OF_NEIGHBOR_ORGANIZATION, NEW_SUBJECT_MASTER }
	
	@Override
	/**
	 * Makes sure, given the current local traffic system role, that the appropriate scenario
	 * role for the local camera within the context of this scenario is set.
	 * 
	 * - If this node is the subject node of this scenario, the CameraIntroductionRole is SUBJECT
	 * - If this node is a master of an organization neighboring on the scenario subject, 
	 * the CameraIntroductionRole is MASTER_OF_NEIGHBOR_ORGANIZATION
	 * - If this node, as a master of an organization neighboring on the subject node, participates
	 * in the first part of the case B control loop computations, i.e. P5B1 & E5B1, the CameraIntroductionRole
	 * is NEW_SUBJECT_MASTER. Also, if its previous scenario role was NEW_SUBJECT_MASTER, it should stay the same.
	 */
	protected void updateCurrentScenarioRole(LocalTrafficSystemRoleType currentRole){
		CameraIntroductionRole newRole;
		if(getSubject().equals(this.getSelfAdaptationModels().getHostNode()))
			newRole = CameraIntroductionRole.SUBJECT;
		else{
			//Check if computations P5B1 or E5B1 are active
			boolean specificCaseBComputationsActive = false;
			for(MapeComputation activeComputation : this.getActiveComputations()){
				if(activeComputation.getComputationID().equals("P5B1") ||
						activeComputation.getComputationID().equals("E5B1")){
					specificCaseBComputationsActive = true;
				}
			}
			
			//If these specific computations are active, or if the scenario's current role already is so,
			// the CameraIntroductionRole should be NEW_SUBJECT_MASTER
			if(specificCaseBComputationsActive || this.getRole() == CameraIntroductionRole.NEW_SUBJECT_MASTER){
				newRole = CameraIntroductionRole.NEW_SUBJECT_MASTER;
			}
			else{
				newRole = CameraIntroductionRole.MASTER_OF_NEIGHBOR_ORGANIZATION;
			}
		}
			
		
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
		CameraIntroductionComputation executionComp = (CameraIntroductionComputation) computation;

		//Execute computation based on the current role of the self-adaptation subsystem within the context of this scenario
		if(this.getRole() == CameraIntroductionRole.SUBJECT)
			executionComp.executeOnAliveSubjectNode();
		if(this.getRole() == CameraIntroductionRole.MASTER_OF_NEIGHBOR_ORGANIZATION)
			executionComp.executeOnNeighborMasterNode();
		if(this.getRole() == CameraIntroductionRole.NEW_SUBJECT_MASTER)
			executionComp.executeOnNewSubjectMasterNode();
	}
	
	@Override
	protected boolean isBusyAdapting(MapeComputation activeComputation){
		//If the scenario is at the end of the control loop
		if(activeComputation instanceof ControlLoopEnd)
			return false;
		
		if(activeComputation instanceof M51)
			return false;
		if(activeComputation instanceof A51)
			return false;
		
		return true;
	}
	
	
	@Override
	public boolean canHaveAsComputation(MapeComputation computation){
		return (computation instanceof CameraIntroductionComputation);
	}
	
}
