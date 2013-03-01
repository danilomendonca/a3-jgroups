package utilities.threading;

import java.util.Iterator;


public class ConcurrentArrayList<Type> implements Iterable, Iterator{

	private Object[] internalArray;
	private int firstEmptySpot;
	private int iteratorSpot;
	
	public ConcurrentArrayList(){
		internalArray = new Object[20];
		firstEmptySpot = 0;
	}
	
	public void add(Type o){
		internalArray[firstEmptySpot] = o;
		findNewFirstEmptySpot();
	}

	private void findNewFirstEmptySpot() {
		boolean availableEmptySlot = false;
		for(int i = 0; i < internalArray.length && !availableEmptySlot; i++){
			if(internalArray[i] == null){
				firstEmptySpot = i;
				availableEmptySlot = true;
			}
		}
		
		if(!availableEmptySlot){
			//create bigger array
			firstEmptySpot = internalArray.length;
			createBiggerArray();
		}
	}

	private void createBiggerArray() {
		Object[] tempArray = new Object[internalArray.length * 2];
		for(int i = 0, j = 0; i < internalArray.length; i++){
			if(internalArray[i] != null){
				tempArray[j] = internalArray[i];
				j++;
			}
		}
		internalArray = tempArray;
	}
	
	public boolean hasNext() {
		for(int i = iteratorSpot; i < internalArray.length; i++ ){
			if(internalArray[i] != null)
				return true;
		}
		return false;
	}

	public Type next() {
		boolean found = false;
		while(iteratorSpot < internalArray.length && !found){ 
			if(internalArray[iteratorSpot] != null){
				found = true;
			}
			iteratorSpot++;
		}
		if(found)
			return (Type) internalArray[iteratorSpot-1];	
		else
			return null;
	}
	
	public void remove(Type t){
		for(int i = 0; i < internalArray.length; i++){
			if(internalArray[i] != null && internalArray[i].equals(t))
				internalArray[i] = null;
		}
	}

	public Iterator<Type> iterator() {
		iteratorSpot = 0;
		return this;
	}

	public void remove() {
		// TODO Auto-generated method stub
	}

	public void clear() {
		internalArray = new Object[internalArray.length];
		firstEmptySpot = 0;
	}

	public Type get(int i) {
		int nbFound = 0;
		int requestElement = i + 1; 
		for(int j = 0; j < internalArray.length; j++){
			if(internalArray[j] != null)
				nbFound++;
				if(nbFound == requestElement)
					return (Type) internalArray[j];
		}
		throw new IndexOutOfBoundsException();
		
	}

	public int size() {
		int nbFound = 0;
		for(int j = 0; j < internalArray.length; j++){
			if(internalArray[j] != null)
				nbFound++;
				
		}
		return nbFound;
	}
	
	public String toString(){
		String result = "[ ";
		for(int i = 0; i < internalArray.length; i++){
			if(internalArray[i] != null){
				result = result + internalArray[i].toString() + " , ";
			}
		}
		result = result + " ]";
		return result;
	}
	
	
	
}
