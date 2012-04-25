package A3JGroups;

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
	private long electionTimeOut = 1000;
	
	public JGFollowerRole(int resourceCost, String groupName) {
		super();
		this.resourceCost = resourceCost;
		this.groupName = groupName;
	}

	public int getResourceCost() {
		return resourceCost;
	}
	
	public void setElectionTimeOut(long electionTimeOut) {
		this.electionTimeOut = electionTimeOut;
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

	public JChannel getChan() {
		return chan;
	}

	public void setChan(JChannel chan) {
		this.chan = chan;
	}

	public void setMap(ReplicatedHashMap<String, Object> map) {
		this.map = map;
	}
	
	public abstract void run();
	
	public void receive(Message msg) {
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
				this.active=false;
				node.getSupervisorRole(groupName).setActive(true);
				node.getSupervisorRole(groupName).setChan(chan);
				node.getSupervisorRole(groupName).setMap(map);
				chan.setReceiver(node.getSupervisorRole(groupName));
				new Thread(node.getSupervisorRole(groupName)).start();
			}
			
		}else{
			A3JGMessage mex = (A3JGMessage) msg.getObject();
			messageFromSupervisor(mex);
		}
	}
	
	public void viewAccepted(View view) {
        if(!view.getMembers().contains(map.get("supervisor"))){
        	if(map.putIfAbsent("change", node.getChannels(groupName).getAddress())==null){
        		map.remove("supervisor");
        		map.put("value", 0);
        		map.put("newSup", null);
        		Message msg = new Message(null, "fitnessFunction");
        		try {
        			this.chan.send(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
        		try {
					Thread.sleep(electionTimeOut);
					if(((Integer) map.get("value"))!=0){
						msg.setDest(((Address) map.get("newSup")));
						msg.setObject("NewSupervisor");
	        			chan.send(msg);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
        		
        		
        		
        	}
        }
    }
	public boolean sendMessageToSupervisor(A3JGMessage mex){
		try {
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
			Message msg = new Message((Address) map.get("supervisor"), mex);
			this.chan.send(msg);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public abstract void messageFromSupervisor(A3JGMessage msg);

		
}
