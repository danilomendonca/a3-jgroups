package jgexampleSplitAndMerge;

import org.jgroups.View;

import A3JGroups.A3JGMessage;
import A3JGroups.A3JGSupervisorRole;

public class RedSupervisor extends A3JGSupervisorRole {

	private int fitness = 2;
	private View vista;
	
	public RedSupervisor(int resourceCost) {
		super(resourceCost);
	}

	@Override
	public void run() {
		int i = 0;
		while (this.active) {
			
			vista = this.node.getChannels("red").getView();
			System.out.println("["+this.getNode().getID()+"] I'm the supervisor, my follower are " + (vista.getMembers().size()-1));
		
			try {
				Thread.sleep(2000);

			} catch (Exception e) {
				e.printStackTrace();
			}
			i++;
			if(i==3 && this.getNode().getID().equals("red1")){
				System.out.println("splitto");
				this.split();
			}
			if(i==3 && this.getNode().getID().equals("red3")){
				System.out.println("mergo");
				this.merge();
			}
			
			
		}
	}

	@Override
	public void messageFromFollower(A3JGMessage msg) {
		
	}

	@Override
	public void updateFromFollower(A3JGMessage msg) {
		
	}

	@Override
	public int fitnessFunc() {
		return fitness;
	}
	

}
