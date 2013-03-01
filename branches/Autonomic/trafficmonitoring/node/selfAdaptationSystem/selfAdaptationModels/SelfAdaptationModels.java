package node.selfAdaptationSystem.selfAdaptationModels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;

import node.organizationMiddleware.contextManager.contextDirectories.NeighbourInfo;
import node.organizationMiddleware.contextManager.contextDirectories.Organization;
import node.organizationMiddleware.contextManager.contextDirectories.OrganizationBoundary;
import node.organizationMiddleware.contextManager.contextDirectories.RolePosition;
import node.organizationMiddleware.contextManager.contextDirectories.TrafficJamInfo;
import node.selfAdaptationSystem.selfAdaptationController.SelfAdaptationController.LocalTrafficSystemRoleType;

import simulator.RoadNetwork;
import utilities.NodeID;

/**
 * 
 */
public class SelfAdaptationModels {
	
	/**************************	 
	 * 
	 * General Information
	 *
	 **************************/
	
	public int getCurrentExecutionCycle(){
		return this.executionCycle;
	}
	
	public void incrementExecutionCycle(){
		this.executionCycle++;
	}
	
	public boolean executionCycleTimestampIsYoungerThan(int executionCycleTimeStamp, int deltaCycles){
		return (executionCycleTimeStamp + deltaCycles > this.getCurrentExecutionCycle());
	}
	
	private int executionCycle = 0;
	
	
	/**************************	 
	 * 
	 * Information Maintained
	 * by SelfAdaptationScenarios
	 *
	 **************************/	
	
	/**
	 * 
	 * @pre	The given organization snapshot provides up-to-date information on the organization that
	 * 		is active on the given neighbor node
	 * @throws	IllegalArgumentException
	 * 			The given node is not a direct and alive neighbor of this local node
	 */
	public synchronized void addNeighborNodeOrganizationInformation(NodeID neighborNode, 
									OrganizationSnapshot neighborOrganizationOfNeighorNode){
		if(!this.aliveNeighborNodes.contains(neighborNode))
			throw new IllegalArgumentException("The given node is not a neighbor");
		
		//Add information, replacing the previously held organization snapshot (if any)
		this.neighborNodeOrganizationInformation.put(neighborNode, neighborOrganizationOfNeighorNode);
	}
	
	public synchronized void removeNeighborNodeOrganizationInformation(NodeID neighborNode){
		this.neighborNodeOrganizationInformation.remove(neighborNode);
	}
	
	public void resetNeighborNodeOrganizationInformationList(){
		this.neighborNodeOrganizationInformation.clear();
	}
	
	/**
	 * Returns information on the organization that is active on the given neighbor node. 
	 * If the given node is not a neighbor of this local node or if these two nodes both belong to the
	 * same organization, the null-reference is returned.
	 * 
	 * Note: only assume up-to-date information on the organization ID and the master of these neighbor
	 * 		 organizations, NOT on the agents (master or slaves)
	 */
	public OrganizationSnapshot getNeighborNodeOrganizationInformation(NodeID neighborNode){
		return this.neighborNodeOrganizationInformation.get(neighborNode);
	}	
	
	/**
	 * Returns a list for organizations for which this local node is a direct neighbor (for each returned
	 * organization, a certain member node is also a direct alive neighbor of this local node)
	 */
	public Collection<OrganizationSnapshot> getAllNeighborNodeOrganizationInformation(){
		return this.neighborNodeOrganizationInformation.values();
	}
	
	/**
	 * Returns a list of organization boundaries, signifying the fact that this node is a direct alive
	 * neighbor to nodes that are part of a different organization than the one currently active on this local node
	 */
	public ArrayList<OrganizationBoundary> calculateLocalOrganizationBoundaries(){
		ArrayList<OrganizationBoundary> result = new ArrayList<OrganizationBoundary>();
		
		for(NodeID neighborNode : this.neighborNodeOrganizationInformation.keySet()){
			//Create organization boundary
			OrganizationBoundary boundary = new OrganizationBoundary();
			boundary.setInternalAgent(this.hostNode);
			boundary.setExternalAgent(neighborNode);
			
			result.add(boundary);
		}
		
		return result;
	}
	
	private HashMap<NodeID, OrganizationSnapshot> neighborNodeOrganizationInformation =
		new HashMap<NodeID, OrganizationSnapshot>();
	
	
	public void addPreviouslyFailedNeighborNode(NodeID newFailedNode){
		if(!this.previouslyFailedNeighborNodes.contains(newFailedNode))		
			this.previouslyFailedNeighborNodes.add(newFailedNode);
	}
	
	/**
	 * Returns a list of all neighbor nodes this local node has encountered and that have failed
	 */
	public ArrayList<NodeID> getPreviouslyFailedNeighborNodes(){
		return this.previouslyFailedNeighborNodes;
	}
	
	public void setPreviouslyFailedNeighborNodeList(ArrayList<NodeID> failedNeighbors){
		this.previouslyFailedNeighborNodes = failedNeighbors;
	}
	
