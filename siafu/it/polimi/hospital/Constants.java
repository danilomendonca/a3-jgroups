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

import de.nec.nle.siafu.types.FlatData;
import de.nec.nle.siafu.types.Publishable;
import de.nec.nle.siafu.types.Text;

/**
 * A list of the constants used by this simulation. None of this is strictly
 * needed, but it makes referring to certain values easier and less error
 * prone.
 * 
 * @author Miquel Martin
 */
public class Constants {

	/** Default agent speed. */
	public static final int DEFAULT_SPEED = 6;

	/** Amount of people. */
	public static final int POPULATION = 4;

	/**
	 * The names of the fields in each agent object.
	 */
	static class Fields {
		/** The agent's current activity. */
		public static final String ACTIVITY = "Activity";

		/** Whether the person's staff or student. */
		public static final String TYPE = "Type";
		
		/** The agent's order of creation */
		public static final String NUMBER = "Number";
		
		/** The agent's time to leave the location. */
		public static final String TIME = "Time";
		
		/** The agent's A3Node that contains the roles. */
		public static final String NODE = "Node";
	}

	/**
	 * List of possible activies. This is implemented as an enum because it
	 * helps us in switch statements. Like the rest of the constants in this
	 * class, they could also have been coded directly in the model
	 */
	enum Activity implements Publishable {
		
		/** The agent is going to a screen. */
		WALKING("Walking"),
		/** The screen is inactive. */
		INACTIVE("Inactive"),
		/** The agent is out of hospital. */
		OUT("Out"),
		/** The agent is waiting for new destination. */
		WAITING("Waiting");

		/** Human readable description of the activity. */
		private String description;

		/**
		 * Get the description of the activity.
		 * 
		 * @return a string describing the activity
		 */
		public String toString() {
			return description;
		}

		/**
		 * Build an instance of Activity which keeps a human readable
		 * description for when it's flattened.
		 * 
		 * @param description the humanreadable description of the activity
		 */
		private Activity(final String description) {
			this.description = description;
		}

		/**
		 * Flatten the description of the activity.
		 * 
		 * @return a flatenned text with the description of the activity
		 */
		public FlatData flatten() {
			return new Text(description).flatten();
		}
	}
}
