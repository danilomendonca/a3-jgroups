package jgexample3;

import A3JGroups.A3JGMessage;
import A3JGroups.JGSupervisorRole;


public class BlueSupervisor extends JGSupervisorRole {

	private int people = 0;
	private int n = 0;
	private int groupSize = 0;
	
	@Override
	public void run() {
		
				while (this.active) {
					
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						
						e.printStackTrace();
					}
					
					System.out.println("["+this.getNodeID()+"] Sending message to blue followers...");
					A3JGMessage msg = new A3JGMessage("people");
					sendMessageToFollower(msg);
				
				}
	}

	@Override
	public void messageFromFollower(A3JGMessage msg) {
		people += (Integer) msg.getContent();
		if(n==groupSize){
			System.out.println("["+this.getNodeID()+"] The total number of people is " + people);
		}		
	}

	@Override
	public void updateFromFollower(A3JGMessage msg) {
		if(msg.getContent().equals("join"))
			groupSize++;
		else
			groupSize--;
		
	}


}