	private ArrayList<NodeID> previouslyFailedNeighborNodes = new ArrayList<NodeID>();
	

	/**
	 * TrafficInfo for a local camera which has not been integrated into a working traffic monitoring system
	 * 
	 * Note: this is NOT the same object as used by the local camera if it is an active part of said traffic system
	 */
	public TrafficJamInfo getTempTrafficInfo() {
		return tempTrafficInfo;
	}

	public void setTempTrafficInfo(TrafficJamInfo tempTrafficInfo) {
		this.tempTrafficInfo = tempTrafficInfo;
	}
	
	/**
	 * NeighborInfo for a local camera which has not been integrated into a working traffic monitoring system
	 * 
	 * Note: this is NOT the same object as used by the local camera if it is an active part of said traffic system
	 */
	public NeighbourInfo getTempNeighborInfo() {
		return tempNeighborInfo;
	}

	public void setTempNeighborInfo(NeighbourInfo tempNeighborInfo) {
		this.tempNeighborInfo = tempNeighborInfo;
	}
	
	private TrafficJamInfo tempTrafficInfo;
	private NeighbourInfo tempNeighborInfo;
	
	
	/**************************	 
	 * 
	 * Information Maintained
	 * by Self-Monitoring Scenarios
	 * obtained from base-level
	 *
	 **************************/		
	
	/**
	 * A list of organizations that neighbor on the local organization
	 * 
	 * Note: only of real use on a master node (since slave nodes have no up-to-date information on organizations
	 * neighboring on organization the slave is a member of.
	 * Note: To be kept up-to-date based on information from the base-level local traffic monitoring system
	 */
	public ArrayList<OrganizationSnapshot> getNeighborOrganizations() {
		return neighborOrganizations;
	}

	public synchronized void setNeighborOrganizations(
			ArrayList<OrganizationSnapshot> neighborOrganizations) {
		this.neighborOrganizations = neighborOrganizations;
	}
	
	/**
	 * Returns the predefined and static list of physical neighbor nodes of the given camera node
	 */
	public ArrayList<NodeID> getPhysicalNeighborsFor(NodeID node){
		ArrayList<NodeID> result = new ArrayList<NodeID>();
		
		Hashtable<String,String[]> neighborTable = this.roadNetwork.getNeighbours();
		String[] physicalNeighbors = neighborTable.get(node.toString());
		
		for(int i=0; i<physicalNeighbors.length; i++)
			result.add(new NodeID(Integer.parseInt(physicalNeighbors[i])));
		
		return result;
	}
	
	public RoadNetwork getRoadNetwork() {
		return roadNetwork;
	}	

	public void setRoadNetwork(RoadNetwork roadNetwork) {
		this.roadNetwork = roadNetwork;
	}
	
	/**
	 * Returns the list of currently alive neighbor nodes of this local node
	 */
	public ArrayList<NodeID> getAliveNeighborNodes() {
		return aliveNeighborNodes;
	}

	public void setAliveNeighborNodes(ArrayList<NodeID> aliveNeighborNodes) {
		this.aliveNeighborNodes = aliveNeighborNodes;
	}
	
	public OrganizationSnapshot getLocalOrganization() {
		return localOrganization;
	}

	public void setLocalOrganization(OrganizationSnapshot localOrganization) {
		this.localOrganization = localOrganization;
	}
	
	public float getTrafficJamTreshold() {
		return trafficJamTreshold;
	}

	public void setTrafficJamTreshold(float trafficJamTreshold) {
		this.trafficJamTreshold = trafficJamTreshold;
	}

	public NodeID getHostNode() {
		return hostNode;
	}

	public void setHostNode(NodeID hostNode) {
		this.hostNode = hostNode;
	}
	
	public RolePosition getLocalRoleposition() {
		return localRoleposition;
	}

	public void setLocalRoleposition(RolePosition localRoleposition) {
		this.localRoleposition = localRoleposition;
	}
	
	public void setFullLocalOrganizationInformation(Organization fullLocalOrganizationInformation) {
		this.fullLocalOrganizationInformation = fullLocalOrganizationInformation;
	}

	public Organization getFullLocalOrganizationInformation() {
		return fullLocalOrganizationInformation;
	}
	
	public LocalTrafficSystemRoleType getCurrentTrafficRole(){
		return this.currentTrafficRole;
	}
	
	public void setCurrentTrafficRole(LocalTrafficSystemRoleType currentRole){
		this.currentTrafficRole = currentRole;
	}

	private NodeID hostNode;	
	private RoadNetwork roadNetwork;	
	private OrganizationSnapshot localOrganization;
	private ArrayList<OrganizationSnapshot> neighborOrganizations = new ArrayList<OrganizationSnapshot>();
	private ArrayList<NodeID> aliveNeighborNodes = new ArrayList<NodeID>();
	private float trafficJamTreshold;
	private RolePosition localRoleposition;
	private Organization fullLocalOrganizationInformation;
	private LocalTrafficSystemRoleType currentTrafficRole;
	
}
