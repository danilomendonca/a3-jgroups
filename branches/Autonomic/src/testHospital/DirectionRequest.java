package testHospital;

import java.io.Serializable;

import org.jgroups.Address;

@SuppressWarnings("serial")
public class DirectionRequest implements Serializable {

	private Address src;
	private Destination destination;
	private int patientsNumber;

	public DirectionRequest(Address src, Destination destination,
			int patientsNumber) {
		this.src = src;
		this.destination = destination;
		this.patientsNumber = patientsNumber;
	}

	public Address getSrc() {
		return src;
	}

	public void setSrc(Address src) {
		this.src = src;
	}

	public Destination getDestination() {
		return destination;
	}

	public void setDestination(Destination destination) {
		this.destination = destination;
	}

	public int getPatientsNumber() {
		return patientsNumber;
	}

	public void setPatienstNumber(int patientsNumber) {
		this.patientsNumber = patientsNumber;
	}

}
