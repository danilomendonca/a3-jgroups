package node.selfAdaptationSystem;

import simulator.RoadNetwork;
import utilities.NodeID;
import node.CameraNode;
import node.agentMiddleware.communication.middleware.CommunicationMiddleware;
import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.coordination.MapeCommunicationManager;
import node.selfAdaptationSystem.mapeManager.MapeManager;
import node.selfAdaptationSystem.mapeManager.cameraIntroduction.CameraIntroductionScenario;
import node.selfAdaptationSystem.mapeManager.cameraIntroduction.CameraIntroductionScenarioManager;
import node.selfAdaptationSystem.mapeManager.masterWithSlavesNodeFailureScenario.MasterWithSlavesNodeFailureScenario;
import node.selfAdaptationSystem.mapeManager.masterWithSlavesNodeFailureScenario.MasterWithSlavesNodeFailureScenarioManager;
import node.selfAdaptationSystem.mapeManager.neighborNodeFailure.NeighborNodeFailureScenario;
import node.selfAdaptationSystem.mapeManager.neighborNodeFailure.NeighborNodeFailureScenarioManager;
import node.selfAdaptationSystem.mapeManager.selfMonitoring.cameraMonitoring.CameraMonitoringScenario;
import node.selfAdaptationSystem.mapeManager.selfMonitoring.cameraMonitoring.CameraMonitoringScenarioManager;
import node.selfAdaptationSystem.mapeManager.slaveNodeFailureScenario.SlaveNodeFailureScenario;
import node.selfAdaptationSystem.mapeManager.slaveNodeFailureScenario.SlaveNodeFailureScenarioManager;
import node.selfAdaptationSystem.selfAdaptationController.SelfAdaptationController;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class SelfAdaptationSubsystem implements Runnable {
	
	//Note: public attributes only to be used/accessed outside of this class during testing
	private BaseLevelConnector baseLevel;
	public SelfAdaptationModels models;
	public MapeManager mapeManager;
	private MapeCommunicationManager communicationManager;
	private CommunicationMiddleware cmw;
	public SelfAdaptationController controller;
	
	public SelfAdaptationSubsystem(CameraNode node, RoadNetwork roadNetwork, CommunicationMiddleware cmw){
		this.baseLevel = new BaseLevelConnector(node);
		this.cmw = cmw;
		
		NodeID localNode = new NodeID(Integer.valueOf(this.cmw.getNodeIdentifier()));
		
		this.models = new SelfAdaptationModels();
		this.models.setRoadNetwork(roadNetwork);
		this.models.setHostNode(localNode);		
		
		this.communicationManager = new MapeCommunicationManager(localNode, this.cmw);		
		
		
		this.mapeManager = new MapeManager(this.communicationManager, this.models, this.baseLevel);
		//Register the necessary ScenarioManagers		
		this.mapeManager.registerScenarioManager(MasterWithSlavesNodeFailureScenario.scenarioType, 
				new MasterWithSlavesNodeFailureScenarioManager());
		this.mapeManager.registerScenarioManager(NeighborNodeFailureScenario.scenarioType, 
				new NeighborNodeFailureScenarioManager());
		this.mapeManager.registerScenarioManager(SlaveNodeFailureScenario.scenarioType, 
				new SlaveNodeFailureScenarioManager());
		this.mapeManager.registerScenarioManager(CameraIntroductionScenario.scenarioType, 
				new CameraIntroductionScenarioManager());
		
		this.mapeManager.registerScenarioManager(CameraMonitoringScenario.scenarioType, 
				new CameraMonitoringScenarioManager());
		
		
		this.controller = new SelfAdaptationController(this.baseLevel, this.mapeManager);
	}

	@Override
	public void run() {
		//Increment execution cycle count
		this.models.incrementExecutionCycle();		

		//If the local traffic system is still busy initializing after having just been started: hold off
		// on executing this subsystem until a later cycle
		if(this.baseLevel.localSystemIsInitializing()){			
			return;
		}
		
		//Run through an execution cycle
		this.controller.execute();
	}
}
