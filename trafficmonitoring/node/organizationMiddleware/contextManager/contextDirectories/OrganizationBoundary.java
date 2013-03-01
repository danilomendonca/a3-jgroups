package node.organizationMiddleware.contextManager.contextDirectories;


import utilities.NodeID;
import utilities.Event;
import utilities.Publisher;

public class OrganizationBoundary extends Publisher {

	//node/agent at the barrier of the organization
	private NodeID internalAgent;
	//node/agent neighboring to the organizastion
	private NodeID externalAgent;
	
	public void setInternalAgent(NodeID internalAgent) {
		this.internalAgent = internalAgent;
		publish(new Event("setInternalAgent"));
	}
	
	public void setExternalAgent(NodeID externalAgent) {
		this.externalAgent = externalAgent;
		publish(new Event("setExternalAgent"));
	}
	
	public NodeID getInternalAgent() {
		return internalAgent;
	}
	
	public NodeID getExternalAgent() {
		return externalAgent;
	}

	/**
	 * deep copy
	 */
	public OrganizationBoundary copy() {
		OrganizationBoundary result = new OrganizationBoundary();
		result.setExternalAgent(getExternalAgent().copy());
		result.setInternalAgent(getInternalAgent().copy());
		return result;
	}
	
	public boolean equals(Object o){
		try{
			OrganizationBoundary b = (OrganizationBoundary) o;
			return (b.getExternalAgent().equals(getExternalAgent())) && 
				(b.getInternalAgent().equals(getInternalAgent()));
		}catch (ClassCastException e){
			return false;
		}
	}
	
	//@Pieter
	public int hashCode(){
		return (this.getInternalAgent().hashCode() ^ this.getExternalAgent().hashCode());
	}
	
	
	
}
