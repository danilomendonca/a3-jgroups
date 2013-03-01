package node.organizationMiddleware.organizationController;

import java.util.ArrayList;

import node.CameraNode;
import node.agentMiddleware.action.interfaces.Action;
import node.agentMiddleware.communication.interfaces.SendInterface;
import node.agentMiddleware.communication.middleware.CommunicationMiddleware;
import node.agentMiddleware.communication.middleware.LockingException;
import node.agentMiddleware.perception.interfaces.Perception;
import node.organizationMiddleware.contextManager.contextDirectories.Context;
import node.organizationMiddleware.contextManager.contextDirectories.NeighbourInfo;
import node.organizationMiddleware.contextManager.contextDirectories.Organization;
import node.organizationMiddleware.contextManager.contextDirectories.RolePosition;
import node.organizationMiddleware.contextManager.contextDirectories.TrafficJamInfo;
import node.organizationMiddleware.contextManager.syncmechanisms.datageneration.NeighbourInfoAggregationMechanism;
import node.organizationMiddleware.contextManager.syncmechanisms.datageneration.NeighbourInfoPerceptionMechanism;
import node.organizationMiddleware.contextManager.syncmechanisms.datageneration.TrafficJamInfoAggregationMechanism;
import node.organizationMiddleware.contextManager.syncmechanisms.datageneration.TrafficJamInfoPerceptionMechanism;
import node.organizationMiddleware.contextManager.syncmechanisms.distribution.MasterIDReplier;
import node.organizationMiddleware.contextManager.syncmechanisms.distribution.NeighbourInfoSyncMechanism_Remote;
import node.organizationMiddleware.contextManager.syncmechanisms.distribution.Neighbours_SyncMechanism;
import node.organizationMiddleware.contextManager.syncmechanisms.distribution.OrganizationState_SyncMechanism;
import node.organizationMiddleware.contextManager.syncmechanisms.distribution.TrafficJamInfoSyncMechanism_Remote;
import node.organizationMiddleware.organizationController.evolutionLaws.MergeLaw;
import node.organizationMiddleware.organizationController.evolutionLaws.SplitLaw;

import utilities.NodeID;
import utilities.Event;
import utilities.GUIDManager;
import utilities.MessageBuffer;
import utilities.Subscriber;
import utilities.threading.ThreadManager;

//@Pieter
public class MasterController extends OrganizationController implements Subscriber{
	
	private Organization personalOrganization;
	
	private SendInterface agentSendLayer;
	private Perception agentPerceptionLayer;
	private MessageBuffer msgBuffer;
	private GUIDManager guidManager;
	
	/**
	 * references to sync and aggregation mechanisms
	 */
		
	private TrafficJamInfoPerceptionMechanism trafficJamInfoPerceptionMechanism;
	private NeighbourInfoPerceptionMechanism neighbourInfoPerceptionMechanism;
	
	private NeighbourInfoSyncMechanism_Remote neighbourInfoSyncMechanism_Remote;
	private TrafficJamInfoSyncMechanism_Remote trafficJamInfoSyncMechanism_Remote;
	
	private NeighbourInfoAggregationMechanism neighbourInfoAggregationMechanism;
	private TrafficJamInfoAggregationMechanism trafficJamInfoAggregationMechanism;
	
	private Neighbours_SyncMechanism neighbours_SyncMechanism;
	private OrganizationState_SyncMechanism organizationState_SyncMechanism;
	
	private MasterIDReplier masterIDReplier;
	
	/**
	 * evolution laws
	 */
	
	private SplitLaw split;
	private MergeLaw merge;
	private Action agentActionLayer;
	

	public MasterController(Organization newPersonalOrganization,
							Context ctx, 
							SendInterface agentSendLayer,
							Perception agentPerceptionLayer,
							Action agentActionLayer,
							ThreadManager threadManager,
							MessageBuffer msgBuffer,
							//@Pieter
							CameraNode cameraNode){
		//@Pieter
		super(cameraNode);
		
		
		
		//System.out.println("de constructor van OrganizationManager wordt opgeroepen op node : "+ctx.getPersonalID());
		
		this.msgBuffer = msgBuffer;
		this.ctx = ctx;
		this.agentSendLayer = agentSendLayer;
		this.agentPerceptionLayer = agentPerceptionLayer;
		this.agentActionLayer = agentActionLayer;
		this.threadManager = threadManager;
		this.guidManager = GUIDManager.getInstance();
		
		this.split = new SplitLaw();
		this.merge = new MergeLaw(guidManager);
					
		msgBuffer.subscribeOnStartAsOrganizationManagerMessages();
		msgBuffer.subscribeOnStartAsSlaveMessages();
		
		//System.out.println("startupNewOrganization via constructor van OrganizationManager op node : "+ctx.getPersonalID());
		startupNewOrganization(newPersonalOrganization);
	
	}

