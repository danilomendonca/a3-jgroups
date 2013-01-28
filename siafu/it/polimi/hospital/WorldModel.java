/*
 * Copyright NEC Europe Ltd. 2006-2007
 * 
 * This file is part of the context simulator called Siafu.
 * 
 * Siafu is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * Siafu is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package it.polimi.hospital;




import static it.polimi.hospital.Constants.Fields.TYPE;
import static it.polimi.hospital.Constants.Fields.NODE;
import it.polimi.a3Behavior.BlueFollower;
import it.polimi.a3Behavior.GreenFollower;
import it.polimi.a3Behavior.MixedNode;
import it.polimi.a3Behavior.RedFollower;
import it.polimi.a3Behavior.YellowFollower;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import de.nec.nle.siafu.behaviormodels.BaseWorldModel;
import de.nec.nle.siafu.exceptions.PlaceTypeUndefinedException;
import de.nec.nle.siafu.model.Agent;
import de.nec.nle.siafu.model.Place;
import de.nec.nle.siafu.model.Trackable;
import de.nec.nle.siafu.model.World;
import de.nec.nle.siafu.types.Text;

/**
 * The world model for the simulation. In this case, the conference room calls
 * for a global meeting which some staff members attend.
 * 
 */
public class WorldModel extends BaseWorldModel {


	
	
	private Place screen;
	private ArrayList<Place> green = new ArrayList<Place>();
	private ArrayList<Place> red = new ArrayList<Place>();
	private ArrayList<Place> blue = new ArrayList<Place>();
	private ArrayList<Place> yellow = new ArrayList<Place>();
	private Place redArrow;
	private Place greenArrow;
	private Place blueArrow;
	private Place yellowArrow;

	private Agent redIndicator;
	private Agent greenIndicator;
	private Agent blueIndicator;
	private Agent yellowIndicator;
	
	public void addRed(Place p) {
		red.add(p);
	}
	public void addBlue(Place p) {
		blue.add(p);
	}
	public void addGreen(Place p) {
		green.add(p);
	}
	public void addYellow(Place p) {
		yellow.add(p);
	}
	public Place getScreen(){
		return screen;
	}
	public Place getRed(int num) {
		return red.get(num);
	}
	public Place getGreen(int num) {
		return green.get(num);
	}
	public Place getYellow(int num) {
		return yellow.get(num);
	}
	public Place getBlue(int num) {
		return blue.get(num);
	}
	
	public ArrayList<Place> getGreen() {
		return green;
	}
	public ArrayList<Place> getRed() {
		return red;
	}
	public ArrayList<Place> getBlue() {
		return blue;
	}
	public ArrayList<Place> getYellow() {
		return yellow;
	}
	public void setScreen(Place screen) {
		this.screen = screen;
	}

	public Place getRedArrow() {
		return redArrow;
	}

	public void setRedArrow(Place redArrow) {
		this.redArrow = redArrow;
	}

	public Place getGreenArrow() {
		return greenArrow;
	}

	public void setGreenArrow(Place greenArrow) {
		this.greenArrow = greenArrow;
	}

	public Place getBlueArrow() {
		return blueArrow;
	}

	public void setBlueArrow(Place blueArrow) {
		this.blueArrow = blueArrow;
	}

	public Place getYellowArrow() {
		return yellowArrow;
	}

	public void setYellowArrow(Place yellowArrow) {
		this.yellowArrow = yellowArrow;
	}

	public Agent getRedIndicator() {
		return redIndicator;
	}

	public void setRedIndicator(Agent redIndicator) {
		this.redIndicator = redIndicator;
	}

	public Agent getGreenIndicator() {
		return greenIndicator;
	}

	public void setGreenIndicator(Agent greenIndicator) {
		this.greenIndicator = greenIndicator;
	}

	public Agent getBlueIndicator() {
		return blueIndicator;
	}

	public void setBlueIndicator(Agent blueIndicator) {
		this.blueIndicator = blueIndicator;
	}

	public Agent getYellowIndicator() {
		return yellowIndicator;
	}

	public void setYellowIndicator(Agent yellowIndicator) {
		this.yellowIndicator = yellowIndicator;
	}

	/**
	 * Create the world model.
	 * 
	 * @param world the simulation's world.
	 */
	public WorldModel(final World world) {
		super(world);
	}

