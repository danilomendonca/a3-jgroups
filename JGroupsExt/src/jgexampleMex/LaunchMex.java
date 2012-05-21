package jgexampleMex;


public class LaunchMex {
	
	public static void main(String[] args){
		
		try {
			
			RedNode node1 = new RedNode("red1");
			node1.addSupervisorRole("red", new RedSupervisor(1,"red"));
			node1.getSupervisorRole("red").setMessageDeleterWaitTime(1500);
			node1.joinGroup("red");
			
			
			RedNode node2 = new RedNode("red2");
			node2.addFollowerRole("red", new RedFollower(1,"red"));
			node2.joinGroup("red");

			Thread.sleep(4000);

			RedNode node3 = new RedNode("red3");
			node3.addSupervisorRole("red", new RedSupervisor(1,"red"));
			node3.addFollowerRole("red", new RedFollower(1,"red"));
			node3.joinGroup("red");
			
			Thread.sleep(5000); 
			
			System.out.println("++++++++++ Wait 3 minutes +++++++++++");
			Thread.sleep(10000);
			
			System.out.println("**************************** delete message 2 ****************************");
			node1.getSupervisorRole("red").removeMessage(2);
			Thread.sleep(100);
			System.out.println(((RedFollower) node2.getFollowerRole("red")).showMessage());
			
			Thread.sleep(200000);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.exit(0);
	}
}
