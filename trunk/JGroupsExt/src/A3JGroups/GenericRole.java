package A3JGroups;


import java.util.Date;
import java.util.Map;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.blocks.ReplicatedHashMap;

public class GenericRole extends ReceiverAdapter{

	private A3JGNode node;
	private String groupName;
	private JChannel chan;
	private ReplicatedHashMap<String, Object> map;
	
	
	public GenericRole(A3JGNode node, String groupName, JChannel chan, ReplicatedHashMap<String, Object> map) {
		super();
		this.node = node;
		this.groupName = groupName;
		this.chan = chan;
		this.map = map;
	}

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
			map.put("A3Change", null);
			node.getSupervisorRole(groupName).setActive(true);
			node.getSupervisorRole(groupName).setChan(chan);
			node.getSupervisorRole(groupName).setMap(map);
			chan.setReceiver(node.getSupervisorRole(groupName));
			node.getSupervisorRole(groupName).index = getLastIndex();
			new Thread(node.getSupervisorRole(groupName)).start();
			
		}else if(msg.getContent().equals("A3Deactivate")){
			node.terminate(groupName);
		}else if(msg.getContent().equals("A3StayFollower")){
			if(node.getFollowerRole(groupName)!=null){
				System.out.println(map.toString());
				node.getFollowerRole(groupName).setActive(true);
				node.getFollowerRole(groupName).setChan(chan);
				node.getFollowerRole(groupName).setMap(map);
				chan.setReceiver(node.getFollowerRole(groupName));
				new Thread(node.getFollowerRole(groupName)).start();
			}else{
				node.waitings.remove(groupName);
				node.close(groupName);
			}
		}
	}
	
	public void waitElection(){
		if(map.get("A3FitnessFunction")!=null){
			int fitness;
			if(node.getSupervisorRole(groupName)!=null)
				fitness = node.getSupervisorRole(groupName).fitnessFunc();
			else
				fitness = 0;
			map.put(chan.getAddressAsString(), fitness);
		}
		if(map.get("A3Deactivate")!=null){
			node.waitings.remove(groupName);
			node.terminate(groupName);
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
