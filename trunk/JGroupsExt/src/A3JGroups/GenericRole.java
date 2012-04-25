package A3JGroups;


import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

public class GenericRole extends ReceiverAdapter{

	private A3JGNode node;
	private String groupName;
	private JChannel chan;
	
	
	public GenericRole(A3JGNode nod, String groupNam, JChannel chann) {
		super();
		this.node = nod;
		this.groupName = groupNam;
		this.chan = chann;
	}

	public void receive (Message m){
		System.out.println(m.getObject());
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
