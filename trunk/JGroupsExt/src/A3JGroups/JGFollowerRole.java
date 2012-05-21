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


public abstract class JGFollowerRole extends ReceiverAdapter implements Runnable{

	protected boolean active;
	private int resourceCost;
	private String groupName;
	private JChannel chan;
	protected A3JGNode node;
	protected ReplicatedHashMap<String, Object> map;
	private ElectionManager em;
	private long electionTime = 10000;
	private int attempt = 0;
	private A3JGRHMNotification notifier;
	
	public JGFollowerRole(int resourceCost, String groupName) {
		super();
		this.resourceCost = resourceCost;
		this.groupName = groupName;
	}

	public int getResourceCost() {
		return resourceCost;
	}

	public void setResourceCost(int resourceCost) {
		this.resourceCost = resourceCost;
	}

	public String getGroupName() {
		return groupName;
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

	public abstract void run();
	
	@SuppressWarnings("unchecked")
	public void receive(Message mex) {
		A3JGMessage msg = (A3JGMessage) mex.getObject();
		if(msg.getContent().equals("A3FitnessFunction")){
			int fitness;
			if(node.getSupervisorRole(groupName)!=null)
				fitness = node.getSupervisorRole(groupName).fitnessFunc();
			else
				fitness = 0;
			
			map.put(chan.getAddressAsString(), fitness);
			
		}else if(msg.getContent().equals("A3NewSupervisor")){
			map.put("A3Supervisor", chan.getAddress());
			
			this.active=false;
			node.getSupervisorRole(groupName).setActive(true);
			node.getSupervisorRole(groupName).setChan(chan);
			node.getSupervisorRole(groupName).setMap(map);
			chan.setReceiver(node.getSupervisorRole(groupName));
			node.getSupervisorRole(groupName).index = getLastIndex();
			if(map.get("A3Message")!=null){
				node.getSupervisorRole(groupName).deleter.setActive(true);
				node.getSupervisorRole(groupName).deleter.setMap(map);
				node.getSupervisorRole(groupName).deleter.setChiavi((HashMap<Integer, Date>) map.get("A3Message"));
				new Thread(node.getSupervisorRole(groupName).deleter).start();
			}
			new Thread(node.getSupervisorRole(groupName)).start();
				
		}else if(msg.getContent().equals("A3Deactivate")){
			node.terminate(groupName);
		
		}else if(((String) msg.getContent()).contains("A3MergeGroup")){
			String group = ((String) msg.getContent()).substring(9);
			try {
				node.joinGroup(group);
			} catch (Exception e) {
				e.printStackTrace();
			}
			node.terminate(groupName);
			
		}else if(((String) msg.getContent()).contains("A3JoinGroup")){
			String group = ((String) msg.getContent()).substring(9);
			try {
				node.joinGroup(group);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(msg.getContent().equals("A3StayFollower")){
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
	
	public abstract void messageFromSupervisor(A3JGMessage msg);

	public A3JGRHMNotification getNotifier() {
		return notifier;
	}

	public void setNotifier(A3JGRHMNotification notifier) {
		this.notifier = notifier;
	}

		
}
