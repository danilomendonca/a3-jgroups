package A3JGroups;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.blocks.ReplicatedHashMap;

public class SplitManager implements Runnable{
	
	private long splitTime;
	private ReplicatedHashMap<String, Object> map;
	private JChannel chan;

	public SplitManager(long splitTime, ReplicatedHashMap<String, Object> map, JChannel chan) {
		this.splitTime = splitTime;
		this.map = map;
		this.chan = chan;
	}

	@Override
	public void run() {
		try {
			
			A3JGMessage mex = new A3JGMessage("A3FitnessFunction");
			sendMessage(mex, "A3SplitFitnessFunction");
			
			Thread.sleep(splitTime);
			
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
				map.remove("A3SplitFitnessFunction");
				for (Address ad : chan.getView().getMembers()) {
					map.remove(ad.toString());
				}
				
				int port = (Integer) chan.getProtocolStack().findProtocol("UDP").getValue("mcast_port");
				port++;
				
				A3JGMessage mex2 = new A3JGMessage("A3SplitNewSupervisor", port);
				Message msg2 = new Message(null, mex2);
				msg2.setDest(newSup);
				chan.send(msg2);
				
				A3JGMessage mex3 = new A3JGMessage("A3SplitChange", port);
				Message msg3 = new Message(null, mex3);
				for (int i=0,f=0; f<(chan.getView().size()/2); i++){
					Address ad = chan.getView().getMembers().get(i);
					if (!ad.equals(newSup) && !ad.equals(chan.getAddress())) {
						msg3.setDest(ad);
						chan.send(msg3);
						f++;
					}
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
