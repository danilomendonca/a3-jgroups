package jgexample3;

import org.jgroups.View;

import A3JGroups.A3JGMessage;
import A3JGroups.JGSupervisorRole;


public class GreenSupervisor extends JGSupervisorRole {

	private int n = 0;
	private int pc = 0;
	private int fitness = 2;
	private View vista;
	
	public GreenSupervisor(int resourceCost, String groupName) {
		super(resourceCost, groupName);
	}

	public void setFitness(int fitness) {
		this.fitness = fitness;
	}

	@Override
	public void run() {
		while (this.active) {
			vista = this.node.getChannels("green").getView();
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			A3JGMessage msg = new A3JGMessage();
			msg.setContent("computer");
			sendMessageToFollower(msg);
			System.out.println("["+this.getNode().getID()+"] Sending message to green followers...  " + (vista.getMembers().size()-1));
		}
	}

	@Override
	public void messageFromFollower(A3JGMessage msg) {
		pc += (Integer) msg.getContent();
		if(n==(vista.getMembers().size()-1)){
			System.out.println("["+this.getNode().getID()+"] The total number of computers on is " + pc);
		}	
		
	}

	@Override
	public void updateFromFollower(A3JGMessage msg) {
		System.out.println("update from someone");	
	}

	@Override
	public int fitnessFunc() {
		return fitness;
	}

	

}
