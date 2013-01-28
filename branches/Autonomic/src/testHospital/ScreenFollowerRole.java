package testHospital;

import A3JGroups.A3JGFollowerRole;
import A3JGroups.A3JGMessage;

public class ScreenFollowerRole extends A3JGFollowerRole {

	private Destination destination;

	public ScreenFollowerRole(int resourceCost, Destination destination) {
		super(resourceCost);
		this.destination = destination;
	}

	@Override
	public PatientNode getNode() {
		return (PatientNode) super.getNode();
	}

	@Override
	public void run() {
		System.out
				.println("["
						+ getNode().getID()
						+ "]: Chiedo al supervisor da che parte devo andare con i miei "
						+ (getChan().getView().getMembers().size() - 2)
						+ " follower per la destinazione " + destination);
		A3JGMessage msg = new A3JGMessage("DirectionRequest");
		msg.setContent(new DirectionRequest(getChan().getAddress(),
				destination, getChan().getView().getMembers().size() - 1));
		sendMessageToSupervisor(msg);
	}

	@Override
	public void messageFromSupervisor(A3JGMessage msg) {
		Direction direction = (Direction) msg.getContent();
		String groupName = getChan().getClusterName();
		if (direction != null) {
			System.out.println("[" + getNode().getID()
					+ "]: Mi è stato detto di andare nella direzione "
					+ direction);
			getNode().goToDirection(direction);
		} else {
			System.out.println("[" + getNode().getID() + "]: " + groupName
					+ " non sa da che parte devo andare");
		}

		getNode().terminate(groupName);
		System.out.println("[" + getNode().getID()
				+ "]: Mi disconnetto dal gruppo " + groupName);
		getNode().setConnectedToScreen(false);
		getNode().setConnectedScreen(null);

	}

}
