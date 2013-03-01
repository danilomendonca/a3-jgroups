package node.agentMiddleware.perception.interfaces;



public interface Perception {

	public void senseNeighbours(NeighbourPerceive callback);
	public void senseTrafficJamInfo(TrafficJamInfoPerceive callback);
	public void stopNeighbourSensing(NeighbourPerceive callback);
	public void stopTrafficJamInfoSensing(TrafficJamInfoPerceive callback);
	
}
