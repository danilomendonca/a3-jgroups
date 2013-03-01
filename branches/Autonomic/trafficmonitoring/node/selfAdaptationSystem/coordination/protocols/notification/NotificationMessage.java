package node.selfAdaptationSystem.coordination.protocols.notification;

import node.selfAdaptationSystem.coordination.SelfAdaptationMessage;

public class NotificationMessage<I> extends SelfAdaptationMessage {
	
	public NotificationMessage(I notificationPayload){
		this.notificationPayload = notificationPayload;
	}
	
	public I getNotificationPayload(){
		return this.notificationPayload;
	}
	
	private I notificationPayload;

}
