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

import static it.polimi.hospital.Constants.Fields.ACTIVITY;
import static it.polimi.hospital.Constants.Fields.TYPE;
import static it.polimi.hospital.Constants.Fields.NUMBER;
import static it.polimi.hospital.Constants.Fields.TIME;
import static it.polimi.hospital.Constants.Fields.NODE;
import static it.polimi.hospital.Constants.POPULATION;

import it.polimi.a3Behavior.BlueFollower;
import it.polimi.a3Behavior.BlueSupervisor;
import it.polimi.a3Behavior.GreenFollower;
import it.polimi.a3Behavior.GreenSupervisor;
import it.polimi.a3Behavior.MixedNode;
import it.polimi.a3Behavior.RedFollower;
import it.polimi.a3Behavior.RedSupervisor;
import it.polimi.a3Behavior.YellowFollower;
import it.polimi.a3Behavior.YellowSupervisor;
import it.polimi.hospital.Constants.Activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;


import de.nec.nle.siafu.behaviormodels.BaseAgentModel;
import de.nec.nle.siafu.exceptions.InfoUndefinedException;
import de.nec.nle.siafu.model.Agent;
import de.nec.nle.siafu.model.Position;
import de.nec.nle.siafu.model.World;
import de.nec.nle.siafu.types.EasyTime;
import de.nec.nle.siafu.types.IntegerNumber;
import de.nec.nle.siafu.types.Text;

/**
 * This class extends the {@link BaseAgentModel} and implements the behaviour
 * of an agent in the office simulation.
 * 
 * @see it.polimi.hospital
 * @author miquel
 * 
 */
public class AgentModel extends BaseAgentModel {

	private WorldModel worldM;

	/**
	 * Instantiates this agent model.
	 * 
	 * @param world the simulation's world
	 */
	public AgentModel(final World world) {
		super(world);	
		worldM = (WorldModel)world.getWorldModel();
	}

