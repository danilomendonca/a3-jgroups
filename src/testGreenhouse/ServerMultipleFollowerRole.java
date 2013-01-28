package testGreenhouse;

import A3JGroups.A3JGFollowerRole;
import A3JGroups.A3JGMessage;

public class ServerMultipleFollowerRole extends A3JGFollowerRole {

	private int representedSensors;

	public ServerMultipleFollowerRole(int resourceCost, int representedSensors) {
		super(resourceCost);
		this.representedSensors = representedSensors;
	}

	public int getRepresentedSensors() {
		return representedSensors;
	}

	public void setRepresentedSensors(int representedSensors) {
		this.representedSensors = representedSensors;
	}

	@Override
	public SensorNode getNode() {
		return (SensorNode) super.getNode();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageFromSupervisor(A3JGMessage msg) {

		if (msg.getValueID().equals("Alive")) {
			System.out.println("[" + getNode().getID()
					+ "]: Mi è arrivata la richiesta di Alive");
			sendMessageToSupervisor(new A3JGMessage("Alive", new Request(
					getNode().getID(), getChan().getAddress(),
					representedSensors)));
			System.out.println("[" + getNode().getID()
					+ "]: Rispondo Alive e che rappresento "
					+ representedSensors + " sensori");
		}

	}

	public void multipleUpdate(int representedSensors, int temperatureValue) {
		System.out.println("[" + getNode().getID()
				+ "]: La temperatura rilevata da " + representedSensors
				+ " di " + temperatureValue
				+ " supera la soglia, lo comunico al supervisor");

		sendUpdateToSupervisor(new A3JGMessage("Temperature", new Update(
				getChan().getAddress(), temperatureValue)));
	}

}
