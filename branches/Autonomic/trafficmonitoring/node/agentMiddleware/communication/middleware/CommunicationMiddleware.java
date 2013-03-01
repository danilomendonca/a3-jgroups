package node.agentMiddleware.communication.middleware;
import utilities.NodeID;
import node.agentMiddleware.communication.middleware.messageBuffers.BufferedMasterIDReply;
import node.agentMiddleware.communication.middleware.messageBuffers.BufferedMasterIDRequest;
import node.agentMiddleware.communication.middleware.messageBuffers.BufferedNeighbourInfo;
import node.agentMiddleware.communication.middleware.messageBuffers.BufferedPing;
import node.agentMiddleware.communication.middleware.messageBuffers.BufferedPong;
import node.agentMiddleware.communication.middleware.messageBuffers.BufferedStartAsMaster;
import node.agentMiddleware.communication.middleware.messageBuffers.BufferedStartAsSlave;
import node.agentMiddleware.communication.middleware.messageBuffers.BufferedTerminationMessage;
import node.agentMiddleware.communication.middleware.messageBuffers.BufferedTrafficJamInfo;

import java.util.*;

import node.agentMiddleware.action.interfaces.Action;
import node.agentMiddleware.communication.interfaces.*;
import node.hostInfrastructure.SimulatedCommunicationNetwork;
import node.organizationMiddleware.contextManager.contextDirectories.NeighbourInfo;
import node.organizationMiddleware.contextManager.contextDirectories.Organization;
import node.organizationMiddleware.contextManager.contextDirectories.TrafficJamInfo;
import node.selfAdaptationSystem.coordination.BufferedSelfAdaptationMessage;
import node.selfAdaptationSystem.coordination.MapeCommunicationManager;
import node.selfAdaptationSystem.coordination.SelfAdaptationMessage;

/**
 * One communication middleware per node!
 * Each communication middleware is connected with the simulated network
 * 
 * @author Robrecht
 *
 */
public class CommunicationMiddleware implements SendInterface, Action{
	
	private Vector<OrganizationReceiver> organizationReceivers;
	private Vector<TrafficJamInfoReceiver> trafficJamInfoReceivers;
	private Vector<NeighbourInfoReceiver> neighbourInfoReceivers;
	private Vector<TerminationReceiver> terminationReceivers;
	private Vector<MasterIDReplyReceiver> masterIDReplyReceivers;
	private Vector<MasterIDRequestReceiver> masterIDRequestReceivers;
	private Vector<PingReceiver> pingReceivers;
	private Vector<PongReceiver> pongReceivers;
	private Vector<LifecycleMessagesReceiver> startAsSlaveReceivers;
	private Vector<LifecycleMessagesReceiver> startAsMasterReceivers;
	//@Pieter
	private MapeCommunicationManager selfHealingMessageReceiver;
	
	private SimulatedCommunicationNetwork network;
	private String nodeIdentifier;
	
	/***************
	 *             *
	 * CONSTRUCTOR *
	 *             *
	 ***************/	
	
	public CommunicationMiddleware(SimulatedCommunicationNetwork network, String nodeIdentifier){
		this.network = network;
		this.nodeIdentifier = nodeIdentifier;
		network.registerCommunicationMiddleware(this);
		
		this.organizationReceivers = new Vector<OrganizationReceiver>();
		this.trafficJamInfoReceivers = new Vector<TrafficJamInfoReceiver>();
		this.neighbourInfoReceivers = new Vector<NeighbourInfoReceiver>();
		this.terminationReceivers = new Vector<TerminationReceiver>();
		this.masterIDReplyReceivers = new Vector<MasterIDReplyReceiver>();
		this.masterIDRequestReceivers = new Vector<MasterIDRequestReceiver>();
		this.pingReceivers = new Vector<PingReceiver>();
		this.pongReceivers = new Vector<PongReceiver>();
		this.startAsSlaveReceivers = new Vector<LifecycleMessagesReceiver>();
		this.startAsMasterReceivers = new Vector<LifecycleMessagesReceiver>();
	}
	
	/*******************************
	 *                             *
	 * PRIVATE AND PACKAGE METHODS *
	 *                             *
	 *******************************/
	
	public synchronized void deliver(Organization data){
		for(Enumeration<OrganizationReceiver> e = this.organizationReceivers.elements(); e.hasMoreElements();){
			e.nextElement().receiveOrganizationInfo(data);
		}
	}
	
	public synchronized void deliver(BufferedTrafficJamInfo info){
		for(Enumeration<TrafficJamInfoReceiver> e = this.trafficJamInfoReceivers.elements(); e.hasMoreElements();){
			e.nextElement().receiveTrafficJamInfo(info.getInfo(), info.getTarget());
		}
	}
	
