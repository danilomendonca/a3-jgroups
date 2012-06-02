package it.polimi.A3Behavior2;

import java.io.Serializable;
import java.util.ArrayList;

import org.jgroups.Address;

import de.nec.nle.siafu.model.Place;

public class Content implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Address ad;
	private int i;
	private String colour;
	private ArrayList<Place> pos;
	private int direction;
	
	public Content(Address ad, int i, String colour) {
		super();
		this.ad = ad;
		this.i = i;
		this.colour = colour;
	}

	public Address getAd() {
		return ad;
	}

	public int getI() {
		return i;
	}

	public String getColour() {
		return colour;
	}
	
	public ArrayList<Place> getPos() {
		return pos;
	}

	public int getDirection() {
		return direction;
	}

	public void setPos(ArrayList<Place> pos) {
		this.pos = pos;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	
	
}
