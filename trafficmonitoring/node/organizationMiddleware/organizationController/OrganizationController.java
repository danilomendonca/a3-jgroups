package node.organizationMiddleware.organizationController;

import node.CameraNode;
import node.organizationMiddleware.contextManager.contextDirectories.Context;
import utilities.threading.ThreadManager;

//@Pieter
public abstract class OrganizationController implements Runnable {

	public Context ctx;
	public ThreadManager threadManager;
	public CameraNode cameraNode;
	
	public OrganizationController(CameraNode cameraNode){
		this.cameraNode = cameraNode;
		
		this.cameraNode.setOrganizationController(this);
	}
	
	public Context getOrganizationContext(){
		return this.ctx;
	}
	
	//@Pieter
	/**
	 * Make sure this organizationController and all its associated Runnable objects are
	 * unregistered from their threadManager, without firing any events or contacting other
	 * cameras
	 */
	public abstract void forceEnd();

}