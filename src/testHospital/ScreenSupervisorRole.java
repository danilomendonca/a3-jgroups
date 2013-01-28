package testHospital;

import java.util.Collections;

import A3JGroups.A3JGMessage;
import A3JGroups.A3JGSupervisorRole;

public class ScreenSupervisorRole extends A3JGSupervisorRole {

	public ScreenSupervisorRole(int resourceCost) {
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
				+ "]: Inizio il mio ruolo di screen supervisor");

	}

	@Override
	public void messageFromFollower(A3JGMessage msg) {

		// msg.getValueID();
		DirectionRequest request = (DirectionRequest) msg.getContent();
		System.out
				.println("["
						+ getNode().getID()
						+ "]: Mi è stata chiesta la direzione per andare nella destinazione "
						+ request.getDestination() + " da un gruppo di "
						+ request.getPatientsNumber() + " pazienti");
		A3JGMessage respMsg = new A3JGMessage("Direction");
		respMsg.setContent(getNode().getDirectionForDestination(
				request.getDestination()));
		sendMessageToFollower(respMsg,
				Collections.singletonList(request.getSrc()));

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

}
