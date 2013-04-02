package a3Solution;

import utilities.threading.ThreadManager;
import A3JGroups.A3JGMessage;
import A3JGroups.A3JGroup;

public class NeighbourFollowerRole extends A3JGSingleThreadedFollowerRole {

	private boolean firstRun;
	private ThreadManager tm;

	public NeighbourFollowerRole(int resourceCost, ThreadManager tm) {
		super(resourceCost, tm);
		// TODO Auto-generated constructor stub
		firstRun = true;
		this.tm = tm;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		if (active) {
			if (firstRun) {
				System.out
						.println("["
								+ getNode().getID()
								+ ":Follower]: Inizio il mio ruolo di Neighbour Follower");
				firstRun = false;
				((A3CameraNode) getNode()).setFollowerIn(Integer
						.parseInt(getChan().getClusterName()));
				sendMessageToSupervisor(new A3JGMessage("Joining", new Request(
						getNode().getID(), getChan().getAddress(),
						((A3CameraNode) getNode()).getNeighburs())));
			}

			// System.out.println("[" + getNode().getID()
			// + "]: Neighbour Follower Step");

			double density = ((A3CameraNode) getNode()).getDensity();
			double avgSpeed = ((A3CameraNode) getNode()).getAvgVelocity();

			if (avgSpeed < 0.8 && density != 0) {
				// System.out.println("[" + getNode().getID()
				// + ":Follower]: Vedo un ingorgo");
				if (sendMessageToSupervisor(new A3JGMessage("TrafficJam",
						getNode().getID()))) {

					// String groupName = getChan().getClusterName();
					// System.out
					// .println("["
					// + getNode().getID()
					// + ":Follower]: Notifico il mio supervisor del gruppo "
					// + groupName);
				} else {
					// System.out
					// .println("["
					// + getNode().getID()
					// +
					// ":Follower]: Non sono riuscito a notificare il mio supervisor");
				}
			}

			// System.out.println("[" + getNode().getID() + ":Follower]: Stat: "
			// + getChan().getSentBytes());

		}

	}

	@Override
	public void messageFromSupervisor(A3JGMessage msg) {

		System.out
				.println("["
						+ getNode().getID()
						+ ":Follower]: Mi è arrivato un messaggio dal supervisor con ID "
						+ msg.getValueID());

		if (msg.getValueID().startsWith("AggregateID")) {

			String newGroupName = msg.getValueID().replace("AggregateID", "");

			System.out.println("[" + getNode().getID()
					+ ":Follower]: Mi chiede di aggregarmi al gruppo "
					+ newGroupName);

			String groupName = getChan().getClusterName();
			System.out.println("[" + getNode().getID()
					+ ":Follower]: Sto per disconnettermi dal gruppo "
					+ groupName);

			sendMessageToSupervisor(new A3JGMessage("Exiting", getNode()
					.getID()));

			getNode().terminate(groupName);
			((A3CameraNode) getNode()).setFollowerIn(0);
			System.out
					.println("[" + getNode().getID()
							+ ":Follower]: Mi sono disconnesso dal gruppo "
							+ groupName);

			A3JGroup gruppoIngorgo = new A3JGroup(
					NeighbourSupervisorRole.class.getCanonicalName(),
					NeighbourFollowerRole.class.getCanonicalName());

			A3CameraNode node = (A3CameraNode) getNode();
			node.addGroupInfo(newGroupName + "", gruppoIngorgo);
			node.addSupervisorRole(new NeighbourSupervisorRole(1, tm));
			node.addFollowerRole(new NeighbourFollowerRole(1, tm));
			boolean connect = false;
			try {
				System.out.println("[" + getNode().getID()
						+ ":Follower]: Richiedo di connettermi a "
						+ newGroupName);
				connect = node.joinGroup(newGroupName + "");
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (connect)
				System.out.println("[" + getNode().getID()
						+ ":Follower]: Mi sono connesso a " + newGroupName);
			else
				System.out.println("[" + getNode().getID()
						+ ":Follower]:  Non sono riuscito a connettermi a "
						+ newGroupName);
		} else if (msg.getValueID().startsWith("ReplaceSupervisorID")) {

			String newGroupName = msg.getValueID().replace(
					"ReplaceSupervisorID", "");

			System.out
					.println("["
							+ getNode().getID()
							+ ":Follower]: Mi chiede di diventare supervisor per il gruppo "
							+ newGroupName);

			A3JGroup gruppoIngorgo = new A3JGroup(
					NeighbourSupervisorRole.class.getCanonicalName(),
					NeighbourFollowerRole.class.getCanonicalName());

			A3CameraNode node = (A3CameraNode) getNode();
			node.addGroupInfo(newGroupName + "", gruppoIngorgo);
			node.addSupervisorRole(new NeighbourSupervisorRole(1, tm));
			node.addFollowerRole(new NeighbourFollowerRole(1, tm));
			boolean connect = false;
			try {
				System.out.println("[" + getNode().getID()
						+ ":Follower]: Richiedo di connettermi a "
						+ newGroupName);
				connect = node.joinGroup(newGroupName + "");
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (connect)
				System.out.println("[" + getNode().getID()
						+ ":Follower]: Mi sono connesso a " + newGroupName);
			else
				System.out.println("[" + getNode().getID()
						+ ":Follower]:  Non sono riuscito a connettermi a "
						+ newGroupName);

		}

	}

}
