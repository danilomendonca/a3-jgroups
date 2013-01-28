package testGreenhouse;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jgroups.Address;

import A3JGroups.A3JGMessage;
import A3JGroups.autonomic.AutonomicJGSupervisorRole;

public class ServerSupervisorRole extends AutonomicJGSupervisorRole {

	private Map<Address, Integer> listaSensori;
	private int alertCounter;
	private boolean congestion = false;

	public ServerSupervisorRole(int resourceCost) {
		super(resourceCost, "ServerSupervisorRole.drl");
		listaSensori = new HashMap<Address, Integer>();
	}

	@Override
	public ServerNode getNode() {
		return (ServerNode) super.getNode();
	}

	@Override
	public void run() {
		super.run();
		// volendo si può verificare che non esista un backup di una
		// listasensori di un supervisor che ha avuto una failure
		System.out.println("[" + getNode().getID()
				+ "]: Inizio il mio ruolo di server supervisor");

		while (active) {
			System.out.println("[" + getNode().getID() + "]: Risultano attivi "
					+ calcolaSensoriTotali() + " sensori");
			listaSensori.clear();
			sendMessageToFollower(new A3JGMessage("Alive"), null);
			try {
				Thread.sleep(6000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

	}

	@Override
	public void messageFromFollower(A3JGMessage msg) {

		if (msg.getValueID().equals("TemperatureTresholdRequest")) {

			System.out.println("[" + getNode().getID()
					+ "]: Mi è stata chiesta la soglia");

			Request request = (Request) msg.getContent();

			listaSensori.put(request.getSrc(),
					request.getRepresentedNodesNumber());

			A3JGMessage respMsg = new A3JGMessage("TemperatureTreshold");
			respMsg.setContent(getNode().getTemperatureTreshold());
			sendMessageToFollower(respMsg,
					Collections.singletonList(request.getSrc()));
		} else if (msg.getValueID().equals("Alive")) {

			Request request = (Request) msg.getContent();

			listaSensori.put(request.getSrc(),
					request.getRepresentedNodesNumber());
		}

	}

	@Override
	public void updateFromFollower(A3JGMessage msg) {

		if (msg.getValueID().equals("Temperature")) {
			System.out.println("[" + getNode().getID()
					+ "]: Mi è stata comunicata una temperatura di "
					+ ((Update) msg.getContent()).getTemperature());
			alertCounter++;
		}

	}

	@Override
	public int fitnessFunc() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean Monitor() {

		synchronized (this) {

			if (alertCounter > 1)
				congestion = true;
			else
				congestion = false;

			alertCounter = 0;
		}

		return true;

	}

	@Override
	public boolean Analyse() {

		if (congestion) {
			System.out.println("[" + getNode().getID()
					+ "]: Sono congestionato");
		} else
			System.out.println("[" + getNode().getID()
					+ "]: Non sono congestionato");

		return true;

	}

	@Override
	public boolean Plan() {
		// TODO Auto-generated method stub
		return true;

	}

	@Override
	public boolean Execute() {

		if (congestion) {
			// in questo caso non tengo conto delle sottozone, per farlo forse
			// dovrei memorizzare in listaSensori per ogni sensore più
			// informazioni si di lui (fare un oggetto SensorInfo, inviato dai
			// sensori e memorizzato???)
			sendMessageToFollower(new A3JGMessage("CreateGroup"), null);
		}

		return true;

	}

	private int calcolaSensoriTotali() {
		int sum = 0;
		for (Address addr : listaSensori.keySet()) {
			sum += listaSensori.get(addr);
		}

		return sum;
	}

}
