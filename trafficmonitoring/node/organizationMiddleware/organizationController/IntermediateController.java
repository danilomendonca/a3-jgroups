package node.organizationMiddleware.organizationController;

import node.CameraNode;
import node.agentMiddleware.action.interfaces.Action;
import node.agentMiddleware.communication.interfaces.SendInterface;
import node.agentMiddleware.perception.interfaces.Perception;
import node.organizationMiddleware.contextManager.contextDirectories.Context;
import node.organizationMiddleware.contextManager.contextDirectories.Organization;
import utilities.GUIDManager;
import utilities.MessageBuffer;
import utilities.threading.ThreadManager;

//@Pieter
public class IntermediateController extends OrganizationController {

	private MessageBuffer msgBuffer;
	private Perception agentPerceptionLayer;
	private SendInterface agentSendLayer; 
	private GUIDManager guidManager;
	private Action agentActionLayer;
		
	public IntermediateController(MessageBuffer msgBuffer,
							 ThreadManager threadManager,
							 Context organizationsContext,
							 Perception agentPerceptionLayer,
							 SendInterface agentSendLayer,
							 Action agentActionLayer,
							 GUIDManager guidManager,
							 //@Pieter
							 CameraNode cameraNode){
		//@Pieter
		super(cameraNode);
		
		
		
		this.threadManager = threadManager;
		this.msgBuffer = msgBuffer;
		this.ctx = organizationsContext;
		this.agentPerceptionLayer = agentPerceptionLayer;
		this.agentSendLayer = agentSendLayer;
		this.agentActionLayer = agentActionLayer;
		this.guidManager = guidManager;
			
		//System.out.println("Intermediate state op node : "+ctx.getPersonalID() +" aangemaakt");
	}
	
	public void run() {		
		if(msgBuffer.hasStartAsOrganizationManagerAsNextMessage()){
			//System.out.println("Intermediate state op node : "+ctx.getPersonalID()+" ontvangt een startAsManager bericht");
			Organization newOrganization = msgBuffer.receiveStartAsOrganizationManagerMessage();
			startupMasterController(newOrganization);
		}else if (msgBuffer.hasStartAsSlaveAsNextMessage()){
			//System.out.println("Intermediate state op node : "+ctx.getPersonalID()+" ontvangt een startAsSlave bericht");
			Organization newOrganization = msgBuffer.receiveStartAsSlaveMessage();
			startupSlaveController(newOrganization);
		}
	}
	
	private void startupMasterController(Organization newOrganization){
		//System.out.println("IntermediateState -> startupOrganizationManager op node : "+ctx.getPersonalID());
		MasterController organizationManager = new MasterController(newOrganization,
																		  ctx,
																		  agentSendLayer,
																		  agentPerceptionLayer,
																		  agentActionLayer,
																		  threadManager,
																		  msgBuffer,
																		  //@Pieter
																		  this.cameraNode);
		threadManager.unregister(this);
		threadManager.register(organizationManager);
	}
	
	private void startupSlaveController(Organization newOrganization){
		SlaveController slave = new SlaveController(newOrganization,
										ctx,
										agentPerceptionLayer,
										agentSendLayer,
										agentActionLayer,
										threadManager,
										msgBuffer,
										guidManager,
										//@Pieter
										this.cameraNode);
		threadManager.unregister(this);
		threadManager.register(slave);
	}
	
	//@Pieter
	public void forceEnd(){
		//Only this object has te be handled
		threadManager.unregister(this);
	}

}
