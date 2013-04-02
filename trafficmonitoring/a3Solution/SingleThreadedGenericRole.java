package a3Solution;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.blocks.ReplicatedHashMap;

import utilities.threading.ThreadManager;
import A3JGroups.A3JGMessage;
import A3JGroups.A3JGRHMNotification;

/**
 * A new instance of this class is created each time a node can not be joined in
 * an immediate way to a group (due to a supervisor election). This object is
 * capable of receiving message, and of participating to the supervisor
 * election.
 * 
 * @author bett.marco88@gmail.com
 * 
 */

// TODO guardare il comportamento di deleter

public class SingleThreadedGenericRole extends ReceiverAdapter {

	private ThreadManager tm;

	private A3JGSingleThreadedNode node;
	private JChannel chan;
	private ReplicatedHashMap<String, Object> map;
	private A3JGRHMNotification notifier;
	private int todo = 0;

	public SingleThreadedGenericRole(A3JGSingleThreadedNode node,
			JChannel chan, ReplicatedHashMap<String, Object> map,
			A3JGRHMNotification notifier, ThreadManager tm) {
		super();
		this.node = node;
		this.chan = chan;
		this.map = map;
		this.notifier = notifier;
		this.tm = tm;
	}

	@SuppressWarnings("unchecked")
	public void receive(Message mex) {
		A3JGMessage msg = (A3JGMessage) mex.getObject();

		if (msg.getValueID().equals("A3FitnessFunction")) {
			int fitness;
			String groupName = this.chan.getClusterName();
			String role = node.getGroupInfo(groupName).getSupervisor().get(0);
			if (node.getSupervisorRole(role) != null)
				fitness = node.getSupervisorRole(role).fitnessFunc();
			else
				fitness = 0;

			map.put(chan.getAddressAsString(), fitness);

		} else if (msg.getValueID().equals("A3NewSupervisor")) {

			String groupName = this.chan.getClusterName();
			String role = node.getGroupInfo(groupName).getSupervisor().get(0);
			node.putActiveRole(groupName, role);
			map.put("A3Supervisor", chan.getAddress());
			map.put("A3Change", null);
			node.getSupervisorRole(role).setActive(true);
			node.getSupervisorRole(role).setChan(chan);
			node.getSupervisorRole(role).setMap(map);
			node.getSupervisorRole(role).setNotifier(notifier);
			chan.setReceiver(node.getSupervisorRole(role));
			node.getSupervisorRole(role).index = getLastIndex();
			if (map.get("A3Message") != null) {
				node.getSupervisorRole(role).deleter.setActive(true);
				node.getSupervisorRole(role).deleter.setMap(map);
				node.getSupervisorRole(role).deleter
						.setChiavi((HashMap<Integer, Date>) map
								.get("A3Message"));
				new Thread(node.getSupervisorRole(role).deleter).start();
			}
			tm.register(node.getSupervisorRole(role));
			// new Thread(node.getSupervisorRole(role)).start();
			node.waitings.remove(groupName);
			endGeneric();

		} else if (msg.getValueID().equals("A3Deactivate")) {
			node.terminate(this.chan.getClusterName());
			node.waitings.remove(this.chan.getClusterName());
			endGeneric();

		} else if (msg.getValueID().equals("A3StayFollower")) {

			String groupName = this.chan.getClusterName();
			String role = node.getGroupInfo(groupName).getFollower().get(0);
			node.putActiveRole(groupName, role);
			if (node.getFollowerRole(role) != null) {
				node.getFollowerRole(role).setActive(true);
				node.getFollowerRole(role).setChan(chan);
				node.getFollowerRole(role).setMap(map);
				node.getFollowerRole(role).setNotifier(notifier);
				chan.setReceiver(node.getFollowerRole(role));
				tm.register(node.getFollowerRole(role));
				// new Thread(node.getFollowerRole(role)).start();

			} else {
				node.close(groupName);
			}
			node.waitings.remove(groupName);
			endGeneric();
		}
	}

	public void waitElection() {
		if (map.get("A3FitnessFunction") != null) {
			int fitness;
			String groupName = this.chan.getClusterName();
			String role = node.getGroupInfo(groupName).getSupervisor().get(0);
			if (node.getSupervisorRole(role) != null)
				fitness = node.getSupervisorRole(role).fitnessFunc();
			else
				fitness = 0;
			map.put(chan.getAddressAsString(), fitness);
			todo = 1;

		}
		if (map.get("A3Deactivate") != null) {
			node.waitings.remove(this.chan.getClusterName());
			node.terminate(this.chan.getClusterName());
			todo = 2;
		}
		if (todo == 0) {
			String groupName = this.chan.getClusterName();
			String role = node.getGroupInfo(groupName).getFollower().get(0);
			node.putActiveRole(groupName, role);
			if (node.getFollowerRole(role) != null) {
				node.getFollowerRole(role).setActive(true);
				node.getFollowerRole(role).setChan(chan);
				node.getFollowerRole(role).setMap(map);
				node.getFollowerRole(role).setNotifier(notifier);
				chan.setReceiver(node.getFollowerRole(role));
				tm.register(node.getFollowerRole(role));
				// new Thread(node.getFollowerRole(role)).start();

			} else {
				node.close(groupName);
			}
			node.waitings.remove(groupName);
			endGeneric();
		}
	}

	public void supervisorChallenge() {
		int fitness;
		String groupName = this.chan.getClusterName();
		String role = node.getGroupInfo(groupName).getSupervisor().get(0);
		if (node.getSupervisorRole(role) != null)
			fitness = node.getSupervisorRole(role).fitnessFunc();
		else
			fitness = 0;
		A3JGMessage mex = new A3JGMessage("A3SupervisorChallenge", fitness);
		Message msg = new Message();
		msg.setObject(mex);
		msg.setDest((Address) map.get("A3Supervisor"));
		try {
			chan.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private int getLastIndex() {
		int max = -1;
		if (map.get("A3Message") != null) {
			Map<Integer, Date> chiavi = (Map<Integer, Date>) map
					.get("A3Message");
			for (int i : chiavi.keySet()) {
				if (i > max)
					max = i;
			}
		}
		return max;
	}

	private void endGeneric() {
		this.node = null;
		this.chan = null;
		this.map = null;
		this.notifier = null;
	}
}
