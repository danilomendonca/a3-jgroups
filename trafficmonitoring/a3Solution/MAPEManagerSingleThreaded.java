package a3Solution;

import org.jgroups.JChannel;

import A3JGroups.A3JGMessage;

public class MAPEManagerSingleThreaded implements Runnable {

	private AutonomicJGSingleThreadedSupervisorRole supervisorRole;
	private long interPhaseMillisTime;

	public MAPEManagerSingleThreaded(
			AutonomicJGSingleThreadedSupervisorRole supervisorRole,
			long interPhaseMillisTime) {
		this.supervisorRole = supervisorRole;
		this.interPhaseMillisTime = interPhaseMillisTime;
	}

	@Override
	public void run() {
		try {

			while (supervisorRole.isActive()) {

				if (supervisorRole.Monitor()) {
					Thread.sleep(interPhaseMillisTime);
					if (supervisorRole.Analyse()) {
						Thread.sleep(interPhaseMillisTime);
						if (supervisorRole.Plan()) {
							Thread.sleep(interPhaseMillisTime);
							supervisorRole.Execute();
						}
					}
				}

				Thread.sleep(interPhaseMillisTime);

			}

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

	}

	public void startMAPELoop() {
		// try {
		if (supervisorRole.isActive()) {
			if (supervisorRole.Monitor()) {
				// Thread.sleep(interPhaseMillisTime);
				if (supervisorRole.Analyse()) {
					// Thread.sleep(interPhaseMillisTime);
					if (supervisorRole.Plan()) {
						// Thread.sleep(interPhaseMillisTime);
						if (supervisorRole.Execute()) {
							supervisorRole.executeRules();
						}
					}
				}
			}
		}
		// } catch (InterruptedException e) {
		// Thread.currentThread().interrupt();
		// }
	}

	public void insertInAdjacentKB(String groupName, Object fact) {

		insertInMyNode(groupName, fact);

	}

	private void insertInMyNode(String groupName, Object fact) {
		A3JGSingleThreadedNode node = supervisorRole.getNode();

		JChannel chan = node.getChannels(groupName);

		if (chan != null) {
			// Il mio nodo fa parte di un gruppo con quel nome

			// Verifico se è supervisor o follower
			String supName = node.getGroupInfo(groupName).getSupervisor()
					.get(0);

			String follName = node.getGroupInfo(groupName).getFollower().get(0);

			String activeName = node.getActiveRole(groupName);

			if (supName != null && activeName.equals(supName)) {
				// Il nodo è supervisor nel gruppo che sto cercando
				((AutonomicJGSingleThreadedSupervisorRole) node
						.getSupervisorRole(supName)).insertOrUpdateKB(fact);
				((AutonomicJGSingleThreadedSupervisorRole) node
						.getSupervisorRole(supName)).executeRules();

			}

			if (follName != null && activeName.equals(follName)) {
				// Il nodo è follower nel gruppo che sto cercando
				node.getFollowerRole(follName).sendMessageToSupervisor(
						new A3JGMessage("A3MapeManagerMessage", fact));
			}
		}
	}

}
