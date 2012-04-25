package jgexample1;

public class Launch {

	public static void main(String[] args){
		
		try {
			RedNode node1 = new RedNode("red1");
			node1.addSupervisorRole("red", new RedSupervisor(1,"red"));
			node1.joinGroup("red");
			
			RedNode node2 = new RedNode("red2");
			node2.addFollowerRole("red", new RedFollower(1,"red"));
			node2.joinGroup("red");

			Thread.sleep(10000);

			RedNode node3 = new RedNode("red3");
			node3.addSupervisorRole("red", new RedSupervisor(1,"red"));
			node3.addFollowerRole("red", new RedFollower(1,"red"));
			node3.joinGroup("red");
			
			Thread.sleep(10000);
			
			node1.terminate("red", true);
			
			
			Thread.sleep(10000);
			
			node3.terminate("red", true);	 

			Thread.sleep(5000);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.exit(0);
	}

}
