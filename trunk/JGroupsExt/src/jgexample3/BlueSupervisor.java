package jgexample3;

import org.jgroups.View;

import A3JGroups.A3JGMessage;
import A3JGroups.JGSupervisorRole;


public class BlueSupervisor extends JGSupervisorRole {
	
	private int fitness = 2;
	private int people = 0;
	private int n = 0;
	private View vista;
	
	public BlueSupervisor(int resourceCost) {
		super(resourceCost);
	}

	public void setFitness(int fitness) {
		this.fitness = fitness;
	}
	
	@Override
	public void run() {
		
				while (this.active) {
					vista = this.node.getChannels("blue").getView();
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						
						e.printStackTrace();
					}
					
					System.out.println("["+this.getNode().getID()+"] Sending message to blue followers... "+(vista.getMembers().size()-1));
					A3JGMessage msg = new A3JGMessage("people");
					sendMessageToFollower(msg, null);
				
				}
	}

	@Override
	public void messageFromFollower(A3JGMessage msg) {
		people += (Integer) msg.getContent();
		n++;
		if(n==(vista.getMembers().size()-1)){
			System.out.println("["+this.getNode().getID()+"] The total number of people is " + people);
			n=0;
			people=0;
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
