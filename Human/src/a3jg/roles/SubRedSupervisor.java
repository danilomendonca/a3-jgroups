package a3jg.roles;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import org.jgroups.Address;

import a3jg.human.HumanNode;

import A3JGroups.A3JGMessage;
import A3JGroups.A3JGSupervisorRole;

public class SubRedSupervisor extends A3JGSupervisorRole{
	
	public boolean vero = true;
	public SubRedSupervisor(int resourceCost) {
		super(resourceCost);
	}
	
	@Override
	public void run() {
		
		String s = System.currentTimeMillis() + " I'm the Supervisor: "+ this.getNode().getID();
		System.out.println(s);
		
		try {
			File file = new File("Super.txt");
			FileOutputStream fos = new FileOutputStream(file, true);
			PrintStream out = new PrintStream(fos);
			out.println(s);
			out.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			this.getNode().joinGroup("Red");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
	}

	@Override
	public void messageFromFollower(A3JGMessage msg) {
		ArrayList<Address> ad = new ArrayList<Address>();
		Content c = (Content) msg.getContent();
		ad.add(c.getAd());
		A3JGMessage mex = new A3JGMessage("SubRedInfo");
		String s = c.getS() + System.currentTimeMillis() + ", ";
		c.setS(s);
		mex.setContent(c);
		sendMessageToFollower(mex, ad);

		System.out.println("Sending subred message");
	}

	@Override
	public void updateFromFollower(A3JGMessage msg) {
	}

	@Override
	public int fitnessFunc() {
		long time = System.currentTimeMillis();
		long tempo = time - ((HumanNode) this.getNode()).getStart();
		int fit =  (int) (((HumanNode) this.getNode()).getAtime() - tempo);
//		int fit = 1 + ((int) (Math.random()*30));
		return fit;
	}

}
