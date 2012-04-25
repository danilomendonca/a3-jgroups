package jgexample1;


public class prova1 {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		RedNode nodo1 = new RedNode("red1");
		RedSupervisor red = new RedSupervisor(1, "red");
		nodo1.addSupervisorRole("red", red);
		nodo1.joinGroup("red");
	}

}
