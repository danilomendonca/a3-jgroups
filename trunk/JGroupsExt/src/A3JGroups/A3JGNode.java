package A3JGroups;

import java.util.HashMap;
import java.util.Map;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.View;
import org.jgroups.blocks.ReplicatedHashMap;

public abstract class A3JGNode{
	
	private int resourceThreshold;
	private String ID;
	private long timeout = 10000;
	
	private Map<String,JGSupervisorRole> supervisorRoles = new HashMap<String, JGSupervisorRole>(); 
	private Map<String,JGFollowerRole> followerRoles = new HashMap<String, JGFollowerRole>();
	private Map<String,JChannel> channels = new HashMap<String, JChannel>();
	protected Map<String,GenericRole> waitings = new HashMap<String, GenericRole>();
	
	
	public A3JGNode(String ID){
		super();
		this.ID = ID;
	}
	
	public void setResourceThreshold(int resourceThreshold) {
		this.resourceThreshold = resourceThreshold;
	}

	public int getResourceThreshold() {
		return resourceThreshold;
	}
	
	public JChannel getChannels(String groupName) {
		return channels.get(groupName);
	}

	public void addSupervisorRole(String groupName, JGSupervisorRole role) {
		this.supervisorRoles.put(groupName, role);
		role.setNode(this);
	}
	
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public JGSupervisorRole getSupervisorRole(String groupName) {
		return supervisorRoles.get(groupName);
	}
	
	public JGFollowerRole getFollowerRole(String groupName) {
		return followerRoles.get(groupName);
	}
	
	public void addFollowerRole(String groupName, JGFollowerRole role) {
		this.followerRoles.put(groupName, role);
		role.setNode(this);
	}

	public String getID() {
		return ID;
	}

	
	public boolean joinGroup(String groupName) throws Exception {
		
		if (channels.get(groupName)!=null)
			return false;
		final JChannel chan = new JChannel();
		channels.put(groupName, chan);
		
		
		ReplicatedHashMap<String, Object> map = new ReplicatedHashMap<String, Object>(chan){
	    	
	    	public void receive(Message m){
				if (chan.getReceiver() != null)
					chan.getReceiver().receive(m);
	    	}
	    	
	    	public void viewAccepted(View v){
	    		if (chan.getReceiver() != null)
	    			chan.getReceiver().viewAccepted(v);
	    		
	    		this.viewAcceptedOriginal(v);
	    		
	    	}
	    };

	    A3JGRHMNotification notifier = new A3JGRHMNotification();
	    notifier.setNodeID(ID);
	    map.addNotifier(notifier);
	    chan.connect(groupName);
	    
	    map.start(timeout);
	    
	    if(map.get("A3Supervisor")==null){
			if(this.getSupervisorRole(groupName)!=null){
				if(map.putIfAbsent("A3Supervisor", chan.getAddress())==null){
					this.getSupervisorRole(groupName).setActive(true);
					this.getSupervisorRole(groupName).setChan(chan);
					this.getSupervisorRole(groupName).setMap(map);
					this.getSupervisorRole(groupName).setNotifier(notifier);
					this.getSupervisorRole(groupName).index=-1;
					chan.setReceiver(this.getSupervisorRole(groupName));
					new Thread(this.getSupervisorRole(groupName)).start();
					return true;
				}else{
					if(this.getFollowerRole(groupName)!=null){
						this.getFollowerRole(groupName).setActive(true);
						this.getFollowerRole(groupName).setChan(chan);
						this.getFollowerRole(groupName).setMap(map);
						this.getFollowerRole(groupName).setNotifier(notifier);
						chan.setReceiver(this.getFollowerRole(groupName));
						new Thread(this.getFollowerRole(groupName)).start();
						return true;
					}
				}
			}
		}else if(chan.getView().getMembers().contains(map.get("A3Supervisor"))){
			if(this.getFollowerRole(groupName)!=null){
				this.getFollowerRole(groupName).setActive(true);
				this.getFollowerRole(groupName).setChan(chan);
				this.getFollowerRole(groupName).setMap(map);
				this.getFollowerRole(groupName).setNotifier(notifier);
				new Thread(this.getFollowerRole(groupName)).start();
				chan.setReceiver(this.getFollowerRole(groupName));
				return true;
			}
		}else{
			GenericRole generic = new GenericRole(this, groupName, chan, map, notifier);
			chan.setReceiver(generic);
			generic.waitElection();
			waitings.put(groupName, generic);
			return true;
		}
		close(groupName);
		return false;
	}
	
	protected void close(String groupName) {
		JChannel chan = channels.get(groupName);
		chan.disconnect();
		chan.close();
		channels.remove(groupName);
	}
	
	public void terminate(String groupName){
		if(this.getSupervisorRole(groupName)!=null && this.getSupervisorRole(groupName).isActive()){
			this.getSupervisorRole(groupName).setActive(false);
		}else if(this.getFollowerRole(groupName)!=null && this.getFollowerRole(groupName).isActive())
			this.getFollowerRole(groupName).setActive(false);
		close(groupName);
	}
	
}
