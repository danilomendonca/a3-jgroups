package jgexample2;


import A3JGroups.A3JGMessage;
import A3JGroups.JGFollowerRole;

public class RedFollower extends JGFollowerRole{

	private int temp;
	public boolean prova = false;
	
	public RedFollower(int resourceCost) {
		super(resourceCost);
	}

	@Override
	public void run() {
		
		while (this.active) {
			
			temp = (int) (Math.random()*35);
			System.out.println(this.getNode().getID()+" "+temp);
			if(prova)
				showMessage();
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}

	@Override
	public void messageFromSupervisor(A3JGMessage msg) {
		if(msg.getValueID().equals("temperature")){
			A3JGMessage mex = new A3JGMessage("temperature");
			mex.setContent(temp);
			sendMessageToSupervisor(mex);
		}
	}
	
	public void showMessage(){
		System.out.println(map);
	}


}
