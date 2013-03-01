package node.organizationMiddleware.organizationController;


import node.CameraNode;
import node.agentMiddleware.action.interfaces.Action;
import node.agentMiddleware.communication.interfaces.SendInterface;
import node.agentMiddleware.perception.interfaces.Perception;
import node.organizationMiddleware.contextManager.contextDirectories.Context;
import node.organizationMiddleware.contextManager.contextDirectories.Organization;
import node.organizationMiddleware.contextManager.contextDirectories.RolePosition;
import node.organizationMiddleware.contextManager.syncmechanisms.datageneration.NeighbourInfoPerceptionMechanism;
import node.organizationMiddleware.contextManager.syncmechanisms.datageneration.TrafficJamInfoPerceptionMechanism;
import node.organizationMiddleware.contextManager.syncmechanisms.distribution.MasterIDReplier;
import node.organizationMiddleware.contextManager.syncmechanisms.distribution.NeighbourInfoSyncMechanism;
import node.organizationMiddleware.contextManager.syncmechanisms.distribution.TrafficJamInfoSyncMechanism;
import utilities.GUIDManager;
import utilities.MessageBuffer;
import utilities.threading.ThreadManager;

//@Pieter
public class SlaveController extends OrganizationController {

	private TrafficJamInfoPerceptionMechanism trafficJamInfoPerceptionMechanism;
	private NeighbourInfoPerceptionMechanism neighbourInfoPerceptionMechanism;
	private NeighbourInfoSyncMechanism neighBourInfoSyncMechanism;
	private TrafficJamInfoSyncMechanism trafficJamInfoSyncMechanism;
	private MasterIDReplier masterIDReplier;
	
	public MessageBuffer messageBuffer;
	public Perception agentPerceptionLayer;
	public SendInterface agentSendLayer;
	public Action agentActionLayer;
	public GUIDManager guidManager;
	
	public SlaveController( Organization newPersonalOrganization,
						   Context organizationsContext,
						   Perception agentPerceptionLayer,
						   SendInterface agentSendLayer, 
						   Action agentActionLayer,
						   ThreadManager threadManager,
						   MessageBuffer messageBuffer,
						   GUIDManager guidManager,
						   //@Pieter
						   CameraNode cameraNode) {
		//@Pieter
		super(cameraNode);
		
		
		
		this.threadManager = threadManager;
		this.ctx = organizationsContext;
		this.ctx.setPersonalOrg(newPersonalOrganization);
		this.agentPerceptionLayer = agentPerceptionLayer;
		this.agentSendLayer = agentSendLayer;
		this.agentActionLayer = agentActionLayer;
		this.guidManager = guidManager;
		
		//@Pieter
		this.initializeLocalPerceptionAndSyncMechanisms();
				
		masterIDReplier = new MasterIDReplier(ctx.getPersonalOrg(),messageBuffer,agentSendLayer);
		this.threadManager.register(masterIDReplier);
		
		this.messageBuffer = messageBuffer;
		messageBuffer.subscribeOnTerminationMessages();
		
		//System.out.println("Slave opgestart op node : "+ctx.getPersonalID() +"\n"+
	    //			" : de organisatie waarbij hij behoort heeft id : "+newPersonalOrganization.getId()+
		//		" en master : "+newPersonalOrganization.getMasterID());
		
		
		//@Pieter
		messageBuffer.subscribeOnStartAsOrganizationManagerMessages();
		messageBuffer.subscribeOnStartAsSlaveMessages();		
	}

	//@Pieter
	public void initializeLocalPerceptionAndSyncMechanisms() {
		//Clean up old perception and sync mechanisms (if necessary)
		if(this.trafficJamInfoPerceptionMechanism != null)
			this.trafficJamInfoPerceptionMechanism.stop();
		if(this.neighbourInfoPerceptionMechanism != null)
			this.neighbourInfoPerceptionMechanism.stop();
		if(this.trafficJamInfoSyncMechanism != null)
			this.trafficJamInfoSyncMechanism.stop();
		if(this.neighBourInfoSyncMechanism != null)
			this.neighBourInfoSyncMechanism.stop();
		
		//Initialize new perception and sync mechanisms		
		for(RolePosition rp : this.ctx.getPersonalOrg().getFilledRolePositions()){
			if(rp.getAgentId().equals(ctx.getPersonalID())){
				//System.out.println(" rp created on: "+this.ctx.getPersonalID()+ " orgID: "+ctx.getPersonalOrg().getId());
				neighbourInfoPerceptionMechanism =  new NeighbourInfoPerceptionMechanism(agentPerceptionLayer,rp.getNeighbourInfo());
				trafficJamInfoPerceptionMechanism = new TrafficJamInfoPerceptionMechanism(agentPerceptionLayer,rp.getTrafficJamInfo());				
				trafficJamInfoSyncMechanism = new TrafficJamInfoSyncMechanism(rp.getTrafficJamInfo(),agentSendLayer, ctx);
				neighBourInfoSyncMechanism = new NeighbourInfoSyncMechanism(rp.getNeighbourInfo(),ctx,agentSendLayer);
			}
		}
	}

	/****************************
	 * 
	 *  private helper functions
	 *
	 ****************************/

	public void run() {						
		if(messageBuffer.hasTerminationMsgAsNextMessage()){
			messageBuffer.popTerminationMessage();
			
			//@Pieter
			this.endSlaveController();
			
			this.threadManager.register(new IntermediateController(messageBuffer,
					  threadManager,
					  ctx,
					  agentPerceptionLayer,
					  agentSendLayer,
					  agentActionLayer,
					  guidManager,
					  //@Pieter
					  this.cameraNode));
		}
	}
	
	//@Pieter
	public void endSlaveController(){		
		this.trafficJamInfoPerceptionMechanism.stop();
		this.neighbourInfoPerceptionMechanism.stop();
		this.neighBourInfoSyncMechanism.stop();
		this.trafficJamInfoSyncMechanism.stop();
		this.masterIDReplier.stop();
		
		this.messageBuffer.unsubscribeOnTerminationMessages();
		
		this.threadManager.unregister(masterIDReplier);
		this.threadManager.unregister(this);	
		
		this.ctx.setPersonalOrg(null);
	}
	
	//@Pieter
	public void forceEnd(){
		this.endSlaveController();
	}

}
