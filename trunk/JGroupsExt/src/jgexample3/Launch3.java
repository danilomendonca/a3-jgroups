package jgexample3;

import A3JGroups.A3JGMiddleware;

public class Launch3 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {

			A3JGMiddleware middleware = new A3JGMiddleware();

			MixedNode node1 = new MixedNode(middleware, "rbg");
			node1.addSupervisorRole("red", new RedSupervisor(), node1.getID());
			node1.addSupervisorRole("blue", new BlueSupervisor(), node1.getID());
			node1.addSupervisorRole("green", new GreenSupervisor(), node1.getID());
			node1.setup("red");
			node1.setup("blue");
			node1.setup("green");

			MixedNode node2 = new MixedNode(middleware, "red1");
			node2.addFollowerRole("red", new RedFollower(), node2.getID());
			node2.setup("red");

			MixedNode node3 = new MixedNode(middleware, "blue1");
			node3.addFollowerRole("blue", new BlueFollower(), node3.getID());
			node3.setup("blue");

			MixedNode node4 = new MixedNode(middleware, "green1");
			node4.addFollowerRole("green", new GreenFollower(), node4.getID());
			node4.setup("green");

			Thread.sleep(10000);

			MixedNode node5 = new MixedNode(middleware, "red2");
			node5.addSupervisorRole("red", new RedSupervisor(), node5.getID());
			node5.addFollowerRole("red", new RedFollower(), node5.getID());
			node5.setup("red");

			MixedNode node6 = new MixedNode(middleware, "blue2");
			node6.addSupervisorRole("blue", new BlueSupervisor(), node6.getID());
			node6.setup("blue");

			Thread.sleep(10000);

			node1.exit("red");

			MixedNode node7 = new MixedNode(middleware, "blue&red");
			node7.addSupervisorRole("blue", new BlueSupervisor(), node7.getID());
			node7.addFollowerRole("blue", new BlueFollower(), node7.getID());
			node7.addSupervisorRole("red", new RedSupervisor(), node7.getID());
			node7.addFollowerRole("red", new RedFollower(), node7.getID());
			node7.setup("blue");
			node7.setup("red");

			Thread.sleep(10000);

			node1.exit("blue");
			node5.exit("red");

			MixedNode node8 = new MixedNode(middleware, "green");
			node8.addFollowerRole("green", new GreenFollower(), node8.getID());
			node8.setup("green");

			Thread.sleep(10000);
			
			System.out.println("\n*************killing green");
			node1.exit("green");
			
			Thread.sleep(5000);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.exit(0);
	}
}
