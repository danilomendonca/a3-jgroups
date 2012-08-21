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


/**
 *	JGSupervisorRole must be extended in order to define a certain type of supervisor. You 
 * must define the behavior of each supervisor implementing the run function. Only one rule
 * at a time can be active in a group. In each group there is only a supervisor that works 
 * with the followers. A group, for exist, must have an active supervisorRole in its members.
 * 
 * @author bett.marco88@gmail.com
 *
 */
public abstract class A3JGSupervisorRole extends ReceiverAdapter implements Runnable{

	protected boolean active;
	private int resourceCost;
	protected int index;
	private JChannel chan;
	protected A3JGNode node;
	protected ReplicatedHashMap<String, Object> map;
	protected MessageDelete deleter =  new MessageDelete();
	private A3JGRHMNotification notifier;
	private boolean splitsup = false;
	

	public A3JGSupervisorRole(int resourceCost) {
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
	
	public boolean isSplitsup() {
		return splitsup;
	}

	public void setSplitsup(boolean splitsup) {
		this.splitsup = splitsup;
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
	
	/**
	 * You have to use this function to send message to followers.
	 * 
	 * @param mex
	 * 			The A3JGMessage sent to followers.
	 * @param dest
	 * 			The list of followers who will receive the message. 			
	 * @return
	 * 			True if the message is sent, false otherwise.
	 */
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
	
	
	/**
	 * You have to use this function to send message, that will be saved over time, to followers.
	 * If days, hours and minutes are equal to 0, the message is valid for infinite.
	 * 
	 * @param mex
	 * 			The A3JGMessage sent to followers.
	 * @param dest
	 * 			The list of followers who will receive the message. 
	 * @param days
	 * 			The number of days of validity.
	 * @param hours
	 * 			The number of hours of validity.
	 * @param minutes	
	 * 			The number of minutes of validity.		
	 * @return
	 * 			True if the message is sent, false otherwise.
	 */
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

	/**
	 * The function is used for remove a message from the ReplicatedHashMap
	 * @param index
	 * 			The Integer key of the message to remove.
	 */
	public void removeMessage(int index){
		deleter.toDelete(index);
	}
	
	/**
	 * Called after a split(), is used for merging the split groups.
	 */
	public void merge(){
		A3JGMessage mex = new A3JGMessage("A3MergeGroup");
		sendMessageToFollower(mex, null);
		String groupName = this.getChan().getClusterName();
		if(splitsup)
			this.getNode().terminate("A3Split"+groupName);
		else{
			this.getNode().terminate(groupName);
			try {
				this.getNode().joinGroup(groupName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
			
	}

	/**
	 * Called for split the group.
	 */
	public void split(){
		new Thread(new SplitManager(1000, map, chan)).start();
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
	
	/**
	 * This function allows the supervisor to change is role while it is active.
	 * 
	 * @param config
	 * 			The Integer key of the configuration that must be activated.
	 * @return
	 * 			True if the change has success, false otherwise.
	 */
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

	/**
	 * This function must be extended in order to define the behavior of the
	 * supervisor when receives a message from the follower.
	 * 
	 * @param msg
	 * 			Is the message sent by the follower.
	 */
	public abstract void messageFromFollower(A3JGMessage msg);
	
	/**
	 * This function must be extended in order to define the behavior of the
	 * supervisor when receives an update from the follower.
	 * 
	 * @param msg
	 * 			Is the update message sent by the follower.
	 */
	public abstract void updateFromFollower(A3JGMessage msg);
	
	
	/**
	 * This function return the fitness value of the supervisor. This value is used for choose
	 * the best candidate for be the new supervisor during an election.
	 * 
	 * @return
	 * 			The Integer value of fitness.
	 */
	public abstract int fitnessFunc();
	
}
