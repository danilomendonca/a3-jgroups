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

package it.polimi.Hospital2;

import static it.polimi.Hospital2.Constants.Fields.TYPE;
import static it.polimi.Hospital2.Constants.Fields.NODE;

import it.polimi.A3Behavior2.BlueFollower;
import it.polimi.A3Behavior2.GreenFollower;
import it.polimi.A3Behavior2.MixedNode;
import it.polimi.A3Behavior2.RedFollower;
import it.polimi.A3Behavior2.YellowFollower;

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


public class WorldModel extends BaseWorldModel {


	private Place leftEntrance;
	private Place rightEntrance;
	private Place block;
	
	private ArrayList<Place> screens = new ArrayList<Place>();

	private ArrayList<Place> lab = new ArrayList<Place>();
	private ArrayList<Place> radiology = new ArrayList<Place>();
	private ArrayList<Place> physiotherapy = new ArrayList<Place>();
	
	private ArrayList<Place> redArrows = new ArrayList<Place>();
	private ArrayList<Place> greenArrows = new ArrayList<Place>();
	private ArrayList<Place> blueArrows = new ArrayList<Place>();
	private ArrayList<Place> yellowArrows = new ArrayList<Place>();
		
	private ArrayList<Agent> redIndicators = new ArrayList<Agent>();
	private ArrayList<Agent> greenIndicators = new ArrayList<Agent>();
	private ArrayList<Agent> blueIndicators = new ArrayList<Agent>();
	private ArrayList<Agent> yellowIndicators = new ArrayList<Agent>();
	
	private Agent blockAgent;
	
	private RoutingManager manager = null;
	
	public RoutingManager getManager() {
		return this.manager;
	}
	
	public void setBlockAgent(Agent a) {
		this.blockAgent = a;
	}
	
	public Agent getBlockAgent() {
		return this.blockAgent;
	}
	
	public void addRedArrow(Place p) {
		redArrows.add(p);
	}
	public void addBlueArrow(Place p) {
		blueArrows.add(p);
	}
	public void addGreenArrow(Place p) {
		greenArrows.add(p);
	}
	public void addYellowArrow(Place p) {
		yellowArrows.add(p);
	}
	
	public void addLab(Place p) {
		lab.add(p);
	}
	public void addRadiology(Place p) {
		radiology.add(p);
	}
	public void addPhysiotherapy(Place p) {
		physiotherapy.add(p);
	}
	
	public void addRedIndicator(Agent a) {
		redIndicators.add(a);
	}
	public void addGreenIndicator(Agent a) {
		greenIndicators.add(a);
	}
	public void addBlueIndicator(Agent a) {
		blueIndicators.add(a);
	}
	public void addYellowIndicator(Agent a) {
		yellowIndicators.add(a);
	}
	public void addScreen(Place p) {
		screens.add(p);
	}
	
	public Place getLeftEntrance() {
		return leftEntrance;
	}
	public Place getRightEntrance() {
		return rightEntrance;
	}
	public Place getBlock(){
		return block;
	}
	public Place getLab(int num) {
		return lab.get(num);
	}
	public Place getRadiology(int num) {
		return radiology.get(num);
	}
	public Place getPhysiotherapy(int num) {
		return physiotherapy.get(num);
	}
	public Place getScreen(int num) {
		return screens.get(num);
	}
	public Place getRedArrow(int num) {
		return redArrows.get(num);
	}
	public Place getGreenArrow(int num) {
		return greenArrows.get(num);
	}
	public Place getYellowArrow(int num) {
		return yellowArrows.get(num);
	}
	public Place getBlueArrow(int num) {
		return blueArrows.get(num);
	}

	public ArrayList<Place> getLab() {
		return lab;
	}

	public ArrayList<Place> getRadiology() {
		return radiology;
	}

