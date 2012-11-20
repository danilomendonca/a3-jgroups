package a3jg.human;

import A3JGroups.A3JGroup;
import a3jg.roles.BlueFollower;
import a3jg.roles.BlueSupervisor;
import a3jg.roles.GreenFollower;
import a3jg.roles.GreenSupervisor;
import a3jg.roles.RedFollower;
import a3jg.roles.RedSupervisor;
import a3jg.roles.SubBlueFollower;
import a3jg.roles.SubBlueSupervisor;
import a3jg.roles.SubGreenFollower;
import a3jg.roles.SubGreenSupervisor;
import a3jg.roles.SubRedFollower;
import a3jg.roles.SubRedSupervisor;
import a3jg.roles.SubYellowFollower;
import a3jg.roles.SubYellowSupervisor;
import a3jg.roles.YellowFollower;
import a3jg.roles.YellowSupervisor;

/**
 * This code generates an human that crossing a Screen receive the information 
 * for the path to his destination. The A3JG library is modified in order to
 * save information on a file about the election in the SubRed group.
 * 
 * @author bett.marco88@gmail.com
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		humanity("_A");
		
	}
	
	public static void humanity(String name){
		
		A3JGroup redInfo = new A3JGroup(RedSupervisor.class.getCanonicalName(), RedFollower.class.getCanonicalName());
		A3JGroup blueInfo = new A3JGroup(BlueSupervisor.class.getCanonicalName(), BlueFollower.class.getCanonicalName());
		A3JGroup greenInfo = new A3JGroup(GreenSupervisor.class.getCanonicalName(), GreenFollower.class.getCanonicalName());
		A3JGroup yellowInfo = new A3JGroup(YellowSupervisor.class.getCanonicalName(), YellowFollower.class.getCanonicalName());
		A3JGroup subredInfo = new A3JGroup(SubRedSupervisor.class.getCanonicalName(), SubRedFollower.class.getCanonicalName());
		A3JGroup subblueInfo = new A3JGroup(SubBlueSupervisor.class.getCanonicalName(), SubBlueFollower.class.getCanonicalName());
		A3JGroup subgreenInfo = new A3JGroup(SubGreenSupervisor.class.getCanonicalName(), SubGreenFollower.class.getCanonicalName());
		A3JGroup subyellowInfo = new A3JGroup(SubYellowSupervisor.class.getCanonicalName(), SubYellowFollower.class.getCanonicalName());

		redInfo.addGroupConnection("red.xml");
		subredInfo.addGroupConnection("subred.xml");
		blueInfo.addGroupConnection("blue.xml");
		subblueInfo.addGroupConnection("subblue.xml");
		greenInfo.addGroupConnection("green.xml");
		subgreenInfo.addGroupConnection("subgreen.xml");
		yellowInfo.addGroupConnection("yellow.xml");
		subyellowInfo.addGroupConnection("subyellow.xml");
	
		HumanNode human[] = {new HumanNode("Hu0"+name),new HumanNode("Hu1"+name),new HumanNode("Hu2"+name),
			new HumanNode("Hu3"+name),new HumanNode("Hu4"+name),new HumanNode("Hu5"+name),
			new HumanNode("Hu6"+name),new HumanNode("Hu7"+name),
			new HumanNode("Hu8"+name),new HumanNode("Hu9"+name), 
			new HumanNode("Hu10"+name),new HumanNode("Hu11"+name),new HumanNode("Hu12"+name),
			new HumanNode("Hu13"+name),new HumanNode("Hu14"+name),new HumanNode("Hu15"+name),
			new HumanNode("Hu16"+name),new HumanNode("Hu17"+name),
			new HumanNode("Hu18"+name),new HumanNode("Hu19"+name),
			new HumanNode("Hu20"+name),new HumanNode("Hu21"+name),new HumanNode("Hu22"+name),
			new HumanNode("Hu23"+name),new HumanNode("Hu24"+name),new HumanNode("Hu25"+name),
			new HumanNode("Hu26"+name),new HumanNode("Hu27"+name),
			new HumanNode("Hu28"+name),new HumanNode("Hu29"+name),
			new HumanNode("Hu30"+name),new HumanNode("Hu31"+name),new HumanNode("Hu32"+name),
			new HumanNode("Hu33"+name),new HumanNode("Hu34"+name),new HumanNode("Hu35"+name),
			new HumanNode("Hu36"+name),new HumanNode("Hu37"+name),
			new HumanNode("Hu38"+name),new HumanNode("Hu39"+name),
			new HumanNode("Hu40"+name),new HumanNode("Hu41"+name),new HumanNode("Hu42"+name),
			new HumanNode("Hu43"+name),new HumanNode("Hu44"+name),new HumanNode("Hu45"+name),
			new HumanNode("Hu46"+name),new HumanNode("Hu47"+name),
			new HumanNode("Hu48"+name),new HumanNode("Hu49"+name)};
	
		for (int i = 0; i < 50; i++) {
			human[i].addGroupInfo("Red", redInfo);
			human[i].addGroupInfo("Blue", blueInfo);
			human[i].addGroupInfo("Green", greenInfo);
			human[i].addGroupInfo("Yellow", yellowInfo);
			human[i].addGroupInfo("SubRed", subredInfo);
			human[i].addGroupInfo("SubBlue", subblueInfo);
			human[i].addGroupInfo("SubGreen", subgreenInfo);
			human[i].addGroupInfo("SubYellow", subyellowInfo);

			human[i].addFollowerRole(new RedFollower(1));
			human[i].addFollowerRole(new GreenFollower(1));
			human[i].addFollowerRole(new BlueFollower(1));
			human[i].addFollowerRole(new YellowFollower(1));
			human[i].addFollowerRole(new SubRedFollower(1));
			human[i].addFollowerRole(new SubGreenFollower(1));
			human[i].addFollowerRole(new SubBlueFollower(1));
			human[i].addFollowerRole(new SubYellowFollower(1));
			human[i].addSupervisorRole(new SubRedSupervisor(1));
			human[i].addSupervisorRole(new SubBlueSupervisor(1));
			human[i].addSupervisorRole(new SubGreenSupervisor(1));
			human[i].addSupervisorRole(new SubYellowSupervisor(1));
		}

		try {
			//this loop creates the human, the value in his condition must be between 1 and 50
			for (int i = 0; i < 30; i++) {
				Thread.sleep(1000);
				new Thread(new Terminator(human[i],i)).start();				
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		while(true){
			
		}
	}
}
