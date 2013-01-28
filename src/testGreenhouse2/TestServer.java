package testGreenhouse2;

import java.util.logging.LogManager;

import A3JGroups.A3JGroup;

public class TestServer {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		LogManager.getLogManager().reset();

		// Gruppo Server-Sensori
		A3JGroup gruppoServerSensori = new A3JGroup(
				ServerSupervisorRole.class.getCanonicalName(),
				ServerFollowerRole.class.getCanonicalName());

		ServerNode server1 = new ServerNode("Server1");
		server1.addGroupInfo("Server1", gruppoServerSensori);
		server1.addSupervisorRole(new ServerSupervisorRole(1));
		server1.joinGroup("Server1");

	}

}
