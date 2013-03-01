package node.selfAdaptationSystem.mapeManager.cameraIntroduction.caseB;

import java.util.ArrayList;

import utilities.NodeID;
import node.selfAdaptationSystem.coordination.protocols.notification.NotificationProtocolHandler;
import node.selfAdaptationSystem.mapeManager.PlanComputation;
import node.selfAdaptationSystem.mapeManager.cameraIntroduction.CameraIntroductionComputation;
import node.selfAdaptationSystem.mapeManager.cameraIntroduction.CameraIntroductionScenario;
import node.selfAdaptationSystem.selfAdaptationModels.OrganizationSnapshot;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class P5B2 extends PlanComputation implements CameraIntroductionComputation {
	
	public P5B2(CameraIntroductionScenario scenario, SelfAdaptationModels models){
		super(scenario, models);
	}
	
	
	/**************************	 
	 * 
	 *	Execution
	 *
	 **************************/
	
	@Override
	/**
	 * As the new master of the scenario subject:
	 * - Notify both the scenario subject (now a slave to this local master) and the masters of all neighboring organizations
	 * to notify them of the successful introduction of the subject camera in the working traffic system, and that all
	 * base-level operations can safely continue
	 */
	public void executeOnNewSubjectMasterNode(){
		//Wait until the base-level has received up-to-date information on all the current neighbor organizations 
		// of the organization of this local master.
		//Implementation-specific: because the NeighborNodeFailureScenario on this node has changed the base-level
		// list of alive neighbor nodes, the base-level temporarily considers the newly introduced camera as a new
		// single master organization. Wait until this is resolved and up-to-date information on all neighbor
		// organizations is available
		//TODO: properly solve this ...
		if(this.waitingPeriod-- > 0)
			return;
		
		//Notify all receivers.
		ArrayList<NodeID> receivers = new ArrayList<NodeID>();
		receivers.add(this.getScenario().getSubject());
		for(OrganizationSnapshot neighborOrg : this.getSelfAdaptationModels().getNeighborOrganizations()){
			receivers.add(neighborOrg.getMasterNode());
		}
		
		// Note: no payload required
		this.notificationHandler = new NotificationProtocolHandler<Void>(this, receivers, null);
		
		this.notificationHandler.execute();
		
		//Move on to next computation: E5B2
		this.transition("E5B2");
	}
	
	private int waitingPeriod = 5;	//in execution cycles
	
	@Override
	/**
	 * As a camera node that has recently come back online after having failed earlier:
	 * - Receive notification from the new master of this local subject to re-allow all base-level operations
	 */
	public void executeOnAliveSubjectNode(){
		if(this.notificationHandler == null){
			this.notificationHandler = new NotificationProtocolHandler<Void>(this);
		}
		
		this.notificationHandler.execute();
		
		if(this.notificationHandler.hasCompleted()){
			//Move on to next computation: E5B2
			this.transition("E5B2");
		}
	}	
	
	@Override
	/**
	 * As a master of an organization neighboring on the scenario subject camera, but not to be become the
	 * scenario subject's new master node:
	 * - Receive notification from the new master of the scenario subject to re-allow all base-level operations
	 */
	public void executeOnNeighborMasterNode(){
		if(this.notificationHandler == null){
			this.notificationHandler = new NotificationProtocolHandler<Void>(this);
		}
		
		this.notificationHandler.execute();
		
		if(this.notificationHandler.hasCompleted()){
			//Move on to next computation: E5B2
			this.transition("E5B2");
		}
	}
	
	private NotificationProtocolHandler<Void> notificationHandler;

}
