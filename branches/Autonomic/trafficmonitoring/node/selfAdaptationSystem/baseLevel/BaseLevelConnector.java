package node.selfAdaptationSystem.baseLevel;

import java.util.ArrayList;

import utilities.GUIDManager;
import utilities.MessageBuffer;
import utilities.NodeID;
import utilities.threading.ThreadManager;
import node.CameraNode;
import node.organizationMiddleware.contextManager.contextDirectories.Context;
import node.organizationMiddleware.contextManager.contextDirectories.NeighbourInfo;
import node.organizationMiddleware.contextManager.contextDirectories.Organization;
import node.organizationMiddleware.contextManager.contextDirectories.RolePosition;
import node.organizationMiddleware.contextManager.contextDirectories.TrafficJamInfo;
import node.organizationMiddleware.contextManager.syncmechanisms.datageneration.NeighbourInfoPerceptionMechanism;
import node.organizationMiddleware.contextManager.syncmechanisms.datageneration.TrafficJamInfoPerceptionMechanism;
import node.organizationMiddleware.organizationController.IntermediateController;
import node.organizationMiddleware.organizationController.MasterController;
import node.organizationMiddleware.organizationController.SlaveController;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;

public class BaseLevelConnector {
	
	public CameraNode node;	
	
	public BaseLevelConnector(CameraNode node){
		this.node = node;
	}
	
	/**************************	 
	 * 
	 *	Local System Role
	 *
	 **************************/
	
	public boolean localSystemIsInitializing(){
		return (this.node.getCamera() == null);
	}
	
	public boolean isRoleless(){
		return (this.node.getOrganizationController() == null);
	}
	
	public boolean isMasterNode() {
		return (this.node.getOrganizationController() instanceof MasterController);
	}
	
	public boolean isInTransition(){		
		if((this.node.getOrganizationController().ctx == null) ||
				(this.node.getOrganizationController().ctx.getPersonalOrg() == null))
			//The OrganizationController is set to transition to a different state in the next
			// computation cycle
			return true;
		
		return (this.node.getOrganizationController() instanceof IntermediateController);
	}
	
	public boolean isBackOnline(){
		return node.isBackOnline();
	}
	
	
	/**************************	 
	 * 
	 *	General
	 *
	 **************************/
	
	
	
	public NodeID getHostNodeID(){
		return this.node.getCamera().getAgentID();
	}
	
	public boolean localCameraSeesTrafficCongestion(){
		return this.node.getPerceptionMiddleware().seesTrafficCongestion();
	}
	
	public boolean localOrganizationSeesTrafficCongestion(){
		return this.getLocalOrganizationContext().getPersonalOrg().everyAgentSeesCongestion().isTrue();
	}

	public Context getLocalOrganizationContext() {
		return this.node.getOrganizationController().getOrganizationContext();
	}
	
	public void setLocalOrganizationContext(Context newContext){
		this.node.getOrganizationController().ctx = newContext;
	}
	
	public ArrayList<NodeID> getAliveNeighbors(){
		return this.node.getPerceptionMiddleware().getLocalCamera().getAliveNeighbourIDlist();
	}
	
	public void setAliveNeighbors(ArrayList<NodeID> aliveNeighbors){
		this.node.getPerceptionMiddleware().getLocalCamera().setAliveNeighbours(aliveNeighbors);
	}
	
	/**
	 * Reset the base-level's list of alive neighbor nodes back to the list of actual physical neighbors
	 */
	public void resetAliveNeighborNodeList(){
		node.getCamera().resetAliveNeighbourList();
	}
	
	public RolePosition getLocalRolePositionObject(){
		Context localContext = this.node.getContext();
		NodeID localNode = localContext.getPersonalID();
		
		if(localContext.getPersonalOrg() == null){
			//The current base-level configuration has no information on the local role position
			return null;
		}
		
		for(RolePosition rp : localContext.getPersonalOrg().getFilledRolePositions()){
			if(rp.getAgentId().equals(localNode))
				return rp;
		}
		
		//Nothing found
		return null;
	}
	
	
	public void changeNeighborOrganization(Organization newOrganizationInfo){
		Context localContext = this.getLocalOrganizationContext();
		
		//Find the neighbor organization with the same id as the given organization
		Organization oldNeighborOrg = null;
		for(Organization neighborOrg : localContext.getNeighbourOrgs()){
			if(neighborOrg.getId() == newOrganizationInfo.getId())
				oldNeighborOrg = neighborOrg;
		}
		
		//Remove the old organization information
		localContext.removeNeighbourOrganization(oldNeighborOrg);
		
		//Add the new organization information
		localContext.addNeighbourOrganization(newOrganizationInfo);
	}
	
