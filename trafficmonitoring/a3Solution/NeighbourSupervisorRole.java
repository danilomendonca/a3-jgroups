package a3Solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgroups.Address;
import org.jgroups.View;

import utilities.threading.ThreadManager;
import A3JGroups.A3JGMessage;
import a3Solution.KB.NewView;

public class NeighbourSupervisorRole extends
		AutonomicJGSingleThreadedSupervisorRole {

	private boolean firstRun;
	private boolean trafficJam;
	private View lastView;
	private ThreadManager tm;

	private Map<Integer, NodeInfo> listaFollowerTrafficJam;
	private List<Integer> listaGroupNeighbour;

	public NeighbourSupervisorRole(int resourceCost, ThreadManager tm) {
		super(resourceCost, "a3Solution/NeighbourSupervisorRole.drl", tm);
		firstRun = true;
		this.tm = tm;
		listaFollowerTrafficJam = new HashMap<Integer, NodeInfo>();
		listaGroupNeighbour = new ArrayList<Integer>();
	}

	@Override
	public void run() {
		// è un unico step

		if (active) {
			if (firstRun) {
				System.out
						.println("["
								+ getNode().getID()
								+ ":Supervisor]: Inizio il mio ruolo di Neighbour Supervisor");
				firstRun = false;
				((A3CameraNode) getNode()).setSupervisorIn(Integer
						.parseInt(getChan().getClusterName()));

				for (String s : ((A3CameraNode) getNode()).getNeighburs()) {
					listaGroupNeighbour.add(Integer.parseInt(s));
				}

				System.out
						.print("["
								+ getNode().getID()
								+ ":Supervisor]: Gli attuali vicini del mio gruppo sono:");
				for (Integer i : listaGroupNeighbour) {
					System.out.print("[" + getNode().getID() + ":Supervisor]: "
							+ i + " ");
				}

				System.out.println();

			}

			// System.out.println("[" + getNode().getID()
			// + "]: Neighbour Supervisor Step");

			double density = ((A3CameraNode) getNode()).getDensity();
			double avgSpeed = ((A3CameraNode) getNode()).getAvgVelocity();

			if (avgSpeed < 0.8 && density != 0) {
				// System.out.println("[" + getNode().getID()
				// + ":Supervisor]: Vedo un ingorgo");
				trafficJam = true;
			} else {
				trafficJam = false;
			}

		}

	}

	@Override
	public void messageFromFollower(A3JGMessage msg) {

		if (msg.getValueID().equals("TrafficJam")) {

			// System.out.println("[" + getNode().getID() + ":Supervisor]: "
			// + msg.getContent() + " vede traffico");

			int followerId = Integer.parseInt(((String) msg.getContent()));

			NodeInfo info = listaFollowerTrafficJam.get(followerId);
			info.trafficJam = true;

			listaFollowerTrafficJam.put(
					Integer.parseInt(((String) msg.getContent())), info);

			if (!(calculateTrafficJam() && trafficJam))
				return;

			if (((A3CameraNode) getNode()).getFollowerIn() > 0) {
				// dire al follower di staccarsi da questo gruppo e di
				// aggregarsi al gruppo di cui si è follower

				System.out
						.println("["
								+ getNode().getID()
								+ ":Supervisor]: Vedo traffico e tutti i miei follower vedono traffico");

				if (sendMessageToFollower(new A3JGMessage("AggregateID"
						+ ((A3CameraNode) getNode()).getFollowerIn()), null)) {
					System.out
							.println("["
									+ getNode().getID()
									+ ":Supervisor]: Dico ai miei followers di collegarsi a "
									+ ((A3CameraNode) getNode())
											.getFollowerIn());
				} else {
					System.out
							.println("["
									+ getNode().getID()
									+ ":Supervisor]: Invio messaggio di collegamento a "
									+ ((A3CameraNode) getNode())
											.getFollowerIn() + " non riuscito");
				}

				String groupName = getChan().getClusterName();
				System.out.println("[" + getNode().getID()
						+ ":Supervisor]: Sto per disconnettermi dal gruppo "
						+ groupName);

				getNode().terminate(groupName);
				((A3CameraNode) getNode()).setSupervisorIn(0);
				System.out.println("[" + getNode().getID()
						+ ":Supervisor]: Mi sono disconnesso dal gruppo "
						+ groupName);
			}
		} else if (msg.getValueID().equals("Joining")) {

			Request req = (Request) msg.getContent();

			int id = Integer.parseInt(req.getNodeID());
			System.out.println("[" + getNode().getID()
					+ ":Supervisor]: Il nodo " + id + " si è unito al gruppo");
			listaFollowerTrafficJam.put(id, new NodeInfo(req.getSrc(), false));
			listaGroupNeighbour.remove(new Integer(id));

			for (String n : req.getPhysicalNeighbour()) {
				if (listaFollowerTrafficJam.keySet().contains(
						Integer.parseInt(n))
						|| n.equals(getNode().getID())) {
					System.out.println("[" + getNode().getID()
							+ ":Supervisor]: Il suo vicino " + n
							+ " fa già parte del nostro gruppo");
				} else {
					System.out
							.println("["
									+ getNode().getID()
									+ ":Supervisor]: Il suo vicino "
									+ n
									+ " non fa parte del nostro gruppo, lo aggiungo alla lista dei nostri vicini");
					listaGroupNeighbour.add(Integer.parseInt(n));
				}
			}

			System.out.print("[" + getNode().getID()
					+ ":Supervisor]: Gli attuali vicini del mio gruppo sono:");
			for (Integer i : listaGroupNeighbour) {
				System.out.print("[" + getNode().getID() + ":Supervisor]: " + i
						+ " ");
			}
			System.out.println();

		} else if (msg.getValueID().equals("Exiting")) {
			int id = Integer.parseInt((String) msg.getContent());
			System.out.println("[" + getNode().getID()
					+ ":Supervisor]: Il nodo " + id + " è uscito dal gruppo");
			listaFollowerTrafficJam.remove(id);
		}

	}

	@Override
	public void viewAccepted(View view) {

		lastView = view;

		insertOrUpdateKB(new NewView());
		executeRules();

		// -------------------------------------------- FUNZIONANTE
		// -----------------------------

		// TODO qui si accorge della failure di un nodo se questo esce dalla
		// view mentre è presente nella lista dei follower
		// devo tenere traccia dei follower che hanno come vicini nodi che non
		// sono in questo gruppo
		// quando questi falliscono devo dire a dei nodi di questo gruppo di
		// riunirmi con loro

		// for (Integer i : listaFollowerTrafficJam.keySet()) {
		//
		// NodeInfo info = listaFollowerTrafficJam.get(i);
		//
		// if (!view.getMembers().contains(info.addr)) {
		// System.out
		// .println("["
		// + getNode().getID()
		// + ":Supervisor]: Il nodo "
		// + i
		// +
		// " che avevo nella mia lista follower non risulta più nel gruppo, assumo che abbia avuto una failure");
		//
		// if (i.equals(Collections.max(listaFollowerTrafficJam.keySet()))) {
		//
		// System.out
		// .println("["
		// + getNode().getID()
		// + ":Supervisor]: Il nodo "
		// + i
		// + " era un nodo che collegava il gruppo ad un vicino");
		//
		// listaFollowerTrafficJam.remove(i);
		// Integer substitute = Collections
		// .max(listaFollowerTrafficJam.keySet());
		// if (!getNode().getID().equals(substitute + "")) {
		// Address substituteAddress = listaFollowerTrafficJam
		// .get(substitute).addr;
		// System.out.println("[" + getNode().getID()
		// + ":Supervisor]: Dico al nodo " + substitute
		// + " di sostituire il nodo " + i);
		// sendMessageToFollower(new A3JGMessage(
		// "ReplaceSupervisorID" + i),
		// Collections.singletonList(substituteAddress));
		// }
		// }
		//
		// }
		// }

		// -------------------------------------------- FINE FUNZIONANTE
		// -----------------------------

	}

	@Override
	public void updateFromFollower(A3JGMessage msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public int fitnessFunc() {
		return -(Integer.parseInt(getNode().getID()));
	}

	@Override
	public boolean Monitor() {
		System.out.println("[" + getNode().getID()
				+ ":Supervisor]: Fase Monitor ");
		return true;
	}

	@Override
	public boolean Analyse() {

		System.out.println("[" + getNode().getID()
				+ ":Supervisor]: Fase Analyse ");

		for (Integer i : listaFollowerTrafficJam.keySet()) {

			NodeInfo info = listaFollowerTrafficJam.get(i);

			if (!lastView.getMembers().contains(info.addr)) {
				System.out
						.println("["
								+ getNode().getID()
								+ ":Supervisor]: Il nodo "
								+ i
								+ " che avevo nella mia lista follower non risulta più nel gruppo, assumo che abbia avuto una failure");

				if (i.equals(Collections.max(listaFollowerTrafficJam.keySet()))) {

					System.out
							.println("["
									+ getNode().getID()
									+ ":Supervisor]: Il nodo "
									+ i
									+ " era un nodo che collegava il gruppo ad un vicino");

					listaFollowerTrafficJam.remove(i);
					Integer substitute = Collections
							.max(listaFollowerTrafficJam.keySet());
					if (!getNode().getID().equals(substitute + "")) {
						Address substituteAddress = listaFollowerTrafficJam
								.get(substitute).addr;
						System.out.println("[" + getNode().getID()
								+ ":Supervisor]: Dico al nodo " + substitute
								+ " di sostituire il nodo " + i);
						sendMessageToFollower(new A3JGMessage(
								"ReplaceSupervisorID" + i),
								Collections.singletonList(substituteAddress));
					}
				}

			}
		}

		return false;
	}

	@Override
	public boolean Plan() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean Execute() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean calculateTrafficJam() {

		boolean result = true;

		for (int id : listaFollowerTrafficJam.keySet()) {
			result = listaFollowerTrafficJam.get(id).trafficJam && result;
		}

		return result;
	}

	private class NodeInfo {
		private Address addr;
		private boolean trafficJam;

		public NodeInfo(Address addr, boolean trafficJam) {
			super();
			this.addr = addr;
			this.trafficJam = trafficJam;
		}
	}

}
