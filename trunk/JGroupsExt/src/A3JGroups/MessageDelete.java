package A3JGroups;

import java.util.Date;
import java.util.HashMap;

import org.jgroups.blocks.ReplicatedHashMap;

public class MessageDelete implements Runnable{
	
	private boolean active = false;
	private ReplicatedHashMap<String, Object> map;
	private HashMap<Integer, Date> chiavi;
	
	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}

	public void setMap(ReplicatedHashMap<String, Object> map) {
		this.map = map;
	}

	public void setChiavi(HashMap<Integer, Date> chiavi) {
		this.chiavi = chiavi;
	}

	@Override
	public void run() {
		while(active){
			Date d = new Date(System.currentTimeMillis());
			for(int i: chiavi.keySet()){
				if(chiavi.get(i).after(d)){
					chiavi.remove(i);
					map.remove("MessageInMemory_"+i);
					map.put("message", chiavi);
					if(chiavi.size()==0){
						map=null;
						active = false;
					}
				}
			}
		}
	}
	
}
