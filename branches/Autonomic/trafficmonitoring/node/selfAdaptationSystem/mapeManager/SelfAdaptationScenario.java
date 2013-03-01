package node.selfAdaptationSystem.mapeManager;

import java.util.ArrayList;
import java.util.List;

import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.coordination.MapeCommunicationManager;
import node.selfAdaptationSystem.coordination.MapeCoordinationPoint;
import node.selfAdaptationSystem.selfAdaptationController.SelfAdaptationController.LocalTrafficSystemRoleType;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

import utilities.NodeID;

public abstract class SelfAdaptationScenario<R> {
	
	public SelfAdaptationScenario(NodeID scenarioSubject, BaseLevelConnector baseLevel, SelfAdaptationModels models){
		this.subject = scenarioSubject;
		
		this.baseLevel = baseLevel;
		this.models = models;
		
		//Print information
//		System.out.println("Scenario started on node " + this.models.getHostNode() + " | " +  this.getIdentifier());
	}
	
	
	/**************************	 
	 * 
	 *	Self-Adaptation Models
	 *
	 **************************/
	
	public SelfAdaptationModels getSelfAdaptationModels(){
		return this.models;
	}
		
	private SelfAdaptationModels models;
	
	
	/**************************	 
	 * 
	 *	Base Level
	 *
	 **************************/
	
	public BaseLevelConnector getBaseLevelConnector(){
		return this.baseLevel;
	}
	
	private BaseLevelConnector baseLevel;
	
	
	/**************************	 
	 * 
	 *	Scenario Identifier
	 *
	 **************************/
	
	public ScenarioIdentifier getIdentifier(){
		return new ScenarioIdentifier(this.subject, this.getScenarioType());
	}
	
	public abstract String getScenarioType();
	
	
	/**************************	 
	 * 
	 *	Scenario Subject
	 *
	 **************************/
	
	public NodeID getSubject(){
		return this.subject;
	}
	
	private NodeID subject;
	
	
	/**************************	 
	 * 
	 *	Scenario Role
	 *
	 **************************/
	
	public R getRole(){
		return this.scenarioRole;
	}
	
	protected void setRole(R newScenarioRole){
		this.scenarioRole = newScenarioRole;
	}
	
	private R scenarioRole;
	
	/**
	 * Make sure the role this local node is occupying within the context of this
	 * scenario is still accurate, based on the given base-level role
	 * 
	 * Note: called before each scenario execution cycle
	 */
	protected abstract void updateCurrentScenarioRole(LocalTrafficSystemRoleType currentRole);
	
	
	/**************************
	 * 
	 *	Communication Manager
	 *
	 **************************/
	
	/**
	 * For each MAPE computation in the control loop of this scenario, create a new Coordination Point and
	 * register it at the given MapeCommunicationManager
	 */
	public void register(MapeCommunicationManager communicationManager){		
		for(MapeComputation comp : this.getControlLoop()){
			MapeCoordinationPoint point = 
				new MapeCoordinationPoint(communicationManager, this.models.getHostNode());

			point.setReflectiveComputation(comp);				
			comp.setCoordinationPoint(point);
			communicationManager.registerCoordinationPoint(point);
		}
		
		//Register for later use
		this.communicationManager = communicationManager;	
	}
	
	/**
	 * For each MAPE computation in the control loop of this scenario, unregister its Coordination Point
	 * and afterwards disconnect it from the associated computation
	 */
	public void unregister(){
		for(MapeComputation comp : this.getControlLoop()){	
			this.communicationManager.unregisterCoordinationPoint(comp.getCoordinationPoint());
			comp.getCoordinationPoint().setReflectiveComputation(null);
			comp.setCoordinationPoint(null);	
		}
	}
	
	private MapeCommunicationManager communicationManager;
	
	
	/**************************
	 * 
	 *	Control Loop
	 *
	 **************************/		
	
