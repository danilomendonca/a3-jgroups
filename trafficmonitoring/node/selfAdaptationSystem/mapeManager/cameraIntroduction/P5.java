package node.selfAdaptationSystem.mapeManager.cameraIntroduction;

import java.util.ArrayList;

import utilities.NodeID;
import node.selfAdaptationSystem.coordination.protocols.notification.NotificationProtocolHandler;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.PlanComputation;
import node.selfAdaptationSystem.selfAdaptationModels.OrganizationSnapshot;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class P5 extends PlanComputation implements CameraIntroductionComputation {
	
	public P5(CameraIntroductionScenario scenario, SelfAdaptationModels models){
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
	 * - Contact the masters of all neighbor organizations in order to notify them that this local
	 * node is about to be resurrected and their base-level dynamics (splitting & merging) should 
	 * therefore be temporarily halted
	 */
	public void executeOnAliveSubjectNode(){
		//Implementation-specific: wait for the NeighborNodeFailureScenario to have gathered information on the
		// masters of the organizations neighboring on the local camera, allowing the subject node to contact
		// them directly
		// Check if, for each of the alive neighbor nodes, information on their current master has been received
		for(NodeID neighborNode : this.getSelfAdaptationModels().getAliveNeighborNodes()){			
			if(this.getSelfAdaptationModels().getNeighborNodeOrganizationInformation(neighborNode) == null){
				//NeighborNodeFailure has not finished completely; try again in next execution cycle
				return;
			}
		}
		
		//Notify all receivers.
		ArrayList<NodeID> receivers = new ArrayList<NodeID>();
		for(OrganizationSnapshot neighborOrg : this.getSelfAdaptationModels().getAllNeighborNodeOrganizationInformation()){
			receivers.add(neighborOrg.getMasterNode());
		}
		// Note: no payload required
		this.notificationHandler = new NotificationProtocolHandler<Void>(this, receivers, null);
		
		this.notificationHandler.execute();
		
		//Move on to next computation: E5
		this.transition("E5");
	}
	
	@Override
	/**
	 * As a master of an organization neighboring on the scenario subject camera:
	 *- React to the notification sent out by a camera neighboring on this master's organization that is
	 * looking to be re-introduced into the traffic monitoring system by planning to temporarily halt
	 * all base-level dynamics (organization splitting & merging) for the duration of this scenario
	 * 
	 * Note: this notification is an unanticipated message for this neighbor master node
	 */
	public void executeOnNeighborMasterNode(){
		//Note: for reasons of symmetry (the local role of NOTIFICATION_RECEIVER to the subject's NOTIFIER,
		// the following implementation uses a NotificationHandler. 
		// However: the fact that a CameraIntroductionScenario activates on a neighbor master node when the notification
		// message is received, means that when this computation is active one could simply transition to computation E5
		// immediately
		
		//Initialize CoordinationHandler (role of NOTIFICATION_RECEIVER) if necessary
		if(this.notificationHandler == null)
			this.notificationHandler = new NotificationProtocolHandler<Void>(this);
		
		this.notificationHandler.execute();		
		
		//Check for notification message
		if(this.notificationHandler.hasCompleted()){
			//Move on to next computation: E5
			this.transition("E5");
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
		throw new ComputationExecutionException("P5 Computation on New Subject Master Node");
	}
	
}
