package a3Solution;

import java.util.List;

import node.developer.sensors.Camera;
import utilities.threading.ThreadManager;
import A3JGroups.A3JGroup;

public class A3CameraNode extends A3JGSingleThreadedNode {

	private Camera camera;

	// valore 0 per non settati
	private int supervisorIn;
	private int followerIn;

	public A3CameraNode(String ID, ThreadManager tm, Camera camera) {
		super(ID, tm);
		// TODO Auto-generated constructor stub
		this.camera = camera;

		System.out.println("[" + getID() + "]: A3CameraNode " + getID()
				+ " created");
		System.out.println("[" + getID() + "]: I miei vicini fisici sono: ");

		for (String n : this.camera.physicalNeighbors) {
			System.out.println("[" + getID() + "]: " + n);
		}

		// Gruppo
		A3JGroup gruppoNeighbour = new A3JGroup(
				NeighbourSupervisorRole.class.getCanonicalName(),
				NeighbourFollowerRole.class.getCanonicalName());

		addSupervisorRole(new NeighbourSupervisorRole(1, tm));
		addFollowerRole(new NeighbourFollowerRole(1, tm));

		for (String n : this.camera.physicalNeighbors) {

			if (Integer.parseInt(getID()) < Integer.parseInt(n)) {
				addGroupInfo(getID(), gruppoNeighbour);
				try {
					joinGroup(getID());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				addGroupInfo(n, gruppoNeighbour);
				try {
					joinGroup(n);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	public double getAvgVelocity() {
		return camera.getAvgVelocity();
	}

	public double getDensity() {
		camera.getIntensity();
		return camera.getDensity();
	}

	public List<String> getNeighburs() {
		return camera.physicalNeighbors;
	}

	public int getSupervisorIn() {
		return supervisorIn;
	}

	public void setSupervisorIn(int supervisorIn) {
		this.supervisorIn = supervisorIn;
	}

	public int getFollowerIn() {
		return followerIn;
	}

	public void setFollowerIn(int followerIn) {
		this.followerIn = followerIn;
	}

}
