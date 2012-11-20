package a3jg.roles;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import a3jg.human.HumanNode;
import A3JGroups.A3JGMessage;
import A3JGroups.A3JGFollowerRole;

public class SubYellowFollower extends A3JGFollowerRole{
	
	public SubYellowFollower(int resourceCost) {
		super(resourceCost);
		this.setElectionTime(1000);
	}

	@Override
	public void run() {
		A3JGMessage mex = new A3JGMessage("info");
		Content c = new Content();
		c.setAd(this.getChan().getAddress());
		c.setS("SYF, "+System.currentTimeMillis()+", ");
		mex.setContent(c);
		sendMessageToSupervisor(mex);
		
		int awake = ((HumanNode) this.getNode()).getAtime();
		((HumanNode) this.getNode()).setStart(System.currentTimeMillis());
		
		try {
			Thread.sleep(awake);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		((HumanNode) this.getNode()).setAwake(false);
		this.getNode().terminate("SubYellow");
	}

	@Override
	public void messageFromSupervisor(A3JGMessage msg) {
		Content c = (Content) msg.getContent();
		String s = c.getS() + System.currentTimeMillis() +", "+getChan().getView().getMembers().size();
		try {
			File file = new File("SYF.txt");
			FileOutputStream fos = new FileOutputStream(file, true);
			PrintStream out = new PrintStream(fos);
			out.println(s);
			out.close();
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



}

