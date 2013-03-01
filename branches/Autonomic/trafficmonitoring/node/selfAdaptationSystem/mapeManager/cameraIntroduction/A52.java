package node.selfAdaptationSystem.mapeManager.cameraIntroduction;

import java.util.ArrayList;
import java.util.Collection;

import utilities.NodeID;
import utilities.Pair;
import node.selfAdaptationSystem.coordination.protocols.notification.NotificationProtocolHandler;
import node.selfAdaptationSystem.mapeManager.AnalyzeComputation;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.selfAdaptationModels.OrganizationSnapshot;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class A52 extends AnalyzeComputation implements CameraIntroductionComputation {
	
	public A52(CameraIntroductionScenario scenario, SelfAdaptationModels models){
		super(scenario, models);
	}
	
	
	/**************************	 
	 * 
	 *	Execution
	 *
	 **************************/
	
	@Override
	/**
	 * Note: "an organization extending/spanning over a camera" implies that two or more of the camera's neighbors 
	 * all sense traffic congestion and are part of the same organization.
	 * 
	 * As a camera node that has recently come back online after having failed earlier:
	 * check to see which of these scenario cases is appropriate, allowing the associated
	 * case-specific computations to be executed:
	 * - If none of the neighbor masters notice traffic congestion, or if a single non-spanning neighbor organization
	 * does but the local subject does not, start a new single member organization through the 
	 * computations for Case A
	 * - If the local subject and one neighbor organization both sense traffic congestion, the subject can
	 * join this organization as a slave through computations for Case B. 
	 * 
	 * Note: not implemented is the third option (Case C), where an organization extending over the subject camera 
	 * notices traffic jams but this subject on the other hand does not, said organization will have to be split into
	 * two or more parts, all of them centered around a new single master organization for the subject camera.
	 * However, normal base-level traffic system functionality (i.e. splitting because of a camera in an organization
	 * not sensing traffic anymore) will ensure that this state will be realized after the completion of this scenario,
	 * specifically case B.
	 */
	public void executeOnAliveSubjectNode(){
		//Retrieve relevant computation input sent by previous computation
		@SuppressWarnings("unchecked")
		Pair<Boolean, Collection<Pair<NodeID, Boolean>>> input = 
			(Pair<Boolean, Collection<Pair<NodeID, Boolean>>>) this.getComputationTransitionMessage().getTargetComputationInput();		
		boolean localCongestion = input.getElement1();
		Collection<Pair<NodeID, Boolean>> neighborMasterCongestions = input.getElement2();
		
		//Find a congested neighbor, if any
		NodeID congestedNeighbor = null;
		ArrayList<NodeID> neighborMasters = new ArrayList<NodeID>();
		for(Pair<NodeID, Boolean> neighborInfo : neighborMasterCongestions){
			if(neighborInfo.getElement2())
				congestedNeighbor = neighborInfo.getElement1();
			
			neighborMasters.add(neighborInfo.getElement1());
		}
		
		//Find the master of the congested organization spanning of the local subject camera, if any
		ArrayList<NodeID> neighborCameras = this.getSelfAdaptationModels().getAliveNeighborNodes();
		NodeID spanningNeighborMaster = null;
		for(NodeID neighborCamera : neighborCameras){
			OrganizationSnapshot neighborOrg = this.getSelfAdaptationModels().getNeighborNodeOrganizationInformation(neighborCamera);
			
			//Check if other neighbor cameras belong to the same organization
			for(NodeID otherNeighborCamera : neighborCameras){
				int otherNeighborOrgID = this.getSelfAdaptationModels().getNeighborNodeOrganizationInformation(neighborCamera).getOrganizationId();
				if(!neighborCamera.equals(otherNeighborCamera) && (neighborOrg.getOrganizationId() == otherNeighborOrgID)){
					//Spanning organization found!
					spanningNeighborMaster = neighborOrg.getMasterNode();
				}
			}
		}		
		
		if( (congestedNeighbor == null) || 
				((congestedNeighbor != null) && (spanningNeighborMaster == null) && !localCongestion) ){
			//Case A
			
			//Notify all neighbor masters of the decision for case A (no additional NodeID is required, as the
			// local subject node will start a single member organization
			this.notifyNeighborMastersOfScenarioCase(neighborMasters, "A", null);
			
			//Move to next computation P5A1
			this.transition("P5A1");
		}
		else{
			//Case B
			
			//Notify all neighbor masters of the decision for case B, along with the id for the master chosen
			// to be the new master for this subject node
			this.notifyNeighborMastersOfScenarioCase(neighborMasters, "B", congestedNeighbor);
			
			//Move to next computation P5B1
			// send along the neighbor master chosen about to become the master of the local subject camera
			this.transition("P5B1", congestedNeighbor);
		}
	}
	
	private void notifyNeighborMastersOfScenarioCase(ArrayList<NodeID> neighborMasters, 
															String scenarioCase, NodeID optionalChosenMaster){
		Pair<String, NodeID> infoToSend = new Pair<String, NodeID>(scenarioCase, optionalChosenMaster);	
		this.notificationHandler = new NotificationProtocolHandler<Pair<String, NodeID>>(this, neighborMasters, infoToSend);
		
		this.notificationHandler.execute();
		
		System.out.println("CameraIntroductionScenario: case " + scenarioCase);
	}
	
	@Override
	/**
	 * As a master of an organization neighboring on the scenario subject camera:
	 * - Receive the subject's decision on which scenario case (A or B) is appropriate, implying the
	 * subsequent execution of the associated case-specific computations
	 */
	public void executeOnNeighborMasterNode(){
		if(this.notificationHandler == null){
			this.notificationHandler = new NotificationProtocolHandler<Pair<String, NodeID>>(this);
		}
		
		this.notificationHandler.execute();
		
		if(this.notificationHandler.hasCompleted()){
			//Analyze the notification payload to determine the chose scenario case
			Pair<String, NodeID> info = this.notificationHandler.getNotificationPayload();
			
			if(info.getElement1().equals("A")){
				//Case A
				
				//Move to next computation P5A2
				this.transition("P5A2");
			}
			else{
				//Case B
				
				//If the local master node was chosen by the subject node to act as its new master:
				// transition to computation P5B1
				if(info.getElement2().equals(this.getSelfAdaptationModels().getHostNode())){
					this.transition("P5B1");
				}
				else{
					//Transition to computation P5B2
					this.transition("P5B2");
				}
			}
		}
		else{
			//No scenario Case decision received yet; try again in next execution cycle
			return;
		}
	}
	
	private NotificationProtocolHandler<Pair<String, NodeID>> notificationHandler;
	
	
	
	@Override
	/**
	 * As the new master of the scenario subject:
	 *- Do nothing. Technically shouldn't occur (This role does not participate in this computation)
	 * 
	 * @throws	ComputationExecutionException
	 * 			Always; this method should never be used.
	 */
	public void executeOnNewSubjectMasterNode() throws ComputationExecutionException {
		throw new ComputationExecutionException("A52 Computation on New Subject Master Node");
	}

}
