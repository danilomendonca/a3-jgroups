package jgexample1;


import org.jgroups.View;

import A3JGroups.A3JGMessage;
import A3JGroups.JGFollowerRole;

public class RedFollower extends JGFollowerRole{

	private int temp;
	
	@Override
	public void run() {
		
		while (this.active) {
			/*
			temp = (int) (Math.random()*35);
			System.out.println(this.getNodeID()+" "+temp);
			*/
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			View vista = this.getChan().getView();
			System.out.println(vista.getMembers());
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
