package evaluation;

import java.util.ArrayList;

import node.agentMiddleware.communication.middleware.CommunicationMiddleware;
import node.agentMiddleware.communication.middleware.MessageProfiler;
import node.agentMiddleware.perception.interfaces.Perception;
import node.hostInfrastructure.SimulatedCommunicationNetwork;
import node.organizationMiddleware.contextManager.contextDirectories.Context;
import node.organizationMiddleware.contextManager.contextDirectories.NeighbourInfo;
import node.organizationMiddleware.contextManager.contextDirectories.Organization;
import node.organizationMiddleware.contextManager.contextDirectories.RolePosition;
import node.organizationMiddleware.contextManager.contextDirectories.TrafficJamInfo;
import node.organizationMiddleware.organizationController.MasterController;
import node.organizationMiddleware.organizationController.SlaveController;


import utilities.NodeID;
import utilities.GUIDManager;
import utilities.MessageBuffer;
import utilities.threading.ThreadManager;

public class MergeOverhead {

	private Organization org1;
	private Organization org2;
	private ThreadManager tmMaster1;
	private ThreadManager tmMaster2;
	private ArrayList<ThreadManager> tmSlaves1;
	private ArrayList<ThreadManager> tmSlaves2;
	private Context ctx1;
	private Context ctx2;
	
	private final float trafficJamThreshold = 10;
	
	private RolePosition rp;

	public static void main(String[] args){
		MergeOverhead m = new MergeOverhead();
		
		for(int i = 2; i < 15; i++){
			//for(int j = 0; j < ; j++){
				m.doEvaluation(i);
			//}
			System.out.println("******************** Test done for " +i+ " nodes");
		}
				
	}
		
