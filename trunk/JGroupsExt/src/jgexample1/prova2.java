package jgexample1;

public class prova2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		RedNode nodo1 = new RedNode("red1");
		RedFollower red = new RedFollower();
		nodo1.addFollowerRole("red", red, "red1");
		nodo1.getFollowerRole("red").activate("red");
		new Thread(nodo1.getFollowerRole("red")).start();
	}

}
