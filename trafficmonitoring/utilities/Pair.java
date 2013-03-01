package utilities;

public class Pair<T1,T2> {
	
	private T1 o1;
	private T2 o2;
	
	public Pair(T1 o1, T2 o2){
		this.o1 = o1;
		this.o2 = o2;
	}
	
	public T1 getElement1(){
		return o1;
	}
	
	public T2 getElement2(){
		return o2;
	}

}
