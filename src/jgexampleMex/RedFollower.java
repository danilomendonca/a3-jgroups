package jgexampleMex;

import A3JGroups.A3JGMessage;
import A3JGroups.A3JGFollowerRole;

public class RedFollower extends A3JGFollowerRole{

	private int temp;
	
	public RedFollower(int resourceCost) {
		super(resourceCost);
	}

	@Override
	public void run() {
		
		while (this.active) {
			try {
				showMessage();
				temp = (int) (Math.random()*35);
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}

	@Override
	public void messageFromSupervisor(A3JGMessage msg) {
		if(msg.getValueID().equals("temperature")){
			System.out.println(map);
			A3JGMessage mex = new A3JGMessage("temperature");
			mex.setContent(temp);
			sendMessageToSupervisor(mex);
		}
	}
	
	public String showMessage(){
		return map.toString();
	}


}
