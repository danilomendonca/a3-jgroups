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
	private String nodeID;
	private JChannel chan;
	protected A3JGroup group;
	private ReplicatedHashMap<String, Address> map;
	
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
	
	public void setGroup(A3JGroup group){
		this.group = group;
	}
	
	public A3JGroup getGroup() {
		return group;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public void setChan(JChannel chan) {
		this.chan = chan;
	}

	public void setMap(ReplicatedHashMap<String, Address> map) {
		this.map = map;
	}

	public abstract void run();
	
	public void receive(Message msg) {
		if(msg.getObject().equals("fitnessFunction")){
			//inviare all'indirizzo in mappa ala chiava change il valore della fitness
		}else if(msg.getObject().equals("fitnessFunctionResult")){
			//add in change riceve risultati della fitness (anche il suo???), se nessuno è abile chiudere cluster
		}else{
			A3JGMessage mex = (A3JGMessage) msg.getObject();
			messageFromSupervisor(mex);
		}
	}
	
	public void viewAccepted(View view) {
        if(!view.getMembers().contains(map.get("supervisor")))
        	if(map.putIfAbsent("change", chan.getAddress())==null){
        		map.remove("supervisor");
        		//invio richiesta per trovare nuovo supervisore
        		Message msg = new Message(null, "fitnessFunction");
        		try {
					chan.send(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
        		
        	}
    }
	
	public boolean sendMessageToSupervisor(A3JGMessage mex){
		try {
			Message msg = new Message(group.getSupAddr(), mex);
			chan.send(msg);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public boolean sendUpdateToSupervisor(A3JGMessage mex){
		mex.setType(true);
		try {
			Message msg = new Message(group.getSupAddr(), mex);
			chan.send(msg);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public abstract void messageFromSupervisor(A3JGMessage msg);
		
}
