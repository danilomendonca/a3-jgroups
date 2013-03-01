package utilities.threading;

import java.util.Iterator;


public class ThreadManager {

	private ConcurrentArrayList<Runnable> runners;
		
	public ThreadManager(){
		runners = new ConcurrentArrayList<Runnable>();
	}
	
	public String toString(){
		return runners.toString();
	}
	
//	public static ThreadManager getThreadManager(){
//		if(manager == null){
//			manager = new ThreadManager();
//			return manager;
//		}
//		else
//			return manager;
//			
//	}
	
	public void register(Runnable thread){
		runners.add(thread);
	}
	
	public void unregister(Runnable thread){
		runners.remove(thread);
	}
	
	public void step(){
		Iterator<Runnable> it = runners.iterator();		
		while(it.hasNext()){
			Runnable runnable = it.next();
			
			runnable.run();			
		}
		try {Thread.sleep (2);} catch(InterruptedException e) {}
	}
	
	public ConcurrentArrayList<Runnable> getThreads(){
		return  runners;
	}
	
	public void unregisterAll(){
		runners.clear();
	}
}
