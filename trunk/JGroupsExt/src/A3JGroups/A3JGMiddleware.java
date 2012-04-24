package A3JGroups;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class A3JGMiddleware {

	Map<String, A3JGroup> existingGroups = new HashMap<String, A3JGroup>();

	public Set<String> getGroups() {
		return existingGroups.keySet();
	}
	
	private void createGroup(String groupName, A3JGNode node) {	
		existingGroups.put(groupName, new A3JGroup(groupName, node));
	}
	
	public void removeGroup(String groupName){
		A3JGroup group = existingGroups.get(groupName);
		existingGroups.remove(group);
		group.getSupervisor().getSupervisorRole(groupName).deactivate();
		for(A3JGNode node : group.getFollowers()){
			node.getFollowerRole(groupName).deactivate();
		}
		System.out.println("\n\nThe gruop "+ groupName +" has been eliminated\n\n");
	}
	
	private void joinGroup(String groupName, A3JGNode node) {
		A3JGroup group = existingGroups.get(groupName);
		if (group!=null) {
			group.addFollower(node);
		}
	}
	
	public void addNodeToGroup(String groupName, A3JGNode node) throws Exception{
		A3JGroup group = existingGroups.get(groupName);
		if (group==null) {
			//create group only if node is Supervisor
			if(node.getSupervisorRole(groupName)!=null){
				createGroup(groupName, node);
				group = existingGroups.get(groupName);
				node.getSupervisorRole(groupName).activate(group);
				new Thread(node.getSupervisorRole(groupName)).start();
				group.setSupAddr(node.getSupervisorRole(groupName).getChan().getAddress());
				System.out.println("\n\n["+node.getID()+"] is the new supervisor of "+groupName);
				//
			}
		}else
			//join group only if node is Follower
			if(node.getFollowerRole(groupName)!=null){
				joinGroup(groupName, node);
				node.getFollowerRole(groupName).activate(group);
				new Thread(node.getFollowerRole(groupName)).start();
				group.addFolAddr(node.getFollowerRole(groupName).getChan().getAddress());
				A3JGMessage msg = new A3JGMessage("join");
				node.getFollowerRole(groupName).sendUpdateToSupervisor(msg);
			}
	}
	
	
	public void removeNodeFromGroup(String groupName, A3JGNode node) throws Exception{
		A3JGroup group = existingGroups.get(groupName);
		if(group!=null){
			//remove a Supervisor node
			if(group.getSupervisor()==node){
				List<A3JGNode> follower = group.getFollowers();
				boolean flag=true;
				for(int i=0; i<follower.size() && flag; i++){
					A3JGNode newSup = follower.get(i);
					if(newSup.getSupervisorRole(groupName)!=null){
						group.removeFolAddr(newSup.getFollowerRole(groupName).getChan().getAddress());
						group.removeFollower(newSup);
						node.getSupervisorRole(groupName).deactivate();
						newSup.getFollowerRole(groupName).deactivate();
						newSup.getSupervisorRole(groupName).activate(group);
						group.setSupervisor(newSup);
						new Thread(newSup.getSupervisorRole(groupName)).start();
						group.setSupAddr(group.getSupervisor().getSupervisorRole(groupName).getChan().getAddress());
						System.out.println("\n\n["+newSup.getID()+"] is the new supervisor of "+groupName);
						flag=false;
						
						A3JGMessage msg = new A3JGMessage("join");
						for(A3JGNode fol: group.getFollowers()){
							fol.getFollowerRole(groupName).sendUpdateToSupervisor(msg);
						}
					}
				}
				if(flag){
					this.removeGroup(groupName);
				}
			}else{
				//remove a Follower node
				group.removeFollower(node);
				group.removeFolAddr(node.getFollowerRole(groupName).getChan().getAddress());
				node.getFollowerRole(groupName).deactivate();
				A3JGMessage msg = new A3JGMessage("leave");
				node.getFollowerRole(groupName).sendUpdateToSupervisor(msg);
			}
			
		}
	}
	
}
