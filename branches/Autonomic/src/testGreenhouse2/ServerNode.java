package testGreenhouse2;

import A3JGroups.A3JGNode;

public class ServerNode extends A3JGNode {

	// Sezione

	private int temperatureTreshold = 25;

	public ServerNode(String ID) {
		super(ID);
	}

	public ServerNode(String ID, int temperatureTreshold) {
		super(ID);
		this.temperatureTreshold = temperatureTreshold;
	}

	public int getTemperatureTreshold() {
		return temperatureTreshold;
	}

	public void setTemperatureTreshold(int temperatureTreshold) {
		this.temperatureTreshold = temperatureTreshold;
	}

}
