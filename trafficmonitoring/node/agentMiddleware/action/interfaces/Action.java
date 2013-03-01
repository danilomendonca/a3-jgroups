package node.agentMiddleware.action.interfaces;

import node.agentMiddleware.communication.middleware.LockingException;

public interface Action {

	public void lock(int organizationID) throws LockingException;
	public void lock(int organizationID1, int organizationID2) throws LockingException;
	public boolean isLocked(int organizationID);
	
}
