package jgexample2;

import jgexample1.RedFollower;
import jgexample1.RedSupervisor;
import A3JGroups.A3JGroup;


public class Launch2 {

	public static void main(String[] args){
		
		try {
			A3JGroup groupInfo = new A3JGroup(RedSupervisor.class.getCanonicalName(),
					RedFollower.class.getCanonicalName());
			A3JGroup groupInfo2 = new A3JGroup(BlueSupervisor.class.getCanonicalName(),
					BlueFollower.class.getCanonicalName());
			
			MixedNode node1 = new MixedNode("red1");
			node1.addGroupInfo("red", groupInfo);
			node1.addSupervisorRole(new RedSupervisor(1));
			node1.joinGroup("red");

			MixedNode node2 = new MixedNode("red2");
			node2.addGroupInfo("red", groupInfo);
			node2.addFollowerRole(new RedFollower(1));
			node2.joinGroup("red");

			Thread.sleep(10000);

			MixedNode node3 = new MixedNode("red3");
			node3.addGroupInfo("red", groupInfo);
			node3.addSupervisorRole(new RedSupervisor(1));
			node3.addFollowerRole(new RedFollower(1));
			node3.joinGroup("red");

			MixedNode node4 = new MixedNode("blue1");
			node4.addGroupInfo("blue", groupInfo2);
			node4.addSupervisorRole(new BlueSupervisor(2));
			node4.joinGroup("blue");

			MixedNode node5 = new MixedNode("blue2");
			node5.addGroupInfo("blue", groupInfo2);
			node5.addFollowerRole(new BlueFollower(2));
			node5.joinGroup("blue");

			Thread.sleep(10000);

			node1.terminate("red");

			MixedNode node6 = new MixedNode("blue&red");
			node6.addGroupInfo("red", groupInfo);
			node6.addGroupInfo("blue", groupInfo2);
			node6.addFollowerRole(new BlueFollower(2));
			node6.addSupervisorRole(new RedSupervisor(1));
			node6.addFollowerRole(new RedFollower(1));
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