	/**
	 * Add the Busy variable to the info field of all the places.
	 * 
	 * @param places the places created so far by the images
	 */
	@Override
	public void createPlaces(final ArrayList<Place> places) {
		
		Iterator<Place> itScreen = null;
		Iterator<Place> itGreen = null;
		Iterator<Place> itRed = null;
		Iterator<Place> itBlue = null;
		Iterator<Place> itYellow = null;
		Iterator<Place> itRedArrow = null;
		Iterator<Place> itGreenArrow = null;
		Iterator<Place> itBlueArrow = null;
		Iterator<Place> itYellowArrow = null;
		
		try {
			itScreen = world.getPlacesOfType("Screen").iterator();
			itGreen = world.getPlacesOfType("Green").iterator();		
			itRed = world.getPlacesOfType("Red").iterator();
			itBlue = world.getPlacesOfType("Blue").iterator();
			itYellow = world.getPlacesOfType("Yellow").iterator();
			itRedArrow = world.getPlacesOfType("RedArrow").iterator();
			itGreenArrow = world.getPlacesOfType("GreenArrow").iterator();
			itBlueArrow = world.getPlacesOfType("BlueArrow").iterator();
			itYellowArrow = world.getPlacesOfType("YellowArrow").iterator();	
		} catch (PlaceTypeUndefinedException e) {
			e.printStackTrace();
		}
		
		screen = itScreen.next();
		redArrow = itRedArrow.next();
		blueArrow = itBlueArrow.next();
		yellowArrow = itYellowArrow.next();
		greenArrow = itGreenArrow.next();
		
		while (itGreen.hasNext()) {
			addGreen(itGreen.next());
		}
		while (itRed.hasNext()){
			addRed(itRed.next());
		}
		while (itBlue.hasNext()) {
			addBlue(itBlue.next());
		}
		while (itYellow.hasNext()) {
			addYellow(itYellow.next());
		}	
	}

	/**
	 * 
	 * @param places the places in the simulation.
	 */
	public void doIteration(final Collection<Place> places) {
		ArrayList<Trackable> agents = null;
		boolean red = false;
		boolean green = false;
		boolean blue = false;
		boolean yellow = false;

		try {
			agents = world.findAllAgentsNear(screen.getPos(), 80, false);

			for (int i = 0; i < agents.size(); i++) {
				Agent ag = (Agent) agents.get(i);
				if (((Text) ag.get(TYPE)).getText().equalsIgnoreCase("Magenta")){
					red = true;
					if(!((MixedNode) ag.get(NODE)).getFollowerRole("red").isActive()){
						((RedFollower) ((MixedNode) ag.get(NODE)).getFollowerRole("red")).setScreen(screen);
						((MixedNode) ag.get(NODE)).joinGroup("red");
					}
				}else if (((Text) ag.get(TYPE)).getText().equalsIgnoreCase("Blue")){
					blue = true;
					if(!((MixedNode) ag.get(NODE)).getFollowerRole("blue").isActive()){
						((BlueFollower) ((MixedNode) ag.get(NODE)).getFollowerRole("blue")).setScreen(screen);
						((MixedNode) ag.get(NODE)).joinGroup("blue");
					}
				}else if (((Text) ag.get(TYPE)).getText().equalsIgnoreCase("Green")){
					green = true;
					if(!((MixedNode) ag.get(NODE)).getFollowerRole("green").isActive()){
						((GreenFollower) ((MixedNode) ag.get(NODE)).getFollowerRole("green")).setScreen(screen);
						((MixedNode) ag.get(NODE)).joinGroup("green");
					}
				}else if (((Text) ag.get(TYPE)).getText().equalsIgnoreCase("Yellow")){
					yellow = true;
					if(!((MixedNode) ag.get(NODE)).getFollowerRole("yellow").isActive()){
						((YellowFollower) ((MixedNode) ag.get(NODE)).getFollowerRole("yellow")).setScreen(screen);
						((MixedNode) ag.get(NODE)).joinGroup("yellow");
					}
				}
			}
			if(red){
				redIndicator.setDir(0);
			}else{
				redIndicator.setDir(1);
			}
			if(green){
				greenIndicator.setDir(6);
			}else{
				greenIndicator.setDir(1);
			}
			if(blue){
				blueIndicator.setDir(2);
			}else{
				blueIndicator.setDir(1);
			}
			if(yellow){
				yellowIndicator.setDir(4);
			}else{
				yellowIndicator.setDir(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
