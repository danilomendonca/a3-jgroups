package jgexample2;


public class Launch2 {

	public static void main(String[] args){
		
		try {

			MixedNode node1 = new MixedNode(middleware, "red1");
			node1.addSupervisorRole("red", new RedSupervisor(), node1.getID());
			node1.setup("red");

			MixedNode node2 = new MixedNode(middleware, "red2");
			node2.addFollowerRole("red", new RedFollower(), node2.getID());
			node2.setup("red");

			Thread.sleep(10000);

			MixedNode node3 = new MixedNode(middleware, "red3");
			node3.addSupervisorRole("red", new RedSupervisor(), node3.getID());
			node3.addFollowerRole("red", new RedFollower(), node3.getID());
			node3.setup("red");

			MixedNode node4 = new MixedNode(middleware, "blue1");
			node4.addSupervisorRole("blue", new BlueSupervisor(), node4.getID());
			node4.setup("blue");

			MixedNode node5 = new MixedNode(middleware, "blue2");
			node5.addFollowerRole("blue", new BlueFollower(), node5.getID());
			node5.setup("blue");

			Thread.sleep(10000);

			node1.exit("red");

			MixedNode node6 = new MixedNode(middleware, "blue&red");
			node6.addFollowerRole("blue", new BlueFollower(), node6.getID());
			node6.addSupervisorRole("red", new RedSupervisor(), node6.getID());
			node6.addFollowerRole("red", new RedFollower(), node6.getID());
			node6.setup("blue");
			node6.setup("red");

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
