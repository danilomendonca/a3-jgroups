package jgexample1;

import java.util.ArrayList;

import org.jgroups.View;

import A3JGroups.A3JGMessage;
import A3JGroups.JGSupervisorRole;

public class RedSupervisor extends JGSupervisorRole {

	private ArrayList<Integer> temp = new ArrayList<Integer>();
	private int groupSize = 0;
	
	@Override
	public void run() {
		
		while (this.active) {
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			View vista = this.getChan().getView();
			System.out.println(vista.getMembers());
			/*
			A3JGMessage msg = new A3JGMessage("temperature");
			sendMessageToFollower(msg);
			System.out.println("["+this.getNodeID()+"] Sending message to followers...  " + groupSize);
			*/
			
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
			System.out.println(this.getNodeID()+" The average temperature is " + avarage);
		}		
	}

	@Override
	public void updateFromFollower(A3JGMessage msg) {
		System.out.println(this.getNodeID()+"  has recived update from someone");
		if(msg.getContent().equals("join"))
			groupSize++;
		else
			groupSize--;
	}

	@Override
	public int fitnessFunc() {
		// TODO Auto-generated method stub
		return 0;
	}

}
