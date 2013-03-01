package node.hostInfrastructure;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import node.agentMiddleware.communication.middleware.CommunicationMiddleware;
import node.agentMiddleware.communication.middleware.LockingException;
import node.agentMiddleware.communication.middleware.MessageProfiler;
import node.agentMiddleware.communication.middleware.messageBuffers.BufferedInfo;
import node.agentMiddleware.communication.middleware.messageBuffers.BufferedMasterIDReply;
import node.agentMiddleware.communication.middleware.messageBuffers.BufferedMasterIDRequest;
import node.agentMiddleware.communication.middleware.messageBuffers.BufferedNeighbourInfo;
import node.agentMiddleware.communication.middleware.messageBuffers.BufferedOrganizationInfo;
import node.agentMiddleware.communication.middleware.messageBuffers.BufferedPing;
import node.agentMiddleware.communication.middleware.messageBuffers.BufferedPong;
import node.agentMiddleware.communication.middleware.messageBuffers.BufferedStartAsMaster;
import node.agentMiddleware.communication.middleware.messageBuffers.BufferedStartAsSlave;
import node.agentMiddleware.communication.middleware.messageBuffers.BufferedTerminationMessage;
import node.agentMiddleware.communication.middleware.messageBuffers.BufferedTrafficJamInfo;
import node.organizationMiddleware.contextManager.contextDirectories.NeighbourInfo;
import node.organizationMiddleware.contextManager.contextDirectories.Organization;
import node.organizationMiddleware.contextManager.contextDirectories.TrafficJamInfo;
import node.selfAdaptationSystem.coordination.BufferedSelfAdaptationMessage;
import node.selfAdaptationSystem.coordination.SelfAdaptationMessage;
import node.selfAdaptationSystem.mapeManager.cameraIntroduction.CameraIntroductionScenario;
import node.selfAdaptationSystem.mapeManager.selfMonitoring.SelfMonitoringScenario;

import utilities.NodeID;

/**
 * Singleton object
 * @author Robrecht
 *
 */
public class SimulatedCommunicationNetwork implements Runnable{
	
	private static final int THREAD_SLEEP = 10;
	
	private HashMap<String,CommunicationMiddleware> nodes;	
	private Vector<BufferedInfo> messageBuffer;
	private static SimulatedCommunicationNetwork me;
	private MessageProfiler profiler;
				
	/***************
	 *             *
	 * CONSTRUCTOR *
	 *             *
	 ***************/
	
	public static SimulatedCommunicationNetwork getInstance(){
		if(me == null)
			me = new SimulatedCommunicationNetwork();
		return me;
	}
	
	//@Pieter
	public static void resetSimulatedNetworkInstance(){
		SimulatedCommunicationNetwork.me = null;
	}
	
	/***********
	 *         *
	 * LOCKING *
	 *         *
	 ***********/
	
	public Vector<Integer> locks;
	
	public void lock(int organizationID) throws LockingException{
		if(this.locks.contains(organizationID))
			throw new LockingException(organizationID);
		
		locks.add(organizationID);
	}
	
	public void lock(int organizationID1, int organizationID2) throws LockingException{
		if(this.locks.contains(organizationID1))
			throw new LockingException(organizationID1);
		if(this.locks.contains(organizationID2))
			throw new LockingException(organizationID2);
		
		locks.add(organizationID1);
		locks.add(organizationID2);
	}
	
	public void unlock(int organizationID){
		this.locks.remove(organizationID);
	}
	
	public boolean isLocked(int organizationID){
		return this.locks.contains(organizationID);
	}
	
	/****************
	 *              *
	 * SELF-HEALING *
	 *              *
	 ****************/
	
	public synchronized void forcedLock(int organizationID){
		if(!locks.contains(organizationID))
			locks.add(organizationID);
	}
	
	//@Pieter
	public synchronized void forcedUnlock(int organizationID){
		locks.remove(new Integer(organizationID));
	}
	
