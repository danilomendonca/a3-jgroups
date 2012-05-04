package A3JGroups;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.blocks.ReplicatedHashMap;

public class ElectionManager implements Runnable{
	
	private boolean decide = true;
	private long electionTime = 10000;
	private ReplicatedHashMap<String, Object> map;
	private JChannel chan;

	public void setDecide(boolean decide) {
		this.decide = decide;
	}
	
	public ElectionManager(long electionTime, ReplicatedHashMap<String, Object> map, JChannel chan) {
		this.electionTime = electionTime;
		this.map = map;
		this.chan = chan;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(electionTime);
			
			if(decide){
				int max = 0;
				Address newSup = null;
				for (Address ad : chan.getView().getMembers()) {
					String s = ad.toString();
					if (map.containsKey(s)) {
						int value = (int) map.get(s);
						if (value > max) {
							max = value;
							newSup = ad;
						}
					}
				}
				if (max > 0) {
					System.out.println("entrato con: "+max+" e sup is: "+newSup);
					A3JGMessage mex = new A3JGMessage();
					mex.setContent("NewSupervisor");
					Message msg2 = new Message(null, mex);
					msg2.setDest(newSup);
					msg2.setObject(mex);
					chan.send(msg2);
				} else {
					A3JGMessage mex = new A3JGMessage();
					mex.setContent("Deactivate");
					Message msg3 = new Message(null, mex);
					chan.send(msg3);
				}
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}


}
