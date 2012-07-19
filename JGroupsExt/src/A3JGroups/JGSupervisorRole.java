package A3JGroups;


import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.blocks.ReplicatedHashMap;


public abstract class JGSupervisorRole extends ReceiverAdapter implements Runnable{

	protected boolean active;
	private int resourceCost;
	protected int index;
	private JChannel chan;
	protected A3JGNode node;
	protected ReplicatedHashMap<String, Object> map;
	protected MessageDelete deleter =  new MessageDelete();
	private A3JGRHMNotification notifier;
	

	public JGSupervisorRole(int resourceCost) {
		super();
		this.resourceCost = resourceCost;
	}
	
	public void setNotifier(A3JGRHMNotification notifier) {
		this.notifier = notifier;
	}

	public A3JGRHMNotification getNotifier() {
		return notifier;
	}

	public int getResourceCost() {
		return resourceCost;
	}

	public void setResourceCost(int resourceCost) {
		this.resourceCost = resourceCost;
	}
 
	public A3JGNode getNode() {
		return node;
	}

	public void setNode(A3JGNode node) {
		this.node = node;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}

	public JChannel getChan() {
		return chan;
	}

	public void setChan(JChannel chan) {
		this.chan = chan;
	}
	
	public void setMap(ReplicatedHashMap<String, Object> map) {
		this.map = map;
	}

	public Object getSupBackupState() {
		return map.get("A3SupBackupState");
	}

	public void putSupBackupState(Object state) {
		
		map.put("A3SupBackupState", state);
	}
	
	public Object getAppSharedState(String stateKey){
		return map.get("A3SharedState"+stateKey);
	}
	
	public void putAppSharedState(String stateKey, Object appState){
		
		map.put("A3SharedState"+stateKey, appState);
	}
	
	public void setMessageDeleterWaitTime(int waitTime){
		deleter.setWaitTime(waitTime);
	}
	
	public abstract void run();
	
	public void receive(Message msg) {
		A3JGMessage mex = (A3JGMessage) msg.getObject();
		if(mex.getValueID().equals("A3SupervisorChallenge")){
			supChallenge(((Integer)mex.getContent()), msg.getSrc());
		}else if(mex.getValueID().equals("A3SupervisorChange")){
			submission();
		}else if (mex.getType()) {
			updateFromFollower(mex);
		} else
			messageFromFollower(mex);
	}
	
	public void viewAccepted(View view) {
		
    }
	
	public boolean sendMessageToFollower(A3JGMessage mex, List<Address> dest){
		mex.setType(false);
		Message msg = new Message();
		msg.setObject(mex);
		if(dest!=null){
			try {
				for (Address ad : dest) {
					msg.setDest(ad);
					this.chan.send(msg);
				}
			}
			 catch (Exception e) {
				return false;
			}
		}else{
		try {
			for (Address ad : this.chan.getView().getMembers()) {
				if (!ad.equals(this.chan.getAddress())) {
					msg.setDest(ad);
					this.chan.send(msg);
				} else {
					;
				}
			}
		}
		 catch (Exception e) {
			return false;
		}
		}
		return true;
	}
	
	
	@SuppressWarnings("unchecked")
	public int sendMessageOverTime(A3JGMessage mex, List<Address> dest, int days, int hours, int minutes){
		if(dest!=null)
			mex.setDest(dest);
		mex.setType(false);
		Message msg = new Message();
		msg.setObject(mex);
		index++;
		Calendar c = Calendar.getInstance();
		if (days == 0 && hours == 0 && minutes == 0) {
			c.set(3000, 12, 31);
		} else {
			c.add(Calendar.DATE, days);
			c.add(Calendar.HOUR, hours);
			c.add(Calendar.MINUTE, minutes);
		}

		HashMap<Integer, Date> chiavi;
		if (map.get("A3Message") == null)
			chiavi = new HashMap<Integer, Date>();
		else
			chiavi = ((HashMap<Integer, Date>) map.get("A3Message"));
		chiavi.put(index, c.getTime());
		
		map.put("A3Message", chiavi);
		map.put("A3MessageInMemory_" + index, mex);
		
		deleter.setChiavi(chiavi);
		if (!deleter.isActive()) {
			deleter.setMap(map);
			deleter.setActive(true);
			new Thread(deleter).start();
		}

		
		return index;
	}

