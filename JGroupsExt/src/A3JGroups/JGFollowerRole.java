package A3JGroups;

import java.util.ArrayList;
import java.util.Date;
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
	private ReplicatedHashMap<String, Object> map;
	private ElectionManager em;
	private long electionTime = 10000;
	private int attempt = 0;
	
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

	public abstract void run();
	
	public void receive(Message mex) {
		A3JGMessage msg = (A3JGMessage) mex.getObject();
		if(msg.getContent().equals("fitnessFunction")){
			int fitness;
			if(node.getSupervisorRole(groupName)!=null)
				fitness = node.getSupervisorRole(groupName).fitnessFunc();
			else
				fitness = 0;
			
			map.put(chan.getAddressAsString(), fitness);
			
		}else if(msg.getContent().equals("NewSupervisor")){
			
				map.put("supervisor", chan.getAddress());
				map.put("change", null);
				this.active=false;
				node.getSupervisorRole(groupName).setActive(true);
				node.getSupervisorRole(groupName).setChan(chan);
				node.getSupervisorRole(groupName).setMap(map);
				chan.setReceiver(node.getSupervisorRole(groupName));
				node.getSupervisorRole(groupName).index = getLastIndex();
				new Thread(node.getSupervisorRole(groupName)).start();
				
		}else if(msg.getContent().equals("Deactivate")){
			node.terminate(groupName);
		
		}else if(((String) msg.getContent()).contains("MergeGroup")){
			String group = ((String) msg.getContent()).substring(9);
			try {
				node.joinGroup(group);
			} catch (Exception e) {
				e.printStackTrace();
			}
			node.terminate(groupName);
			
		}else if(((String) msg.getContent()).contains("JoinGroup")){
			String group = ((String) msg.getContent()).substring(9);
			try {
				node.joinGroup(group);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(msg.getContent().equals("StayFollower")){
			attempt = 0;
		}else{
			messageFromSupervisor(msg);
		}
	}
	
	public void viewAccepted(View view) {
		if (!view.getMembers().contains(map.get("supervisor")) && view.getMembers().get(0).equals(chan.getAddress()) && attempt < 4) {
			map.put("change", chan.getAddress());
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
			if(!chan.getView().containsMember((Address) map.get("supervisor")))
				return false;
			Message msg = new Message((Address) map.get("supervisor"), mex);
			this.chan.send(msg);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public boolean sendUpdateToSupervisor(A3JGMessage mex){
		mex.setType(true);
		try {
			if(!chan.getView().containsMember((Address) map.get("supervisor")))
				return false;
			Message msg = new Message((Address) map.get("supervisor"), mex);
			this.chan.send(msg);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public List<A3JGMessage> getMessageOverTime(){
		ArrayList<A3JGMessage> mex = new ArrayList<A3JGMessage>();
		List<Integer> chiavi = ((List<Integer>) map.get("message"));
		for(int i: chiavi){
			Message msg = ((Message) map.get("MessageInMemory_"+i));
			mex.add((A3JGMessage) msg.getObject());
		}
		return mex;
	}
	
	@SuppressWarnings("unchecked")
	private int getLastIndex(){
		int max = -1;
		if(map.get("message")!=null){
			Map<Integer, Date> chiavi = (Map<Integer, Date>) map.get("message");
			for (int i : chiavi.keySet()) {
				if (i > max)
					max = i;
			}
		}
		return max;
	}
	
	public abstract void messageFromSupervisor(A3JGMessage msg);

		
}
