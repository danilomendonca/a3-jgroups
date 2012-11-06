package A3JGroups;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.blocks.ReplicatedHashMap;

/**
 * ElectionManager is a Thread used by the follower who carry out the election
 * of the new supervisor. You have to define the electionTime (10000ms as 
 * default) in which to await the results. 
 * 
 * The ElectionManager is created in automatic by the first follower of the 
 * groupMember List of the Jchannel view.
 * 
 * @author bett.marco88@gmail.com
 *
 */
public class ElectionManager implements Runnable{
	
	private boolean decide = true;
	private boolean decTake = false;
	private long electionTime = 10000;
	private ReplicatedHashMap<String, Object> map;
	private JChannel chan;

	public void setDecide(boolean decide) {
		this.decide = decide;
	}
	
	public boolean isDecTake(){
		return decTake;
	}
	
	public ElectionManager(long electionTime, ReplicatedHashMap<String, Object> map, JChannel chan) {
		this.electionTime = electionTime;
		this.map = map;
		this.chan = chan;
	}

	@Override
	public void run() {
		try {
			A3JGMessage mex = new A3JGMessage("A3FitnessFunction");
			sendMessage(mex, "A3FitnessFunction");
			
			Thread.sleep(electionTime);
			
			if(decide && chan.getView()!=null){
				map.put("A3JGElectionAttempt",0);
				decTake = true;
				int max = 0;
				Address newSup = null;
				for (Address ad : chan.getView().getMembers()) {
					String s = ad.toString();
					if (map.containsKey(s)) {
						int value = (Integer) map.get(s);
						if (value > max) {
							max = value;
							newSup = ad;
						}
					}
				}
				if (max > 0) {
					map.remove("A3Change");
					map.remove("A3FitnessFunction");
					for(Address ad: chan.getView().getMembers()){
						map.remove(ad.toString());
						
					}
					A3JGMessage mex2 = new A3JGMessage("A3NewSupervisor");
					Message msg2 = new Message(null, mex2);
					msg2.setDest(newSup);
					chan.send(msg2);
					A3JGMessage mex3 = new A3JGMessage("A3StayFollower");
					Message msg3 = new Message(null, mex3);
					for(Address ad: chan.getView().getMembers()){
						if(!ad.equals(newSup)){
							msg3.setDest(ad);
							chan.send(msg3);
						}
					}
				} else {
					A3JGMessage mex3 = new A3JGMessage("A3Deactivate");
					sendMessage(mex3, "A3Deactivate");
				}
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void sendMessage(A3JGMessage mex, String content) throws Exception{
		Message msg = new Message(null, mex);
		map.put(content, true);
		chan.send(msg);
	}


}
