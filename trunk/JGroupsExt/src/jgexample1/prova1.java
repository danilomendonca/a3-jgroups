package jgexample1;


public class prova1 {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		RedNode nodo1 = new RedNode("red1");
		RedSupervisor red = new RedSupervisor();
		nodo1.addSupervisorRole("red", red, "red1");
		nodo1.getSupervisorRole("red").activate("red");
		new Thread(nodo1.getSupervisorRole("red")).start();
	}

}
