package A3JGroups;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.jgroups.blocks.ReplicatedHashMap;

public class MessageDelete implements Runnable{
	
	private boolean active = false;
	private ReplicatedHashMap<String, Object> map;
	private HashMap<Integer, Date> chiavi;
	private int waitTime = 30000;
	private ArrayList<Integer> deleteKey = new ArrayList<Integer>();
	
	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}

	public void setMap(ReplicatedHashMap<String, Object> map) {
		this.map = map;
	}
	
	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}

	public void setChiavi(HashMap<Integer, Date> chiavi) {
		this.chiavi = chiavi;
	}

	public void toDelete(int index){
		chiavi.remove(index);
		map.remove("A3MessageInMemory_"+index);
		map.put("A3Message", chiavi);
	}
	
	@Override
	public void run() {
		while(active){
				
			Date d = new Date(System.currentTimeMillis());
			for(int i: chiavi.keySet()){
				if(d.after(chiavi.get(i))){
					map.remove("A3MessageInMemory_"+i);	
					deleteKey.add(i);
					System.out.println("******************* Remove A3MessageInMemory_"+i);
				}
			}
			
			if(deleteKey.size()>0){
				for(int i: deleteKey){
					chiavi.remove(i);
				}
				map.put("A3Message", chiavi);
				if(chiavi.size()==0){
					map=null;
					active = false;
				}
				deleteKey = new ArrayList<Integer>();
			}
			
			try {
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
