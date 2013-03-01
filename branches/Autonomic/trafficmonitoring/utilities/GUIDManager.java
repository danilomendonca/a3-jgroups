package utilities;

public class GUIDManager {
	
	private int currentID;
	private static GUIDManager manager;
	
	public synchronized static GUIDManager getInstance(){
		if(manager == null){
			manager = new GUIDManager();
		}
		return manager;
	}
	
	private GUIDManager(){
		this.currentID = 0;
	}
	
	synchronized public int getNextID(){
		return currentID++;
	}

	public void reset() {
		this.currentID = 0;
		
	}
	
}
