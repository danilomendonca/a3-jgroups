package a3jg.roles;

import java.util.ArrayList;

import org.jgroups.Address;
import A3JGroups.A3JGMessage;
import A3JGroups.A3JGSupervisorRole;


public class BlueSupervisor extends A3JGSupervisorRole {
	
	public BlueSupervisor(int resourceCost) {
		super(resourceCost);
	}
	
	
	@Override
	public void run() {
		
	}

	@Override
	public void messageFromFollower(A3JGMessage msg) {
		ArrayList<Address> ad = new ArrayList<Address>();
		Content c = (Content) msg.getContent();
		ad.add(c.getAd());
		A3JGMessage mex = new A3JGMessage("BlueInfo");
		String s = c.getS() + System.currentTimeMillis() + ", ";
		c.setS(s);
		mex.setContent(c);
		sendMessageToFollower(mex, ad);
		System.out.println("Sending blue message");
	   
	}

	@Override
	public void updateFromFollower(A3JGMessage msg) {
		
	}

	@Override
	public int fitnessFunc() {
		return 1;
	}
}
