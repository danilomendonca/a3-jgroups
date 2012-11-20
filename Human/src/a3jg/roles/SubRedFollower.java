package a3jg.roles;

import A3JGroups.A3JGMessage;
import A3JGroups.A3JGFollowerRole;

public class SubRedFollower extends A3JGFollowerRole{
	
	
	public SubRedFollower(int resourceCost) {
		super(resourceCost);
		this.setElectionTime(1000);
		this.setMaxAttempt(3);
	}

	@Override
	public void run() {
		A3JGMessage mex = new A3JGMessage("info");
		Content c = new Content();
		c.setAd(this.getChan().getAddress());
		c.setS("SRF, "+System.currentTimeMillis()+", ");
		mex.setContent(c);
		sendMessageToSupervisor(mex);
	}

	@Override
	public void messageFromSupervisor(A3JGMessage msg) {
		Content c = (Content) msg.getContent();
		String s = c.getS() + System.currentTimeMillis() +", "+getChan().getView().getMembers().size();
		System.out.println(s);		
	}

}