	public synchronized void deliver(BufferedNeighbourInfo info){
		for(Enumeration<NeighbourInfoReceiver> e = this.neighbourInfoReceivers.elements(); e.hasMoreElements();){
			e.nextElement().receiveNeighbourInfo(info.getInfo(), info.getTarget());
		}
	}
	
	public synchronized void deliver(BufferedMasterIDRequest request){
		//System.out.println("### "+request+" "+this.masterIDRequestReceivers);
		for(Enumeration<MasterIDRequestReceiver> e = this.masterIDRequestReceivers.elements(); e.hasMoreElements();){
			e.nextElement().receiveMasterIDRequest(request.getReturnID());
		}
	}
	
	public synchronized void deliver(BufferedMasterIDReply reply){
		for(Enumeration<MasterIDReplyReceiver> e = this.masterIDReplyReceivers.elements(); e.hasMoreElements();){
			e.nextElement().receiveMasterIDReply(reply.getMasterID());
		}
	}
	
	public synchronized void deliver(BufferedTerminationMessage reply){
		for(Enumeration<TerminationReceiver> e = this.terminationReceivers.elements(); e.hasMoreElements();){
			TerminationReceiver tr = e.nextElement();
			tr.receiveTerminationMessage();
		}
	}
	
	public synchronized void deliver(BufferedStartAsMaster start){
		for(Enumeration<LifecycleMessagesReceiver> e = this.startAsMasterReceivers.elements(); e.hasMoreElements();){
			e.nextElement().receiveStartAsOrganizationManagerMessage(start.getOrganization());
		}
	}
	
	public synchronized void deliver(BufferedStartAsSlave start){
		for(Enumeration<LifecycleMessagesReceiver> e = this.startAsSlaveReceivers.elements(); e.hasMoreElements();){
			e.nextElement().receiveStartAsSlaveMessage(start.getOrganization());
		}
	}
	
	public synchronized void deliver(BufferedPing ping){
		for(Enumeration<PingReceiver> e = this.pingReceivers.elements(); e.hasMoreElements();){
			e.nextElement().receivePing(ping.getOrganization());
		}
	}
	
	public synchronized void deliver(BufferedPong pong){		
		for(Enumeration<PongReceiver> e = this.pongReceivers.elements(); e.hasMoreElements();){
			e.nextElement().receivePong(pong.getOrganization());
		}
	}	
	
	//@Pieter
	public synchronized void deliver(BufferedSelfAdaptationMessage selfHealingMessage){		
		this.selfHealingMessageReceiver.receiveSelfAdaptationMessage(selfHealingMessage.getMessage());
	}	
	
	
	
	public String getNodeIdentifier(){
		return this.nodeIdentifier;
	}
	
	/*******************************
	 *                             *
	 * SYNCHRONIZATION AND LOCKING *
	 *                             *
	 *******************************/
	
	public synchronized void lock(int organizationID)throws LockingException{
		this.network.lock(organizationID);
	}
	
	public synchronized void lock(int organizationID1, int organizationID2)throws LockingException{
		this.network.lock(organizationID1, organizationID2);
	}
	
	public synchronized void unlock(int organizationID){
		this.network.unlock(organizationID);
	}
	
	public synchronized boolean isLocked(int organizationID){
		return this.network.isLocked(organizationID);
		
	}
	
	/****************
	 *              *
	 * SELF-HEALING *
	 *              *
	 ****************/
	
	//@Pieter
	/**
	 * Used by the local self-healing subsystem to block the organization with the given
	 * id from merging or splitting.
	 * 
	 * @pre	The given organization id should always be of the organization the local node currently
	 * 		belongs to. In other words, a node can only block its own organization.
	 * @pre	This method should only be used by a master of an organization
	 */
	public synchronized void forcedLock(int organizationID){
		this.network.forcedLock(organizationID);
	}
	
	//@Pieter
	/**
	 * Used by the local self-healing subsystem to lift the merge/split block on the organization with 
	 * the given
	 * 
	 * @pre	The given organization id should always be of the organization the local node currently
	 * 		belongs to. In other words, a node can only unblock its own organization.
	 * @pre	This method should only be used by a master of an organization
	 */
	public synchronized void forcedUnlock(int organizationID){
		this.network.forcedUnlock(organizationID);
	}
	
	/*************************************
	 *                                   *
	 * IMPLEMENTATION OF SEND INTERFACES *
	 *                                   *
	 *************************************/
	
	public void sendOrganizationInfo(Organization data, String nodeIdentifier){
		this.network.send(data, nodeIdentifier);
	}
	
	public synchronized void receiveOrganizationInfo(OrganizationReceiver receiver){
		this.organizationReceivers.add(receiver);
	}
	
