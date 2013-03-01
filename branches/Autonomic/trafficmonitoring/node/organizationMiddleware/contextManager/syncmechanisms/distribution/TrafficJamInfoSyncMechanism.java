package node.organizationMiddleware.contextManager.syncmechanisms.distribution;

import node.agentMiddleware.communication.interfaces.SendInterface;
import node.organizationMiddleware.contextManager.contextDirectories.Context;
import node.organizationMiddleware.contextManager.contextDirectories.TrafficJamInfo;
import utilities.Event;
import utilities.Subscriber;

public class TrafficJamInfoSyncMechanism implements Subscriber {

	private TrafficJamInfo trafficJamInfo;
	private SendInterface agentLayer;
	private Context ctx;
	
	public TrafficJamInfoSyncMechanism(TrafficJamInfo trafficJamInfo,
									   SendInterface agentLayer, Context ctx){
		this.ctx = ctx;
		this.agentLayer = agentLayer;
		this.trafficJamInfo = trafficJamInfo;
		trafficJamInfo.subscribe("setDensity", this);
		trafficJamInfo.subscribe("setAvgVelocity", this);
		sendOutInfo();
	}

	private void sendOutInfo() {
		//System.out.println("** Send Out info: from: "+this.ctx.getPersonalID()+" to: "+ctx.getPersonalOrg().getMasterID()+" velo: "+this.trafficJamInfo.getAvgVelocity());
		String nodeId = ctx.getPersonalOrg().getMasterID().toString();
		TrafficJamInfo info = new TrafficJamInfo();
		info.setAvgVelocity(trafficJamInfo.getAvgVelocity());
		info.setDensity(trafficJamInfo.getDensity());
		agentLayer.sendTrafficJamInfo(info, ctx.getPersonalID(), nodeId);
	}

	public void publish(Event e) {
		sendOutInfo();
	}

	public void stop() {
		trafficJamInfo.unsubscribe("setAvgVelocity", this);
	}
	
}
