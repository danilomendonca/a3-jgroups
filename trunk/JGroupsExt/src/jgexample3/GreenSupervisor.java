package jgexample3;

import A3JGroups.A3JGMessage;
import A3JGroups.JGSupervisorRole;


public class GreenSupervisor extends JGSupervisorRole {

	private int n = 0;
	private int pc = 0;
	
	public GreenSupervisor(int resourceCost, String groupName) {
		super(resourceCost, groupName);
	}

	@Override
	public void run() {
		while (this.active) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			A3JGMessage msg = new A3JGMessage();
			msg.setContent("computer");
			sendMessageToFollower(msg);
			System.out.println("["+this.getNode().getID()+"] Sending message to green followers...  " + (this.getChan().getView().getMembers().size()-1));
		}
	}

	@Override
	public void messageFromFollower(A3JGMessage msg) {
		pc += (Integer) msg.getContent();
		if(n==(this.getChan().getView().getMembers().size()-1)){
			System.out.println("["+this.getNode().getID()+"] The total number of computers on is " + pc);
		}	
		
	}

	@Override
	public void updateFromFollower(A3JGMessage msg) {
		System.out.println("update from someone");	
	}

	@Override
	public int fitnessFunc() {
		return 4;
	}

	

}
