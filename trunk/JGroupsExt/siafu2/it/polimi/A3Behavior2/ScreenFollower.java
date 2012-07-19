package it.polimi.A3Behavior2;

import A3JGroups.A3JGMessage;
import A3JGroups.JGFollowerRole;

public class ScreenFollower extends JGFollowerRole {

	private int index;
	private boolean obs = false;
	private boolean flag = false;
	
	public ScreenFollower(int resourceCost, int index) {
		super(resourceCost);
		this.index = index;
	}

	@Override
	public void run() {
		Content c1 = new Content(this.getChan().getAddress(), index, "red");
		Content c2 = new Content(this.getChan().getAddress(), index, "green");
		Content c3 = new Content(this.getChan().getAddress(), index, "yellow");
		Content c4 = new Content(this.getChan().getAddress(), index, "blue");
		A3JGMessage mex = new A3JGMessage("info");
		mex.setContent(c1);
		sendMessageToSupervisor(mex);
		mex.setContent(c2);
		sendMessageToSupervisor(mex);
		mex.setContent(c3);
		sendMessageToSupervisor(mex);
		mex.setContent(c4);
		sendMessageToSupervisor(mex);
		
		
		while(this.active){
			
			if (((MixedNode) this.getNode()).isObstacle() && !obs) {
				A3JGMessage mes = new A3JGMessage("Obstacle on");
				sendUpdateToSupervisor(mes);
				obs = true;
				flag = true;
			}
			if (!((MixedNode) this.getNode()).isObstacle() && obs && flag) {
				A3JGMessage mes = new A3JGMessage("Obstacle off");
				sendUpdateToSupervisor(mes);
				obs = false;
			}
		}
	}

	@Override
	public void messageFromSupervisor(A3JGMessage msg) {
		Content c = ((Content) msg.getContent());
		if(c.getColour().equals("black")){
			Content c1 = new Content(this.getChan().getAddress(), index, "red");
			Content c2 = new Content(this.getChan().getAddress(), index, "green");
			Content c3 = new Content(this.getChan().getAddress(), index, "yellow");
			Content c4 = new Content(this.getChan().getAddress(), index, "blue");
			A3JGMessage mex = new A3JGMessage("info");
			mex.setContent(c1);
			sendMessageToSupervisor(mex);
			mex.setContent(c2);
			sendMessageToSupervisor(mex);
			mex.setContent(c3);
			sendMessageToSupervisor(mex);
			mex.setContent(c4);
			sendMessageToSupervisor(mex);
		}else if(c.getColour().equals("red")){
			((MixedNode) this.getNode()).setRed(c.getPos());
			((MixedNode) this.getNode()).setRedDir(c.getDirection());
		}else if(c.getColour().equals("green")){
			((MixedNode) this.getNode()).setGreen(c.getPos());
			((MixedNode) this.getNode()).setGreenDir(c.getDirection());
		}else if(c.getColour().equals("blue")){
			((MixedNode) this.getNode()).setBlue(c.getPos());
			((MixedNode) this.getNode()).setBlueDir(c.getDirection());
		}else if(c.getColour().equals("yellow")){
			((MixedNode) this.getNode()).setYellow(c.getPos());
			((MixedNode) this.getNode()).setYellowDir(c.getDirection());
		}
		
	}

	

	

}
