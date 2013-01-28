package testHospital;

import A3JGroups.A3JGMessage;
import A3JGroups.A3JGroup;
import A3JGroups.autonomic.AutonomicJGSupervisorRole;

public class PatientSupervisorRole extends AutonomicJGSupervisorRole {

	private final static long WAIT_LONG = 4000;
	private final static long WAIT_SHORT = 2000;

	private Destination destination;
	private long waitTime;

	public PatientSupervisorRole(int resourceCost, Destination destination) {
		super(resourceCost, "PatientSupervisorRoleRules.drl");
		this.destination = destination;
	}

	@Override
	public PatientNode getNode() {
		return (PatientNode) super.getNode();
	}

	@Override
	public void run() {

		// TODO chiamare super().run() per far partire il loop MAPE

		System.out.println("[" + getNode().getID()
				+ "]: Sono attivo come supervisore di un gruppo di pazienti");

		while (active) {

			ScreenNode targetScreen = null;

			if (!getNode().isConnectedToScreen()) {
				// Non connesso
				System.out.println("[" + getNode().getID()
						+ "]: Non sono connesso ad uno screen");
				targetScreen = getNode().getNearestScreen();
			} else {
				// Connesso
				System.out.println("[" + getNode().getID()
						+ "]: Sono già connesso ad uno screen");
				waitTime = WAIT_LONG;
			}
			if (targetScreen != null) {
				// Non connesso con possibilità di connettersi
				System.out.println("[" + getNode().getID()
						+ "]: Provo a connettermi allo screen "
						+ targetScreen.getID());
				A3JGroup targetGroup = new A3JGroup(
						ScreenSupervisorRole.class.getCanonicalName(),
						ScreenFollowerRole.class.getCanonicalName());
				getNode().addGroupInfo(targetScreen.getID(), targetGroup);
				getNode().addFollowerRole(
						new ScreenFollowerRole(1, destination));
				boolean connected = false;
				try {
					connected = getNode().joinGroup(targetScreen.getID());
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (!connected) {
					// Connessione non riuscita
					System.out.println("[" + getNode().getID()
							+ "]: Connessione non riuscita con "
							+ targetScreen.getID());
					waitTime = WAIT_SHORT;
				} else {
					// Connessione riuscita
					System.out.println("[" + getNode().getID()
							+ "]: Connessione riuscita con "
							+ targetScreen.getID());
					waitTime = WAIT_LONG;
					getNode().setConnectedToScreen(true);
					getNode().setConnectedScreen(targetScreen);
				}

			}
			try {
				getNode().getNearestScreen();
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}

		}

	}

	public void tellOthers(Direction direction) {
		A3JGMessage msg = new A3JGMessage("DirectionMessage");
		msg.setContent(direction);
		sendMessageToFollower(msg, null);
	}

	@Override
	public void messageFromFollower(A3JGMessage msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateFromFollower(A3JGMessage msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public int fitnessFunc() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean Monitor() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean Analyse() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean Plan() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean Execute() {
		// TODO Auto-generated method stub
		return true;
	}

}
