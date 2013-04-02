package node;


import java.util.ArrayList;

import node.agentMiddleware.communication.middleware.*;
import node.agentMiddleware.perception.middleware.*;
import node.developer.sensors.Camera;
import node.hostInfrastructure.SimulatedCommunicationNetwork;
import node.organizationMiddleware.contextManager.contextDirectories.Context;
import node.organizationMiddleware.contextManager.contextDirectories.NeighbourInfo;
import node.organizationMiddleware.contextManager.contextDirectories.Organization;
import node.organizationMiddleware.contextManager.contextDirectories.RolePosition;
import node.organizationMiddleware.contextManager.contextDirectories.TrafficJamInfo;
import node.organizationMiddleware.organizationController.MasterController;
import node.organizationMiddleware.organizationController.OrganizationController;
import node.selfAdaptationSystem.SelfAdaptationSubsystem;

import simulator.RoadNetwork;
import utilities.NodeID;
import utilities.GUIDManager;
import utilities.MessageBuffer;
import utilities.threading.ThreadManager;

public class CameraNode implements Runnable {
	
	private CommunicationMiddleware communicationMiddleware;
	private PerceptionMiddleware perceptionMiddleware;
	private boolean running = false;
	//@Pieter
	public ThreadManager threadManager;
//	private Context context;
	//@Pieter
	private OrganizationController organizationManager;
	
	//@Pieter
	public SelfAdaptationSubsystem selfAdaptationSubsystem;
	private RoadNetwork roadNetwork;
	private Camera camera;
	
	
	/****************
	 *              *
	 * CONSTRUCTOR *
	 *              *
	 ****************/
	
	/**
	 * Camera node as real thread
	 */
	//@Pieter
	public CameraNode(boolean selfhealing,
					  String nodeID,
					  Camera camera,
					  float trafficJamThreshold,
					  RoadNetwork roadNetwork, int sendTime){		
		this(selfhealing, nodeID, camera, trafficJamThreshold,
			 new ThreadManager(), roadNetwork, sendTime);
		
		//@Pieter
		//TODO: Implement multi-threaded mode
		throw new IllegalArgumentException("Multi-threaded mode not implemented yet");
	}
	
	/**
	 * Camera node as simulated thread
	 */
	//@Pieter
	public CameraNode(boolean selfhealing,
					  String nodeID,
			  		  Camera camera,
			  		  float trafficJamThreshold,
			  		  ThreadManager threadManager,
			  		  RoadNetwork roadNetwork, int sendTime){
		
		this.threadManager = threadManager;
		
		this.communicationMiddleware = new CommunicationMiddleware(SimulatedCommunicationNetwork.getInstance(), nodeID);
		this.perceptionMiddleware = new PerceptionMiddleware(camera);
		
		Organization startOrganization = createStartOrganization(trafficJamThreshold, Integer.parseInt(nodeID));
		//@Pieter
//		context = new Context(new NodeID(Integer.parseInt(nodeID)));
		Context context = new Context(new NodeID(Integer.parseInt(nodeID)));

		organizationManager = new MasterController(startOrganization,
		                                      		  context,
		                                      		  communicationMiddleware,
		                                      		  perceptionMiddleware,
		                                      		  communicationMiddleware,
		                                      		  threadManager,
		                                      		  new MessageBuffer(communicationMiddleware),
		                                      		  //@Pieter
		                                      		  this);
		
		threadManager.register(perceptionMiddleware);
		threadManager.register(organizationManager);
		
		camera.setOrganizationContext(context);
		
		
		//@Pieter
		this.selfAdaptationSubsystem = new SelfAdaptationSubsystem(this, roadNetwork, this.communicationMiddleware);
		threadManager.register(this.selfAdaptationSubsystem);
		
		//@Pieter
		this.roadNetwork = roadNetwork;
		this.camera = camera;
	}
	
	/***********
	 *         *
	 * GETTERS *
	 *         *
	 ***********/
	
	public CommunicationMiddleware getComMid(){
		return this.communicationMiddleware;
	}
	
	//@Pieter
	public OrganizationController getOrganizationController(){
		return this.organizationManager;
	}
	
	//@Pieter
	public void setOrganizationController(OrganizationController controller){
		this.organizationManager = controller;
	}
	
	//@Pieter
	public PerceptionMiddleware getPerceptionMiddleware(){
		return this.perceptionMiddleware;
	}
	
	//@Pieter
	public Camera getCamera(){
		return this.camera;
	}
	
	
	/*******************
	 * 
	 * helper functions
	 *
	 *******************/
	
	private Organization createStartOrganization(float trafficJamThreshold, int nodeID){
		int orgId = GUIDManager.getInstance().getNextID();
		//System.out.println("++++"+orgId);
		
		Organization personalOrganization = new Organization(trafficJamThreshold,
				 											 orgId,
				 											 //new ArrayList<Integer>(),
				 											 new NodeID(nodeID));
		
		ArrayList<NodeID> agents = new ArrayList<NodeID>();
		agents.add(new NodeID(nodeID));
		
		RolePosition rp1 = new RolePosition(new TrafficJamInfo(),new NeighbourInfo(),"dummyType");
		rp1.setAgentId(new NodeID(nodeID));
			
		personalOrganization.addFilledRolePosition(rp1);
		personalOrganization.changeAgents(agents);
		
		return personalOrganization;
	}
	
	public Context getContext(){
		//@Pieter
		return this.organizationManager.ctx;
//		return this.context;
	}
	
	/*************
	 *           *
	 * THREADING *
	 *           *
	 *************/
	
	//non usato
	

	public void run() { 
	    while (running){
	    	threadManager.step();
	    	try {Thread.sleep (2);} catch(InterruptedException e) {}
		}
	}
		
	public void start() {  
	    if (!running) {
	    	running = true;
	    	new Thread(this).start();
	    }
	}
	  
	public void stop() { 
		running = false;
	}
	
	/***********
	 *         *
	 * FAILING *
	 *         *
	 ***********/        
	
	public void fail(){
		this.stop();
		
		threadManager.unregister(perceptionMiddleware);		
		this.organizationManager.forceEnd();		
		threadManager.unregister(organizationManager);
		
		this.perceptionMiddleware.getLocalCamera().fail();
		
		//@Pieter
		threadManager.unregister(this.selfAdaptationSubsystem);
		this.hasFailed = true;
	}
	
	//@Pieter
	public void bringBackOnline(){
		this.hasFailed = false;
		this.isBackOnline = true;
		
		this.running = true;
		
		//Perform necessary actions to reset the local camera
		this.camera.bringBackOnline();
		
		//Note: initially only bring a fresh self-adaptation subsystem and a fresh perceptionMW online. 
		// The SA-subsystem will then handle the rest
		this.selfAdaptationSubsystem = new SelfAdaptationSubsystem(this, this.roadNetwork, this.communicationMiddleware);
		threadManager.register(this.selfAdaptationSubsystem);		
		//this.perceptionMiddleware = new PerceptionMiddleware(this.camera);
		threadManager.register(this.perceptionMiddleware);
		
		//OrganizationManager will be initialized by the self-adaptation subsystem
		this.organizationManager = null;	
	}
	
	//@Pieter
	public boolean isBackOnline(){
		return this.isBackOnline;
	}
	private boolean isBackOnline = false;

	//@Pieter
	public boolean hasFailed(){
		return this.hasFailed;
	}
	private boolean hasFailed = false;
	
}
