package A3JGroups;

import java.util.HashMap;
import java.util.Map;


/**
 * A3JGroup is used to define the information relating to a group of A3. For each 
 * group, you must create an instance of A3JGroup, with the respective information.
 * 
 * @author bett.marco88@gmail.com
 *
 */
public class A3JGroup {
	
	private Map<Integer, String> supervisor = new HashMap<Integer, String>();
	private Map<Integer, String> follower = new HashMap<Integer, String>();
	private String groupConnection;
	private String groupDescriptor;
	
	
	/**
	 * THe constructor has as input the class name of the JGSupervisorRole and 
	 * JGFollowerRole used as default roles of the group.
	 * 
	 * @param supervisorDef
	 * 			JGSupervisorRole default class name.
	 * @param followerDef
	 * 			JGFollowerRole default class name.
	 */
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
