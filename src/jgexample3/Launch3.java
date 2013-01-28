package jgexample3;

import jgexample1.RedFollower;
import jgexample1.RedSupervisor;
import jgexample2.BlueFollower;
import jgexample2.BlueSupervisor;
import A3JGroups.A3JGroup;

public class Launch3 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			A3JGroup groupInfo = new A3JGroup(RedSupervisor.class.getCanonicalName(), RedFollower.class.getCanonicalName());
			A3JGroup groupInfo2 = new A3JGroup(BlueSupervisor.class.getCanonicalName(), BlueFollower.class.getCanonicalName());
			A3JGroup groupInfo3 = new A3JGroup(GreenSupervisor.class.getCanonicalName(), GreenFollower.class.getCanonicalName());
			
			MixedNode node1 = new MixedNode("rbg");
			node1.addGroupInfo("red", groupInfo);
			node1.addGroupInfo("blue", groupInfo2);
			node1.addGroupInfo("green", groupInfo3);
			node1.addSupervisorRole(new RedSupervisor(1));
			node1.addSupervisorRole(new BlueSupervisor(2));
			node1.addSupervisorRole(new GreenSupervisor(4));
			node1.joinGroup("red");
			node1.joinGroup("blue");
			node1.joinGroup("green");

			MixedNode node2 = new MixedNode("red1");
			node2.addGroupInfo("red", groupInfo);
			node2.addFollowerRole(new RedFollower(1));
			node2.joinGroup("red");

			MixedNode node3 = new MixedNode("blue1");
			node3.addGroupInfo("blue", groupInfo2);
			node3.addFollowerRole(new BlueFollower(2));
			node3.joinGroup("blue");

			MixedNode node4 = new MixedNode("green1");
			node4.addGroupInfo("green", groupInfo3);
			node4.addFollowerRole(new GreenFollower(4));
			node4.joinGroup("green");

			Thread.sleep(10000);

			MixedNode node5 = new MixedNode("red2");
			node5.addGroupInfo("red", groupInfo);
			node5.addSupervisorRole(new RedSupervisor(1));
			node5.addFollowerRole(new RedFollower(1));
			node5.joinGroup("red");

			MixedNode node6 = new MixedNode("blue2");
			node6.addGroupInfo("blue", groupInfo2);
			node6.addSupervisorRole(new BlueSupervisor(2));
			node6.joinGroup("blue");

			Thread.sleep(10000);

			node1.terminate("red");

			MixedNode node7 = new MixedNode("blue&red");
			node7.addGroupInfo("red", groupInfo);
			node7.addGroupInfo("blue", groupInfo2);
			node7.addSupervisorRole(new BlueSupervisor(2));
			node7.addFollowerRole(new BlueFollower(2));
			node7.addSupervisorRole(new RedSupervisor(1));
			node7.addFollowerRole(new RedFollower(1));
			node7.joinGroup("blue");
			node7.joinGroup("red");

			Thread.sleep(10000);
			System.out.println("killing blue     **********************************          *********************");
			node1.terminate("blue");
			node5.terminate("red");

			MixedNode node8 = new MixedNode("green");
			node8.addGroupInfo("green", groupInfo3);
			node8.addFollowerRole(new GreenFollower(4));
			node8.joinGroup("green");

			Thread.sleep(10000);
			
			System.out.println("\n*************killing green");
			node1.terminate("green");
			
			Thread.sleep(5000);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.exit(0);
	}
}
