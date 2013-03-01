package utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import utilities.threading.ConcurrentArrayList;


public abstract class Publisher implements Publish{

	private HashMap<String,ConcurrentArrayList<Subscriber>> subscribers;
		
	public Publisher(){
		this.subscribers = new HashMap<String, ConcurrentArrayList<Subscriber>>();
	}
	
	/*******************************
	 *                             *
	 * publish/subscribe interface *
	 *                             *
	 *******************************/
	public void subscribe(String eventType, Subscriber s){
		try{
			//if(eventType.equals("setAvgVelocity"))
			//System.out.println("subscribe on "+eventType+" by "+s);
			subscribers.get(eventType).add(s);
		}catch(NullPointerException e){
			ConcurrentArrayList<Subscriber> list = new ConcurrentArrayList<Subscriber>();
			list.add(s);
			subscribers.put(eventType, list);
		}
	}
	
	public void unsubscribe(String eventType, Subscriber s){
		subscribers.get(eventType).remove(s);
	}
	
	protected void publish(Event e){
		ConcurrentArrayList<Subscriber> list = this.subscribers.get(e.getEventType());
		try{
			for(Iterator<Subscriber> it = list.iterator(); it.hasNext();){
				Subscriber s = it.next();
				//System.out.println("publish "+e.getEventType() +" aan "+s);
				s.publish(e);
			}
		}catch (NullPointerException exc){
			//nobody is registered for the given event => hashmap returns null when searched for that event
			//do nothing
		}
	}
	
}
