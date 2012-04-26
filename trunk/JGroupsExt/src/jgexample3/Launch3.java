package jgexample3;

public class Launch3 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {

			MixedNode node1 = new MixedNode("rbg");
			node1.addSupervisorRole("red", new RedSupervisor(1,"red"));
			node1.addSupervisorRole("blue", new BlueSupervisor(2,"blue"));
			node1.addSupervisorRole("green", new GreenSupervisor(4,"green"));
			node1.joinGroup("red");
			node1.joinGroup("blue");
			node1.joinGroup("green");

			MixedNode node2 = new MixedNode("red1");
			node2.addFollowerRole("red", new RedFollower(1,"red"));
			node2.joinGroup("red");

			MixedNode node3 = new MixedNode("blue1");
			node3.addFollowerRole("blue", new BlueFollower(2,"blue"));
			node3.joinGroup("blue");

			MixedNode node4 = new MixedNode("green1");
			node4.addFollowerRole("green", new GreenFollower(4,"green"));
			node4.joinGroup("green");

			Thread.sleep(10000);

			MixedNode node5 = new MixedNode("red2");
			node5.addSupervisorRole("red", new RedSupervisor(1,"red"));
			node5.addFollowerRole("red", new RedFollower(1,"red"));
			node5.joinGroup("red");

			MixedNode node6 = new MixedNode("blue2");
			node6.addSupervisorRole("blue", new BlueSupervisor(2,"blue"));
			node6.joinGroup("blue");

			Thread.sleep(10000);

			node1.terminate("red");

			MixedNode node7 = new MixedNode("blue&red");
			node7.addSupervisorRole("blue", new BlueSupervisor(2,"blue"));
			node7.addFollowerRole("blue", new BlueFollower(2,"blue"));
			node7.addSupervisorRole("red", new RedSupervisor(1,"red"));
			node7.addFollowerRole("red", new RedFollower(1,"red"));
			node7.joinGroup("blue");
			node7.joinGroup("red");

			Thread.sleep(10000);
			System.out.println("killing blue     **********************************          *********************");
			node1.terminate("blue");
			node5.terminate("red");

			MixedNode node8 = new MixedNode("green");
			node8.addFollowerRole("green", new GreenFollower(4,"green"));
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
