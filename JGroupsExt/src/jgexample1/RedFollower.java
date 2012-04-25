package jgexample1;



import A3JGroups.A3JGMessage;
import A3JGroups.JGFollowerRole;

public class RedFollower extends JGFollowerRole{

	private int temp;
	
	public RedFollower(int resourceCost, String groupName) {
		super(resourceCost, groupName);
	}

	@Override
	public void run() {
		
		while (this.active) {
			
			temp = (int) (Math.random()*35);
			System.out.println(this.getNode().getID()+" "+temp);
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}

	@Override
	public void messageFromSupervisor(A3JGMessage msg) {
		System.out.println(msg.getObject());
		if(msg.getObject().equals("temperature")){
			A3JGMessage mex = new A3JGMessage();
			mex.setObject(temp);
			sendMessageToSupervisor(mex);
		}
	}


}
