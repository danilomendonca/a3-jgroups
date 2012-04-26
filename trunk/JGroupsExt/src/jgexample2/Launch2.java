package jgexample2;


public class Launch2 {

	public static void main(String[] args){
		
		try {

			MixedNode node1 = new MixedNode("red1");
			node1.addSupervisorRole("red", new RedSupervisor(1, "red"));
			node1.joinGroup("red");

			MixedNode node2 = new MixedNode(middleware, "red2");
			node2.addFollowerRole("red", new RedFollower(1,"red"));
			node2.joinGroup("red");

			Thread.sleep(10000);

			MixedNode node3 = new MixedNode(middleware, "red3");
			node3.addSupervisorRole("red", new RedSupervisor(), node3.getID());
			node3.addFollowerRole("red", new RedFollower(), node3.getID());
			node3.joinGroup("red");

			MixedNode node4 = new MixedNode(middleware, "blue1");
			node4.addSupervisorRole("blue", new BlueSupervisor(), node4.getID());
			node4.joinGroup("blue");

			MixedNode node5 = new MixedNode(middleware, "blue2");
			node5.addFollowerRole("blue", new BlueFollower(), node5.getID());
			node5.joinGroup("blue");

			Thread.sleep(10000);

			node1.terminate("red", true);

			MixedNode node6 = new MixedNode(middleware, "blue&red");
			node6.addFollowerRole("blue", new BlueFollower(), node6.getID());
			node6.addSupervisorRole("red", new RedSupervisor(), node6.getID());
			node6.addFollowerRole("red", new RedFollower(), node6.getID());
			node6.joinGroup("blue");
			node6.joinGroup("red");

			Thread.sleep(10000);

			node3.exit("red");

			Thread.sleep(10000);

			node6.exit("blue");

			Thread.sleep(5000);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.exit(0);
	}

}
