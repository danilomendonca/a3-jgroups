package a3Solution;

import java.util.HashMap;
import java.util.Map;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.View;
import org.jgroups.blocks.ReplicatedHashMap;

import utilities.threading.ThreadManager;
import A3JGroups.A3JGMessage;
import A3JGroups.A3JGRHMNotification;
import A3JGroups.A3JGroup;

//TODO vedere dove fare l'unregister dei threads al threadmanager

/**
 * A3JGNode is the main element of A3. A node represents an element connected to
 * the network of distributed system. Each node can be part of any group, in the
 * limit of its resources. Contains two map of Roles (one for JGSupervisorRole
 * and one for JGFollowerRole),and a map with the details of each group that can
 * participate.
 * 
 * A3JGNode is used for join and leave a group of the distributed system. After
 * the creation of a node (with the public constructor), you must enter
 * information of groups which want to participate actively and roles required
 * to act in them.
 * 
 * Each A3JGNode has a unique identifier, and there is a space of shared memory
 * between roles of different groups.
 * 
 * @author bett.marco88@gmail.com
 * 
 */
public abstract class A3JGSingleThreadedNode {

	private boolean logging = false;

	private ThreadManager tm;

	private int resourceThreshold;
	private String ID;
	private long timeout = 10000;

	private Map<String, A3JGSingleThreadedSupervisorRole> supervisorRoles = new HashMap<String, A3JGSingleThreadedSupervisorRole>();
	private Map<String, A3JGSingleThreadedFollowerRole> followerRoles = new HashMap<String, A3JGSingleThreadedFollowerRole>();
	private Map<String, A3JGroup> groupInfo = new HashMap<String, A3JGroup>();
	private Map<String, JChannel> channels = new HashMap<String, JChannel>();
	private Map<String, String> activeRole = new HashMap<String, String>();
	protected Map<String, SingleThreadedGenericRole> waitings = new HashMap<String, SingleThreadedGenericRole>();
	private Object inNodeSharedMemory;

	public A3JGSingleThreadedNode(String ID, ThreadManager tm) {
		super();
		this.ID = ID;
		this.tm = tm;
		if (logging)
			tm.register(new Logger());
	}

	private class Logger implements Runnable {

		@Override
		public void run() {

			long sentBytes = 0;

			for (String channel : channels.keySet()) {
				// System.out.println("[" + getID() + "]: Node " + getID()
				// + " channel " + channel + " sent bytes = "
				// + channels.get(channel).getSentBytes());
				sentBytes += channels.get(channel).getSentBytes();
			}

			System.out.println("[" + getID() + " Log]: Node " + getID()
					+ " total of sent bytes = " + sentBytes);

		}

	}

	public void setResourceThreshold(int resourceThreshold) {
		this.resourceThreshold = resourceThreshold;
	}

	public int getResourceThreshold() {
		return resourceThreshold;
	}

	public JChannel getChannels(String groupName) {
		return channels.get(groupName);
	}

	public String getActiveRole(String groupName) {
		return activeRole.get(groupName);
	}

	public void putActiveRole(String groupName, String className) {
		activeRole.put(groupName, className);
	}

	public void addGroupInfo(String groupName, A3JGroup group) {
		this.groupInfo.put(groupName, group);
	}

	public A3JGroup getGroupInfo(String groupName) {
		return groupInfo.get(groupName);
	}

