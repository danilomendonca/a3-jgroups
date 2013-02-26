package A3JGroups.autonomic;

import org.jgroups.Message;

import A3JGroups.A3JGFollowerRole;
import A3JGroups.A3JGMessage;

public abstract class AutonomicJGFollowerRole extends A3JGFollowerRole {

	public AutonomicJGFollowerRole(int resourceCost) {
		super(resourceCost);
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
