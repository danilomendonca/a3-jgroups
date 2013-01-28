package testGreenhouse;

import java.util.HashMap;
import java.util.List;

import org.jgroups.Address;

import A3JGroups.A3JGMessage;
import A3JGroups.A3JGSupervisorRole;
import A3JGroups.A3JGroup;

public class SensorSupervisorRole extends A3JGSupervisorRole {

	private HashMap<Address, Boolean> listaSensori;
	private ServerMultipleFollowerRole serverFollower;

	public SensorSupervisorRole(int resourceCost) {
		super(resourceCost);
		listaSensori = new HashMap<Address, Boolean>();
	}

	@Override
	public SensorNode getNode() {
		return (SensorNode) super.getNode();
	}

	@Override
	public void run() {
		System.out.println("[" + getNode().getID()
				+ "]: Inizio il mio ruolo di sensor supervisor");

		boolean connected = false;

		// Gruppo Server-SensoriMultipli
		A3JGroup gruppoServerSensori = new A3JGroup(
				ServerSupervisorRole.class.getCanonicalName(),
				ServerMultipleFollowerRole.class.getCanonicalName());
		SensorNode node = getNode();
		node.addGroupInfo("Server1", gruppoServerSensori);
		serverFollower = new ServerMultipleFollowerRole(1, 1);
		node.addFollowerRole(serverFollower);

		while (!connected) {
			System.out.println("[" + getNode().getID()
					+ "]: Provo a connettermi al Server1");

			try {
				connected = node.joinGroup("Server1");
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (connected)
				System.out.println("[" + getNode().getID()
						+ "]: Connessione al Server1 riuscita");
			else
				System.out.println("[" + getNode().getID()
						+ "]: Connessione al Server1 non riuscita");
		}

		while (active) {

			// TODO anche lui dovrebbe continuare a controllare la temperatura
			// del suo nodo
			int sensorsNumber = listaSensori.size();
			int alertsNumber = calcolaAlertTotali();

			System.out.println("[" + getNode().getID() + "]: Risultano attivi "
					+ sensorsNumber + " sensori");
			System.out.println("[" + getNode().getID() + "]: Ci sono stati "
					+ alertsNumber + " alerts.");
			serverFollower.setRepresentedSensors(sensorsNumber);

			if (sensorsNumber > 0)
				System.out.println("[" + getNode().getID()
						+ "]: Percentuale del " + alertsNumber / sensorsNumber
						* 100 + "%");

			if (sensorsNumber > 0 && alertsNumber / sensorsNumber > 0.5) {
				// TODO non ho considerato che andrebbe meglio calcolare la
				// media, per ora invio la soglia + 1 vista come valore medio da
				// tutti i sensori (poi potrei fare la media ed inviarla solo se
				// supera la trheshold)
				serverFollower.multipleUpdate(sensorsNumber, getNode()
						.getTemperatureTreshold() + 1);
			}

			inizializzaListaSensori();
			try {
				Thread.sleep(6000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

	}

	@Override
	public void messageFromFollower(A3JGMessage msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateFromFollower(A3JGMessage msg) {

		if (msg.getValueID().equals("Temperature")) {

			int temperature = ((Update) msg.getContent()).getTemperature();

			System.out.println("[" + getNode().getID()
					+ "]: Mi è stata comunicata una temperatura di "
					+ temperature);
			boolean alert = temperature > getNode().getTemperatureTreshold();

			listaSensori.put(((Update) msg.getContent()).getAddress(), alert);

		}

	}

	@Override
	public int fitnessFunc() {
		// TODO Auto-generated method stub
		return 0;
	}

	private void inizializzaListaSensori() {
		List<Address> addrs = getChan().getView().getMembers();
		for (Address addr : addrs) {
			if (!addr.equals(getChan().getAddress()))
				listaSensori.put(addr, false);
		}
	}

	private int calcolaAlertTotali() {
		int sum = 0;
		for (boolean alert : listaSensori.values()) {
			if (alert)
				sum++;
		}

		return sum;
	}

}
