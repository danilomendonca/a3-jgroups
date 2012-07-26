package A3JGroups;

import java.util.HashMap;
import java.util.Map;

public class A3JGroup {
	
	private Map<Integer, String> supervisor = new HashMap<Integer, String>();
	private Map<Integer, String> follower = new HashMap<Integer, String>();
	private String groupConnection;
	private String groupDescriptor;
	
	public A3JGroup(String supervisorDef, String followerDef) {
		super();
		supervisor.put(0, supervisorDef);
		follower.put(0, followerDef);
	}

	public Map<Integer, String> getSupervisor() {
		return supervisor;
	}
	
	public Map<Integer, String> getFollower() {
		return follower;
	}
	
	public void addSupervisor(int index, String className){
		supervisor.put(index, className);
	}
	
	public void addFollower(int index, String className){
		follower.put(index, className);
	}

	public void addGroupConnection(String pathConfig){
		groupConnection = pathConfig;
	}
	
	public String getGroupConnection(){
		return groupConnection;
	}
	
	public String getGroupDescriptor() {
		return groupDescriptor;
	}

	public void setGroupDescriptor(String groupDescriptor) {
		this.groupDescriptor = groupDescriptor;
	}
		
}
