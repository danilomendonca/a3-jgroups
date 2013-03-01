package node.organizationMiddleware.contextManager.syncmechanisms.datageneration;

import node.agentMiddleware.perception.interfaces.Perception;
import node.agentMiddleware.perception.interfaces.TrafficJamInfoPerceive;
import node.organizationMiddleware.contextManager.contextDirectories.TrafficJamInfo;


public class TrafficJamInfoPerceptionMechanism implements TrafficJamInfoPerceive{

	private Perception perceptionLayer;
	private TrafficJamInfo trafficJamInfo;
	
	public TrafficJamInfoPerceptionMechanism(Perception perceptionLayer,
											 TrafficJamInfo trafficJamInfo){
		this.perceptionLayer = perceptionLayer;
		this.trafficJamInfo = trafficJamInfo;
		perceptionLayer.senseTrafficJamInfo(this);
	}

	public void setAvgVelocity(float avgVelocity) {
		trafficJamInfo.setAvgVelocity(avgVelocity);
	}

	public void setDensity(float density) {
		trafficJamInfo.setDensity(density);
	}

	public void setIntensity(float intensity) {
		trafficJamInfo.setIntensity(intensity);
	}

	public void stop() {
		perceptionLayer.stopTrafficJamInfoSensing(this);
	}
	
		
}
