package jgexample3;

import A3JGroups.A3JGMessage;
import A3JGroups.JGSupervisorRole;


public class BlueSupervisor extends JGSupervisorRole {

	public BlueSupervisor(int resourceCost, String groupName) {
		super(resourceCost, groupName);
	}

	private int people = 0;
	private int n = 0;
	
	@Override
	public void run() {
		
				while (this.active) {
					
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						
						e.printStackTrace();
					}
					
					System.out.println("["+this.getNode().getID()+"] Sending message to blue followers... "+(this.getChan().getView().getMembers().size()-1));
					A3JGMessage msg = new A3JGMessage();
					msg.setContent("people");
					sendMessageToFollower(msg);
				
				}
	}

	@Override
	public void messageFromFollower(A3JGMessage msg) {
		people += (Integer) msg.getContent();
		n++;
		if(n==(this.getChan().getView().getMembers().size()-1)){
			System.out.println("["+this.getNode().getID()+"] The total number of people is " + people);
			n=0;
		}		
	}

	@Override
	public void updateFromFollower(A3JGMessage msg) {
		System.out.println("update from someone");
		
	}

	@Override
	public int fitnessFunc() {
		// TODO Auto-generated method stub
		return 3;
	}


}
