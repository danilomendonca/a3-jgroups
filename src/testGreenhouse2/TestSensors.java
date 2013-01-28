package testGreenhouse2;

import java.util.logging.LogManager;

import A3JGroups.A3JGroup;

public class TestSensors {

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

		SensorNode sensor1 = new SensorNode("Sensor1");
		sensor1.addGroupInfo("Server1", gruppoServerSensori);
		sensor1.addFollowerRole(new ServerFollowerRole(1));
		sensor1.joinGroup("Server1");

		// Thread.sleep(5000);

		SensorNode sensor2 = new SensorNode("Sensor2");
		sensor2.addGroupInfo("Server1", gruppoServerSensori);
		sensor2.addFollowerRole(new ServerFollowerRole(1));
		sensor2.joinGroup("Server1");

		SensorNode sensor3 = new SensorNode("Sensor3");
		sensor3.addGroupInfo("Server1", gruppoServerSensori);
		sensor3.addFollowerRole(new ServerFollowerRole(1));
		sensor3.joinGroup("Server1");

	}

}
