package a3Solution;

import org.jgroups.Message;

import utilities.threading.ThreadManager;
import A3JGroups.A3JGMessage;

public abstract class AutonomicJGSingleThreadedFollowerRole extends
		A3JGSingleThreadedFollowerRole {

	private ThreadManager tm;

	public AutonomicJGSingleThreadedFollowerRole(int resourceCost,
			ThreadManager tm) {
		super(resourceCost, tm);
		this.tm = tm;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public void receive(Message mex) {
		A3JGMessage msg = (A3JGMessage) mex.getObject();
		if (msg.getValueID().startsWith("MAPEMessage")) {
			msg.setValueID(msg.getValueID().replaceFirst("MAPEMessage", ""));
			MAPEMessage(msg);
		} else {
			super.receive(mex);
		}
	}

	public abstract void MAPEMessage(A3JGMessage msg);

}
