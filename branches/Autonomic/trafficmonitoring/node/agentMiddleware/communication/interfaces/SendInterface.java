package node.agentMiddleware.communication.interfaces;

import node.organizationMiddleware.contextManager.contextDirectories.NeighbourInfo;
import node.organizationMiddleware.contextManager.contextDirectories.Organization;
import node.organizationMiddleware.contextManager.contextDirectories.TrafficJamInfo;

import utilities.NodeID;

public interface SendInterface {

	public void sendOrganizationInfo(Organization data, String nodeIdentifier);
	public void receiveOrganizationInfo(OrganizationReceiver receiver);
	public void stopOrganizationInfoReception(OrganizationReceiver receiver);
	
	public void sendPing(Organization data, String nodeIdentifier);
	public void receivePing(PingReceiver receiver);
	public void stopPingReception(PingReceiver receiver);
	
	public void sendPong(Organization data, String nodeIdentifier);
	public void receivePong(PongReceiver receiver);
	public void stopPongReception(PongReceiver receiver);
		
	public void sendTrafficJamInfo(TrafficJamInfo info, NodeID target, String node);
	public void receiveTrafficJamInfo(TrafficJamInfoReceiver receiver);
	public void stopTrafficJamInfoReception(TrafficJamInfoReceiver receiver);

	public void sendNeighbourInfo(NeighbourInfo info, NodeID target, String node);
	public void receiveNeighbourInfo(NeighbourInfoReceiver receiver);
	public void stopNeighbourInfoReception(NeighbourInfoReceiver receiver);
	
	public void sendTerminateOrganizationMessage(String node);
	public void receiveTerminationMessage(TerminationReceiver receiver);
	public void stopTerminationMessageReception(TerminationReceiver receiver);

	public void sendStartAsSlaveMessage(Organization organization, String nodeIdentifier);
	public void receiveStartAsSlaveMessages(LifecycleMessagesReceiver receiver);
	public void stopStartAsSlaveMessages(LifecycleMessagesReceiver receiver);
	
	public void sendStartAsOrganizationManagerMessage(Organization personalOrganization, String nodeIdentifier);
	public void receiveStartAsOrganizationManagerMessages(LifecycleMessagesReceiver receiver);
	public void stopStartAsOrganizationManagerMessages(LifecycleMessagesReceiver receiver);
	
//	public void sendNewPersonalOrganizationInfo(Organization personalOrganization, String nodeIdentifier);
//	public void receivePersonalOrganizationInfo(PersonalOrganizationInfoReceiver receiver);
//	public void stopPersonalOrganizationInfoReception(PersonalOrganizationInfoReceiver receiver);

	public void sendMasterIDRequest(NodeID returnIdentifier, String nodeIdentifier);
	public void receiveMasterIDRequest(MasterIDRequestReceiver receiver);
	public void stopMasterIDRequestReception(MasterIDRequestReceiver receiver);
	
	public void sendMasterIDReply(NodeID masterID, String nodeIdentifier);
	public void receiveMasterIDReply(MasterIDReplyReceiver receiver);
	public void stopMasterIDReplyReception(MasterIDReplyReceiver receiver);
	
}
