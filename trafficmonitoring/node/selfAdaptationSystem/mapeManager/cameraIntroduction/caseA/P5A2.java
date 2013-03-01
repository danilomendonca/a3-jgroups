package node.selfAdaptationSystem.mapeManager.cameraIntroduction.caseA;

import java.util.ArrayList;

import utilities.NodeID;
import node.selfAdaptationSystem.coordination.protocols.notification.NotificationProtocolHandler;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.PlanComputation;
import node.selfAdaptationSystem.mapeManager.cameraIntroduction.CameraIntroductionComputation;
import node.selfAdaptationSystem.mapeManager.cameraIntroduction.CameraIntroductionScenario;
import node.selfAdaptationSystem.selfAdaptationModels.OrganizationSnapshot;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class P5A2 extends PlanComputation implements CameraIntroductionComputation {
	
	public P5A2(CameraIntroductionScenario scenario, SelfAdaptationModels models){
		super(scenario, models);
	}
	
	
	/**************************	 
	 * 
	 *	Execution
	 *
	 **************************/
	
	@Override
	/**
	 * As a camera node that has recently come back online after having failed earlier:
	 * - Contact the masters of all neighbor organizations in order to notify them that the subject camera
	 * has been successfully introduced into the traffic monitoring system and that the earlier pausing of
	 * base-level dynamics (i.e. splitting & merging) can be undone safely
	 */
	public void executeOnAliveSubjectNode(){		
		//Notify all receivers
		ArrayList<NodeID> receivers = new ArrayList<NodeID>();
		for(OrganizationSnapshot neighborOrg : this.getSelfAdaptationModels().getAllNeighborNodeOrganizationInformation()){
			receivers.add(neighborOrg.getMasterNode());
		}
		// Note: no payload required
		this.notificationHandler = new NotificationProtocolHandler<Void>(this, receivers, null);
		
		this.notificationHandler.execute();
		
		//Move on to next computation: E5A2
		this.transition("E5A2");
	}
	
	@Override
	/**
	 * As a master of an organization neighboring on the scenario subject camera:
	 * - React to the notification sent out by the newly introduced subject camera in order to undo the
	 * earlier pausing of base-level dynamics (i.e. splitting & merging)
	 */
	public void executeOnNeighborMasterNode(){		
		//Initialize CoordinationHandler (role of NOTIFICATION_RECEIVER) if necessary
		if(this.notificationHandler == null)
			this.notificationHandler = new NotificationProtocolHandler<Void>(this);
		
		this.notificationHandler.execute();		
		
		//Check for notification message
		if(this.notificationHandler.hasCompleted()){
			//Move on to next computation: E5A2
			this.transition("E5A2");
		}
		else {			
			//Stay in this computation for now
			return;
		}
	}
	
	private NotificationProtocolHandler<Void> notificationHandler;
	
	
	
	@Override
	/**
	 * As the new master of the scenario subject:
	 *- Do nothing. Technically shouldn't occur (This role does not participate in this computation)
	 * 
	 * @throws	ComputationExecutionException
	 * 			Always; this method should never be used.
	 */
	public void executeOnNewSubjectMasterNode() throws ComputationExecutionException {
		throw new ComputationExecutionException("P5A2 Computation on New Subject Master Node");
	}

}