	public void addSupervisorRole(A3JGSingleThreadedSupervisorRole role) {
		this.supervisorRoles.put(role.getClass().getName(), role);
		role.setNode(this);
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public A3JGSingleThreadedSupervisorRole getSupervisorRole(String className) {
		return supervisorRoles.get(className);
	}

	public A3JGSingleThreadedFollowerRole getFollowerRole(String className) {
		return followerRoles.get(className);
	}

	public void addFollowerRole(A3JGSingleThreadedFollowerRole role) {
		this.followerRoles.put(role.getClass().getName(), role);
		role.setNode(this);
	}

	public String getID() {
		return ID;
	}

	public Object getInNodeSharedMemory() {
		return inNodeSharedMemory;
	}

	public void setInNodeSharedMemory(Object inNodeSharedMemory) {
		this.inNodeSharedMemory = inNodeSharedMemory;
	}

	/**
	 * This function is used for join a group. The function tries to establish a
	 * connection with the group defined by the parameter passed in. If the node
	 * meets the conditions of access, the connection is successful, and
	 * activates the appropriate role, otherwise there is no established link
	 * with the group.
	 * 
	 * @param groupName
	 *            The name of the group to join.
	 * @return True if join has success, false otherwise.
	 * @throws Exception
	 *             If problems occur during the connection with the group.
	 */
	public boolean joinGroup(String groupName) throws Exception {

		if (channels.get(groupName) != null)
			return false;
		if (groupInfo.get(groupName) == null)
			return false;

		final JChannel chan;
		if (groupInfo.get(groupName).getGroupConnection() != null)
			chan = new JChannel(groupInfo.get(groupName).getGroupConnection());
		else
			chan = new JChannel();
		channels.put(groupName, chan);

		ReplicatedHashMap<String, Object> map = new ReplicatedHashMap<String, Object>(
				chan) {

			public void receive(Message m) {
				if (chan.getReceiver() != null)
					chan.getReceiver().receive(m);
			}

			public void viewAccepted(View v) {
				if (chan.getReceiver() != null)
					chan.getReceiver().viewAccepted(v);

				this.viewAcceptedOriginal(v);

			}
		};

		A3JGRHMNotification notifier = new A3JGRHMNotification();
		notifier.setNodeID(ID);
		map.addNotifier(notifier);
		chan.connect(groupName);

		map.start(timeout);

		if (groupInfo.containsKey(groupName)) {

			String supName = getGroupInfo(groupName).getSupervisor().get(0);
			String folName = getGroupInfo(groupName).getFollower().get(0);

			if (map.get("A3Supervisor") == null) {
				if (this.getSupervisorRole(supName) != null) {
					if (map.putIfAbsent("A3Supervisor", chan.getAddress()) == null) {
						map.remove("A3FitnessFunction");
						this.getSupervisorRole(supName).setActive(true);
						this.getSupervisorRole(supName).setChan(chan);
						this.getSupervisorRole(supName).setMap(map);
						this.getSupervisorRole(supName).setNotifier(notifier);
						this.getSupervisorRole(supName).index = -1;
						chan.setReceiver(this.getSupervisorRole(supName));
						tm.register(this.getSupervisorRole(supName));
						// new Thread(this.getSupervisorRole(supName)).start();
						putActiveRole(groupName, supName);
						return true;
					} else {
						if (this.getFollowerRole(folName) != null) {
							this.getFollowerRole(folName).setActive(true);
							this.getFollowerRole(folName).setChan(chan);
							this.getFollowerRole(folName).setMap(map);
							this.getFollowerRole(folName).setNotifier(notifier);
							chan.setReceiver(this.getFollowerRole(folName));
							tm.register(this.getFollowerRole(folName));
							// new
							// Thread(this.getFollowerRole(folName)).start();
							putActiveRole(groupName, folName);
							return true;
						}
					}
				}
			} else if (chan.getView().getMembers()
					.contains(map.get("A3Supervisor"))) {

				if (this.getFollowerRole(folName) != null) {

					this.getFollowerRole(folName).setActive(true);
					this.getFollowerRole(folName).setChan(chan);
					this.getFollowerRole(folName).setMap(map);
					this.getFollowerRole(folName).setNotifier(notifier);
					tm.register(this.getFollowerRole(folName));
					// new Thread(this.getFollowerRole(folName)).start();
					chan.setReceiver(this.getFollowerRole(folName));
					putActiveRole(groupName, folName);
					return true;
				} else {

				}
			} else {
				SingleThreadedGenericRole generic = new SingleThreadedGenericRole(
						this, chan, map, notifier, tm);
				chan.setReceiver(generic);
				generic.waitElection();
				waitings.put(groupName, generic);
				putActiveRole(groupName, "GenericRole");
				return true;
			}
		}
		close(groupName);
		return false;
	}

	protected void close(String groupName) {
		JChannel chan = channels.get(groupName);
		chan.disconnect();
		chan.close();
		channels.remove(groupName);
		activeRole.remove(groupName);
	}

	/**
	 * This function is called to terminate participation in the group passed in
	 * input.
	 * 
	 * @param groupName
	 *            The name of the group to terminate.
	 */
	public void terminate(String groupName) {
		String role = getActiveRole(groupName);
		if (this.getSupervisorRole(role) != null
				&& this.getSupervisorRole(role).isActive()) {
			this.getSupervisorRole(role).setActive(false);
			tm.unregister(this.getSupervisorRole(role));
		} else if (this.getFollowerRole(role) != null
				&& this.getFollowerRole(role).isActive()) {
			this.getFollowerRole(role).setActive(false);
			tm.unregister(this.getFollowerRole(role));
		}
		close(groupName);
	}

	public synchronized void fail() {
		// Copia della map activeRole per non avere eccezione
		// concorrenza
		Map<String, String> copy = new HashMap<String, String>(activeRole);

		for (String group : copy.keySet()) {
			terminate(group);
		}
	}

	/**
	 * This function is similar to the Join, with the difference that the node
	 * can join the group only as a supervisor. If the node cannot be a
	 * supervisor in the specified group, the join fails. There is the
	 * possibility to have a challenge with the current supervisor of the group
	 * specified.
	 * 
	 * @param groupName
	 *            The name of the group to join.
	 * @param challenge
	 *            If true there is a challenge with the current supervisor, if
	 *            false this node becomes the new supervisor.
	 * @return True if join as supervisor has success, false otherwise.
	 * @throws Exception
	 *             If problems occur during the connection with the group.
	 */
	public boolean joinAsSupervisor(String groupName, boolean challenge)
			throws Exception {

		if (channels.get(groupName) != null)
			return false;
		if (groupInfo.get(groupName) == null)
			return false;

		final JChannel chan;
		if (groupInfo.get(groupName).getGroupConnection() != null)
			chan = new JChannel(groupInfo.get(groupName).getGroupConnection());
		else
			chan = new JChannel();
		channels.put(groupName, chan);

		ReplicatedHashMap<String, Object> map = new ReplicatedHashMap<String, Object>(
				chan) {

			public void receive(Message m) {
				if (chan.getReceiver() != null)
					chan.getReceiver().receive(m);
			}

			public void viewAccepted(View v) {
				if (chan.getReceiver() != null)
					chan.getReceiver().viewAccepted(v);

				this.viewAcceptedOriginal(v);

			}
		};

		A3JGRHMNotification notifier = new A3JGRHMNotification();
		notifier.setNodeID(ID);
		map.addNotifier(notifier);
		chan.connect(groupName);

		map.start(timeout);
		if (groupInfo.containsKey(groupName)) {
			String supName = getGroupInfo(groupName).getSupervisor().get(0);

			if (this.getSupervisorRole(supName) != null) {
				if (map.get("A3Supervisor") == null || !challenge) {
					if (map.putIfAbsent("A3Supervisor", chan.getAddress()) == null) {
						this.getSupervisorRole(supName).setActive(true);
						this.getSupervisorRole(supName).setChan(chan);
						this.getSupervisorRole(supName).setMap(map);
						this.getSupervisorRole(supName).setNotifier(notifier);
						this.getSupervisorRole(supName).index = -1;
						chan.setReceiver(this.getSupervisorRole(supName));
						tm.register(this.getSupervisorRole(supName));
						// new Thread(this.getSupervisorRole(supName)).start();
						putActiveRole(groupName, supName);

						A3JGMessage mex = new A3JGMessage("A3SupervisorChange");
						Message msg = new Message();
						msg.setObject(mex);
						msg.setDest((Address) map.get("A3Supervisor"));
						try {
							chan.send(msg);
						} catch (Exception e) {
							e.printStackTrace();
						}
						return true;
					}
				}
				if (challenge) {
					SingleThreadedGenericRole generic = new SingleThreadedGenericRole(
							this, chan, map, notifier, tm);
					chan.setReceiver(generic);
					generic.supervisorChallenge();
					waitings.put(groupName, generic);
					putActiveRole(groupName, "GenericRole");
					return true;
				}
			}
		}

		return false;
	}

	public boolean joinSplitGroup(String groupName, JChannel jc, int port,
			boolean role) throws Exception {

		jc.getProtocolStack().findProtocol("UDP").setValue("mcast_port", port);
		final JChannel chan = new JChannel(jc);
		if (role)
			channels.put("A3Split" + groupName, chan);
		else {
			terminate(groupName);
			channels.put(groupName, chan);
		}

		ReplicatedHashMap<String, Object> map = new ReplicatedHashMap<String, Object>(
				chan) {

			public void receive(Message m) {
				if (chan.getReceiver() != null)
					chan.getReceiver().receive(m);
			}

			public void viewAccepted(View v) {
				if (chan.getReceiver() != null)
					chan.getReceiver().viewAccepted(v);

				this.viewAcceptedOriginal(v);

			}
		};

		A3JGRHMNotification notifier = new A3JGRHMNotification();
		notifier.setNodeID(ID);
		map.addNotifier(notifier);
		chan.connect(groupName);

		map.start(timeout);

		if (groupInfo.containsKey(groupName)) {
			String supName = getGroupInfo(groupName).getSupervisor().get(0);
			String folName = getGroupInfo(groupName).getFollower().get(0);
			if (map.get("A3Supervisor") == null && role) {
				if (this.getSupervisorRole(supName) != null) {
					if (map.putIfAbsent("A3Supervisor", chan.getAddress()) == null) {
						this.getSupervisorRole(supName).setActive(true);
						this.getSupervisorRole(supName).setChan(chan);
						this.getSupervisorRole(supName).setMap(map);
						this.getSupervisorRole(supName).setNotifier(notifier);
						this.getSupervisorRole(supName).index = -1;
						this.getSupervisorRole(supName).setSplitsup(true);
						chan.setReceiver(this.getSupervisorRole(supName));
						tm.register(this.getSupervisorRole(supName));
						// new Thread(this.getSupervisorRole(supName)).start();
						putActiveRole("A3Split" + groupName, supName);
						return true;
					}
				}
			} else if (!role) {
				if (this.getFollowerRole(folName) != null) {
					this.getFollowerRole(folName).setActive(true);
					this.getFollowerRole(folName).setChan(chan);
					this.getFollowerRole(folName).setMap(map);
					this.getFollowerRole(folName).setNotifier(notifier);
					tm.register(this.getFollowerRole(folName));
					// new Thread(this.getFollowerRole(folName)).start();
					chan.setReceiver(this.getFollowerRole(folName));
					putActiveRole(groupName, folName);
					return true;
				}
			}
		}
		if (role)
			close("A3Split" + groupName);
		else
			close(groupName);
		return false;
	}

}
