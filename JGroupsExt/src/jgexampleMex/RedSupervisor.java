package jgexampleMex;

import java.util.ArrayList;

import org.jgroups.View;

import A3JGroups.A3JGMessage;
import A3JGroups.JGSupervisorRole;

public class RedSupervisor extends JGSupervisorRole {

	
	private int fitness = 2;
	private ArrayList<Integer> temp = new ArrayList<Integer>();
	private View vista;
	private int i = 0;
	private int[] min = {1,2,0,3,2,4,5,2};
	
	public RedSupervisor(int resourceCost) {
		super(resourceCost);
	}

	public void setFitness(int fitness) {
		this.fitness = fitness;
	}

	@Override
	public void run() {
		
		while (this.active) {
			vista = this.node.getChannels("red").getView();
			try {
				Thread.sleep(2000);

			} catch (Exception e) {
				e.printStackTrace();
			}
			if (i < 8) {
				A3JGMessage msg = new A3JGMessage("temperature");
				sendMessageOverTime(msg, null, 0, 0, min[i]);
				System.out.println("[" + this.getNode().getID() + "] Sending message to followers...  " + i);
				i++;
			}
		}
	}

	@Override
	public void messageFromFollower(A3JGMessage msg) {
		System.out.println(map);
		temp.add((Integer) msg.getContent());
		if(temp.size()==(vista.getMembers().size()-1)){
			int avarage = 0;
			for(int i=0;i<(vista.getMembers().size()-1);i++){
				avarage += temp.get(i);
			}
			avarage = (avarage/(vista.getMembers().size()-1));
			temp = new ArrayList<Integer>();
			
		}
	}

	@Override
	public void updateFromFollower(A3JGMessage msg) {
		
	}

	@Override
	public int fitnessFunc() {
		return fitness;
	}


}
