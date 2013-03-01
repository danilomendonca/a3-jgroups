package utilities;

public class Event {

	private String eventType;
	
	public Event(String eventType){
		this.eventType = eventType; 
	}
	
	public String getEventType() {
		return eventType;
	}
	
	public boolean equals(Object o){
		try{
			return eventType.equals(((Event) o).getEventType());
		}catch (ClassCastException e){
			return false;
		}
	}

}
