package utilities;

import java.util.ArrayList;

import node.agentMiddleware.communication.interfaces.LifecycleMessagesReceiver;
import node.agentMiddleware.communication.interfaces.MasterIDReplyReceiver;
import node.agentMiddleware.communication.interfaces.MasterIDRequestReceiver;
import node.agentMiddleware.communication.interfaces.NeighbourInfoReceiver;
import node.agentMiddleware.communication.interfaces.OrganizationReceiver;
import node.agentMiddleware.communication.interfaces.PingReceiver;
import node.agentMiddleware.communication.interfaces.PongReceiver;
import node.agentMiddleware.communication.interfaces.SendInterface;
import node.agentMiddleware.communication.interfaces.TerminationReceiver;
import node.agentMiddleware.communication.interfaces.TrafficJamInfoReceiver;
import node.organizationMiddleware.contextManager.contextDirectories.NeighbourInfo;
import node.organizationMiddleware.contextManager.contextDirectories.Organization;
import node.organizationMiddleware.contextManager.contextDirectories.TrafficJamInfo;



public class MessageBuffer implements OrganizationReceiver,
									  TrafficJamInfoReceiver,
									  NeighbourInfoReceiver, 
									  TerminationReceiver,
									  MasterIDRequestReceiver,
									  MasterIDReplyReceiver,
									  LifecycleMessagesReceiver,
									  PingReceiver,
									  PongReceiver{
	
	private ArrayList<Pair<String,Object>> messages;
	private int nbNeighBourInfoSubscribers;
	private int nbTrafficJamInfoSubscribers;
	private int nbOrganizationInfoSubscribers;
	private int nbTerminationSubscribers;
	private int nbStartAsSlaveSubscribers;
	private int nbMasterIDRequestSubscribers;
	
	private SendInterface agentLayer;
	private int nbMasterIDRepliesSubscribers;
	private int nbStartAsOrganizationManagerSubscribers;
	private int nbPingReceivers;
	private int nbPongReceivers;
	
	public MessageBuffer(SendInterface agentLayer){
		this.messages = new ArrayList<Pair<String,Object>>();
		this.agentLayer = agentLayer;
	}
	
	/********************************************
	 *
	 * 
	 *  interfaces to the remote sync mechanisms
	 *
	 *
	 ********************************************/
	
	/*****************************
	 * 
	 *   receive masterID request
	 * 
	 *****************************/
	
	public void subscribeOnMasterIDRequests(){
		nbMasterIDRequestSubscribers++;
		if(nbMasterIDRequestSubscribers == 1){
			//subscribe on agentLayer
			agentLayer.receiveMasterIDRequest(this);
		}
	}
	
    public void unsubscribeOnMasterIDRequests(){
    	nbMasterIDRequestSubscribers--;
		if(nbMasterIDRequestSubscribers == 0){
			agentLayer.stopMasterIDRequestReception(this);
			removeMessages("receiveMasterIDRequest");
		}	
	}
	
	synchronized public boolean hasMasterIDRequestAsNextMessage() {
		try{
			Pair<String,Object> p = messages.get(0);
			if(p.getElement1().equals("receiveMasterIDRequest"))
				return true;
			else return false;
			
		}catch (IndexOutOfBoundsException e){
			return false;
		}
	}
	
	synchronized public NodeID receiveMasterIDRequest() {
		try{
			Pair<String,Object> p = messages.get(0);
			if(p.getElement1().equals("receiveMasterIDRequest")){
				messages.remove(0);
				return (NodeID) p.getElement2();
			}
			else 
				throw new IllegalArgumentException("next message to be received is not addressed to receiveMasterIDRequest");
			
		}catch (IndexOutOfBoundsException e){
			throw new IllegalArgumentException("there are no new messages");
		}
	}
	
	/*****************************
	 * 
	 *   receive masterID reply
	 * 
	 *****************************/
	
	public void subscribeOnMasterIDReplies(){
		nbMasterIDRepliesSubscribers++;
		if(nbMasterIDRepliesSubscribers == 1){
			//subscribe on agentLayer
			agentLayer.receiveMasterIDReply(this);
		}
	}
	
    public void unsubscribeOnMasterIDReplies(){
    	nbMasterIDRepliesSubscribers--;
		if(nbMasterIDRepliesSubscribers == 0){
			agentLayer.stopMasterIDReplyReception(this);
			removeMessages("receiveMasterIDReply");
		}	
	}
	
	synchronized public boolean hasMasterIDReplyAsNextMessage() {
		try{
			Pair<String,Object> p = messages.get(0);
			if(p.getElement1().equals("receiveMasterIDReply"))
				return true;
			else return false;
			
		}catch (IndexOutOfBoundsException e){
			return false;
		}
	}
	
	synchronized public NodeID receiveMasterIDReply() {
		try{
			Pair<String,Object> p = messages.get(0);
			if(p.getElement1().equals("receiveMasterIDReply")){
				messages.remove(0);
				return (NodeID) p.getElement2();
			}
			else 
				throw new IllegalArgumentException("next message to be received is not addressed to receiveMasterIDReply");
			
		}catch (IndexOutOfBoundsException e){
			throw new IllegalArgumentException("there are no new messages");
		}
	}
	
	/*****************************
	 * 
	 *   receive organization info
	 * 
	 *****************************/
	
	public void subscribeOnOrganizationInfoMessages(){
		nbOrganizationInfoSubscribers++;
		if(nbOrganizationInfoSubscribers == 1){
			//subscribe on agentLayer
			agentLayer.receiveOrganizationInfo(this);
		}
	}
	
    public void unsubscribeOnOrganizationInfoMessages(){
		nbOrganizationInfoSubscribers--;
		if(nbOrganizationInfoSubscribers == 0){
			agentLayer.stopOrganizationInfoReception(this);
			removeMessages("receiveOrganizationInfo");
		}	
	}
	
	synchronized public boolean hasOrganizationInfoAsNextMessage() {
		try{
			Pair<String,Object> p = messages.get(0);
			if(p.getElement1().equals("receiveOrganizationInfo"))
				return true;
			else return false;
			
		}catch (IndexOutOfBoundsException e){
			return false;
		}
	}
	
	synchronized public Organization receiveOrganizationInfo() {
		try{
			Pair<String,Object> p = messages.get(0);
			if(p.getElement1().equals("receiveOrganizationInfo")){
				Organization result = (Organization) p.getElement2();
				messages.remove(0);
				return result;
			}
			else 
				throw new IllegalArgumentException("next message to be received is not addressed to receiveOrganizationInfo");
			
		}catch (IndexOutOfBoundsException e){
			throw new IllegalArgumentException("there are no new messages");
		}
	}
	
	/****************************************
	 * 
	 *   receive start as slave messages
	 * 
	 ****************************************/
	
	public void subscribeOnStartAsSlaveMessages(){
		nbStartAsSlaveSubscribers++;
		if(nbStartAsSlaveSubscribers == 1){
			//subscribe on agentLayer
			agentLayer.receiveStartAsSlaveMessages(this);
		}
	}
	
    public void unsubscribeOnOnStartAsSlaveMessages(){
    	nbStartAsSlaveSubscribers--;
		if(nbStartAsSlaveSubscribers == 0){
			agentLayer.stopStartAsSlaveMessages(this);
			removeMessages("receiveStartAsSlaveMessage");
		}	
	}
	
	synchronized public boolean hasStartAsSlaveAsNextMessage() {
		try{
			Pair<String,Object> p = messages.get(0);
			if(p.getElement1().equals("receiveStartAsSlaveMessage"))
				return true;
			else return false;
			
		}catch (IndexOutOfBoundsException e){
			return false;
		}
	}
	
	synchronized public Organization receiveStartAsSlaveMessage() {
		try{
			Pair<String,Object> p = messages.get(0);
			if(p.getElement1().equals("receiveStartAsSlaveMessage")){
				Organization result = (Organization) p.getElement2();
				messages.remove(0);
				return result;
			}
			else 
				throw new IllegalArgumentException("next message to be received is not addressed to receiveStartAsSlaveMessage");
			
		}catch (IndexOutOfBoundsException e){
			throw new IllegalArgumentException("there are no new messages");
		}
	}
	
	/**************************************************
	 * 
	 *   receive start as OrganizationManager messages
	 * 
	 **************************************************/
	
	public void subscribeOnStartAsOrganizationManagerMessages(){
		nbStartAsOrganizationManagerSubscribers++;
		if(nbStartAsOrganizationManagerSubscribers == 1){
			//subscribe on agentLayer
			agentLayer.receiveStartAsOrganizationManagerMessages(this);
		}
	}
	
    public void unsubscribeOnOnStartAsOrganizationManagerMessages(){
    	nbStartAsOrganizationManagerSubscribers--;
		if(nbStartAsOrganizationManagerSubscribers == 0){
			agentLayer.stopStartAsOrganizationManagerMessages(this);
			removeMessages("receiveStartAsOrganizationManagerMessage");
		}	
	}
	
	synchronized public boolean hasStartAsOrganizationManagerAsNextMessage() {
		try{
			Pair<String,Object> p = messages.get(0);
			if(p.getElement1().equals("receiveStartAsOrganizationManagerMessage"))
				return true;
			else return false;
			
		}catch (IndexOutOfBoundsException e){
			return false;
		}
	}
	
	synchronized public Organization receiveStartAsOrganizationManagerMessage() {
		try{
			Pair<String,Object> p = messages.get(0);
			if(p.getElement1().equals("receiveStartAsOrganizationManagerMessage")){
				Organization result = (Organization) p.getElement2();
				messages.remove(0);
				return result;
			}
			else 
				throw new IllegalArgumentException("next message to be received is not addressed to receiveStartAsOrganizationManagerMessage");
			
		}catch (IndexOutOfBoundsException e){
			throw new IllegalArgumentException("there are no new messages");
		}
	}
	
	/*****************************
	 * 
	 *   receive traffic jam info
	 * 
	 *****************************/
	
	public void subscribeOnTrafficJamInfoMessages(){
		nbTrafficJamInfoSubscribers++;
		if(nbTrafficJamInfoSubscribers == 1){
			//subscribe on agentLayer
			agentLayer.receiveTrafficJamInfo(this);
		}
	}
	
	public void unsubscribeOnTrafficJamInfoMessages(){
		nbTrafficJamInfoSubscribers--;
		if(nbTrafficJamInfoSubscribers == 0){
			agentLayer.stopTrafficJamInfoReception(this);
			removeMessages("receiveTrafficJamInfo");
		}
	}
		
	synchronized public boolean hasReceiveTrafficJamInfoAsNextMessage() {
		try{
			Pair<String,Object> p = messages.get(0);
			if(p.getElement1().equals("receiveTrafficJamInfo"))
				return true;
			else return false;
			
		}catch (IndexOutOfBoundsException e){
			return false;
		}
	}
	
	
	synchronized public ArrayList<Object> receiveTrafficJamInfo() {
		try{
			Pair<String,Object> p = messages.get(0);
			if(p.getElement1().equals("receiveTrafficJamInfo")){
				messages.remove(0);
				return (ArrayList<Object>) p.getElement2();
			}
			else 
				throw new IllegalArgumentException("next message to be received is not addressed to receiveTrafficJamInfo");
			
		}catch (IndexOutOfBoundsException e){
			throw new IllegalArgumentException("there are no new messages");
		}
	}
	
	/*****************************
	 * 
	 *   receive neighbour info
	 * 
	 *****************************/
	
	public void subscribeOnNeighbourInfoMessages(){
		nbNeighBourInfoSubscribers++;
		if(nbNeighBourInfoSubscribers == 1){
			//subscribe on agentLayer
			agentLayer.receiveNeighbourInfo(this);
		}
	}

	public void unsubscribeOnNeighbourInfoMessages(){
		nbNeighBourInfoSubscribers--;
		if(nbNeighBourInfoSubscribers == 0){
			agentLayer.stopNeighbourInfoReception(this);
			removeMessages("receiveNeighbourInfo");
		}
	}
	
	synchronized public boolean hasReceiveNeighbourInfoAsNextMessage() {
		try{
			Pair<String,Object> p = messages.get(0);
			if(p.getElement1().equals("receiveNeighbourInfo"))
				return true;
			else return false;
			
		}catch (IndexOutOfBoundsException e){
			return false;
		}
	}
	
	
	synchronized public ArrayList<Object> receiveNeighbourInfo() {
		try{
			Pair<String,Object> p = messages.get(0);
			if(p.getElement1().equals("receiveNeighbourInfo")){
				messages.remove(0);
				return (ArrayList<Object>) p.getElement2();
			}
			else 
				throw new IllegalArgumentException("next message to be received is not addressed to receiveNeighbourInfo");
			
		}catch (IndexOutOfBoundsException e){
			throw new IllegalArgumentException("there are no new messages");
		}
	}
	
	/********************
	 * 
	 *   receive ping
	 * 
	 ********************/
	
	public void subscribeOnPings(){
		nbPingReceivers++;
		if(nbPingReceivers == 1){
			//subscribe on agentLayer
			agentLayer.receivePing(this);
		}
	}

	public void unsubscribeOnPings(){
		nbPingReceivers--;
		if(nbPingReceivers == 0){
			agentLayer.stopPingReception(this);
			removeMessages("Ping");
		}
	}
	
	synchronized public boolean hasPingAsNextMessage() {
		try{
			Pair<String,Object> p = messages.get(0);
			if(p.getElement1().equals("Ping"))
				return true;
			else return false;
			
		}catch (IndexOutOfBoundsException e){
			return false;
		}
	}
	
	
	synchronized public Organization receivePing() {
		try{
			Pair<String,Object> p = messages.get(0);
			if(p.getElement1().equals("Ping")){
				messages.remove(0);
				return (Organization) p.getElement2();
			}
			else 
				throw new IllegalArgumentException("next message to be received is not Ping");
			
		}catch (IndexOutOfBoundsException e){
			throw new IllegalArgumentException("there are no new messages");
		}
	}
	
	/*******************
	 * 
	 *   receive pong
	 * 
	 *******************/
	
	public void subscribeOnPongs(){
		nbPongReceivers++;
		if(nbPongReceivers == 1){
			//subscribe on agentLayer
			agentLayer.receivePong(this);
		}
	}

	public void unsubscribeOnPongs(){
		nbPongReceivers--;
		if(nbPongReceivers == 0){
			agentLayer.stopPongReception(this);
			removeMessages("Pong");
		}
	}
	
	synchronized public boolean hasPongAsNextMessage() {
		try{
			Pair<String,Object> p = messages.get(0);
			if(p.getElement1().equalsIgnoreCase("Pong"))
				return true;
			else return false;
			
		}catch (IndexOutOfBoundsException e){
			return false;
		}
	}
	
	
	synchronized public Organization receivePong() {
		try{
			Pair<String,Object> p = messages.get(0);
			if(p.getElement1().equalsIgnoreCase("Pong")){
				messages.remove(0);
				return (Organization) p.getElement2();
			}
			else 
				throw new IllegalArgumentException("next message to be received is not Pong");
			
		}catch (IndexOutOfBoundsException e){
			throw new IllegalArgumentException("there are no new messages");
		}
	}
	
	/*********************************
	 * 
	 *    receive termination message
	 * 
	 *********************************/
	
	public void subscribeOnTerminationMessages(){
		nbTerminationSubscribers++;
		if(nbTerminationSubscribers == 1){
			//subscribe on agentLayer
			agentLayer.receiveTerminationMessage(this);
		}
	}

	public void unsubscribeOnTerminationMessages(){
		nbTerminationSubscribers--;
		if(nbTerminationSubscribers == 0){
			agentLayer.stopTerminationMessageReception(this);
			removeMessages("receiveTerminationMessage");
		}
	}
	
	synchronized public boolean hasTerminationMsgAsNextMessage() {		
		try{
			for(Pair<String,Object> message : messages){
				if(message.getElement1().equals("receiveTerminationMessage"))
					return true;
			}
			return false;			
		}catch (IndexOutOfBoundsException e){
			return false;
		}
	}
	
	
	synchronized public void popTerminationMessage() {
		try{
			ArrayList<Pair<String,Object>> toBeRemoved = new ArrayList<Pair<String,Object>>();
			for(Pair<String,Object> message : messages){
				if(message.getElement1().equals("receiveTerminationMessage")){
					toBeRemoved.add(message);
					break;
				}
			}
			if(toBeRemoved.size() == 0){
				throw new IllegalArgumentException("no termination messages");
			}
			messages.removeAll(toBeRemoved);
		}catch (IndexOutOfBoundsException e){
			throw new IllegalArgumentException("there are no new messages");
		}
	}
	
	/***********************************************************
	 * 
	 * 
	 * implementation of callback interfaces to the agent layer
	 *
	 *
	 ***********************************************************/
	
	synchronized public void receiveOrganizationInfo(Organization orgInfo) {
		Pair<String,Object> message = new Pair<String, Object>("receiveOrganizationInfo",orgInfo);
		messages.add(message);
	}

	synchronized public void receiveTrafficJamInfo(TrafficJamInfo info, NodeID target) {
		ArrayList<Object> data = new ArrayList<Object>();
		data.add(info);
		data.add(target);
		Pair<String,Object> message = new Pair<String, Object>("receiveTrafficJamInfo",data);
		messages.add(message);
	}
	
	synchronized public void receiveNeighbourInfo(NeighbourInfo info, NodeID target) {
		ArrayList<Object> data = new ArrayList<Object>();
		data.add(info);
		data.add(target);
		Pair<String,Object> message = new Pair<String, Object>("receiveNeighbourInfo",data);
		messages.add(message);
	}

	synchronized public void receiveTerminationMessage() {
		Pair<String,Object> dummyMessage = new Pair<String, Object>("receiveTerminationMessage",null);
		messages.add(dummyMessage);
	}

	synchronized public void receiveStartAsSlaveMessage(Organization info) {
		Pair<String,Object> message = new Pair<String, Object>("receiveStartAsSlaveMessage",info);
		messages.add(message);
	}
	
	synchronized public void receiveStartAsOrganizationManagerMessage(Organization info) {
		Pair<String,Object> message = new Pair<String, Object>("receiveStartAsOrganizationManagerMessage",info);
		messages.add(message);
	}

	synchronized public void receiveMasterIDRequest(NodeID returnAdress) {
		Pair<String,Object> message = new Pair<String, Object>("receiveMasterIDRequest",returnAdress);
		messages.add(message);
	}

	synchronized public void receiveMasterIDReply(NodeID masterID) {
		Pair<String, Object> message = new Pair<String, Object>("receiveMasterIDReply",masterID);
		messages.add(message);
	}

	//Ping
    synchronized public void receivePing(Organization data) {
    	Pair<String, Object> message = new Pair<String, Object>("Ping",data);
		messages.add(message);
	}

    //Pong
	synchronized public void receivePong(Organization data) {
		Pair<String, Object> message = new Pair<String, Object>("Pong",data);
		messages.add(message);
	}

	/*****************************
	 * 
	 * 	private helper functions
	 * 
	 *****************************/
	
	private synchronized void removeMessages(String type) {
		ArrayList<Pair<String, Object>> toBeRemoved = new ArrayList<Pair<String,Object>>();
		for(Pair<String, Object> message : this.messages){
			if(message.getElement1().equalsIgnoreCase(type))
				toBeRemoved.add(message);
		}
		
		this.messages.removeAll(toBeRemoved);
	}
	
}