	/**
	 * Create (and register) sensing mechanisms for both objects so that up-to-date information from the
	 * local perception middleware can be retrieved from the given objects
	 */
	public void registerTempNeighborAndTrafficSensing(TrafficJamInfo trafficInfo, NeighbourInfo neighborInfo){		
		new NeighbourInfoPerceptionMechanism(this.node.getPerceptionMiddleware(), neighborInfo);
		new TrafficJamInfoPerceptionMechanism(this.node.getPerceptionMiddleware(), trafficInfo);
	}
	
	/**
	 * While the local perception middleware usually only sends out traffic info to interested local parties (i.e. event subscribers)
	 * if the traffic state has changed, explicitly trigger this for the current traffic state
	 */
	public void forceTrafficInfoEvent(){
		this.node.getPerceptionMiddleware().sendTrafficJamInfo();
	}
	
	
	/**************************	 
	 * 
	 *	Role Changes
	 *
	 **************************/
	
	/**
	 * Adapt the base-level to make the local node, a current slave of an organization, its new master
	 * 
	 * @note	After this, not all information needed to properly proceed base-level operations has been
	 * 			provided; additional adaptations regarding organizational structure might be needed.
	 */
	public synchronized void changeSlaveToMaster(){		
		NodeID hostID = this.getLocalOrganizationContext().getPersonalID();
		Organization oldOrg = this.getLocalOrganizationContext().getPersonalOrg();		
		SlaveController slaveCont = (SlaveController) this.node.getOrganizationController();
		
		//End slave role
		ThreadManager threadManager = slaveCont.threadManager;
		slaveCont.endSlaveController();
		
		//Start master role
		Organization newOrg = new Organization(oldOrg.getTrafficJamTreshhold(),
												oldOrg.getId(), hostID);
		
		ArrayList<NodeID> agents = new ArrayList<NodeID>();
		agents.add(hostID);
		newOrg.changeAgents(agents);	
		
		//Add roleposition just for this node (so traffic and neighbor sensing mechanisms can initialize)
		RolePosition rp = new RolePosition(new TrafficJamInfo(), new NeighbourInfo(),"dummy");
		rp.setAgentId(hostID);
		newOrg.addFilledRolePosition(rp);
		
		Context newCont = new Context(hostID);		
		
		MasterController masterCont = new MasterController(	  newOrg,
															  newCont,
															  slaveCont.agentSendLayer,
															  slaveCont.agentPerceptionLayer,
															  slaveCont.agentActionLayer,
															  slaveCont.threadManager,
															  slaveCont.messageBuffer,
															  slaveCont.cameraNode);
		
		//GUI-hack (SimulatorWindow): set context on local Camera-object
		this.node.getCamera().setOrganizationContext(newCont);
		
		threadManager.register(masterCont);		
	}
	
	public synchronized void createNewSingleMasterOrganization(){
		float trafficJamTreshold = (float) 0.8;		//TODO: don't hardcode (value take from ApplicationFactory)
		int nextOrgID = GUIDManager.getInstance().getNextID();
		NodeID localNode = this.node.getCamera().getAgentID();
		Organization newOrg = new Organization(trafficJamTreshold, nextOrgID, localNode);

		ArrayList<NodeID> agents = new ArrayList<NodeID>();
		agents.add(localNode);
		newOrg.changeAgents(agents);
		
		//Add roleposition just for this node (so traffic and neighbor sensing mechanisms can initialize)
		RolePosition rp = new RolePosition(new TrafficJamInfo(), new NeighbourInfo(),"dummy");
		rp.setAgentId(localNode);
		newOrg.addFilledRolePosition(rp);
		
		Context newCont = new Context(localNode);		
		
		MasterController masterCont = new MasterController(	  newOrg,
															  newCont,
															  this.node.getComMid(),
															  this.node.getPerceptionMiddleware(),
															  this.node.getComMid(),
															  this.node.threadManager,
															  new MessageBuffer(this.node.getComMid()),
															  this.node);	
		
		//GUI-hack (SimulatorWindow): set context on local Camera-object
		this.node.getCamera().setOrganizationContext(newCont);
		
		this.node.threadManager.register(masterCont);		
	}
	
