package testHospital;

import A3JGroups.A3JGroup;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		// Tipi di gruppi:

		// Gruppo server-schermi
		A3JGroup groupServer = new A3JGroup(
				ServerSupervisorRole.class.getCanonicalName(),
				ServerFollowerRole.class.getCanonicalName());

		// Gruppo schermo-paziente
		A3JGroup groupScreen = new A3JGroup(
				ScreenSupervisorRole.class.getCanonicalName(),
				ScreenFollowerRole.class.getCanonicalName());

		// Gruppo pazienti
		A3JGroup groupPatient = new A3JGroup(
				PatientSupervisorRole.class.getCanonicalName(),
				PatientFollowerRole.class.getCanonicalName());

		ServerNode server = new ServerNode("Server");
		server.addGroupInfo("Server", groupServer);
		server.addSupervisorRole(new ServerSupervisorRole(1));
		server.joinGroup("Server");

		Thread.sleep(2000);

		ScreenNode screen1 = new ScreenNode("Screen1");
		screen1.addGroupInfo("Server", groupServer);
		screen1.addFollowerRole(new ServerFollowerRole(1));
		screen1.joinGroup("Server");
		screen1.addGroupInfo("Screen1", groupScreen);
		screen1.addSupervisorRole(new ScreenSupervisorRole(1));
		screen1.joinGroup("Screen1");

		Thread.sleep(2000);

		PatientNode patient1 = new PatientNode("Patient1", screen1);
		patient1.addGroupInfo("Red1", groupPatient);
		patient1.addSupervisorRole(new PatientSupervisorRole(1, Destination.RED));
		if (patient1.joinGroup("Red1"))
			patient1.setPatientGroup("Red1");

	}
}
