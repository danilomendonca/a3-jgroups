package node.selfAdaptationSystem.mapeManager.selfMonitoring.cameraMonitoring;

import utilities.NodeID;
import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.MapeComputation;
import node.selfAdaptationSystem.mapeManager.selfMonitoring.SelfMonitoringScenario;
import node.selfAdaptationSystem.selfAdaptationController.SelfAdaptationController.LocalTrafficSystemRoleType;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

/**
 * Gathers information that is relevant and critical for every camera, regardless of its current traffic system role:
 * - locally: node id and list of alive neighbor nodes
 * - Remotely from each of the current alive neighbors (if belonging to a different organization): id and master of this organization *
 */
public class CameraMonitoringScenario extends SelfMonitoringScenario<CameraMonitoringScenario.CameraMonitoringRole> {
	
	/**
	 * Create a new instance of a CameraMonitoring-scenario in order to collect information for the given
	 * camera subject
	 */
	public CameraMonitoringScenario(NodeID cameraSubject, SelfAdaptationModels models, 
																	BaseLevelConnector baseLevel){
		super(cameraSubject, baseLevel, models);
		
		//Set up Reflective Computations
		M91 m91 = new M91(this, models, baseLevel);
		M92 m92 = new M92(this, models, baseLevel);
		
		//Add computations to scenario
		this.addReflectiveComputation(m91);
		this.addReflectiveComputation(m92);
		
		//Set both computations as active
		this.setStartComputation(m91);
		this.setStartComputation(m92);
	}
	
	
	/**************************	 
	 * 
	 *	Scenario Identifier
	 *
	 **************************/
	
	@Override
	public String getScenarioType() {
		return CameraMonitoringScenario.scenarioType;
	}
	
	public static final String scenarioType = "CameraSelfMonitoring";
	
	
	/**************************	 
	 * 
	 *	Scenario Roles
	 *
	 **************************/
	
	public enum CameraMonitoringRole { SUBJECT, SUBJECT_NEIGHBOR }	
	
	@Override
	/**
	 * Makes sure, given the current local traffic system role, that the appropriate scenario
	 * role for the local camera within the context of this scenario is set.
	 * 
	 * - If this node is the subject node of this scenario, the CameraMonitoringRole is SUBJECT
	 * - If this node is a direct alive neighbor node of the subject, the CameraMonitoringRole is SUBJECT_NEIGHBOR
	 */
	protected void updateCurrentScenarioRole(LocalTrafficSystemRoleType currentRole){
		//Determine new CameraMonitoring role
		CameraMonitoringRole newRole;
		if(getSubject().equals(this.getSelfAdaptationModels().getHostNode()))
			newRole = CameraMonitoringRole.SUBJECT;
		else
			newRole = CameraMonitoringRole.SUBJECT_NEIGHBOR;
		
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
		CameraMonitoringComputation executionComp = (CameraMonitoringComputation) computation;

		//Execute computation based on the current role of the self-adaptation subsystem within the context of this scenario
		if(this.getRole() == CameraMonitoringRole.SUBJECT)
			executionComp.executeOnSubjectNode();
		if(this.getRole() == CameraMonitoringRole.SUBJECT_NEIGHBOR)
			executionComp.executeOnSubjectNeighborNode();
	}
	
	@Override
	/*
	 * This scenario never adapts the local traffic system
	 */
	protected boolean isBusyAdapting(MapeComputation activeComputation){
		return false;
	}
	

	@Override
	public boolean canHaveAsComputation(MapeComputation computation){
		return (computation instanceof CameraMonitoringComputation);
	}
	
}
