package A3JGroups;


import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.blocks.ReplicatedHashMap;


public abstract class JGSupervisorRole extends ReceiverAdapter implements Runnable{

	protected boolean active;
	private int resourceCost;
	protected int index;
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
	
	public int sendMessageOverTime(A3JGMessage mex, int days, int hours, int minutes){
		mex.setType(false);
		Message msg = new Message();
		msg.setObject(mex);
		index++;
		Calendar c = Calendar.getInstance();
		if(days == 0 && hours == 0 && minutes == 0){
			c=null;
		}else{
			c.add(Calendar.DATE, days);
			c.add(Calendar.HOUR, hours);
			c.add(Calendar.MINUTE, minutes);
		}
		HashMap<Integer, Date> chiavi = ((HashMap<Integer, Date>) map.get("message"));
		chiavi.put(index, c.getTime());
		map.put("message", chiavi);
		map.put("MessageInMemory_"+index, msg);
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
				return -1;
			}
		return index;
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
