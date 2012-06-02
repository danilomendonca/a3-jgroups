package it.polimi.A3Behavior2;

import java.util.ArrayList;

import de.nec.nle.siafu.model.Place;
import de.nec.nle.siafu.types.FlatData;
import de.nec.nle.siafu.types.Publishable;
import A3JGroups.A3JGNode;

public class MixedNode extends A3JGNode implements Publishable {
	
	private ArrayList<Place> red;
	private ArrayList<Place> blue;
	private ArrayList<Place> yellow;
	private ArrayList<Place> green;
	private int redDir;
	private int blueDir;
	private int yellowDir;
	private int greenDir;
	
	public MixedNode(String ID) {
		super(ID);
	}

	@Override
	public FlatData flatten() {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<Place> getRed() {
		return red;
	}

	public void setRed(ArrayList<Place> red) {
		this.red = red;
	}

	public ArrayList<Place> getBlue() {
		return blue;
	}

	public void setBlue(ArrayList<Place> blue) {
		this.blue = blue;
	}

	public ArrayList<Place> getYellow() {
		return yellow;
	}

	public void setYellow(ArrayList<Place> yellow) {
		this.yellow = yellow;
	}

	public ArrayList<Place> getGreen() {
		return green;
	}

	public void setGreen(ArrayList<Place> green) {
		this.green = green;
	}

	public int getRedDir() {
		return redDir;
	}

	public void setRedDir(int redDir) {
		this.redDir = redDir;
	}

	public int getBlueDir() {
		return blueDir;
	}

	public void setBlueDir(int blueDir) {
		this.blueDir = blueDir;
	}

	public int getYellowDir() {
		return yellowDir;
	}

	public void setYellowDir(int yellowDir) {
		this.yellowDir = yellowDir;
	}

	public int getGreenDir() {
		return greenDir;
	}

	public void setGreenDir(int greenDir) {
		this.greenDir = greenDir;
	}

	
	
}