	/***********************
	 * 	Start organization
	 ***********************/
	
	private void startupNewOrganization(Organization newPersonalOrganization){					
		//subscribe for termination messages
		this.msgBuffer.subscribeOnTerminationMessages();
		
		//make changes to the context
		this.personalOrganization = newPersonalOrganization;
		this.ctx.setNeighbourOrgs(new ArrayList<Organization>());
		this.ctx.setPersonalOrg(newPersonalOrganization);
				
		//subscribe on context data
		//needed for knowing when to merge and/or split
		this.personalOrganization.subscribe("changeTrafficStateOfOrganization", this);
		this.ctx.subscribe("addNeighbourOrganization", this);
				
		masterIDReplier = new MasterIDReplier(ctx.getPersonalOrg(),msgBuffer,agentSendLayer);
		
		//create local sync mechanisms
		//@Pieter
		this.initializeLocalPerceptionMechanisms();				
		
		trafficJamInfoSyncMechanism_Remote	= new TrafficJamInfoSyncMechanism_Remote(ctx,msgBuffer);	
		neighbourInfoSyncMechanism_Remote = new NeighbourInfoSyncMechanism_Remote(msgBuffer,ctx);
		
		trafficJamInfoAggregationMechanism = new TrafficJamInfoAggregationMechanism(this.personalOrganization);
		neighbourInfoAggregationMechanism = new NeighbourInfoAggregationMechanism(this.personalOrganization);
		
		neighbours_SyncMechanism = new Neighbours_SyncMechanism(this.ctx.getPersonalOrg(),this.agentSendLayer,msgBuffer,ctx);
		organizationState_SyncMechanism = new OrganizationState_SyncMechanism(ctx,agentSendLayer,msgBuffer);
		
		this.threadManager.register(trafficJamInfoSyncMechanism_Remote);
		this.threadManager.register(neighbourInfoSyncMechanism_Remote);
		this.threadManager.register(neighbours_SyncMechanism);
		this.threadManager.register(masterIDReplier);
		this.threadManager.register(organizationState_SyncMechanism);
		
		//notify slave nodes
		notifySlavecontrollersAboutNewOrganization();		
	}

	//@Pieter
	public void initializeLocalPerceptionMechanisms() {
		//Clean up old perception mechanisms (if necessary)
		if(this.trafficJamInfoPerceptionMechanism != null)
			this.trafficJamInfoPerceptionMechanism.stop();
		if(this.neighbourInfoPerceptionMechanism != null)
			this.neighbourInfoPerceptionMechanism.stop();
		
		//Initialize new perception mechanisms
		for(RolePosition rp : this.ctx.getPersonalOrg().getFilledRolePositions()){
			if(rp.getAgentId().equals(ctx.getPersonalID())){
				trafficJamInfoPerceptionMechanism = new TrafficJamInfoPerceptionMechanism(agentPerceptionLayer,rp.getTrafficJamInfo());
				neighbourInfoPerceptionMechanism =  new NeighbourInfoPerceptionMechanism(agentPerceptionLayer,rp.getNeighbourInfo());
			}
		}
	}	
	
			
	/*********************
	 * 	End organization
	 *********************/

	public void run(){		
		if(msgBuffer.hasTerminationMsgAsNextMessage()){
			//System.out.println("De organization manager op node : "+ctx.getPersonalID()+" ontvangt een terminatie bericht");
			msgBuffer.popTerminationMessage();
			endOrganization();
			threadManager.unregister(this);
			IntermediateController intermediateState = 
				new IntermediateController(msgBuffer,threadManager,ctx,agentPerceptionLayer,
						agentSendLayer,agentActionLayer,guidManager,
						//@Pieter
						this.cameraNode);
			threadManager.register(intermediateState);
			//System.out.println("registered threads op node "+ctx.getPersonalID()+" :"+ threadManager.toString());
		}
		
//		if(this.ctx.getPersonalID().getId()==4)
//			System.out.println(this.ctx.getPersonalOrg().getAgents());
	}
	