	public synchronized void stopOrganizationInfoReception(OrganizationReceiver receiver){
		this.organizationReceivers.remove(receiver);
	}
	
	public void sendTrafficJamInfo(TrafficJamInfo info, NodeID target, String node){
		this.network.send(info, target, node);
	}
	
	public synchronized void receiveTrafficJamInfo(TrafficJamInfoReceiver receiver){
		this.trafficJamInfoReceivers.add(receiver);
	}
	
	public synchronized void stopTrafficJamInfoReception(TrafficJamInfoReceiver receiver){
		this.trafficJamInfoReceivers.remove(receiver);
	}

	public void sendNeighbourInfo(NeighbourInfo info, NodeID target, String node){
		this.network.sendNeighbourInfo(info, target, node);
	}
	
	public synchronized void receiveNeighbourInfo(NeighbourInfoReceiver receiver){
		this.neighbourInfoReceivers.add(receiver);
	}
	
	public synchronized void stopNeighbourInfoReception(NeighbourInfoReceiver receiver){
		this.neighbourInfoReceivers.remove(receiver);
	}
	
	public void sendTerminateOrganizationMessage(String node){
		this.network.sendTerminationMessage(node);
	}
	
	public synchronized void receiveTerminationMessage(TerminationReceiver receiver){
		this.terminationReceivers.add(receiver);
	}
	
	public synchronized void stopTerminationMessageReception(TerminationReceiver receiver){
		this.terminationReceivers.remove(receiver);
	}
	
	public void sendMasterIDRequest(NodeID returnIdentifier, String nodeIdentifier){
		this.network.sendMasterIDRequest(returnIdentifier, nodeIdentifier);
	}
	
	public synchronized void receiveMasterIDRequest(MasterIDRequestReceiver receiver){
		this.masterIDRequestReceivers.add(receiver);
	}
	
	public synchronized void stopMasterIDRequestReception(MasterIDRequestReceiver receiver){
		this.masterIDRequestReceivers.remove(receiver);
	}
	
	public void sendMasterIDReply(NodeID masterID, String nodeIdentifier){
		this.network.sendMasterIDReply(masterID, nodeIdentifier);
	}
	
	public synchronized void receiveMasterIDReply(MasterIDReplyReceiver receiver){
		this.masterIDReplyReceivers.add(receiver);
	}
	
	public synchronized void stopMasterIDReplyReception(MasterIDReplyReceiver receiver){
		this.masterIDReplyReceivers.remove(receiver);
	}	
	
	public void sendPing(Organization data, String nodeIdentifier){
		this.network.sendPing(data, nodeIdentifier);
	}
	
	public synchronized void receivePing(PingReceiver receiver){
		this.pingReceivers.add(receiver);
	}
	
	public synchronized void stopPingReception(PingReceiver receiver){
		this.pingReceivers.remove(receiver);
	}
	
	public void sendPong(Organization data, String nodeIdentifier){
		this.network.sendPong(data, nodeIdentifier);
	}
	
	public synchronized void receivePong(PongReceiver receiver){
		this.pongReceivers.add(receiver);
	}
	
	public synchronized void stopPongReception(PongReceiver receiver){
		this.pongReceivers.remove(receiver);
	}
	
	public void sendStartAsSlaveMessage(Organization organization, String nodeIdentifier){
		this.network.sendStartAsSlave(organization, nodeIdentifier);
	}
	
	public synchronized void receiveStartAsSlaveMessages(LifecycleMessagesReceiver receiver){
		this.startAsSlaveReceivers.add(receiver);
	}
	
	public synchronized void stopStartAsSlaveMessages(LifecycleMessagesReceiver receiver){
		this.startAsSlaveReceivers.remove(receiver);
	}
	
	public void sendStartAsOrganizationManagerMessage(Organization organization, String nodeIdentifier){
		this.network.sendStartAsMaster(organization, nodeIdentifier);
	}
	
	public synchronized void receiveStartAsOrganizationManagerMessages(LifecycleMessagesReceiver receiver){
		this.startAsMasterReceivers.add(receiver);
	}
	
	public synchronized void stopStartAsOrganizationManagerMessages(LifecycleMessagesReceiver receiver){
		this.startAsMasterReceivers.remove(receiver);
	}
	
	
	
	
	
	//@Pieter
	public synchronized void sendSelfHealingMessage(SelfAdaptationMessage message, String nodeIdentifier){
		this.network.sendSelfHealingMessage(message, nodeIdentifier);
	}
	
	//@Pieter
	public synchronized void receiveSelfHealingMessages(MapeCommunicationManager receiver){
		this.selfHealingMessageReceiver = receiver;
	}	
}
