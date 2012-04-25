package A3JGroups;


import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
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

	public void receive (Message msg){
		if(msg.getObject().equals("fitnessFunction")){
			int fitness;
			if(node.getSupervisorRole(groupName)!=null)
				fitness = node.getSupervisorRole(groupName).fitnessFunc();
			else
				fitness = 0;
			if(fitness > ((Integer) map.get("value"))){
				map.replace("value", fitness);
				map.replace("newSup", chan.getAddress());
			}
		}else if(msg.getObject().equals("NewSupervisor")){
			if(map.putIfAbsent("supervisor", chan.getAddress())==null){
				chan.setReceiver(node.getSupervisorRole(groupName));
				node.getSupervisorRole(groupName).setActive(true);
				node.getSupervisorRole(groupName).setChan(chan);
				node.getSupervisorRole(groupName).setMap(map);
				new Thread(node.getSupervisorRole(groupName)).start();
			}
		}
	}
	
	public void viewAccepted(View v){
		System.out.println(v);
		if (v.size()==1){
			if(node.getSupervisorRole(groupName)!=null){
				node.getSupervisorRole(groupName).setActive(true);
				chan.setReceiver(node.getSupervisorRole(groupName));
				new Thread(node.getSupervisorRole(groupName)).start();
			}
		}else
			if(node.getFollowerRole(groupName)!=null){
				node.getFollowerRole(groupName).setActive(true);
				chan.setReceiver(node.getFollowerRole(groupName));
				new Thread(node.getFollowerRole(groupName)).start();
			}
	}
}
