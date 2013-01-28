package testHospital;

import java.util.Map;

import A3JGroups.A3JGNode;

public class ScreenNode extends A3JGNode {

	private Map<Destination, Direction> map;

	public ScreenNode(String ID) {
		super(ID);
		// map = new HashMap<Destination, Direction>();
		// map.put(Destination.BLUE, Direction.DOWN);
		// map.put(Destination.GREEN, Direction.LEFT);
		// map.put(Destination.RED, Direction.RIGHT);
		// map.put(Destination.YELLOW, Direction.UP);
	}

	public Map<Destination, Direction> getMap() {
		return map;
	}

	public void setMap(Map<Destination, Direction> map) {
		this.map = map;
	}

	public Direction getDirectionForDestination(Destination destination) {
		if (map != null)
			return map.get(destination);
		else
			return null;
	}

}
