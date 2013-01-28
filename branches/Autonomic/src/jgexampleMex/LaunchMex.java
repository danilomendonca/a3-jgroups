package jgexampleMex;

import A3JGroups.A3JGroup;


public class LaunchMex {
	
	public static void main(String[] args){
		
		try {
			A3JGroup groupInfo = new A3JGroup(RedSupervisor.class.getCanonicalName(), RedFollower.class.getCanonicalName());
			
			RedNode node1 = new RedNode("red1");
			node1.addGroupInfo("red", groupInfo);
			node1.addSupervisorRole(new RedSupervisor(1));
			node1.getSupervisorRole("red").setMessageDeleterWaitTime(1500);
			node1.joinGroup("red");
			
			
			RedNode node2 = new RedNode("red2");
			RedFollower red2 = new RedFollower(1);
			node2.addFollowerRole(red2);
			node2.joinGroup("red");

			Thread.sleep(4000);

			RedNode node3 = new RedNode("red3");
			node3.addSupervisorRole(new RedSupervisor(1));
			node3.addFollowerRole(new RedFollower(1));
			node3.joinGroup("red");
			
			Thread.sleep(5000); 
			
			System.out.println("++++++++++ Wait 3 minutes +++++++++++");
			Thread.sleep(10000);
			
			System.out.println("**************************** delete message 2 ****************************");
			node1.getSupervisorRole("red").removeMessage(2);
			Thread.sleep(100);
			System.out.println(red2.showMessage());
			
			Thread.sleep(200000);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.exit(0);
	}
}