	public void removeMessage(int index){
		deleter.toDelete(index);
	}
	
	public void merge(String groupName) throws Exception{
		A3JGMessage mex = new A3JGMessage("A3MergeGroup"+groupName);
		sendMessageToFollower(mex, null);
		node.joinGroup(groupName);
		node.terminate(this.chan.getClusterName());
	}
	
	
	public void join(String groupName) throws Exception{
		A3JGMessage mex = new A3JGMessage("A3JoinGroup"+groupName);
		sendMessageToFollower(mex, null);
		node.joinGroup(groupName);
	}
	
	//doesn't work
	public void split(String newGroupName){
		A3JGMessage mex = new A3JGMessage("A3FitnessFunction");
		sendMessageToFollower(mex, null);
	}
	
	private void supChallenge(int fit, Address ad){
		Message msg = new Message(ad);
		A3JGMessage mex;
		if( fit > fitnessFunc() ){
			mex = new A3JGMessage("A3NewSupervisor");
			msg.setObject(mex);
			try {
				chan.send(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			String groupName = chan.getClusterName();
			String folName = node.getGroupInfo(groupName).getFollower().get(0);
			
			if(node.getFollowerRole(folName)!=null){
				this.setActive(false);
				node.getFollowerRole(folName).setActive(true);
				node.getFollowerRole(folName).setChan(chan);
				node.getFollowerRole(folName).setMap(map);
				node.getFollowerRole(folName).setNotifier(notifier);
				new Thread(node.getFollowerRole(folName)).start();
				chan.setReceiver(node.getFollowerRole(folName));
				node.putActiveRole(groupName, folName);
			}else
				node.terminate(groupName);
		}else{
			mex = new A3JGMessage("A3StayFollower");
			msg.setObject(mex);
			try {
				chan.send(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
	}
	
	private void submission(){
		String groupName = chan.getClusterName();
		String folName = node.getGroupInfo(groupName).getFollower().get(0);
		
		if(node.getFollowerRole(folName)!=null){
			this.setActive(false);
			node.getFollowerRole(folName).setActive(true);
			node.getFollowerRole(folName).setChan(chan);
			node.getFollowerRole(folName).setMap(map);
			node.getFollowerRole(folName).setNotifier(notifier);
			new Thread(node.getFollowerRole(folName)).start();
			chan.setReceiver(node.getFollowerRole(folName));
			node.putActiveRole(groupName, folName);
		}else
			node.terminate(groupName);
	}
	
	@SuppressWarnings("unchecked")
	public boolean changeRoleInGroup(int config){
		
		String role = node.getGroupInfo(chan.getClusterName()).getSupervisor().get(config);
		if (node.getSupervisorRole(role) != null) {
			node.putActiveRole(chan.getClusterName(), role);
			this.active = false;

			node.getSupervisorRole(role).setActive(true);
			node.getSupervisorRole(role).setChan(chan);
			node.getSupervisorRole(role).setMap(map);
			node.getSupervisorRole(role).setNotifier(notifier);
			chan.setReceiver(node.getSupervisorRole(role));
			node.getSupervisorRole(role).index = index;
			if (map.get("A3Message") != null) {
				node.getSupervisorRole(role).deleter.setActive(true);
				node.getSupervisorRole(role).deleter.setMap(map);
				node.getSupervisorRole(role).deleter.setChiavi((HashMap<Integer, Date>) map.get("A3Message"));
				new Thread(node.getSupervisorRole(role).deleter).start();
			}
			new Thread(node.getSupervisorRole(role)).start();
			return true;
		}
		return false;
	}

	public abstract void messageFromFollower(A3JGMessage msg);
	public abstract void updateFromFollower(A3JGMessage msg);
	public abstract int fitnessFunc();
	
}
