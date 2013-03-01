package utilities;


public interface Publish {

	public void subscribe(String eventType, Subscriber s);
	
	public void unsubscribe(String eventType, Subscriber s);
	
	
}
