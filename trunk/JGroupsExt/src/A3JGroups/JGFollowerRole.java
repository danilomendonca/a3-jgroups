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
			if(node.getSupervisorRole(groupName)!=null)
				msg.setObject(node.getSupervisorRole(groupName).fitnessFunc());
			else
				msg.setObject(0);
			try {
				msg.setObject("fitnessFunctionResult");
				msg.setDest((Address) map.get("change"));
				chan.send(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(msg.getObject().equals("fitnessFunctionResult")){
			//add in change riceve risultati della fitness (anche il suo???), se nessuno è abile chiudere cluster
		}else{
			A3JGMessage mex = (A3JGMessage) msg.getObject();
			messageFromSupervisor(mex);
		}
	}
	
	public void viewAccepted(View view) {
		System.out.println("there is a change");
        if(!view.getMembers().contains(map.get("supervisor")))
        	if(map.putIfAbsent("change", node.getChannels(groupName).getAddress())==null){
        		map.remove("supervisor");
        		//invio richiesta per trovare nuovo supervisore
        		Message msg = new Message(null, "fitnessFunction");
        		try {
        			this.chan.send(msg);
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
