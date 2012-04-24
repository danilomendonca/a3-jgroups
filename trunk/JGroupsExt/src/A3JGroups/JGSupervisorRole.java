package A3JGroups;


import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.blocks.ReplicatedHashMap;


public abstract class JGSupervisorRole extends ReceiverAdapter implements Runnable{

	protected boolean active;
	private int resourceCost;
	private String nodeID;
	private JChannel chan;
	private A3JGroup group;
	private long timeout = 1000;
	private ReplicatedHashMap<String, Address> map;
	
	/*
	public void activate(A3JGroup group) throws Exception {
		active = true;
		chan = new JChannel();
		chan.setReceiver(this);
		chan.connect(group.getGroupName());
		this.group = group;
	}
	*/
	
	public void activate(String groupName) throws Exception {
		active = true;
		chan = new JChannel();
		chan.setReceiver(this);
		chan.connect(groupName);
		map = new ReplicatedHashMap<String, Address>(chan);
		map.start(timeout);
		if(map.get("supervisor")==null)
			map.put("supervisor", chan.getAddress());
		else
			deactivate();
			//try follower activation if exist
	}
	
	public void deactivate() {
		active=false;
		chan.disconnect();
		chan.close();
	}

	public int getResourceCost() {
		return resourceCost;
	}

	public void setResourceCost(int resourceCost) {
		this.resourceCost = resourceCost;
	}

	public String getNodeID() {
		return nodeID;
	}

	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}
 
	public JChannel getChan() {
		return chan;
	}

	public void setChan(JChannel chan) {
		this.chan = chan;
	}
	
	public void setGroup(A3JGroup group){
		this.group = group;
	}

	public A3JGroup getGroup() {
		return group;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}

	public abstract void run();
	
	public void receive(Message msg) {
		A3JGMessage mex = (A3JGMessage) msg.getObject();
	
		if(mex.getType()){
			updateFromFollower(mex);
		}else
			messageFromFollower(mex);	
	}
	
	public boolean sendMessageToFollower(A3JGMessage mex){
		mex.setType(false);
		Message msg = new Message(null, mex);
			try {
				chan.send(msg);
			} catch (Exception e) {
				return false;
			}
		return true;
	}
	
	public abstract void messageFromFollower(A3JGMessage msg);
	public abstract void updateFromFollower(A3JGMessage msg);
	public abstract int fitnessFunc();
	
}