	public ArrayList<Place> getPhysiotherapy() {
		return physiotherapy;
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
		
		Iterator<Place> itDoors = null;
		Iterator<Place> itScreens = null;
		Iterator<Place> itLabs = null;
		Iterator<Place> itRadiology = null;
		Iterator<Place> itPhysiotherapy = null;
		Iterator<Place> itRedArrows = null;
		Iterator<Place> itGreenArrows = null;
		Iterator<Place> itBlueArrows = null;
		Iterator<Place> itYellowArrows = null;
		Iterator<Place> itBlock = null;
		
		try {
			itDoors = world.getPlacesOfType("Door").iterator();		
			itScreens = world.getPlacesOfType("Screen").iterator();
			itLabs = world.getPlacesOfType("Lab").iterator();
			itRadiology = world.getPlacesOfType("Radiology").iterator();
			itPhysiotherapy = world.getPlacesOfType("Physiotherapy").iterator();
			itRedArrows = world.getPlacesOfType("RedArrow").iterator();
			itGreenArrows = world.getPlacesOfType("GreenArrow").iterator();
			itBlueArrows = world.getPlacesOfType("BlueArrow").iterator();
			itYellowArrows = world.getPlacesOfType("YellowArrow").iterator();
			itBlock = world.getPlacesOfType("Block").iterator();	
		} catch (PlaceTypeUndefinedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		leftEntrance = itDoors.next();
		rightEntrance = itDoors.next();
		block = itBlock.next();
		
		while (itScreens.hasNext()) {
			addScreen(itScreens.next());
		}
		
		
		while (itLabs.hasNext()){
			addLab(itLabs.next());
		}
		while (itRadiology.hasNext()) {
			addRadiology(itRadiology.next());
		}
		while (itPhysiotherapy.hasNext()) {
			addPhysiotherapy(itPhysiotherapy.next());
		}
		
		while (itRedArrows.hasNext()){
			addRedArrow(itRedArrows.next());
		}
		while (itBlueArrows.hasNext()) {
			addBlueArrow(itBlueArrows.next());
		}
		while (itGreenArrows.hasNext()) {
			addGreenArrow(itGreenArrows.next());
		}
		while (itYellowArrows.hasNext()) {
			addYellowArrow(itYellowArrows.next());
		}
		
		manager = new RoutingManager(this);
		
	
	}

	/**
	 * Schedule a daily meeting, and ensure all the necessary Agents are
	 * invited over to it when the time comes.
	 * 
	 * @param places the places in the simulation.
	 */
	public void doIteration(final Collection<Place> places) {
		
		ArrayList<Trackable> agents = null;
		boolean red = false;
		boolean green = false;
		boolean blue = false;
		boolean yellow = false;
		
		for (int index = 0; index < 6; index++) {
			try {
				agents = world.findAllAgentsNear(screens.get(index).getPos(), 70, false);
				for (int i = 0; i < agents.size(); i++) {
					Agent ag = (Agent) agents.get(i);
					if (((Text) ag.get(TYPE)).getText().equalsIgnoreCase("Magenta")) {
						red = true;
						if (!((MixedNode) ag.get(NODE)).getFollowerRole("red" + index).isActive()) {
							((RedFollower) ((MixedNode) ag.get(NODE)).getFollowerRole("red" + index)).setScreen(screens.get(index));
							((MixedNode) ag.get(NODE)).joinGroup("red" + index);
						}
					} else if (((Text) ag.get(TYPE)).getText().equalsIgnoreCase("Blue")) {
						blue = true;
						if (!((MixedNode) ag.get(NODE)).getFollowerRole("blue" + index).isActive()) {
							((BlueFollower) ((MixedNode) ag.get(NODE)).getFollowerRole("blue" + index)).setScreen(screens.get(index));
							((MixedNode) ag.get(NODE)).joinGroup("blue" + index);
						}
					} else if (((Text) ag.get(TYPE)).getText().equalsIgnoreCase("Green")) {
						green = true;
						if (!((MixedNode) ag.get(NODE)).getFollowerRole("green" + index).isActive()) {
							((GreenFollower) ((MixedNode) ag.get(NODE)).getFollowerRole("green" + index)).setScreen(screens.get(index));
							((MixedNode) ag.get(NODE)).joinGroup("green" + index);
						}
					} else if (((Text) ag.get(TYPE)).getText().equalsIgnoreCase("Yellow")) {
						yellow = true;
						if (!((MixedNode) ag.get(NODE)).getFollowerRole("yellow" + index).isActive()) {
							((YellowFollower) ((MixedNode) ag.get(NODE)).getFollowerRole("yellow" + index)).setScreen(screens.get(index));
							((MixedNode) ag.get(NODE)).joinGroup("yellow" + index);
						}
					}

					if (red) {
						redIndicators.get(index).setDir(((MixedNode) redIndicators.get(index).get(NODE)).getRedDir());
					} else {
						redIndicators.get(index).setDir(1);
					}
					if (green) {
						greenIndicators.get(index).setDir(((MixedNode) greenIndicators.get(index).get(NODE)).getGreenDir());
					} else {
						greenIndicators.get(index).setDir(1);
					}
					if (blue) {
						blueIndicators.get(index).setDir(((MixedNode) blueIndicators.get(index).get(NODE)).getBlueDir());
					} else {
						blueIndicators.get(index).setDir(1);
					}
					if (yellow) {
						yellowIndicators.get(index).setDir(((MixedNode) yellowIndicators.get(index).get(NODE)).getYellowDir());
					} else {
						yellowIndicators.get(index).setDir(1);
					}
					
				}
			} catch (Exception e) {
			}
		
			red = false;
			green = false;
			blue = false;
			yellow = false;
		}
		
		
		

	}

}
