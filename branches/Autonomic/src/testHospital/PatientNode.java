package testHospital;

import A3JGroups.A3JGNode;

public class PatientNode extends A3JGNode {

	private String patientGroup;
	private boolean connectedToScreen;
	private ScreenNode connectedScreen;
	private ScreenNode nearestScreen; // for tests

	public PatientNode(String ID, ScreenNode nearestScreen /* for tests */) {
		super(ID);
		this.nearestScreen = nearestScreen; // for tests
	}

	public boolean isConnectedToScreen() {
		return connectedToScreen;
	}

	public void setConnectedToScreen(boolean connectedToScreen) {
		this.connectedToScreen = connectedToScreen;
	}

	public ScreenNode getConnectedScreen() {
		return connectedScreen;
	}

	public void setConnectedScreen(ScreenNode connectedScreen) {
		this.connectedScreen = connectedScreen;
	}

	public String getPatientGroup() {
		return patientGroup;
	}

	public void setPatientGroup(String patientGroup) {
		this.patientGroup = patientGroup;
	}

	public ScreenNode getNearestScreen() {
		return nearestScreen;
	}

	public boolean goToDirection(Direction direction) {
		System.out.println("[" + getID() + "]: Mi dirigo nella direzione "
				+ direction);
		if (getActiveRole(patientGroup).equals(
				PatientSupervisorRole.class.getCanonicalName())) {
			((PatientSupervisorRole) getSupervisorRole(getActiveRole(patientGroup)))
					.tellOthers(direction);
		}
		return true;
	}

}
