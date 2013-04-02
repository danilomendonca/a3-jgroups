package testKB;

import A3JGroups.A3JGMessage;
import A3JGroups.autonomic.AutonomicJGSupervisorRole;

public class TestKBSupervisor extends AutonomicJGSupervisorRole {

	public TestKBSupervisor(int resourceCost, String rulesFilename) {
		super(resourceCost, rulesFilename);
	}

	@Override
	public boolean Monitor() {
		System.out.println("Monitor");
		return true;
	}

	@Override
	public boolean Analyse() {
		System.out.println("Analyse");
		return true;
	}

	@Override
	public boolean Plan() {
		System.out.println("Plan");
		return true;
	}

	@Override
	public boolean Execute() {
		System.out.println("Execute");
		// insertOrUpdateKB(new PeriodicLoop());
		return true;
	}

	@Override
	public void messageFromFollower(A3JGMessage msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateFromFollower(A3JGMessage msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public int fitnessFunc() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void run() {
		insertOrUpdateKB(new PeriodicLoop());
	}

}
