package A3JGroups;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.blocks.ReplicatedHashMap;

public class GenericRole extends ReceiverAdapter{

	private A3JGNode node;
	private JChannel chan;
	private ReplicatedHashMap<String, Object> map;
	private A3JGRHMNotification notifier;
	
	
	public GenericRole(A3JGNode node, JChannel chan, ReplicatedHashMap<String, Object> map, A3JGRHMNotification notifier) {
		super();
		this.node = node;
		this.chan = chan;
		this.map = map;
		this.notifier = notifier;
	}

	@SuppressWarnings("unchecked")
	public void receive(Message mex) {
		A3JGMessage msg = (A3JGMessage) mex.getObject();
		
		if(msg.getValueID().equals("A3FitnessFunction")){
			int fitness;
			String groupName = this.chan.getClusterName();
			String role = node.getGroupInfo(groupName).getSupervisor().get(0);
			if(node.getSupervisorRole(role)!=null)
				fitness = node.getSupervisorRole(role).fitnessFunc();
			else
				fitness = 0;
			
			map.put(chan.getAddressAsString(), fitness);
			
		}else if(msg.getValueID().equals("A3NewSupervisor")){
			
			String groupName = this.chan.getClusterName();
			String role = node.getGroupInfo(groupName).getSupervisor().get(0);
			node.putActiveRole(groupName, role);
			map.put("A3Supervisor", chan.getAddress());
			map.put("A3Change", null);
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
			node.waitings.remove(groupName);
			
		}else if(msg.getValueID().equals("A3Deactivate")){
			node.terminate(this.chan.getClusterName());
			node.waitings.remove(this.chan.getClusterName());
			
		}else if(msg.getValueID().equals("A3StayFollower")){
			
			String groupName = this.chan.getClusterName();
			String role = node.getGroupInfo(groupName).getFollower().get(0);
			node.putActiveRole(groupName, role);
			if(node.getFollowerRole(role)!=null){
				node.getFollowerRole(role).setActive(true);
				node.getFollowerRole(role).setChan(chan);
				node.getFollowerRole(role).setMap(map);
				node.getFollowerRole(role).setNotifier(notifier);
				chan.setReceiver(node.getFollowerRole(role));
				new Thread(node.getFollowerRole(role)).start();

			}else{
				node.close(groupName);
			}
			node.waitings.remove(groupName);
		}
	}
	
	public void waitElection(){
		if(map.get("A3FitnessFunction")!=null){
			int fitness;
			String groupName = this.chan.getClusterName();
			String role = node.getGroupInfo(groupName).getSupervisor().get(0);
			if(node.getSupervisorRole(role)!=null)
				fitness = node.getSupervisorRole(role).fitnessFunc();
			else
				fitness = 0;
			map.put(chan.getAddressAsString(), fitness);
		}
		if(map.get("A3Deactivate")!=null){
			node.waitings.remove(this.chan.getClusterName());
			node.terminate(this.chan.getClusterName());
		}
	}
	
	public void supervisorChallenge(){
		int fitness;
		String groupName = this.chan.getClusterName();
		String role = node.getGroupInfo(groupName).getSupervisor().get(0);
		if(node.getSupervisorRole(role)!=null)
			fitness = node.getSupervisorRole(role).fitnessFunc();
		else
			fitness = 0;
		A3JGMessage mex = new A3JGMessage("A3SupervisorChallenge", fitness);
		Message msg = new Message();
		msg.setObject(mex);
		msg.setDest((Address) map.get("A3Supervisor"));
		try {
			chan.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	
}
