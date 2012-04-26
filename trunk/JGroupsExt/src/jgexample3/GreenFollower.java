package jgexample3;

import A3JGroups.A3JGMessage;
import A3JGroups.JGFollowerRole;



public class GreenFollower extends JGFollowerRole {
	
	public GreenFollower(int resourceCost, String groupName) {
		super(resourceCost, groupName);
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
		if(msg.getContent().equals("computer")){
			A3JGMessage mex = new A3JGMessage();
			mex.setContent(pc);
			sendMessageToSupervisor(mex);
		}
		
	}

}
