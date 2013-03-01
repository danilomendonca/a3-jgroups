package node.organizationMiddleware.contextManager.syncmechanisms.datageneration;

import java.util.ArrayList;

import node.organizationMiddleware.contextManager.contextDirectories.Organization;
import node.organizationMiddleware.contextManager.contextDirectories.RolePosition;
import node.organizationMiddleware.contextManager.contextDirectories.TrafficJamInfo;


import utilities.Event;
import utilities.Subscriber;
import utilities.ThreevaluedLogic;

public class TrafficJamInfoAggregationMechanism implements Subscriber{
	
	private Organization organization;
	private ArrayList<TrafficJamInfo> currentlySubscribedOn;
	
	public TrafficJamInfoAggregationMechanism(Organization org){
		this.organization = org;
		this.currentlySubscribedOn = new ArrayList<TrafficJamInfo>();
		organization.subscribe("addFilledRolePosition", this);
		organization.subscribe("removeFilledRolePosition", this);
		subscribeOnNewTrafficJamInfo();
		//System.out.println("TrafficJamInfoAggregationMechanism -> recalculateOrganizationTrafficJamInfo | called via constructor");
		recalculateOrganizationTrafficJamInfo();
	}
	
	public void stop(){
		organization.unsubscribe("addFilledRolePosition", this);
		organization.unsubscribe("removeFilledRolePosition", this);
		unsubscribeOnOldTrafficJamInfo();
	}
	
	public void publish(Event e) {
		//System.out.println("TrafficJamInfoAggregationMechanism.publish("+e.getEventType()+")");
		if(e.getEventType().equals("addFilledRolePosition") ||
				e.getEventType().equals("removeFilledRolePosition")){
			unsubscribeOnOldTrafficJamInfo();
			subscribeOnNewTrafficJamInfo();
			recalculateOrganizationTrafficJamInfo();
		}else if(e.getEventType().equals("setAvgVelocity") ||
				 e.getEventType().equals("setDensity") ||
				 e.getEventType().equals("setIntensity") ){
			//System.out.println("TrafficJamInfoAggregationMechanism -> recalculateOrganizationTrafficJamInfo | called via publish");
			recalculateOrganizationTrafficJamInfo();
		}
						
	}
	
	/***************************
	 * 						   *
	 *   AggregationMechanism  *
	 *                         *
	 ***************************/
	
	private void recalculateOrganizationTrafficJamInfo() {
		if(organization.getFilledRolePositions().size() == 0){
			ThreevaluedLogic result = new ThreevaluedLogic();
			result.setFalse();
			organization.changeTrafficStateOfOrganization(result);
		}else{
			ArrayList<RolePosition> filledRolePositions = organization.getFilledRolePositions();
			ThreevaluedLogic result = new ThreevaluedLogic(); //default == unknown
			
			boolean everyAgentSeesCongestion = true;
			boolean unknown = false;
			for(RolePosition filledRolePosition : filledRolePositions){
				//System.out.println("== " +filledRolePosition.getAgentId()+" den: "+filledRolePosition.getTrafficJamInfo().getDensity());
				if(filledRolePosition.getTrafficJamInfo().getAvgVelocity() != -1){
					//if == -1 then value hasn't been initialised yet
					if(filledRolePosition.getTrafficJamInfo().getAvgVelocity() > organization.getTrafficJamTreshhold()
							|| filledRolePosition.getTrafficJamInfo().getDensity() == 0){
						everyAgentSeesCongestion = false;
					}
				}else
					unknown = true;
			}
			
			if(unknown){
				//one or more rp hasn't been initialised yet
				//System.out.println("TrafficJamInfoAggregationMechanism -> recalculateOrganizationTrafficJamInfo() " +
				//		"result unknown? : " + result.unknown() );
				organization.changeTrafficStateOfOrganization(result);
			}else{
				result.set(everyAgentSeesCongestion);
				//System.out.println("Org of " + organization.getMasterID() + " | TrafficJamInfoAggregationMechanism -> recalculateOrganizationTrafficJamInfo() " +
				//		"result? : " + result.isTrue() );
				organization.changeTrafficStateOfOrganization(result);
			}
		}
		
		
	}
	
	/****************************
	 *                          *
	 * 		helper function     *
	 * 						    *
	 ****************************/
	
	private void subscribeOnNewTrafficJamInfo(){
		ArrayList<RolePosition> filledRolePositions = organization.getFilledRolePositions();
		for(RolePosition filledRolePosition : filledRolePositions){
			TrafficJamInfo trafficInfo = filledRolePosition.getTrafficJamInfo();
			trafficInfo.subscribe("setAvgVelocity", this);
			trafficInfo.subscribe("setDensity", this);
			trafficInfo.subscribe("setIntensity", this);
			currentlySubscribedOn.add(trafficInfo);
		}
	}
	
	private void unsubscribeOnOldTrafficJamInfo(){
		for(TrafficJamInfo trafficJamInfo : currentlySubscribedOn){
			trafficJamInfo.unsubscribe("setAvgVelocity", this);
			trafficJamInfo.unsubscribe("setDensity", this);
			trafficJamInfo.unsubscribe("setIntensity", this);
		}
		currentlySubscribedOn.clear();
		
	}

}
