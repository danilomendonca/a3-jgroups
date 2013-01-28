package testKB;

import A3JGroups.A3JGNode;
import A3JGroups.A3JGroup;

public class TestKB {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		A3JGroup testGroup = new A3JGroup(
				TestKBSupervisor.class.getCanonicalName(), null);

		A3JGNode node = new A3JGNode("Node1") {
		};
		node.addGroupInfo("Node1", testGroup);
		node.addSupervisorRole(new TestKBSupervisor(1, "testKB/TestKB.drl"));
		node.joinGroup("Node1");

	}

}