	/**
	 * Create the agents for the Office simulation. There's two types: staff
	 * and students. The main difference lies in the amount of meetings they
	 * have to attend.
	 * 
	 * @return the created agents.
	 */
	@Override
	public ArrayList<Agent> createAgents() {
		ArrayList<Agent> people = new ArrayList<Agent>(POPULATION + 4);
		
		//create Red Indicator Agent
		Agent a = new Agent("redIndicator", worldM.getRedArrow().getPos(),"RedArrow", world);
		a.setSpeed(0);
		a.set(TYPE, new Text("redIndicator"));
		a.set(ACTIVITY, Activity.INACTIVE);
		a.set(NUMBER, new IntegerNumber(-1));
		a.set(TIME, null);
		a.setDir(1);
		people.add(a);
		worldM.setRedIndicator(a);
		System.out.println("Created red indicator ");
		
		
		//Create Green Indicator Agent
		Agent a2 = new Agent("greenIndicator", worldM.getGreenArrow().getPos(),"GreenArrow", world);
		a2.setSpeed(0);
		a2.set(TYPE, new Text("greenIndicator"));
		a2.set(ACTIVITY, Activity.INACTIVE);
		a2.set(NUMBER, new IntegerNumber(-1));
		a2.set(TIME, null);
		a2.setDir(1);
		people.add(a2);
		worldM.setGreenIndicator(a2);
		System.out.println("Created green indicator ");
		
		//Create Blue Indicator Agent
		Agent a3 = new Agent("blueIndicator", worldM.getBlueArrow().getPos(),"BlueArrow", world);
		a3.setSpeed(0);
		a3.set(TYPE, new Text("blueIndicator"));
		a3.set(ACTIVITY, Activity.INACTIVE);
		a3.set(NUMBER, new IntegerNumber(-1));
		a3.set(TIME, null);
		a3.setDir(1);
		people.add(a3);
		worldM.setBlueIndicator(a3);
		System.out.println("Created blue indicator ");
		
		//Create Yellow Indicator Agent
		Agent a4 = new Agent("yellowIndicator", worldM.getYellowArrow().getPos(),"YellowArrow", world);
		a4.setSpeed(0);
		a4.set(TYPE, new Text("yellowIndicator"));
		a4.set(ACTIVITY, Activity.INACTIVE);
		a4.set(NUMBER, new IntegerNumber(-1));
		a4.set(TIME, null);
		a4.setDir(1);
		people.add(a4);
		worldM.setYellowIndicator(a4);
		System.out.println("Created yellow indicator ");
		
		//Create ScreenNode with 4 SupervisorRole
		MixedNode node = new MixedNode("Screen");
		RedSupervisor redIndicator = new RedSupervisor(0, "red");
		GreenSupervisor greenIndicator = new GreenSupervisor(0, "green");
		BlueSupervisor blueIndicator = new BlueSupervisor(0, "blue");
		YellowSupervisor yellowIndicator = new YellowSupervisor(0, "yellow");
		redIndicator.setAgent(a);
		greenIndicator.setAgent(a2);
		blueIndicator.setAgent(a3);
		yellowIndicator.setAgent(a4);
		node.addSupervisorRole("red", redIndicator);
		node.addSupervisorRole("green", greenIndicator);
		node.addSupervisorRole("blue", blueIndicator);
		node.addSupervisorRole("yellow", yellowIndicator);
		a.set(NODE, node);
		a2.set(NODE, node);
		a3.set(NODE, node);
		a4.set(NODE, node);
		
		//Create people
		for (int i=0; i<POPULATION; i++) {
			
			Position pos = null;
			String type = null;
			
			//generate position and type
			int posNum = (int) (Math.random()*4);
			pos = worldM.getGreen(posNum).getPos();
			
			
			int typeNum = (int) (Math.random()*3);
			
			if (typeNum==0) {
				type = "Magenta";
			}
			else if (typeNum==1) {
				type = "Blue";
			}
			else {
				type = "Yellow";
			}
			
			Agent a5 =new Agent(type + i, pos, "Human" + type, world);
			a5.setVisible(false);
			a5.set(TYPE, new Text(type));
			a5.set(ACTIVITY, Activity.OUT);
			a5.set(NUMBER, new IntegerNumber(i));
			a5.set(TIME, null);
			a5.setSpeed(0);
			
			//create personNode
			MixedNode mixed = new MixedNode("p_"+i);
			RedFollower red = new RedFollower(0, "red");
			GreenFollower green = new GreenFollower(0, "green");
			BlueFollower blue = new BlueFollower(0, "bue");
			YellowFollower yellow = new YellowFollower(0, "yellow");
			red.setAgent(a5);
			red.setWorld(world);
			green.setAgent(a5);
			green.setWorld(world);
			blue.setAgent(a5);
			blue.setWorld(world);
			yellow.setAgent(a5);
			yellow.setWorld(world);
			mixed.addFollowerRole("red", red);
			mixed.addFollowerRole("green", green);
			mixed.addFollowerRole("blue", blue);
			mixed.addFollowerRole("yellow", yellow);
			a5.set(NODE, mixed);
			
			people.add(a5);
			
			System.out.println("Created person " + i + " - " + type + " - " + posNum);
		}
		
		try {
			node.joinGroup("red");
			node.joinGroup("green");
			node.joinGroup("blue");
			node.joinGroup("yellow");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return people;
	}

	


	
	/**
	 * Handle the agents by checking if they need to respond to an event
	 * 
	 * @param agents the people in the simulation
	 */
	@Override
	public void doIteration(final Collection<Agent> agents) {
		Iterator<Agent> peopleIt = agents.iterator();
		while (peopleIt.hasNext()) {
			handlePerson(peopleIt.next());
		}
	}

	/**
	 * Handle the people in the simulation.
	 * 
	 * @param a the agent to handle
	 * @param now the current time
	 */
	private void handlePerson(final Agent a) {
		
		Calendar time = world.getTime();
		EasyTime now =	new EasyTime(time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE));
		
		if (!a.isOnAuto()) {
			return; // This guy's being managed by the user interface
		}
		try {
			switch ((Activity) a.get(ACTIVITY)) {

			case WALKING:
					if (a.isAtDestination()) {
						a.set(ACTIVITY, Activity.WAITING);
						a.set(TIME, now.shift(0, 20));
					}
				break;
			
			case WAITING:
				if (now.isAfter((EasyTime) a.get(TIME))) {
					int colour = (int) (Math.random() * 4);
					if (colour == 0 && !((Text) a.get(TYPE)).getText().equalsIgnoreCase("Magenta")) {
						a.set(TYPE, new Text("Magenta"));
						a.setImage("HumanMagenta");

					} else if (colour == 1 && !((Text) a.get(TYPE)).getText().equalsIgnoreCase("Blue")) {
						a.set(TYPE, new Text("Blue"));
						a.setImage("HumanBlue");

					} else if (colour == 2 && !((Text) a.get(TYPE)).getText().equalsIgnoreCase("Yellow")) {
						a.set(TYPE, new Text("Yellow"));
						a.setImage("HumanYellow");

					} else {
						a.set(TYPE, new Text("Green"));
						a.setImage("HumanGreen");
					}
					a.setDestination(worldM.getScreen());
					a.set(ACTIVITY, Activity.WALKING);
				}else{
					if(((Text) a.get(TYPE)).getText().equalsIgnoreCase("Magenta"))
						a.wanderAround(worldM.getRed((int)(Math.random()*4)), 100, 10);
					else if(((Text) a.get(TYPE)).getText().equalsIgnoreCase("Blue"))
						a.wanderAround(worldM.getBlue((int)(Math.random()*4)), 100, 5);
					else if(((Text) a.get(TYPE)).getText().equalsIgnoreCase("Yellow"))
						a.wanderAround(worldM.getYellow((int)(Math.random()*4)), 100, 1);
					else
						a.wanderAround(worldM.getGreen((int)(Math.random()*4)), 100, 1);
				}
				break;
				
			case INACTIVE:
				break;
				
			case OUT:
				int div = 2;
				int rand = 100;
				boolean start = false;
				int visible = (int) (Math.random()*rand);
				if(visible==0 && now.getMinute()%div==0){
					a.setSpeed(2 + (int) (Math.random()*3));
					start=true;
				}
				
				if (start) {
					int typeNum = (int) (Math.random()*3);
					String type;
					if (typeNum==0) {
						type = "Magenta";
					}
					else if (typeNum==1) {
						type = "Blue";
					}
					else {
						type = "Yellow";
					}
					a.set(TYPE, new Text(type));
					a.setImage("Human"+type);
					a.setDestination(worldM.getScreen());
					a.set(ACTIVITY, Activity.WALKING);
					a.setVisible(true);
					
				}
				
				break;
				

			default:
				throw new RuntimeException("Unknown Activity");
			}

		} catch (InfoUndefinedException e) {
			throw new RuntimeException("Unknown info requested for " + a, e);
		}
	}
	

}
