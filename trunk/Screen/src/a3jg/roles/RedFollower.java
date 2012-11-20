package a3jg.roles;


import A3JGroups.A3JGMessage;
import A3JGroups.A3JGFollowerRole;

public class RedFollower extends A3JGFollowerRole{

	public RedFollower(int resourceCost) {
		super(resourceCost);
		this.setElectionTime(1000);
	}
	
	@Override
	public void run() {
		A3JGMessage mex = new A3JGMessage("info");
		Content c = new Content();
		c.setAd(this.getChan().getAddress());
		c.setS("RF, "+System.currentTimeMillis()+", ");
		mex.setContent(c);
		sendMessageToSupervisor(mex);
	}

	@Override
	public void messageFromSupervisor(A3JGMessage msg) {
		Content c = (Content) msg.getContent();
		String s = c.getS() + System.currentTimeMillis();
		System.out.println(s);
	}

	@Override
	public void toWrite(A3JGMessage msg) {
		// TODO Auto-generated method stub
		
	}
}
