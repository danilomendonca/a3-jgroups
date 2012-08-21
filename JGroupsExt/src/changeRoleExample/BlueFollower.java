package changeRoleExample;

import A3JGroups.A3JGFollowerRole;
import A3JGroups.A3JGMessage;


public class BlueFollower extends A3JGFollowerRole {

	public BlueFollower(int resourceCost) {
		super(resourceCost);
	}

	private int people;
	
	@Override
	public void run() {
		System.out.println("blue follower active");
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
