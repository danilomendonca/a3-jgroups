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
	private ElectionManager em;
	private long electionTime = 10000;
	
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
	
	public void setElectionTime(long electionTime) {
		this.electionTime = electionTime;
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
			
			map.put(chan.getAddressAsString(), fitness);
			
		}else if(msg.getContent().equals("NewSupervisor")){
			
				map.put("supervisor", chan.getAddress());
				map.put("change", null);
				this.active=false;
				node.getSupervisorRole(groupName).setActive(true);
				node.getSupervisorRole(groupName).setChan(chan);
				node.getSupervisorRole(groupName).setMap(map);
				chan.setReceiver(node.getSupervisorRole(groupName));
				new Thread(node.getSupervisorRole(groupName)).start();
				
		}else if(msg.getContent().equals("Deactivate")){
			node.terminate(groupName);
		
		}else if(((String) msg.getContent()).contains("MergeGroup")){
			String group = ((String) msg.getContent()).substring(9);
			try {
				node.joinGroup(group);
			} catch (Exception e) {
				e.printStackTrace();
			}
			node.terminate(groupName);
			
		}else if(((String) msg.getContent()).contains("JoinGroup")){
			String group = ((String) msg.getContent()).substring(9);
			try {
				node.joinGroup(group);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}else{
			messageFromSupervisor(msg);
		}
	}
	
	public void viewAccepted(View view) {
		if (!view.getMembers().contains(map.get("supervisor")) && view.getMembers().get(0).equals(chan.getAddress())) {
			System.out.println("vista cambiata e supervisore morto **************** "+map.values());
			map.put("change", chan.getAddress());
			
			try {
				A3JGMessage mex = new A3JGMessage();
				mex.setContent("fitnessFunction");
				Message msg = new Message(null, mex);
				this.chan.send(msg);
			
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(em!=null){
				em.setDecide(false);
			}
			em = new ElectionManager(electionTime, map, chan);
			new Thread(em).start();
			
			/*try {
				Thread.sleep(electionTimeOut);
				prova--;
				System.out.println("attesa finita *****************" + prova + map.values());
				if (prova == 0) {
					int max = 0;
					Address newSup = null;
					for (Address ad : chan.getView().getMembers()) {
						String s = ad.toString();
						if (map.containsKey(s)) {
							int value = (int) map.get(s);
							if (value > max) {
								max = value;
								newSup = ad;
							}
						}
					}
					if (max > 0) {
						System.out.println("entrato con: "+max+" e sup is: "+newSup+" e prova: "+prova);
						A3JGMessage mex = new A3JGMessage();
						mex.setContent("NewSupervisor");
						Message msg2 = new Message(null, mex);
						msg2.setDest(newSup);
						msg2.setObject(mex);
						chan.send(msg2);
					} else {
						A3JGMessage mex = new A3JGMessage();
						mex.setContent("Deactivate");
						Message msg3 = new Message(null, mex);
						chan.send(msg3);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}*/
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
	
	public Object getMessageOverTime(){
		A3JGMessage msg = (A3JGMessage) map.get("message");
		return msg.getContent();
	}
	
	public abstract void messageFromSupervisor(A3JGMessage msg);

		
}
