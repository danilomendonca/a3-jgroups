package A3JGroups;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.blocks.ReplicatedHashMap;


/**
 * A3JGFollowerRole must be extended in order to define a certain type of follower. You 
 * must define the behavior of each follower implementing the run function. A followerRole
 * can be used in more than one group, and different role can be used in the same group.
 * In each group there are more follower that work with a supervisor.
 * 
 * @author bett.marco88@gmail.com
 *
 */
public abstract class A3JGFollowerRole extends ReceiverAdapter implements Runnable{

	protected boolean active;
	private int resourceCost;
	private JChannel chan;
	private A3JGNode node;
	protected ReplicatedHashMap<String, Object> map;
	private ElectionManager em;
	private long electionTime = 1000;
	private int attempt = 0;
	private A3JGRHMNotification notifier;
	
	public A3JGFollowerRole(int resourceCost) {
		super();
		this.resourceCost = resourceCost;
	}

	public int getResourceCost() {
		return resourceCost;
	}

	public void setResourceCost(int resourceCost) {
		this.resourceCost = resourceCost;
	}
	
	public void setNode(A3JGNode node){
		this.node = node;
	}
	
	public A3JGNode getNode() {
		return node;
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
	
	public void setElectionTime(long electionTime) {
		this.electionTime = electionTime;
	}

	public Object getAppSharedState(String stateKey){
		return map.get("A3SharedState"+stateKey);
	}
	
	public void putAppSharedState(String stateKey, Object appState){
		map.put("A3SharedState"+stateKey, appState);
	}	
	
	public A3JGRHMNotification getNotifier() {
		return notifier;
	}

	public void setNotifier(A3JGRHMNotification notifier) {
		this.notifier = notifier;
	}
	
	public abstract void run();
	
	@SuppressWarnings("unchecked")
	public void receive(Message mex) {
		A3JGMessage msg = (A3JGMessage) mex.getObject();
		if(msg.getValueID().equals("A3FitnessFunction")){
			int fitness;
			String groupName = this.chan.getClusterName();
			if(groupName.contains("A3Split"))
				groupName = groupName.substring(groupName.lastIndexOf("A3Split"));
			String role = node.getGroupInfo(groupName).getSupervisor().get(0);
			if(node.getSupervisorRole(role)!=null)
				fitness = node.getSupervisorRole(role).fitnessFunc();
			else
				fitness = 0;
			map.put(chan.getAddressAsString(), fitness);
			
		}else if(msg.getValueID().equals("A3NewSupervisor")){
			map.put("A3Supervisor", chan.getAddress());
			String groupName = this.chan.getClusterName();
			String role = node.getGroupInfo(groupName).getSupervisor().get(0);
			node.putActiveRole(groupName, role);
			this.active=false;
			
			node.getSupervisorRole(role).setActive(true);
			node.getSupervisorRole(role).setChan(chan);
			node.getSupervisorRole(role).setMap(map);
			node.getSupervisorRole(role).setNotifier(notifier);
			chan.setReceiver(node.getSupervisorRole(role));
			node.getSupervisorRole(role).index = getLastIndex();
			if(map.get("A3Message")!=null){
				node.getSupervisorRole(role).deleter.setActive(true);
				node.getSupervisorRole(role).deleter.setMap(map);
				node.getSupervisorRole(role).deleter.setChiavi((HashMap<Integer, Date>) map.get("A3Message"));
				new Thread(node.getSupervisorRole(role).deleter).start();
			}
			new Thread(node.getSupervisorRole(role)).start();
				
		}else if(msg.getValueID().equals("A3Deactivate")){
			node.terminate(this.chan.getClusterName());
		
		}else if(msg.getValueID().equals("A3SplitNewSupervisor")){
			int port = (Integer) msg.getContent();
			try {
				this.getNode().joinSplitGroup(this.getChan().getClusterName(), this.getChan(), port, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(msg.getValueID().equals("A3SplitChange")){
			int port = (Integer) msg.getContent();
			try {
				this.getNode().joinSplitGroup(this.getChan().getClusterName(), this.getChan(), port, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(msg.getValueID().equals("A3MergeGroup")){
			String groupName = this.getChan().getClusterName();
			this.getNode().terminate(groupName);
			try {
				this.getNode().joinGroup(groupName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}else if(msg.getValueID().equals("A3StayFollower")){
			attempt = 0;
		}else{
			messageFromSupervisor(msg);
		}
	}
	
	public void viewAccepted(View view) {
		if (!view.getMembers().contains(map.get("A3Supervisor")) && view.getMembers().get(0).equals(chan.getAddress()) && attempt < 4) {
			map.put("A3Change", chan.getAddress());
			attempt++;
			if(em!=null){
				em.setDecide(false);
			}
			em = new ElectionManager(electionTime, map, chan);
			new Thread(em).start();
		}
    }
	
	/**
	 * You have to use this function to send message to the supervisor.
	 * 
	 * @param mex
	 * 			The A3JGMessage sent to the supervisor.
	 * @return
	 * 			True if the message is sent, false otherwise.
	 */
	public boolean sendMessageToSupervisor(A3JGMessage mex){
		try {
			if(!chan.getView().containsMember((Address) map.get("A3Supervisor")))
				return false;
			Message msg = new Message((Address) map.get("A3Supervisor"), mex);
			this.chan.send(msg);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	
	/**
	 * This function is used to send an update to the supervisor, using a message.
	 * 
	 * @param mex
	 * 			The A3JGMessage sent to the supervisor.
	 * @return
	 * 			True if the message is sent, false otherwise.
	 */
	public boolean sendUpdateToSupervisor(A3JGMessage mex){
		mex.setType(true);
		try {
			if(!chan.getView().containsMember((Address) map.get("A3Supervisor")))
				return false;
			Message msg = new Message((Address) map.get("A3Supervisor"), mex);
			this.chan.send(msg);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * This function is used to retrieve messages left in memory on ReplicatedHashMap.
	 * @return
	 * 			The list of A3JGMessage presents on the map.
	 */
	@SuppressWarnings("unchecked")
	public List<A3JGMessage> getMessageOverTime(){
		ArrayList<A3JGMessage> mex = new ArrayList<A3JGMessage>();
		List<Integer> chiavi = ((List<Integer>) map.get("A3Message"));
		for(int i: chiavi){
			A3JGMessage msg = ((A3JGMessage) map.get("A3MessageInMemory_"+i));
			mex.add(msg);
		}
		return mex;
	}
	
	@SuppressWarnings("unchecked")
	private int getLastIndex(){
		int max = -1;
		if(map.get("A3Message")!=null){
			Map<Integer, Date> chiavi = (Map<Integer, Date>) map.get("A3Message");
			for (int i : chiavi.keySet()) {
				if (i > max)
					max = i;
			}
		}
		return max;
	}
	
	
	/**
	 * This function allows the follower to change is role while it is active.
	 * 
	 * @param config
	 * 			The Integer key of the configuration that must be activated.
	 * @return
	 * 			True if the change has success, false otherwise.
	 */
	public boolean changeRoleInGroup(int config){
		
		String folName = node.getGroupInfo(chan.getClusterName()).getFollower().get(config);
		if(node.getFollowerRole(folName)!=null){
			this.setActive(false);
			node.getFollowerRole(folName).setActive(true);
			node.getFollowerRole(folName).setChan(chan);
			node.getFollowerRole(folName).setMap(map);
			node.getFollowerRole(folName).setNotifier(notifier);
			new Thread(node.getFollowerRole(folName)).start();
			chan.setReceiver(node.getFollowerRole(folName));
			node.putActiveRole(chan.getClusterName(), folName);
			return true;
		}
		return false;
	}

	/**
	 * This function must be extended in order to define the behavior of the
	 * follower when receives a message from the supervisor.
	 * 
	 * @param msg
	 * 			Is the message sent by the supervisor.
	 */
	public abstract void messageFromSupervisor(A3JGMessage msg);
		
}
