package A3JGroups;

import java.util.HashMap;
import java.util.Map;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.View;
import org.jgroups.blocks.ReplicatedHashMap;

public abstract class A3JGNode{
	
	private int resourceThreshold;
	private String ID;
	private long timeout = 10000;
	
	private Map<String, JGSupervisorRole> supervisorRoles = new HashMap<String, JGSupervisorRole>(); 
	private Map<String, JGFollowerRole> followerRoles = new HashMap<String, JGFollowerRole>();
	private Map<String, A3JGroup> groupInfo = new HashMap<String, A3JGroup>();
	private Map<String, JChannel> channels = new HashMap<String, JChannel>();
	private Map<String, String> activeRole = new HashMap<String, String>();
	protected Map<String, GenericRole> waitings = new HashMap<String, GenericRole>();
	private Object inNodeSharedMemory;
	
	
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
	
	public String getActiveRole(String groupName) {
		return activeRole.get(groupName);
	}
	
	public void putActiveRole(String groupName, String className){
		activeRole.put(groupName, className);
	}
	
	public void addGroupInfo(String groupName, A3JGroup group) {
		this.groupInfo.put(groupName, group);
	}
	
	public A3JGroup getGroupInfo(String groupName){
		return groupInfo.get(groupName);
	}

	public void addSupervisorRole(JGSupervisorRole role) {
		this.supervisorRoles.put(role.getClass().getName(), role);
		role.setNode(this);
	}
	
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public JGSupervisorRole getSupervisorRole(String className) {
		return supervisorRoles.get(className);
	}
	
	public JGFollowerRole getFollowerRole(String className) {
		return followerRoles.get(className);
	}
	
	public void addFollowerRole(JGFollowerRole role) {
		this.followerRoles.put(role.getClass().getName(), role);
		role.setNode(this);
	}

	public String getID() {
		return ID;
	}
	
	public Object getInNodeSharedMemory() {
		return inNodeSharedMemory;
	}

	public void setInNodeSharedMemory(Object inNodeSharedMemory) {
		this.inNodeSharedMemory = inNodeSharedMemory;
	}

	public boolean joinGroup(String groupName) throws Exception {
		
		if(channels.get(groupName)!=null)
			return false;
		if(groupInfo.get(groupName)==null)
			return false;
		
		final JChannel chan;
		if(groupInfo.get(groupName).getGroupConnection()!=null)
			chan = new JChannel(groupInfo.get(groupName).getGroupConnection());
		else
			chan = new JChannel();
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
	    
	    //vedere che ruolo attivare e salvare stringa in activeRole
		if (groupInfo.containsKey(groupName)) {
			String supName = getGroupInfo(groupName).getSupervisor().get(0);
			String folName = getGroupInfo(groupName).getFollower().get(0);
			if (map.get("A3Supervisor") == null) {
				if (this.getSupervisorRole(supName) != null) {
					if (map.putIfAbsent("A3Supervisor", chan.getAddress()) == null) {
						this.getSupervisorRole(supName).setActive(true);
						this.getSupervisorRole(supName).setChan(chan);
						this.getSupervisorRole(supName).setMap(map);
						this.getSupervisorRole(supName).setNotifier(notifier);
						this.getSupervisorRole(supName).index = -1;
						chan.setReceiver(this.getSupervisorRole(supName));
						new Thread(this.getSupervisorRole(supName)).start();
						putActiveRole(groupName, supName);
						return true;
					} else {
						if (this.getFollowerRole(folName) != null) {
							this.getFollowerRole(folName).setActive(true);
							this.getFollowerRole(folName).setChan(chan);
							this.getFollowerRole(folName).setMap(map);
							this.getFollowerRole(folName).setNotifier(notifier);
							chan.setReceiver(this.getFollowerRole(folName));
							new Thread(this.getFollowerRole(folName)).start();
							putActiveRole(groupName, folName);
							return true;
						}
					}
				}
			} else if (chan.getView().getMembers().contains(map.get("A3Supervisor"))) {
				if (this.getFollowerRole(folName) != null) {
					this.getFollowerRole(folName).setActive(true);
					this.getFollowerRole(folName).setChan(chan);
					this.getFollowerRole(folName).setMap(map);
					this.getFollowerRole(folName).setNotifier(notifier);
					new Thread(this.getFollowerRole(folName)).start();
					chan.setReceiver(this.getFollowerRole(folName));
					putActiveRole(groupName, folName);
					return true;
				}
			} else {
				GenericRole generic = new GenericRole(this, chan, map, notifier);
				chan.setReceiver(generic);
				generic.waitElection();
				waitings.put(groupName, generic);
				putActiveRole(groupName, "GenericRole");
				return true;
			}
		}
		close(groupName);
		return false;
	}
	
