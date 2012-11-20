package a3jg.roles;

import java.util.ArrayList;

import org.jgroups.Address;

import a3jg.human.HumanNode;

import A3JGroups.A3JGMessage;
import A3JGroups.A3JGSupervisorRole;

public class SubBlueSupervisor extends A3JGSupervisorRole{
	
	public SubBlueSupervisor(int resourceCost) {
		super(resourceCost);
	}
	
	@Override
	public void run() {
		try {
			this.getNode().joinGroup("Blue");
			
			int awake = ((HumanNode) this.getNode()).getAtime();
			((HumanNode) this.getNode()).setStart(System.currentTimeMillis());
			
			Thread.sleep(awake);
			
			((HumanNode) this.getNode()).setAwake(false);
			this.getNode().terminate("Blue");
			this.getNode().terminate("SubBlue");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void messageFromFollower(A3JGMessage msg) {
		ArrayList<Address> ad = new ArrayList<Address>();
		Content c = (Content) msg.getContent();
		ad.add(c.getAd());
		A3JGMessage mex = new A3JGMessage("SubBlueInfo");
		String s = c.getS() + System.currentTimeMillis() + ", ";
		c.setS(s);
		mex.setContent(c);
		sendMessageToFollower(mex, ad);
		
		System.out.println("Sending subblue message");
	}

	@Override
	public void updateFromFollower(A3JGMessage msg) {
	}

	@Override
	public int fitnessFunc() {
		int fit =  (int) (((HumanNode) this.getNode()).getAtime() + ((HumanNode) this.getNode()).getStart());
		return fit;
	}

}

