package node.organizationMiddleware.contextManager.contextDirectories;


import utilities.Event;
import utilities.Publisher;


public class TrafficJamInfo extends Publisher {

	private float density;
	private float intensity;
	private float avgVelocity;
	
	public TrafficJamInfo(){
		this.density = -1;
		this.intensity = -1;
		this.avgVelocity = -1;
	}
	
	/****************
	 *              *
	 *    getters   *
	 *              *
	 ****************/
	
	public float getAvgVelocity() {
		return avgVelocity;
	}

	public float getDensity() {
		return density;
	}

	public float getIntensity() {
		return intensity;
	}

	/*********************
	 *                   *
	 *      modifiers    *
	 *                   *
	 *********************/
	
	public void setAvgVelocity(float avgVelocity) {
		this.avgVelocity = avgVelocity;
		publish(new Event("setAvgVelocity"));
	}
	
	public void setAvgVelocityAndDensity(float avgVelocity, float density) {
		this.avgVelocity = avgVelocity;
		this.density = density;
		publish(new Event("setAvgVelocity"));
	}

	public void setDensity(float density) {
		this.density = density;
		publish(new Event("setDensity"));
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
		publish(new Event("setIntensity"));
	}
	
	/**
	 * easymock noodzaak
	 */
	public boolean equals(Object info){
		try{
			TrafficJamInfo trafficInfo = (TrafficJamInfo) info;
			return (trafficInfo.getAvgVelocity() == getAvgVelocity() ) &&
				   (trafficInfo.getDensity() == getDensity()) &&
				   (trafficInfo.getIntensity() == getIntensity());
		}catch (ClassCastException e){
			return false;
		}
	}
}
