package testHospital;

import A3JGroups.A3JGFollowerRole;
import A3JGroups.A3JGMessage;

public class PatientFollowerRole extends A3JGFollowerRole {

	private Destination destination;

	public PatientFollowerRole(int resourceCost, Destination destination) {
		super(resourceCost);
		this.destination = destination;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageFromSupervisor(A3JGMessage msg) {
		// TODO Auto-generated method stub

	}

}
