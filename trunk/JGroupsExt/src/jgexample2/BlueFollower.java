package jgexample2;

import A3JGroups.A3JGMessage;
import A3JGroups.A3JGFollowerRole;


public class BlueFollower extends A3JGFollowerRole {

	public BlueFollower(int resourceCost) {
		super(resourceCost);
	}

	private int people;
	
	@Override
	public void run() {
		
		while (this.active) {
			
			people = (int) (Math.random()*35);
			System.out.println("["+this.getNode().getID()+"] number of people: "+people);
			
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void messageFromSupervisor(A3JGMessage msg) {
		if(msg.getValueID().equals("people")){
			A3JGMessage mex = new A3JGMessage("people");
			mex.setContent(people);
			sendMessageToSupervisor(mex);
		}
	}
}
