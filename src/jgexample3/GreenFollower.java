package jgexample3;

import A3JGroups.A3JGMessage;
import A3JGroups.A3JGFollowerRole;



public class GreenFollower extends A3JGFollowerRole {
	
	public GreenFollower(int resourceCost) {
		super(resourceCost);
		// TODO Auto-generated constructor stub
	}

	private int pc;
	
	@Override
	public void run() {
		
		while (this.active) {
			
			pc = (int) (Math.random()*35);
			System.out.println("["+this.getNode().getID()+"] number of computers on: "+pc);
			
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void messageFromSupervisor(A3JGMessage msg) {
		if(msg.getValueID().equals("computer")){
			A3JGMessage mex = new A3JGMessage("computer");
			mex.setContent(pc);
			sendMessageToSupervisor(mex);
		}
		
	}

}
