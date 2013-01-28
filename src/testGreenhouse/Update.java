package testGreenhouse;

import java.io.Serializable;

import org.jgroups.Address;

public class Update implements Serializable {

	private Address address;
	private int temperature;

	public Update(Address address, int temperature) {
		this.address = address;
		this.temperature = temperature;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public int getTemperature() {
		return temperature;
	}

	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}

}
