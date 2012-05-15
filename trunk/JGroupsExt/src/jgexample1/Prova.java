package jgexample1;

public class Prova {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		RedNode node1 = new RedNode("red1");
		
		RedSupervisor red1 = new RedSupervisor(1,"red");
		red1.setFitness(3);
		RedSupervisor red2 = new RedSupervisor(1,"red");
		red2.setFitness(4);
		RedSupervisor red3 = new RedSupervisor(1,"red");
		red3.setFitness(2);
		RedSupervisor red4 = new RedSupervisor(1,"red");
		red4.setFitness(6);
		RedSupervisor red5 = new RedSupervisor(1,"red");
		red5.setFitness(3);
		RedSupervisor red6 = new RedSupervisor(1,"red");
		red6.setFitness(10);
		
		node1.addSupervisorRole("red", red1);
		node1.addFollowerRole("red", new RedFollower(1,"red"));
		node1.joinGroup("red");
		RedNode node2 = new RedNode("red2");
		node2.addSupervisorRole("red", red2);
		node2.addFollowerRole("red", new RedFollower(1,"red"));
		node2.joinGroup("red");
		RedNode node3 = new RedNode("red3");
		node3.addSupervisorRole("red", red3);
		node3.addFollowerRole("red", new RedFollower(1,"red"));
		node3.joinGroup("red");
		RedNode node4 = new RedNode("red4");
		node4.addSupervisorRole("red", red4);
		node4.addFollowerRole("red", new RedFollower(1,"red"));
		((RedFollower) node2.getFollowerRole("red")).prova=true;
		node4.joinGroup("red");
		RedNode node5 = new RedNode("red5");
		node5.addSupervisorRole("red", red5);
		node5.addFollowerRole("red", new RedFollower(1,"red"));
		node5.joinGroup("red");

		
		Thread.sleep(2000);
		
		System.out.println("scrivo sulla mappa***********************************************************");
		((RedSupervisor) node1.getSupervisorRole("red")).writeOnMap();
		
		Thread.sleep(2000);
		
		node1.terminate("red");
		
		Thread.sleep(2000);
		
		
		
		Thread.sleep(2000);
		
		RedNode node6 = new RedNode("red6");
		node6.addSupervisorRole("red", red6);
		node6.addFollowerRole("red", new RedFollower(1,"red"));
		node6.joinGroup("red");
		
		Thread.sleep(12000);
		
		System.out.println("scrivo sulla mappa***********************************************************");
		((RedSupervisor) node6.getSupervisorRole("red")).writeOnMap();
	}

}
