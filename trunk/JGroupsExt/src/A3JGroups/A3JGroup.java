package A3JGroups;

import java.util.ArrayList;
import java.util.List;

import org.jgroups.Address;



public class A3JGroup {
	
	private String groupName;
	private A3JGNode supervisor;
	private List<A3JGNode> followers = new ArrayList<A3JGNode>();
	private Address supAddr;
	private List<Address> folAddr = new ArrayList<Address>();
	
	
	
	public A3JGroup(String groupName, A3JGNode supervisor) {
		super();
		this.groupName = groupName;
		this.supervisor = supervisor;
	}

	public String getGroupName() {
		return groupName;
	}
	
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	public A3JGNode getSupervisor() {
		return supervisor;
	}
	
	public void setSupervisor(A3JGNode supervisor) {
		this.supervisor = supervisor;
	}
	
	public List<A3JGNode> getFollowers() {
		return followers;
	}
	
	public void addFollower(A3JGNode node) {
		followers.add(node);
	}
	
	public void removeFollower(A3JGNode node) {
		followers.remove(node);
	}
	
	public Address getSupAddr() {
		return supAddr;
	}

	public void setSupAddr(Address supAddr) {
		this.supAddr = supAddr;
	}

	public List<Address> getFolAddr() {
		return folAddr;
	}

	public void addFolAddr(Address address) {
		folAddr.add(address);
	}
	
	public void removeFolAddr(Address address) {
		folAddr.remove(address);
	}

	
}
