package a3jg.roles;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import A3JGroups.A3JGMessage;
import A3JGroups.A3JGFollowerRole;

public class YellowFollower extends A3JGFollowerRole{
	
	public YellowFollower(int resourceCost) {
		super(resourceCost);
		this.setElectionTime(1000);
	}
	
	@Override
	public void run() {
		A3JGMessage mex = new A3JGMessage("info");
		Content c = new Content();
		c.setAd(this.getChan().getAddress());
		c.setS("YF, "+System.currentTimeMillis()+", ");
		mex.setContent(c);
		sendMessageToSupervisor(mex);
	}

	@Override
	public void messageFromSupervisor(A3JGMessage msg) {
		Content c = (Content) msg.getContent();
		String s = c.getS() + System.currentTimeMillis();
		try {
			File file = new File("YF.txt");
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

	@Override
	public void toWrite(A3JGMessage msg) {
		// TODO Auto-generated method stub
		
	}

}
