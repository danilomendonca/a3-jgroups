package node.agentMiddleware.communication.middleware;

public class LockingException extends Exception{
	private int organizationID;
	
	public LockingException(int organizastionID){
		this.organizationID = organizationID;
	}
	
	public int getOrganizationID(){
		return this.organizationID;
	}
}