	protected void close(String groupName) {
		JChannel chan = channels.get(groupName);
		chan.disconnect();
		chan.close();
		channels.remove(groupName);
		activeRole.remove(groupName);
	}
	
	public void terminate(String groupName){
		String role = getActiveRole(groupName);
		if(this.getSupervisorRole(role)!=null && this.getSupervisorRole(role).isActive()){
			this.getSupervisorRole(role).setActive(false);
		}else if(this.getFollowerRole(role)!=null && this.getFollowerRole(role).isActive())
			this.getFollowerRole(role).setActive(false);
		close(groupName);
	}
	
	public boolean joinAsSupervisor(String groupName, boolean challenge) throws Exception{
		
		if (channels.get(groupName)!=null)
			return false;
		
		final JChannel chan;
		if(groupInfo.get(groupName).getGroupConnection()!=null)
			chan = new JChannel(groupInfo.get(groupName).getGroupConnection());
		else
			chan = new JChannel();
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
	    if (groupInfo.containsKey(groupName)) {
			String supName = getGroupInfo(groupName).getSupervisor().get(0);
			
			if (this.getSupervisorRole(supName) != null) {
				if (map.get("A3Supervisor") == null || !challenge) {
					if (map.putIfAbsent("A3Supervisor", chan.getAddress()) == null) {
						this.getSupervisorRole(supName).setActive(true);
						this.getSupervisorRole(supName).setChan(chan);
						this.getSupervisorRole(supName).setMap(map);
						this.getSupervisorRole(supName).setNotifier(notifier);
						this.getSupervisorRole(supName).index = -1;
						chan.setReceiver(this.getSupervisorRole(supName));
						new Thread(this.getSupervisorRole(supName)).start();
						putActiveRole(groupName, supName);
						
						A3JGMessage mex = new A3JGMessage("A3SupervisorChange");
						Message msg = new Message();
						msg.setObject(mex);
						msg.setDest((Address) map.get("A3Supervisor"));
						try {
							chan.send(msg);
						} catch (Exception e) {
							e.printStackTrace();
						}
						return true;
					}
				}
				if(challenge){
					GenericRole generic = new GenericRole(this, chan, map, notifier);
					chan.setReceiver(generic);
					generic.supervisorChallenge();
					waitings.put(groupName, generic);
					putActiveRole(groupName, "GenericRole");
					return true;
				}
			}
	    }
	    
	    return false;
	}
	
	public boolean joinSplitGroup(String groupName, JChannel jc, int port, boolean role) throws Exception {
		
		jc.getProtocolStack().findProtocol("UDP").setValue("mcast_port", port);
		final JChannel chan = new JChannel(jc);
		if(role)
			channels.put("A3Split"+groupName, chan);
		else{
			terminate(groupName);
			channels.put(groupName, chan);
		}
		
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
	    
	    if (groupInfo.containsKey(groupName)) {
			String supName = getGroupInfo(groupName).getSupervisor().get(0);
			String folName = getGroupInfo(groupName).getFollower().get(0);
			if (map.get("A3Supervisor") == null && role) {
				if (this.getSupervisorRole(supName) != null) {
					if (map.putIfAbsent("A3Supervisor", chan.getAddress()) == null) {
						this.getSupervisorRole(supName).setActive(true);
						this.getSupervisorRole(supName).setChan(chan);
						this.getSupervisorRole(supName).setMap(map);
						this.getSupervisorRole(supName).setNotifier(notifier);
						this.getSupervisorRole(supName).index = -1;
						this.getSupervisorRole(supName).setSplitsup(true);
						chan.setReceiver(this.getSupervisorRole(supName));
						new Thread(this.getSupervisorRole(supName)).start();
						putActiveRole("A3Split"+groupName, supName);
						return true;
					}
				}
			} else if (!role) {
				if (this.getFollowerRole(folName) != null) {
					this.getFollowerRole(folName).setActive(true);
					this.getFollowerRole(folName).setChan(chan);
					this.getFollowerRole(folName).setMap(map);
					this.getFollowerRole(folName).setNotifier(notifier);
					new Thread(this.getFollowerRole(folName)).start();
					chan.setReceiver(this.getFollowerRole(folName));
					putActiveRole(groupName, folName);
					return true;
				}
			}
		}
	    if(role)
	    	close("A3Split"+groupName);
	    else
	    	close(groupName);
		return false;
	}
	
}