	public void doEvaluation(int nbNodes){
		
		this.rp = null;
		this.tmMaster1 = new ThreadManager();
		this.tmMaster2 = new ThreadManager();
		this.tmSlaves1 = new ArrayList<ThreadManager>();
		this.tmSlaves2 = new ArrayList<ThreadManager>();
				
		int masterOrg1 = getMasterOfOrg(1,nbNodes);
		int masterOrg2 = getMasterOfOrg(nbNodes+1,nbNodes*2);
		
		this.ctx1 = new Context(new NodeID(masterOrg1));
		this.ctx2 = new Context(new NodeID(masterOrg2));
		
		createOrganization1(nbNodes, trafficJamThreshold, GUIDManager.getInstance(), masterOrg1);
		createMaster(nbNodes, ctx1, org1, tmMaster1);
		
		createOrganization2(nbNodes, trafficJamThreshold, GUIDManager.getInstance(), masterOrg2);
		createMaster(nbNodes, ctx2, org2, tmMaster2);
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(int i = 1; i <= nbNodes; i++){
			if(!org1.getMasterID().equals(new NodeID(i))){
				ThreadManager tmSlave = new ThreadManager();
				createSlave(copy(org1), tmSlave, new NodeID(i));
				tmSlaves1.add(tmSlave);
			}
		}
		
		
		for(int i = nbNodes + 1; i <= 2*nbNodes ; i++){
			if(!org2.getMasterID().equals(new NodeID(i))){
				ThreadManager tmSlave = new ThreadManager();
				Organization o = copy(org2);
				createSlave(o, tmSlave, new NodeID(i));
				tmSlaves2.add(tmSlave);
				if(i == 2*nbNodes){
					for(RolePosition rp : o.getFilledRolePositions()){
						if(rp.getAgentId().equals(new NodeID(i)))
							this.rp = rp;
					}
				}
			}
		}
		
		try {
			Thread.sleep(10000);
			int steps = 1;
			while(steps <= (nbNodes-1)){
				tmMaster2.step();
				tmMaster1.step();
				steps++;
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		
		ctx1.addNeighbourOrganization(copy(org2));
		ctx2.addNeighbourOrganization(copy(org1));
		
		MessageProfiler msgProfiler = SimulatedCommunicationNetwork.getInstance().getProfiler();
		msgProfiler.startNewExperiment(nbNodes);
		
		try{
			this.rp.getTrafficJamInfo().setAvgVelocity(8);
		}catch (NullPointerException e){
			for(RolePosition rp : org2.getFilledRolePositions()){
				if(rp.getAgentId().equals(new NodeID(2*nbNodes)))
					this.rp = rp;
			}
			this.rp.getTrafficJamInfo().setAvgVelocity(8);
		}
		
		permute(tmSlaves1);
		permute(tmSlaves2);
		
		int nbLoops = 200;
		
		while(nbLoops > 0){ 
			
			try {
				tmMaster2.step();
				tmMaster1.step();
				
				for(ThreadManager tmSlave : tmSlaves1){
					tmSlave.step();
					
				}
				
				for(ThreadManager tmSlave : tmSlaves2){
					tmSlave.step();
				}
				
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			nbLoops--;
		}
		
		msgProfiler.stopExperiment(ctx1,ctx2);
		SimulatedCommunicationNetwork.getInstance().reset();
		GUIDManager.getInstance().reset();
		
	}
	
	
	private int getMasterOfOrg(int begin, int end) {
		double r = Math.random();
		r *= ( end - begin + 1 );
		return ((int) r ) + begin;
		
	}

	
	private void permute(ArrayList<ThreadManager> list){
		for(int i = list.size() - 1 ; i > 0; i--){
			int w = (int)Math.floor(Math.random() * (i+1));
			ThreadManager temp = list.get(w);
			ThreadManager temp2 = list.get(i);
			list.add(w,temp2);
			list.remove(w+1);
			list.add(i,temp);
			list.remove(i+1);
		}
	}
	

	public void createMaster(int nbNodes, Context ctx, Organization org, ThreadManager tm){
		
		SimulatedCommunicationNetwork network = SimulatedCommunicationNetwork.getInstance();
		network.start();
		
		CommunicationMiddleware cmw = new CommunicationMiddleware(network, org.getMasterID().toString());
		Perception pmw = new PerceptionDummy(org.getMasterID());
		
		GUIDManager guidManager = GUIDManager.getInstance();
				
		MessageBuffer msgBuffer = new MessageBuffer(cmw);
		
		//@Pieter
		//TODO: temporarily removed; re-implement!!
//		MasterController organizationManager = new MasterController(org,
//				                                                    ctx,
//				                                                    cmw,
//				                                                    pmw,
//				                                                    cmw,
//				                                                    tm,
//				                                                    msgBuffer);
//		
//		tm.register(organizationManager);
		
		throw new IllegalArgumentException("Not implemented yet!!");
	}
	
	public void createSlave(Organization org, ThreadManager tm, NodeID slaveID){
		SimulatedCommunicationNetwork network = SimulatedCommunicationNetwork.getInstance();
		network.start();
		
		CommunicationMiddleware cmw = new CommunicationMiddleware(network, slaveID.toString());
		Perception pmw = new PerceptionDummy(slaveID);
		
		GUIDManager guidManager = GUIDManager.getInstance();
				
		Context ctx = new Context(slaveID);
		MessageBuffer msgBuffer = new MessageBuffer(cmw);
		msgBuffer.subscribeOnStartAsSlaveMessages();
		msgBuffer.subscribeOnStartAsOrganizationManagerMessages();
		
		//@Pieter
		//TODO: temporarily removed; re-implement!!
//		SlaveController slave = new SlaveController(org,
//				                        ctx,
//				                        pmw,
//				                        cmw,
//				                        cmw,
//				                        tm,
//				                        msgBuffer,
//				                        guidManager);
//		
//		tm.register(slave);
		
		throw new IllegalArgumentException("Not implemented yet!!");
	}
	
	private Organization copy(Organization org){
		Organization copy = new Organization(org.getTrafficJamTreshhold(), org.getId(), org.getMasterID());
		
		ArrayList<NodeID> agents = new ArrayList<NodeID>();
		
		for(NodeID agent : org.getAgents()){
			agents.add(agent.copy());
		}
		
		copy.changeAgents(agents);
				
		for(RolePosition rp : org.getFilledRolePositions()){
			RolePosition rpCopy = rp.copy();
			rpCopy.getTrafficJamInfo().setAvgVelocity(rp.getTrafficJamInfo().getAvgVelocity());
			copy.addFilledRolePosition(rpCopy);
		}
		
		return copy;
	}
	
	private void createOrganization1(int nbNodes, float trafficJamThreshold,GUIDManager guidManager, int nodeID){
		int orgId = guidManager.getNextID();
		
		org1 = new Organization(trafficJamThreshold, orgId, new NodeID(nodeID));
		
		ArrayList<NodeID> agents = new ArrayList<NodeID>();
		
		for(int i = nbNodes; i > 0 ; i--){
			RolePosition rp = new RolePosition(new TrafficJamInfo(), new NeighbourInfo(),"dummy");
			rp.getTrafficJamInfo().setAvgVelocity(8);
			rp.setAgentId(new NodeID(i));
			
			org1.addFilledRolePosition(rp);
			agents.add(new NodeID(i));
		}
		
		org1.changeAgents(agents);
	}
	
	private void createOrganization2(int nbNodes, float trafficJamThreshold,GUIDManager guidManager, int nodeID){
		int orgId = guidManager.getNextID();
		
		org2 = new Organization(trafficJamThreshold, orgId, new NodeID(nodeID));
		
		ArrayList<NodeID> agents = new ArrayList<NodeID>();
		
		for(int i = nbNodes + 1; i <= 2*nbNodes ; i++){
				RolePosition rp = new RolePosition(new TrafficJamInfo(), new NeighbourInfo(),"dummy");
				if(i != 2*nbNodes)
					rp.getTrafficJamInfo().setAvgVelocity(8);
				rp.setAgentId(new NodeID(i));
				org2.addFilledRolePosition(rp);
				agents.add(new NodeID(i));
		
		}
		org2.changeAgents(agents);
	}
	
	
	
}