	/***************************
	 * 	  Merging/Splitting
	 ***************************/
	
	public void publish(Event e) {
			
			removeInvalidNeighbours();
		
			if(e.getEventType().equals("changeTrafficStateOfOrganization")){
				//@Pieter
				if(this.personalOrganization.everyAgentSeesCongestion().isFalse() && this.personalOrganization.getAgents().size()>1
						&& !this.splittingBlocked){
									
				//organization must be split 
					try {
						agentActionLayer.lock(this.personalOrganization.getId());
						
						//System.out.println("Organization Manager op Node : "+ctx.getPersonalID() + " gaat splitten");
						endOrganization();
						ArrayList<ArrayList<NodeID>> agentGroups = this.split.split(this.personalOrganization);
						for(ArrayList<NodeID> agentGroup : agentGroups){
							if(!agentGroup.contains(this.ctx.getPersonalID())){
								Organization newOrganization = createNewOrganization(agentGroup,agentGroup.get(0));
								agentSendLayer.sendStartAsOrganizationManagerMessage(newOrganization, newOrganization.getAgents().get(0).toString());
							}else{
								Organization newOrganization = createNewOrganization(agentGroup,this.ctx.getPersonalID());
								startupNewOrganization(newOrganization);
							}
						}
					} catch (LockingException e1) {
						// my personal organization has been locked by somebody else
						// this implies that a termination message will soon be processed
					}
				}
			}
			else if(e.getEventType().equals("addNeighbourOrganization")){
				
				for(Organization neighbour : this.ctx.getNeighbourOrgs()){
					if(this.merge.mustBeMerged(neighbour, this.ctx.getPersonalOrg())){
							try {
								agentActionLayer.lock(neighbour.getId(), this.personalOrganization.getId());
								
								//System.out.println("Organization Manager op Node : "+ctx.getPersonalID() + " gaat mergen \n "+
								//		"Hij doet dit met de organizatie met id : "+neighbour.getId()+" en met master "+neighbour.getMasterID());
								
								agentSendLayer.sendTerminateOrganizationMessage(neighbour.getMasterID().toString());
								endOrganization();
								Organization newOrganization = this.merge.mergeOrganisations(this.personalOrganization, neighbour);
								startupNewOrganization(newOrganization);
								break;
								
							} catch (LockingException e1) {
								// my own organisation or the neighbour are locked
								// this implies that one of them will be or is terminated 
								
								//@Pieter
								//OR: that the local organization or other organization has been blocked (by their respective masters)
								// from merging/splitting during self-adaptation. This mechanic simulates an organization merge confirmation
								// conversation between both involved masters, as without it one of these masters can unilaterally decide
								// to merge (basing this decision on local information) without consulting the other master ... This can
								// lead to invalid decisions during self-adaptation and therefore inconsistent system states.
							}
					}
				}
			}
	}
	
	private void removeInvalidNeighbours() {
		ArrayList<Organization> toBeRemoved = new ArrayList<Organization>();
		for(Organization neighbour : ctx.getNeighbourOrgs()){
			if(agentActionLayer.isLocked(neighbour.getId()))
				toBeRemoved.add(neighbour);
		}
		
		ctx.removeNeighbours(toBeRemoved);
	}

	private Organization createNewOrganization(ArrayList<NodeID> agentGroup, NodeID master) {
		
		//ArrayList<Integer> history = (ArrayList<Integer>) this.personalOrganization.getHistory();
		//ArrayList<Integer> history = (ArrayList<Integer>) this.personalOrganization.getHistoryByRef();
		//history.add(this.personalOrganization.getId());
				
		//add the id; (history;) masterID; trafficJamTreshold
		//organization boundaries and everyAgentSeesCongestion should remain "uninitialised"
		Organization result = new Organization(this.personalOrganization.getTrafficJamTreshhold(),
				this.guidManager.getNextID()/*,history*/, master);
		
		//add the agents
		result.changeAgents(agentGroup);
		
		//create the rolepositions
		for(NodeID agent : agentGroup){
			RolePosition rp = new RolePosition(new TrafficJamInfo(), new NeighbourInfo(),"dummy");
			rp.setAgentId(agent);
			result.addFilledRolePosition(rp);
		}
	
		return result;
	}
	
	/****************************
	 * private helper functions
	 ****************************/
	
