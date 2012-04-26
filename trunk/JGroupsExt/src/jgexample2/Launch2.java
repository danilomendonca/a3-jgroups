package jgexample2;


public class Launch2 {

	public static void main(String[] args){
		
		try {

			MixedNode node1 = new MixedNode("red1");
			node1.addSupervisorRole("red", new RedSupervisor(1, "red"));
			node1.joinGroup("red");

			MixedNode node2 = new MixedNode("red2");
			node2.addFollowerRole("red", new RedFollower(1,"red"));
			node2.joinGroup("red");

			Thread.sleep(10000);

			MixedNode node3 = new MixedNode("red3");
			node3.addSupervisorRole("red", new RedSupervisor(1,"red"));
			node3.addFollowerRole("red", new RedFollower(1,"red"));
			node3.joinGroup("red");

			MixedNode node4 = new MixedNode("blue1");
			node4.addSupervisorRole("blue", new BlueSupervisor(2,"blue"));
			node4.joinGroup("blue");

			MixedNode node5 = new MixedNode("blue2");
			node5.addFollowerRole("blue", new BlueFollower(2,"blue"));
			node5.joinGroup("blue");

			Thread.sleep(10000);

			node1.terminate("red");

			MixedNode node6 = new MixedNode("blue&red");
			node6.addFollowerRole("blue", new BlueFollower(2,"blue"));
			node6.addSupervisorRole("red", new RedSupervisor(1,"red"));
			node6.addFollowerRole("red", new RedFollower(1,"red"));
			node6.joinGroup("blue");
			node6.joinGroup("red");

			Thread.sleep(10000);

			node3.terminate("red");

			Thread.sleep(10000);

			node6.terminate("blue");

			Thread.sleep(5000);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.exit(0);
	}

}
