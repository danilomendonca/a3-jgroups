package testHospital;

import java.util.HashMap;
import java.util.Map;

import A3JGroups.A3JGNode;

public class ServerNode extends A3JGNode {

	private Map<String, Map<Destination, Direction>> mapOfMaps;
	private Map<String, Integer> congestionStatus;

	public ServerNode(String ID) {
		super(ID);
		mapOfMaps = new HashMap<String, Map<Destination, Direction>>();
		Map<Destination, Direction> map = new HashMap<Destination, Direction>();
		map = new HashMap<Destination, Direction>();
		map.put(Destination.BLUE, Direction.DOWN);
		map.put(Destination.GREEN, Direction.LEFT);
		map.put(Destination.RED, Direction.RIGHT);
		map.put(Destination.YELLOW, Direction.UP);
		mapOfMaps.put("Screen1", map);
	}

	public Map<Destination, Direction> getMapForScreen(String ScreenID) {
		return mapOfMaps.get(ScreenID);
	}

	public void updateStatus(String screenID, int patientNumber) {

		congestionStatus.put(screenID, patientNumber);

	}

}
