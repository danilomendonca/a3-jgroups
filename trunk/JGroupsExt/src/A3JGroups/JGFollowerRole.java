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
	
	public abstract void run();
	
	public void receive(Message mex) {
		A3JGMessage msg = (A3JGMessage) mex.getObject();
		if(msg.getContent().equals("fitnessFunction")){
			int fitness;
			if(node.getSupervisorRole(groupName)!=null)
				fitness = node.getSupervisorRole(groupName).fitnessFunc();
			else
				fitness = 0;
			if(map.get("supervisor")==null && fitness > ((Integer) map.get("value")) ){
				map.put("value", fitness);
				map.put("newSup", chan.getAddress());
			}
		}else if(msg.getContent().equals("NewSupervisor")){
			if(map.putIfAbsent("supervisor", chan.getAddress())==null){
				this.active=false;
				node.getSupervisorRole(groupName).setActive(true);
				node.getSupervisorRole(groupName).setChan(chan);
				node.getSupervisorRole(groupName).setMap(map);
				chan.setReceiver(node.getSupervisorRole(groupName));
				new Thread(node.getSupervisorRole(groupName)).start();
				map.put("value", 0);
				map.put("newSup", null);
				map.put("change", null);
			}
			
		}else if(msg.getContent().equals("Deactivate")){
			node.terminate(groupName);
		}
		else{
			messageFromSupervisor(msg);
		}
	}
	
	public void viewAccepted(View view) {
		if (!view.getMembers().contains(map.get("supervisor")) && view.getMembers().get(0).equals(chan.getAddress())) {
			map.put("change", chan.getAddress());
			map.put("value", 0);
			map.put("newSup", null);
			map.remove("supervisor");
			A3JGMessage mex = new A3JGMessage();
			mex.setContent("fitnessFunction");
			Message msg = new Message(null, mex);
			try {
				this.chan.send(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(electionTimeOut);
				if (((Integer) map.get("value")) > 0) {
					mex.setContent("NewSupervisor");
					Message msg2 = new Message(null, mex);
					msg2.setDest(((Address) map.get("newSup")));
					msg2.setObject(mex);
					chan.send(msg2);
				} else {
					mex.setContent("Deactivate");
					Message msg3 = new Message(null, mex);
					chan.send(msg3);

				}
			} catch (Exception e) {
				e.printStackTrace();
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
