package jgexample1;

import A3JGroups.A3JGroup;

public class Prova {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		A3JGroup groupInfo = new A3JGroup(RedSupervisor.class.getCanonicalName(), RedFollower.class.getCanonicalName());
		
		RedNode node1 = new RedNode("red1");
		
		RedSupervisor red1 = new RedSupervisor(1);
		red1.setFitness(3);
		RedSupervisor red2 = new RedSupervisor(1);
		red2.setFitness(4);
		RedSupervisor red3 = new RedSupervisor(1);
		red3.setFitness(2);
		RedSupervisor red4 = new RedSupervisor(1);
		red4.setFitness(6);
		RedSupervisor red5 = new RedSupervisor(1);
		red5.setFitness(3);
		RedSupervisor red6 = new RedSupervisor(1);
		red6.setFitness(10);
		node1.addGroupInfo("red", groupInfo);
		
		node1.addSupervisorRole(red1);
		node1.addFollowerRole(new RedFollower(1));
		node1.joinGroup("red");
		RedNode node2 = new RedNode("red2");
		node2.addGroupInfo("red", groupInfo);
		node2.addSupervisorRole(red2);
		node2.addFollowerRole(new RedFollower(1));
		node2.joinGroup("red");
		RedNode node3 = new RedNode("red3");
		node3.addGroupInfo("red", groupInfo);
		node3.addSupervisorRole(red3);
		node3.addFollowerRole(new RedFollower(1));
		node3.joinGroup("red");
		RedNode node4 = new RedNode("red4");
		node4.addGroupInfo("red", groupInfo);
		node4.addSupervisorRole(red4);
		node4.addFollowerRole(new RedFollower(1));
		node4.joinGroup("red");
		RedNode node5 = new RedNode("red5");
		node5.addGroupInfo("red", groupInfo);
		node5.addSupervisorRole(red5);
		node5.addFollowerRole(new RedFollower(1));
		node5.joinGroup("red");

		
		Thread.sleep(2000);
		
		Thread.sleep(2000);
		
		node1.terminate("red");
		
		Thread.sleep(2000);
		
		
		
		Thread.sleep(2000);
		
		RedNode node6 = new RedNode("red6");
		node6.addGroupInfo("red", groupInfo);
		node6.addSupervisorRole(red6);
		node6.addFollowerRole(new RedFollower(1));
		node6.joinGroup("red");
		
		Thread.sleep(12000);
		
	}

}
