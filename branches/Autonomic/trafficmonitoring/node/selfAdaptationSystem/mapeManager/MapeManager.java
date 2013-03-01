package node.selfAdaptationSystem.mapeManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.coordination.MapeCommunicationManager;
import node.selfAdaptationSystem.coordination.SelfAdaptationMessage;
import node.selfAdaptationSystem.mapeManager.selfMonitoring.SelfMonitoringScenario;
import node.selfAdaptationSystem.selfAdaptationController.SelfAdaptationController.LocalTrafficSystemRoleType;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class MapeManager {

	public MapeManager(MapeCommunicationManager comm, SelfAdaptationModels models, BaseLevelConnector baseLevel){
		this.communicationManager = comm;
		this.models = models;
		this.baseLevel = baseLevel;
	}

	private SelfAdaptationModels models;
	private BaseLevelConnector baseLevel;
	private MapeCommunicationManager communicationManager;


	/**************************	 
	 * 
	 *	Execution
	 *
	 **************************/

	/**
	 * Tasks:
	 * 1) Verify instantiation of appropriate scenario's (based on the given local system role)
	 * 2) Execute scenario instances
	 */
	public void execute(LocalTrafficSystemRoleType currentRole){
		//In order to be able to print scenario instance information: store local traffic system role in
		// self-adaptation models
		models.setCurrentTrafficRole(currentRole);
		
		//Instantiate and execute only the self-monitoring scenario types first, in order to update the
		// self-adaptation models and allow other scenario managers to decide based on up-to-date information
		this.instantiateNeededScenarios(currentRole, SelfMonitoringScenario.class);
		
		for(SelfAdaptationScenario<?> scenario : this.scenarios){
			try {				
				if(scenario instanceof SelfMonitoringScenario<?>)
					scenario.execute(currentRole);
			} 
			catch (ComputationExecutionException e) {
				e.printStackTrace();
			}
		}
		
		//Verify scenario instantiation (for all scenario type now, especially all non-SelfMonitoring types)
		this.instantiateNeededScenarios(currentRole);
		//Execute all non-SelfMonitoring scenario instances
		for(SelfAdaptationScenario<?> scenario : this.scenarios){
			try {				
				if(!(scenario instanceof SelfMonitoringScenario<?>))
					scenario.execute(currentRole);
			} 
			catch (ComputationExecutionException e) {
				e.printStackTrace();
			}
		}
	}
	

	/**************************	 
	 * 
	 *	Scenario Instantiation
	 *
	 **************************/
	
	/*
	 * Makes sure that the right SelfAdaptationScenarios are instantiated on this node, based on
	 * the given local system role
	 */
	private void instantiateNeededScenarios(LocalTrafficSystemRoleType currentRole){
		//Review instantiation of scenarios of all types
		this.instantiateNeededScenarios(currentRole, SelfAdaptationScenario.class);
	}

	/*
	 * Makes sure that the right SelfAdaptationScenarios of the given type are instantiated on this node, 
	 * based on the given local system role
	 * 
	 * Note: no changes are made as to the (de)instantiation of scenarios of other types
	 */
	private <T extends SelfAdaptationScenario<?>> void instantiateNeededScenarios(LocalTrafficSystemRoleType currentRole, 
																						Class<T> scenarioType){
		//Retain all currently active scenario instances of types other than the given type
		HashSet<SelfAdaptationScenario<?>> instantiatedScenarios = new HashSet<SelfAdaptationScenario<?>>();
		for(SelfAdaptationScenario<?> activeScenario : this.scenarios){
			if(!scenarioType.isInstance(activeScenario))
				instantiatedScenarios.add(activeScenario);
		}
		
		//Continue executing scenarios that are actually busy adapting in response to an issue,
		// regardless of type
		for(SelfAdaptationScenario<?> activeScenario : this.scenarios){
			if(activeScenario.isBusyAdapting()){
				instantiatedScenarios.add(activeScenario);
			}
		}
		
		//Check instances of anticipated scenarios through scenario managers, but only if the manager handles
		// scenarios of the given scenario type
		for(ScenarioManager<?> scenarioManager : this.scenarioManagers.values()){
			if(scenarioManager.instantiatesScenariosOfType(scenarioType)){
				List<ScenarioIdentifier> toBeInstantiatedScenarios = 
					scenarioManager.determineAnticipatedScenarios(currentRole, this.models);
				
				//Iterate over all scenario-identifiers to ensure their instantiation
				for(ScenarioIdentifier scenarioID : toBeInstantiatedScenarios){
					SelfAdaptationScenario<?> scenarioInstance = this.getScenarioInstance(scenarioID);

					//Instantiate a new scenario if no instance was found
					if(scenarioInstance == null){
						scenarioInstance = scenarioManager.instantiateScenario(scenarioID, this.models, this.baseLevel);
						
						//Connect scenario computations with communication manager
						scenarioInstance.register(this.communicationManager);					
					}
					
					instantiatedScenarios.add(scenarioInstance);
				}
			}			
		}		
		
		//Un-instantiate scenarios that were active in the previous execution cycle, but are now no longer needed
		// (e.g. because of a change in LocalTrafficSystemRoleType)
		//Note: again only consider scenario instances of the given scenario type
		for(SelfAdaptationScenario<?> activeScenario : this.scenarios){
			if(!instantiatedScenarios.contains(activeScenario) && (scenarioType.isInstance(activeScenario))){				
				//Disconnect scenario computations from communication manager
				activeScenario.unregister();
				
//				System.out.println("Scenario ended on node " + this.models.getHostNode() + ": " + scenario.getIdentifier());
			}
		}
		
		this.scenarios = instantiatedScenarios;
		
		
		//Handle unanticipated messages (and scenarios), but only in the context of scenarios of the given type
		for(SelfAdaptationMessage unanticipatedMessage : this.communicationManager.previewAllUnanticipatedMessages()){
			//Look up the appropriate ScenarioManager using the message's scenario type
			ScenarioManager<?> scenarioManager = 
				this.scenarioManagers.get(unanticipatedMessage.getScenario().getScenarioType());
			
			if(scenarioManager.instantiatesScenariosOfType(scenarioType)){
				//Check if this message is unanticipated, but actually desired
				if(scenarioManager.canAcceptUnanticipatedMessage(unanticipatedMessage, this.models)){
					String startComputationID = unanticipatedMessage.getTargetMapeComputationID();
					
					SelfAdaptationScenario<?> scenarioInstance = 
						scenarioManager.instantiateScenario(unanticipatedMessage.getScenario(), 
								startComputationID, this.models, this.baseLevel);
					
					//Connect scenario computations with communication manager
					scenarioInstance.register(this.communicationManager);
					
					//Order the communication manager to re-try the delivery of the unanticipated message
					this.communicationManager.deliverUnanticipatedMessage(unanticipatedMessage);
					
					this.scenarios.add(scenarioInstance);
					
//					System.out.println("Unanticipated Scenario instantiated: " + scenarioInstance.getIdentifier());
				}
				else{
					//Discard this unwanted message
					this.communicationManager.removeUnanticipatedMessage(unanticipatedMessage);
				}
			}
		}
	}
	
	/*
	 * Returns the scenario instance characterized by the given scenario identifier.
	 * If no such scenario is found, the null-reference is returned.
	 */
	private SelfAdaptationScenario<?> getScenarioInstance(ScenarioIdentifier scenarioID){
		for(SelfAdaptationScenario<?> scenario : this.scenarios){
			if(scenario.getIdentifier().equals(scenarioID))
				return scenario;
		}
		
		return null;
	}
	
	public Set<SelfAdaptationScenario<?>> getScenarioInstances(){
		return this.scenarios;
	}

	public List<SelfAdaptationScenario<?>> getAllScenariosOfType(String scenarioType){
		ArrayList<SelfAdaptationScenario<?>> result = new ArrayList<SelfAdaptationScenario<?>>();
		
		for(SelfAdaptationScenario<?> scenario : this.scenarios){
			if(scenario.getScenarioType().equals(scenarioType))
				result.add(scenario);
		}
		
		return result;
	}
	
	private HashSet<SelfAdaptationScenario<?>> scenarios = new HashSet<SelfAdaptationScenario<?>>();
	
	
	/**************************	 
	 * 
	 *	Scenario Managers
	 *
	 **************************/
	
	public void registerScenarioManager(String scenarioType, ScenarioManager<?> scenarioManager){
		this.scenarioManagers.put(scenarioType, scenarioManager);
	}
	
	private HashMap<String, ScenarioManager<?>> scenarioManagers = new HashMap<String, ScenarioManager<?>>();
}