	public synchronized void startLocalSlave(NodeID master, int organizationID){
		float trafficJamTreshold = (float) 0.8;		//TODO: don't hardcode (value take from ApplicationFactory)
		NodeID localNode = this.node.getCamera().getAgentID();
		Organization newOrg = new Organization(trafficJamTreshold, organizationID, master);
		
		ArrayList<NodeID> agents = new ArrayList<NodeID>();
		agents.add(localNode);
		newOrg.changeAgents(agents);
		
		//Add roleposition just for this node (so traffic and neighbor sensing mechanisms can initialize)
		RolePosition rp = new RolePosition(new TrafficJamInfo(), new NeighbourInfo(),"dummy");
		rp.setAgentId(localNode);
		newOrg.addFilledRolePosition(rp);
		
		Context newCont = new Context(localNode);
		
		SlaveController slaveCont = new SlaveController(	  newOrg,
															  newCont,
															  this.node.getPerceptionMiddleware(),
															  this.node.getComMid(),
															  this.node.getComMid(),
															  this.node.threadManager,
															  new MessageBuffer(this.node.getComMid()),
															  GUIDManager.getInstance(),
															  this.node);
		
		//GUI-hack (SimulatorWindow): set context on local Camera-object
		this.node.getCamera().setOrganizationContext(newCont);
		
		this.node.threadManager.register(slaveCont);
	}
	
	public synchronized void initializePerceptionMechanismsOnSlaveNode() throws ComputationExecutionException{
		if(this.isMasterNode() || this.isInTransition())
			throw new ComputationExecutionException("Can't initialize perception mechanisms: not on a slave node");
		
		((SlaveController) this.node.getOrganizationController()).initializeLocalPerceptionAndSyncMechanisms();
	}
	
	public synchronized void initializePerceptionMechanismsOnMasterNode() throws ComputationExecutionException{
		if(!this.isMasterNode())
			throw new ComputationExecutionException("Can't initialize perception mechanisms: not on a master node");
		
		((MasterController) this.node.getOrganizationController()).initializeLocalPerceptionMechanisms();
	}
	
	
	
	/**************************	 
	 * 
	 *	Merge & Split
	 *
	 **************************/
	
	public synchronized void blockMerging() throws ComputationExecutionException{
		if(!this.isMasterNode())
			throw new ComputationExecutionException("Can't disable MergeLaw: not on master node");
		
		//Avoid the local master node deciding that it needs to merge with a neighbor organization
		((MasterController) this.node.getOrganizationController()).blockMerging();
		
		//Avoid masters of neighboring organizations deciding that they need to merge with the
		// organization of which this local node is the master
		((MasterController) this.node.getOrganizationController()).forcedMergeSplitBlockWithLocalOrganization();
	}
	
	public synchronized void unblockMerging() throws ComputationExecutionException{
		if(!this.isMasterNode())
			throw new ComputationExecutionException("Can't enable MergeLaw: not on master node");
		
		//Allow the local master node deciding that it needs to merge with a neighbor organization
		((MasterController) this.node.getOrganizationController()).unblockMerging();
		
		//Allow masters of neighboring organizations deciding that they need to merge with the
		// organization of which this local node is the master
		((MasterController) this.node.getOrganizationController()).forcedMergeSplitUnblockWithLocalOrganization();
	}
	
	public synchronized void blockSplitting() throws ComputationExecutionException{
		if(!this.isMasterNode())
			throw new ComputationExecutionException("Can't disable SplitLaw: not on master node");
		
		//Avoid the local master node deciding that it needs to merge with a neighbor organization
		((MasterController) this.node.getOrganizationController()).blockSplitting();
		
		//Avoid masters of neighboring organizations deciding that they need to merge with the
		// organization of which this local node is the master
		((MasterController) this.node.getOrganizationController()).forcedMergeSplitBlockWithLocalOrganization();
	}
	
	public synchronized void unblockSplitting() throws ComputationExecutionException{
		if(!this.isMasterNode())
			throw new ComputationExecutionException("Can't enable SplitLaw: not on master node");
		
		//Allow the local master node deciding that it needs to merge with a neighbor organization
		((MasterController) this.node.getOrganizationController()).unblockSplitting();
		
		//Allow masters of neighboring organizations deciding that they need to merge with the
		// organization of which this local node is the master
		((MasterController) this.node.getOrganizationController()).forcedMergeSplitUnblockWithLocalOrganization();
	}
	
}
