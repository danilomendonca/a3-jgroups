package jgexample3;

import java.util.ArrayList;

import A3JGroups.A3JGMessage;
import A3JGroups.JGSupervisorRole;

public class RedSupervisor extends JGSupervisorRole {

	private ArrayList<Integer> temp = new ArrayList<Integer>();
	private int groupSize = 0;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (this.active) {
			//Add behavioral code...
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			A3JGMessage msg = new A3JGMessage("temperature");
			sendMessageToFollower(msg);
			System.out.println("["+this.getNodeID()+"] Sending message to red followers...  " + groupSize);
			
			
		}
	}

	@Override
	public void messageFromFollower(A3JGMessage msg) {
		temp.add((Integer) msg.getContent());
		if(temp.size()==groupSize){
			int avarage = 0;
			for(int i=0;i<groupSize;i++){
				avarage += temp.get(i);
			}
			avarage = (avarage/groupSize);
			temp = new ArrayList<Integer>();
			System.out.println("["+this.getNodeID()+"] The average temperature is " + avarage);
		}		
	}

	@Override
	public void updateFromFollower(A3JGMessage msg) {
		if(msg.getContent().equals("join"))
			groupSize++;
		else
			groupSize--;
	}

}
