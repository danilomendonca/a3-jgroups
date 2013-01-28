package testHospital;

import java.util.Collections;

import org.jgroups.Address;

import A3JGroups.A3JGMessage;
import A3JGroups.autonomic.AutonomicJGSupervisorRole;

public class ServerSupervisorRole extends AutonomicJGSupervisorRole {

	public ServerSupervisorRole(int resourceCost) {
		super(resourceCost, "ServerSupervisorRoleRules.drl");
		// TODO Auto-generated constructor stub
	}

	@Override
	public ServerNode getNode() {
		return (ServerNode) super.getNode();
	}

	@Override
	public void run() {

		// TODO chiamare super().run() per far partire il loop MAPE

		System.out.println("[" + getNode().getID()
				+ "]: Inizio il mio ruolo di server supervisor");

	}

	@Override
	public void messageFromFollower(A3JGMessage msg) {

		if (msg.getValueID().startsWith("MapRequest")) {

			String ID = msg.getValueID().replace("MapRequest", "");

			System.out.println("[" + getNode().getID()
					+ "]: Mi è stato chiesta la map per lo schermo " + ID);

			A3JGMessage respMsg = new A3JGMessage("Map");
			respMsg.setContent(getNode().getMapForScreen(ID));
			sendMessageToFollower(respMsg,
					Collections.singletonList((Address) msg.getContent()));

		}

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
