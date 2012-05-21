package A3JGroups;

import java.util.List;
import java.util.Map;

import org.jgroups.Address;
import org.jgroups.View;
import org.jgroups.blocks.ReplicatedHashMap;

public class A3JGRHMNotification implements ReplicatedHashMap.Notification<String, Object>{
	private String nodeID;
	@Override
	public void contentsCleared() {
		//System.out.println(nodeID + "contents cleared");
		
	}

	@Override
	public void contentsSet(Map<String, Object> arg0) {
		//System.out.println(nodeID + "contentsSet: " +arg0);
		
	}

	@Override
	public void entryRemoved(String arg0) {
		//System.out.println(nodeID + "entryRemoved: "+arg0);
		
	}

	@Override
	public void entrySet(String arg0, Object arg1) {
		//System.out.println(nodeID + "entrySet: "+arg0 +"  -- "+arg1);
		
	}

	@Override
	public void viewChange(View arg0, List<Address> arg1, List<Address> arg2) {
		
	}

	public String getNodeID() {
		return nodeID;
	}

	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}

}