	/**
	 * Note: 	more than one computation can potentially be active at the same time
	 * 
	 * @throws 	ComputationExecutionException 
	 */
	public void execute(LocalTrafficSystemRoleType currentRole) throws ComputationExecutionException {
		//Update scenario role
		this.updateCurrentScenarioRole(currentRole);
		
		if(this.hasEnded()){
			throw new ComputationExecutionException("This scenario has ended");
		}
		
		//When healing, print execution information each execution cycle
//		if(this.isBusyAdapting()){
//			System.out.println("Node " + this.models.getHostNode() + " - " + this.getRole() + " | " + this.toString());   
//		}
		
		//Execute all active computations
		for(MapeComputation activeComputation : this.getActiveComputations()){
			this.execute(activeComputation);
		}
		
		//Check whether any active computations have just sent a computation transition
		// messages by asking all computations if they've received any transition messages and therefore 
		// like to become a new active computation within this self-healing scenario
		for(MapeComputation comp : this.getControlLoop()){
			if(comp.hasReceivedComputationTransitionMessage()){
				//Instruct the new active computation to handle the transition message, in order to be able to properly
				// start the next execution as a new active computation
				comp.handleTransitionMessage();
			}
		}
	}
	
	/**
	 * Execute the given computation in the context of this scenario instance
	 * 
	 * @throws 	ComputationExecutionException 
	 */
	protected abstract void execute(MapeComputation computation) throws ComputationExecutionException;
		
	/**
	 * Check whether any of the active computations for this scenario instance are actively adapting to certain
	 * self-adaptation concerns
	 */
	public boolean isBusyAdapting(){
		for(MapeComputation activeComputation : this.getActiveComputations()){
			if(this.isBusyAdapting(activeComputation))
				return true;
		}
		
		return false;
	}
	
	/*
	 * Checks whether the given active computation is a critical part of the actual adaptation for
	 * this scenario instance
	 */
	protected abstract boolean isBusyAdapting(MapeComputation activeComputation);
	
	
	/**
	 * Returns true if the active computations for this scenario instance are all of the ControlLoopEnd type.
	 * False otherwise.
	 */
	public boolean hasEnded(){
		for(MapeComputation scenarioComputation : this.getActiveComputations()){
			if( !(scenarioComputation instanceof ControlLoopEnd) )
				return false;
		}
		
		return true;
	}
	
	public List<MapeComputation> getActiveComputations(){
		ArrayList<MapeComputation> result = new ArrayList<MapeComputation>();
		
		for(MapeComputation current : this.controlLoop){
			if(current.isActive())
				result.add(current);
		}
		
		return result;
	}
	
	/*
	 * 
	 * @throws	IllegalArgumentException
	 * 			Invalid computations for this SelfAdaptationScenario
	 */
	protected void addReflectiveComputation(MapeComputation computation){
		if(!this.canHaveAsComputation(computation))
			throw new IllegalArgumentException("Invalid computation for this Self-Adaptation Scenario");
		
		this.controlLoop.add(computation);
	}
	
	public abstract boolean canHaveAsComputation(MapeComputation computation);
	
	/*
	 * Note: more than one computation can be active at the same time in the context of one scenario instance
	 */
	protected void setStartComputation(MapeComputation startComp){
		if(!this.controlLoop.contains(startComp))
			throw new IllegalArgumentException("Computation not a part of this scenario");
		
		//Set start computation as active
		startComp.setActive(true);
	}
	
	public void setOnlyActiveComputation(String mapeComputationID){
		for(MapeComputation computation : this.controlLoop){
			if(computation.getComputationID().equals(mapeComputationID)){
				computation.setActive(true);
			}
			else{
				computation.setActive(false);
			}
		}
	}
	
	public List<MapeComputation> getControlLoop(){
		return this.controlLoop;
	}
		
	private ArrayList<MapeComputation> controlLoop = new ArrayList<MapeComputation>();	
	
	
	@Override
	public boolean equals(Object o){
		try{
			SelfAdaptationScenario<?> scenario = (SelfAdaptationScenario<?>) o;
			
			return (scenario.getIdentifier().equals(this.getIdentifier()));
		}
		catch(ClassCastException e){
			return false;
		}
		
	}
	
	@Override
	public int hashCode(){
		return this.getIdentifier().hashCode();
	}
	
	@Override
	public String toString(){
		String print = this.getScenarioType() + "(Subject Node " + this.getSubject() + ") - " + this.getRole() + " |";
		
		//Print all active computations for this scenario instance
		for(MapeComputation activeComputation : this.getActiveComputations()){
			print += " " + activeComputation.getComputationID();
		}
		
		return print;
	}	
	
}
