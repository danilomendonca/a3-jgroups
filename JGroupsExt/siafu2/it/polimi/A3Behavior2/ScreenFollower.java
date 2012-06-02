package it.polimi.A3Behavior2;

import A3JGroups.A3JGMessage;
import A3JGroups.JGFollowerRole;

public class ScreenFollower extends JGFollowerRole {

	private int index;
	
	public ScreenFollower(int resourceCost, String groupName, int index) {
		super(resourceCost, groupName);
		this.index = index;
	}

	@Override
	public void run() {
		Content c1 = new Content(this.getChan().getAddress(), index, "red");
		Content c2 = new Content(this.getChan().getAddress(), index, "green");
		Content c3 = new Content(this.getChan().getAddress(), index, "yellow");
		Content c4 = new Content(this.getChan().getAddress(), index, "blue");
		A3JGMessage mex = new A3JGMessage();
		mex.setContent(c1);
		sendMessageToSupervisor(mex);
		mex.setContent(c2);
		sendMessageToSupervisor(mex);
		mex.setContent(c3);
		sendMessageToSupervisor(mex);
		mex.setContent(c4);
		sendMessageToSupervisor(mex);
		
		while(this.active){
		//se ci son cambiamenti in zona chiedere al supervisore	
		}
	}

	@Override
	public void messageFromSupervisor(A3JGMessage msg) {
		Content c = ((Content) msg.getContent());
		if(c.getColour().equals("red")){
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
