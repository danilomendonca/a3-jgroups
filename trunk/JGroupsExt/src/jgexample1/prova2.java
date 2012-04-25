package jgexample1;

public class prova2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		RedNode nodo1 = new RedNode("red1");
		RedFollower red = new RedFollower(1, "red");
		nodo1.addFollowerRole("red", red);
		nodo1.joinGroup("red");
	}

}