	/*******************************
	 *                             *
	 * PRIVATE AND PACKAGE METHODS *
	 *                             *
	 *******************************/
	
	private SimulatedCommunicationNetwork(){
		this.nodes = new HashMap<String, CommunicationMiddleware>();
		this.messageBuffer = new Vector<BufferedInfo>();
		this.isolatedNodes = new Vector<String>();
		this.locks = new Vector<Integer>();
		profiler = MessageProfiler.getMessageProfiler();
	}
	
	public void registerCommunicationMiddleware(CommunicationMiddleware cmw){
		nodes.put(cmw.getNodeIdentifier(), cmw);
	}
	
	public synchronized void send(Organization data, String nodeIdentifier){
		this.messageBuffer.add(new BufferedOrganizationInfo(data, messageDelay, nodeIdentifier));
		this.profiler.register("OrganizationData");
	}
	
	public synchronized void send(TrafficJamInfo info, NodeID target, String nodeIdentifier){
		this.messageBuffer.add(new BufferedTrafficJamInfo(info, target, messageDelay, nodeIdentifier));
		this.profiler.register("TrafficJamInfo");
	}

	public synchronized void sendNeighbourInfo(NeighbourInfo info, NodeID target, String nodeIdentifier){
		this.messageBuffer.add(new BufferedNeighbourInfo(info, target, messageDelay, nodeIdentifier));
		this.profiler.register("NeighbourInfo");
	}
	
	public synchronized void sendTerminationMessage(String receiver){
		this.messageBuffer.add(new BufferedTerminationMessage(messageDelay, receiver));
		this.profiler.countTerminationMessage(receiver);
	}
	
	public synchronized void sendMasterIDRequest(NodeID returnIdentifier, String receiver){
		this.messageBuffer.add(new BufferedMasterIDRequest(messageDelay, returnIdentifier, receiver));
		this.profiler.register("MasterIDRequest");
	}
	
	public synchronized void sendMasterIDReply(NodeID masterID, String receiver){
		this.messageBuffer.add(new BufferedMasterIDReply(messageDelay, masterID, receiver));
		this.profiler.register("MasterIDReply");
	}
	
	public synchronized void sendPing(Organization org, String receiver){
		this.messageBuffer.add(new BufferedPing(org, messageDelay, receiver));
		this.profiler.register("Ping");
	}
	
	public synchronized void sendPong(Organization org, String receiver){
		this.messageBuffer.add(new BufferedPong(org, messageDelay, receiver));
		this.profiler.register("Pong");
	}
	
	public synchronized void sendStartAsMaster(Organization organization, String nodeIdentifier){
		this.messageBuffer.add(new BufferedStartAsMaster(organization, messageDelay, nodeIdentifier));
		this.profiler.register("StartAsMaster");
	}
	
	public synchronized void sendStartAsSlave(Organization organization, String nodeIdentifier){
		this.messageBuffer.add(new BufferedStartAsSlave(organization, messageDelay, nodeIdentifier));
		this.profiler.countStartAsSlave(organization,nodeIdentifier);
	}
	
	//@Pieter
	public synchronized void sendSelfHealingMessage(SelfAdaptationMessage message, String nodeIdentifier){
		this.messageBuffer.add(new BufferedSelfAdaptationMessage(message, messageDelay, nodeIdentifier));
		this.profiler.register("SelfHealingMessage");
	}
	
	
	private synchronized void processMessages(){
		Vector<BufferedInfo> updatedBuffer = new Vector<BufferedInfo>();
		
		for(Enumeration<BufferedInfo> e = this.messageBuffer.elements(); e.hasMoreElements(); ){
			BufferedInfo info = e.nextElement();	
			if(!isolatedNodes.contains(info.getReceiverID())){
				if(info.hasDelay()){
					info.advanceTime();
					updatedBuffer.add(info);
				}else{
					if(this.nodes.get(info.getReceiverID()) != null){
						info.deliver(this.nodes.get(info.getReceiverID()));
					}else{
						System.out.println("ERROR: receiver "+info.getReceiverID()+" does not exist");
					}
					profiler.countReceivedMessage();
				}
			}else{
				//System.out.println("??? Isolated node could not receive message - "+info.getReceiverID());
			}
		}		
		this.messageBuffer = updatedBuffer;
	}
	
