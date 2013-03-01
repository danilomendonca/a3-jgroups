package simulator;

public class Car {
	public int speed;
	public int[] path;
	public int pathIndex;
	
	public Car(int speed, int[] path){
		this.speed = speed;
		this.path = path;
		this.pathIndex = 0;
	}
	
	public int getChoice(){
		if(pathIndex<path.length)
			return path[pathIndex];
		else return 500;
	}
	
	public void removeChoice(){
		pathIndex++;
	}
	
	
}
