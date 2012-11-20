package a3jg.screen;

import A3JGroups.A3JGroup;
import a3jg.roles.BlueFollower;
import a3jg.roles.BlueSupervisor;
import a3jg.roles.GreenFollower;
import a3jg.roles.GreenSupervisor;
import a3jg.roles.RedFollower;
import a3jg.roles.RedSupervisor;
import a3jg.roles.YellowFollower;
import a3jg.roles.YellowSupervisor;

/**
 * This class creates a Screen able to help people to find the 
 * correct direction for their destination.
 * 
 * @author bett.marco88@gmail.com
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		A3JGroup redInfo = new A3JGroup(RedSupervisor.class.getCanonicalName(), RedFollower.class.getCanonicalName());
		A3JGroup blueInfo = new A3JGroup(BlueSupervisor.class.getCanonicalName(), BlueFollower.class.getCanonicalName());
		A3JGroup greenInfo = new A3JGroup(GreenSupervisor.class.getCanonicalName(), GreenFollower.class.getCanonicalName());
		A3JGroup yellowInfo = new A3JGroup(YellowSupervisor.class.getCanonicalName(), YellowFollower.class.getCanonicalName());
		
		redInfo.addGroupConnection("red.xml");
		blueInfo.addGroupConnection("blue.xml");
		greenInfo.addGroupConnection("green.xml");
		yellowInfo.addGroupConnection("yellow.xml");
		
		ScreenNode screen = new ScreenNode("Screen");
		
		screen.addGroupInfo("Red", redInfo);
		screen.addGroupInfo("Blue", blueInfo);
		screen.addGroupInfo("Green", greenInfo);
		screen.addGroupInfo("Yellow", yellowInfo);
		
		screen.addSupervisorRole(new RedSupervisor(1));
		screen.addSupervisorRole(new BlueSupervisor(1));
		screen.addSupervisorRole(new GreenSupervisor(1));
		screen.addSupervisorRole(new YellowSupervisor(1));
		
		try {
			screen.joinGroup("Red");
			screen.joinGroup("Blue");
			screen.joinGroup("Green");
			screen.joinGroup("Yellow");
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		while(true){
		}
	}

}
