package testGreenhouse2;

import A3JGroups.A3JGFollowerRole;
import A3JGroups.A3JGMessage;
import A3JGroups.A3JGroup;

public class ServerFollowerRole extends A3JGFollowerRole {

	private Integer temperatureTreshold = null;

	public ServerFollowerRole(int resourceCost) {
		super(resourceCost);
		// TODO Auto-generated constructor stub
	}

	@Override
	public SensorNode getNode() {
		return (SensorNode) super.getNode();
	}

	@Override
	public void run() {

		if (getNode().getTemperatureTreshold() == 0) {

			System.out.println("[" + getNode().getID()
					+ "]: Richiedo la soglia al supervisor");
			sendMessageToSupervisor(new A3JGMessage(
					"TemperatureTresholdRequest", new Request(
							getNode().getID(), getChan().getAddress(), 1)));
		} else {
			temperatureTreshold = getNode().getTemperatureTreshold();
		}

		while (active) {
			if (temperatureTreshold != null) {
				int actualTemperature = getNode().getActualTemperature();
				System.out.println("[" + getNode().getID() + "]: Actual "
						+ actualTemperature);
				if (actualTemperature > temperatureTreshold) {

					System.out.println("[" + getNode().getID()
							+ "]: La temperatura rilevata di "
							+ actualTemperature
							+ " supera la soglia, lo comunico al supervisor");

					sendUpdateToSupervisor(new A3JGMessage("Temperature",
							new Update(getChan().getAddress(),
									actualTemperature)));
				}

			}

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}

		}

	}

	@Override
	public void messageFromSupervisor(A3JGMessage msg) {
		if (msg.getValueID().equals("TemperatureTreshold")) {
			temperatureTreshold = (Integer) msg.getContent();
			getNode().setTemperatureTreshold(temperatureTreshold);

			getNode().setTemperatureTreshold(temperatureTreshold);

			System.out.println("[" + getNode().getID()
					+ "]: Mi è stata inviata la soglia di "
					+ temperatureTreshold);

		} else if (msg.getValueID().equals("Alive")) {
			System.out.println("[" + getNode().getID()
					+ "]: Mi è arrivata la richiesta di Alive");
			sendMessageToSupervisor(new A3JGMessage("Alive", new Request(
					getNode().getID(), getChan().getAddress(), 1)));
			System.out.println("[" + getNode().getID()
					+ "]: Rispondo Alive e che rappresento un sensore");
		} else if (msg.getValueID().equals("CreateGroup")) {
			String groupName = getChan().getClusterName();
			getNode().terminate(groupName);
			System.out.println("[" + getNode().getID()
					+ "]: Mi disconnetto dal gruppo " + groupName);
			createSubgroup();
		} else if (msg.getValueID().equals("TerminateSubGroup")) {
			String groupName = getChan().getClusterName();
			getNode().terminate(groupName);
			System.out.println("[" + getNode().getID()
					+ "]: Mi disconnetto dal gruppo " + groupName);
			connectToServer();
		}

	}

	private void createSubgroup() {
		// Gruppo Sensori
		A3JGroup gruppoSensori = new A3JGroup(
				SensorSupervisorRole.class.getCanonicalName(),
				ServerFollowerRole.class.getCanonicalName());

		SensorNode node = getNode();
		node.addGroupInfo("Subgroup"/*
									 * Qui probabilmente ci andrà il nome della
									 * sottosezione da gestire
									 */, gruppoSensori);
		node.addSupervisorRole(new SensorSupervisorRole(1));
		node.addFollowerRole(new ServerFollowerRole(1));
		boolean connect = false;
		try {
			connect = node.joinGroup("Subgroup");
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (connect)
			System.out.println("[" + getNode().getID()
					+ "]: Mi sono connesso a Subgroup");
		else
			System.out.println("[" + getNode().getID()
					+ "]:  Non sono riuscito a connettermi a Subgroup");

	}

	private void connectToServer() {

		// mi riconnetto al gruppo del Server1
		boolean connect = false;
		try {
			connect = getNode().joinGroup("Server1");
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (connect)
			System.out.println("[" + getNode().getID()
					+ "]: Mi sono connesso al gruppo del Server1");
		else
			System.out
					.println("["
							+ getNode().getID()
							+ "]:  Non sono riuscito a connettermi al gruppo del Server1");

	}
}
