package jgexample1;

import A3JGroups.A3JGMiddleware;




public class Launch {

	public static void main(String[] args){
		
		try {
			A3JGMiddleware middleware = new A3JGMiddleware();
			
			RedNode node1 = new RedNode("red1", middleware);
			node1.addSupervisorRole("red", new RedSupervisor(), node1.getID());
			node1.joinGroup("red");
			
			RedNode node2 = new RedNode("red2", middleware);
			node2.addFollowerRole("red", new RedFollower(), node2.getID());
			node2.joinGroup("red");

			Thread.sleep(10000);

			RedNode node3 = new RedNode("red3", middleware);
			node3.addSupervisorRole("red", new RedSupervisor(), node3.getID());
			node3.addFollowerRole("red", new RedFollower(), node3.getID());
			node3.joinGroup("red");
			
			Thread.sleep(10000);
			
			node1.exit("red");
			
			
			Thread.sleep(10000);
			
			node3.exit("red");		 

			Thread.sleep(5000);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.exit(0);
	}

}
