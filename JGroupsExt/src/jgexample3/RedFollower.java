package jgexample3;


import A3JGroups.A3JGMessage;
import A3JGroups.JGFollowerRole;

public class RedFollower extends JGFollowerRole{

	private int temp;
	
	@Override
	public void run() {
		
		while (this.active) {
			
			temp = (int) (Math.random()*35);
			System.out.println("["+this.getNodeID()+"] degrees: "+temp);
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void messageFromSupervisor(A3JGMessage msg) {
		if(msg.getContent().equals("temperature")){
			A3JGMessage mex = new A3JGMessage(temp);
			sendMessageToSupervisor(mex);
		}
	}


}
