package node.selfAdaptationSystem.coordination.protocols.notification;

import java.util.List;

import utilities.NodeID;

import node.selfAdaptationSystem.coordination.protocols.CoordinationProtocolHandler;
import node.selfAdaptationSystem.mapeManager.MapeComputation;

public class NotificationProtocolHandler<I>
					extends CoordinationProtocolHandler<NotificationProtocolHandler.NotificationProtocolRole> {

	public enum NotificationProtocolRole { NOTIFIER, NOTIFICATION_RECEIVER }
	
	
	public NotificationProtocolHandler(MapeComputation localMapeComputation, 
										List<NodeID> notificationReceivers, I notificationPayload){
		super(NotificationProtocolRole.NOTIFIER, localMapeComputation);
		
		this.notificationReceivers = notificationReceivers;
		this.notificationPayload = notificationPayload;
	}
	
	public NotificationProtocolHandler(MapeComputation localMapeComputation){
		super(NotificationProtocolRole.NOTIFICATION_RECEIVER, localMapeComputation);
	}
	
	
	/**************************	 
	 * 
	 *	Execution
	 *
	 **************************/
	
	@Override
	/**
	 * If this handler is occupying the NotificationProtocolRole of NOTIFIER:
	 * - 
	 * 
	 * If this handler is occupying the NotificationProtocolRole of NOTIFICATION_RECEIVER:
	 * -
	 */
	public void execute() {
		if(this.getProtocolRole() == NotificationProtocolRole.NOTIFIER)
			this.executeOnNotifier();
		else
			this.executeOnNotificationReceiver();
	}
	
	@Override
	/**
	 * If this handler is occupying the NotificationProtocolRole of NOTIFIER:
	 * - true if a notification message to every receiver has been sent
	 * 
	 * If this handler is occupying the NotificationProtocolRole of NOTIFICATION_RECEIVER:
	 * - true if a notification message has been received
	 */
	public boolean hasCompleted(){
		return this.hasCompleted;
	}	
	
	private boolean hasCompleted;	
	private List<NodeID> notificationReceivers;
	
	public I getNotificationPayload(){
		return this.notificationPayload;
	}
	
	private I notificationPayload;
	
	
	/**************************	 
	 * 
	 *	Execution on
	 *	NOTIFIER
	 *
	 **************************/
	
	private void executeOnNotifier(){
		//Send notifications (along with possible payload) to all receivers
		for(NodeID receiver : this.notificationReceivers){
			this.sendNotification(receiver);
		}
		
		//Notifier job done
		this.hasCompleted = true;
	}
	
	private void sendNotification(NodeID destinationNode) {
		this.getMapeComputation().sendRemoteMessage(destinationNode, new NotificationMessage<I>(this.notificationPayload));
	}
	
	
	/**************************	 
	 * 
	 *	Execution on
	 *	NOTIFICATION_RECEIVER
	 *
	 **************************/
	
	private void executeOnNotificationReceiver(){
		if(this.getMapeComputation().hasNextRemoteMessage()){
			//Notification message received; register payload
			@SuppressWarnings("unchecked")
			NotificationMessage<I> message = (NotificationMessage<I>) this.getMapeComputation().getNextRemoteMessage();
			this.notificationPayload = message.getNotificationPayload();
			
			//Receiver job done
			this.hasCompleted = true;
		}
		else{
			//Nothing received yet; try again during next execution cycle
			return;
		}
	}
}
