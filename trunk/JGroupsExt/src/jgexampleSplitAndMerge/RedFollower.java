package jgexampleSplitAndMerge;

import A3JGroups.A3JGMessage;
import A3JGroups.JGFollowerRole;

public class RedFollower extends JGFollowerRole{

	private int temp;
	
	public RedFollower(int resourceCost) {
		super(resourceCost);
	}

	@Override
	public void run() {
		while (this.active) {
			temp = (int) (Math.random()*35);
			System.out.println(this.getNode().getID()+" "+temp+"   "+this.getChan().getClusterName());
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void messageFromSupervisor(A3JGMessage msg) {
		
	}
	
}
