package A3JGroups;


import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.blocks.ReplicatedHashMap;


public abstract class JGSupervisorRole extends ReceiverAdapter implements Runnable{

	protected boolean active;
	private int resourceCost;
	private String groupName;
	private JChannel chan;
	protected A3JGNode node;
	private ReplicatedHashMap<String, Object> map;
	

	public JGSupervisorRole(int resourceCost, String groupName) {
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
 
	public A3JGNode getNode() {
		return node;
	}

	public void setNode(A3JGNode node) {
		this.node = node;
	}

	public String getGroupName() {
		return groupName;
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

	public Object getState() {
		return map.get("state");
	}

	public void setState(Object state) {
		map.put("state", state);
	}
	
	public abstract void run();
	
	public void receive(Message msg) {
		A3JGMessage mex = (A3JGMessage) msg.getObject();
		if (mex.getType()) {
			updateFromFollower(mex);
		} else
			messageFromFollower(mex);
	}
	
	public boolean sendMessageToFollower(A3JGMessage mex){
		mex.setType(false);
		Message msg = new Message();
		msg.setObject(mex);
			try {
				for(Address ad: this.chan.getView().getMembers()){
					if(!ad.equals(this.chan.getAddress())){
						msg.setDest(ad);
						this.chan.send(msg);
					}else{
						;
					}
				}
			} catch (Exception e) {
				return false;
			}
		return true;
	}
	
	public boolean sendMessageOverTime(A3JGMessage mex){
		mex.setType(false);
		Message msg = new Message();
		msg.setObject(mex);
		map.put("message", mex);
			try {
				for(Address ad: this.chan.getView().getMembers()){
					if(!ad.equals(this.chan.getAddress())){
						msg.setDest(ad);
						this.chan.send(msg);
					}else{
						;
					}
				}
			} catch (Exception e) {
				return false;
			}
		return true;
	}

	public void merge(String groupName) throws Exception{
		A3JGMessage mex = new A3JGMessage();
		mex.setContent("MergeGroup"+groupName);
		sendMessageToFollower(mex);
		node.joinGroup(groupName);
		node.terminate(this.groupName);
	}
	
	public void join(String groupName) throws Exception{
		A3JGMessage mex = new A3JGMessage();
		mex.setContent("JoinGroup"+groupName);
		sendMessageToFollower(mex);
		node.joinGroup(groupName);
	}
	
	public void split(String newGroupName){
		A3JGMessage mex = new A3JGMessage();
		mex.setContent("fitnessFunction");
		sendMessageToFollower(mex);
	}
	
	public A3JGroup infoGroup(){
		return (A3JGroup) map.get("groupInfo");
	}

	public abstract void messageFromFollower(A3JGMessage msg);
	public abstract void updateFromFollower(A3JGMessage msg);
	public abstract int fitnessFunc();
	
}
