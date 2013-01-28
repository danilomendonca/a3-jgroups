package testHospital;

import java.util.Map;

import A3JGroups.A3JGFollowerRole;
import A3JGroups.A3JGMessage;

public class ServerFollowerRole extends A3JGFollowerRole {

	public ServerFollowerRole(int resourceCost) {
		super(resourceCost);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ScreenNode getNode() {
		return (ScreenNode) super.getNode();
	}

	@Override
	public void run() {

		System.out.println("[" + getNode().getID()
				+ "]: Chiedo al supervisor di inizializzare la mia map");
		A3JGMessage msg = new A3JGMessage("MapRequest" + getNode().getID());
		msg.setContent(getChan().getAddress());
		sendMessageToSupervisor(msg);

	}

	@SuppressWarnings("unchecked")
	@Override
	public void messageFromSupervisor(A3JGMessage msg) {

		getNode().setMap((Map<Destination, Direction>) msg.getContent());
		System.out.println("[" + getNode().getID()
				+ "]: La mia map è stata inizializzata");

	}

}