	private void notifySlavecontrollersAboutNewOrganization() {
		//create information to send
		Organization infoToSend = new Organization(
				this.personalOrganization.getTrafficJamTreshhold(), //irrelevant
				this.personalOrganization.getId(), //irrelevant
				//this.personalOrganization.getHistory(), //irrelevant
				this.personalOrganization.getMasterID()); 
		
		for(RolePosition rp : this.personalOrganization.getFilledRolePositions()){
			RolePosition copy = new RolePosition(new TrafficJamInfo(),new NeighbourInfo(),rp.getRoleType());
			copy.setAgentId(rp.getAgentId());
			infoToSend.addFilledRolePosition(copy);
		}
		
		//System.out.println("OrganizationManager op node "+ctx.getPersonalID()+" executes notifySlavesAboutNewOrganization");
		for(RolePosition rp : this.personalOrganization.getFilledRolePositions()){
			if(!rp.getAgentId().equals(ctx.getPersonalID())){
				//System.out.println("OrganizationManager op node "+ctx.getPersonalID()+" verstuurt start as slave boodschap naar "+rp.getAgentId().toString());
				//System.out.println("met daarin volgende info : master : "+infoToSend.getMasterID()+ " org id : "+infoToSend.getId());
				agentSendLayer.sendStartAsSlaveMessage(infoToSend, rp.getAgentId().toString());
			}
		}
	}
	
	public void endOrganization() {
		cleanupLocal();
		notifySlaveControllersAboutEndOfOrganization();
	}
	
	private void cleanupLocal() {
		
		msgBuffer.unsubscribeOnTerminationMessages();
		
		this.personalOrganization.unsubscribe("changeTrafficStateOfOrganization", this);
		this.ctx.unsubscribe("addNeighbourOrganization", this);
		
		trafficJamInfoPerceptionMechanism.stop();
		neighbourInfoPerceptionMechanism.stop();
		
		trafficJamInfoSyncMechanism_Remote.stop();
		neighbourInfoSyncMechanism_Remote.stop();
		
		trafficJamInfoAggregationMechanism.stop();
		neighbourInfoAggregationMechanism.stop();
		
		neighbours_SyncMechanism.stop();
		masterIDReplier.stop();
		
		organizationState_SyncMechanism.stop();
					
		threadManager.unregister(trafficJamInfoSyncMechanism_Remote);
		threadManager.unregister(neighbourInfoSyncMechanism_Remote);
		threadManager.unregister(neighbours_SyncMechanism);
		threadManager.unregister(masterIDReplier);
		threadManager.unregister(organizationState_SyncMechanism);
			
		this.ctx.setPersonalOrg(null);
		this.ctx.setNeighbourOrgs(new ArrayList<Organization>());
			
	}
	
	private void notifySlaveControllersAboutEndOfOrganization() {
		for(RolePosition rp : personalOrganization.getFilledRolePositions()){
			if(!rp.getAgentId().equals(ctx.getPersonalID())){
				//System.out.println("Organization manager op node "+ctx.getPersonalID()+ " stuurt een terminatie bericht naar node "+rp.getAgentId());
				agentSendLayer.sendTerminateOrganizationMessage(rp.getAgentId().toString());
			}
		}
	}
	
	
	/**************************	 
	 * 
	 *	Self-Healing 
	 *
	 **************************/	
	
	//@Pieter
	public void blockMerging(){
		this.merge.blockMerging();
	}
	public void unblockMerging(){
		this.merge.unblockMerging();
	}
	public void blockSplitting(){
		this.splittingBlocked = true;
	}
	public void unblockSplitting(){
		this.splittingBlocked = false;
	}
	private boolean splittingBlocked = false;
	
	//@Pieter
	/**
	 * Makes sure that masters of neighboring organizations don't decide on merging/splitting
	 * with the organization active on the local master node
	 */
	public void forcedMergeSplitBlockWithLocalOrganization(){
		int personalOrgID = personalOrganization.getId();
		((CommunicationMiddleware) this.agentActionLayer).forcedLock(personalOrgID);
	}
	public void forcedMergeSplitUnblockWithLocalOrganization(){
		int personalOrgID = personalOrganization.getId();
		((CommunicationMiddleware) this.agentActionLayer).forcedUnlock(personalOrgID);
	}
	
	//@Pieter
	public void forceEnd(){
		this.cleanupLocal();
	}
	
}

