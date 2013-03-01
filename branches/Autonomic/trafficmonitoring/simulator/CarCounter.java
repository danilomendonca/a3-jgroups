package simulator;

public class CarCounter {
	private static CarCounter me;
	
	private int sourceCars = 0;
	private int sinkCars = 0;
	
	public static CarCounter getCounter(){
		if(me == null)
			me = new CarCounter();
		return me;
	}
	
	public void sourceCar(){
		sourceCars++;
	}
	
	public void sinkCar(){
		sinkCars++;
	}
	
	public int getCurrentCars(){
		return sourceCars - sinkCars;
	}
}