	/*****************
	 *               *
	 * MESSAGE DELAY *
	 *               *
	 *****************/
	
	private int messageDelay = 0;
	
	public void setMessageDelay(int messageDelay){
		this.messageDelay = messageDelay;
	}
	
	/******************
	 *                *
	 * NODE ISOLATION *
	 *                *
	 ******************/
	
	private Vector<String> isolatedNodes;
	
	public synchronized void isolateNode(String nodeIdentifier){
		this.isolatedNodes.add(nodeIdentifier);
	}
	
	public synchronized void deIsolateNode(String nodeIdentifier){
		this.isolatedNodes.remove(nodeIdentifier);
	}
	
	/***********
	 *         *
	 * TESTING *
	 *         *
	 ***********/
	
	public synchronized void reset(){
		this.isolatedNodes.removeAllElements();
		this.messageBuffer.removeAllElements();
		this.messageDelay = 0;
		this.locks = new Vector<Integer>();
	}
	
	/*************
	 *           *
	 * THREADING *
	 *           *
	 *************/
	
	private Thread runner = null;
	
	private String[] toPrint = {"AliveRequest", "AliveSignal"};

	public void run() { 
		while (runner != null){
	    	this.processMessages();	    	
	    	try {Thread.sleep (THREAD_SLEEP);} catch(InterruptedException e) {}
	    	//profiler.printOutMessageInfo();
	    	//TODO
	    	//profiler.printOutMessageInfo(toPrint, true);
	    }
	}
	
	public void step(){
//		//@Pieter
//		HashMap<String, Integer> messageCount = new HashMap<String, Integer>();
//		
//		for(BufferedInfo msg : this.messageBuffer){
//			if(msg instanceof BufferedSelfAdaptationMessage){
//				SelfAdaptationMessage message = ((BufferedSelfAdaptationMessage) msg).getMessage();
//				String scenarioType = message.getScenario().getScenarioType();
//				
//				Integer currentCount = messageCount.get(scenarioType);
//				
//				if(currentCount == null){
//					//First message of this type for this execution step
//					messageCount.put(scenarioType, 1);
//				}
//				else{
//					messageCount.put(scenarioType, currentCount++);
//				}
//				
////				if( (message instanceof PlanningMessage) || (message instanceof ExecutionMessage) )
////					selfHealingMsgCount++;
//			}
//			else{
//				//Regular inter-node message
//				String messageClassifier = "domain";
//				Integer currentCount = messageCount.get(messageClassifier);
//				
//				if(currentCount == null){
//					//First message of this type for this execution step
//					messageCount.put(messageClassifier, 1);
//				}
//				else{
//					messageCount.put(messageClassifier, currentCount++);
//				}
//			}
//				
//		}
//		
//		String print = "";
//		for(String scenarioType : messageCount.keySet()){
//			print += " | " + scenarioType + " " + messageCount.get(scenarioType);
//		}
//		
//		System.out.println(print);
//		
//		messageCount.clear();
		
		
		
		
		this.processMessages();
		//TODO
		//profiler.printOutMessageInfo();
	}
		
	public void start() {  
	    if (runner == null) {
	      runner = new Thread(this);
	      runner.start();
	    }
	}
	  
	public void stop() { 
		runner = null;
	}
	
	public void resetMessageProfiler(){
		this.profiler.reset();
	}

	public MessageProfiler getProfiler() {
		return this.profiler;
	}
}
