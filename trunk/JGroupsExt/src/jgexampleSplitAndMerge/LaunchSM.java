package jgexampleSplitAndMerge;


import A3JGroups.A3JGroup;

public class LaunchSM {

	public static void main(String[] args){
		
		try {
			A3JGroup groupInfo = new A3JGroup(RedSupervisor.class.getCanonicalName(), RedFollower.class.getCanonicalName());
			groupInfo.addGroupConnection("utils/udp.xml");
			
			RedNode node1 = new RedNode("red1");
			node1.addGroupInfo("red", groupInfo);
			node1.addSupervisorRole(new RedSupervisor(1));
			node1.joinGroup("red");
			
			RedNode node2 = new RedNode("red2");
			node2.addGroupInfo("red", groupInfo);
			node2.addFollowerRole(new RedFollower(1));
			node2.joinGroup("red");
			
			RedNode node3 = new RedNode("red3");
			node3.addGroupInfo("red", groupInfo);
			node3.addSupervisorRole(new RedSupervisor(1));
			node3.addFollowerRole(new RedFollower(1));
			node3.joinGroup("red");
			
			Thread.sleep(25000);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.exit(0);
	}

}
