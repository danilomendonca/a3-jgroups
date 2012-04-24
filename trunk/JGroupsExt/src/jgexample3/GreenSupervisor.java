package jgexample3;

import A3JGroups.A3JGMessage;
import A3JGroups.JGSupervisorRole;


public class GreenSupervisor extends JGSupervisorRole {

	private int groupSize = 0;
	private int n = 0;
	private int pc = 0;
	
	@Override
	public void run() {
		while (this.active) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			A3JGMessage msg = new A3JGMessage("computer");
			sendMessageToFollower(msg);
			System.out.println("["+this.getNodeID()+"] Sending message to green followers...  " + groupSize);
		}
	}

	@Override
	public void messageFromFollower(A3JGMessage msg) {
		pc += (Integer) msg.getContent();
		if(n==groupSize){
			System.out.println("["+this.getNodeID()+"] The total number of computers on is " + pc);
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
