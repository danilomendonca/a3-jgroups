package changeRoleExample;


import A3JGroups.A3JGroup;


public class LaunchChange {

	public static void main(String[] args){
		
		try {
			A3JGroup groupInfo = new A3JGroup(RedSupervisor.class.getCanonicalName(), RedFollower.class.getCanonicalName());
			groupInfo.addFollower(1, BlueFollower.class.getCanonicalName());
			groupInfo.addSupervisor(1, BlueSupervisor.class.getCanonicalName());
			
			MixedNode node1 = new MixedNode("mixed1");
			node1.addGroupInfo("mix", groupInfo);
			node1.addSupervisorRole(new RedSupervisor(1));
			node1.addSupervisorRole(new BlueSupervisor(1));
			node1.addFollowerRole(new RedFollower(1));
			node1.joinGroup("mix");

			MixedNode node2 = new MixedNode("mixed2");
			node2.addGroupInfo("mix", groupInfo);
			node2.addFollowerRole(new RedFollower(1));
			node2.addFollowerRole(new BlueFollower(1));
			node2.joinGroup("mix");

			Thread.sleep(7000);
			
			node1.getSupervisorRole(groupInfo.getSupervisor().get(0)).changeRoleInGroup(1);
			
			Thread.sleep(5000);
			
			MixedNode node3 = new MixedNode("mixed3");
			node3.addGroupInfo("mix", groupInfo);
			node3.addSupervisorRole(new RedSupervisor(1));
			node3.addFollowerRole(new RedFollower(1));
			node3.joinAsSupervisor("mix", true);
			
			Thread.sleep(7000);
			
			MixedNode node4 = new MixedNode("mixed4");
			node4.addGroupInfo("mix", groupInfo);
			node4.addSupervisorRole(new RedSupervisor(1));
			node4.addFollowerRole(new RedFollower(1));
			node4.joinAsSupervisor("mix", true);
		
			Thread.sleep(7000);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.exit(0);
	}

}
