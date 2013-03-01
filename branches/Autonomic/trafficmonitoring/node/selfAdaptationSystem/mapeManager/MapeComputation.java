package node.selfAdaptationSystem.mapeManager;

import utilities.NodeID;
import node.selfAdaptationSystem.coordination.ComputationTransitionMessage;
import node.selfAdaptationSystem.coordination.MapeCoordinationPoint;
import node.selfAdaptationSystem.coordination.SelfAdaptationMessage;
import node.selfAdaptationSystem.coordination.SelfAdaptationMessageSelector;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public abstract class MapeComputation {
	
	public MapeComputation(SelfAdaptationScenario<?> scenario, SelfAdaptationModels models) {
		this.models = models;
		this.scenario = scenario;
		
		//Provide default computation ID based on the name of the subclass		
		String fullClassName = this.getClass().getName();		
		int indexLastDot = fullClassName.lastIndexOf(".");		
		this.computationID = fullClassName.substring(indexLastDot+1, fullClassName.length());
	}	
	
	
	/**************************	 
	 * 
	 *	Identifier
	 *
	 **************************/
	
	public String getComputationID(){
		return this.computationID;
	}
	
	private String computationID;
	
	
	/**************************	 
	 * 
	 * Computation Transition
	 * (intra-loop coordination)
	 *
	 **************************/
	
	/**
	 * Send a computation transition message, together with the given computation input (to
	 * be used by the target computation), to a different local MAPE computation, 
	 * but within the context of the same self-healing scenario.
	 */
	protected <A> void transition(String targetComputationID, A targetComputationInput){
		ComputationTransitionMessage<A> transitionMessage = 
			new ComputationTransitionMessage<A>(this.getCoordinationPoint().getLocalNode(),
												this.getScenario().getIdentifier(),
												this.getComputationID(), 
												targetComputationID, 
												targetComputationInput);
		
		this.getCoordinationPoint().send(this.getCoordinationPoint().getLocalNode(), transitionMessage);
		
		//Print information
		if(this.getScenario().isBusyAdapting()){
			System.out.println("Node " + this.models.getHostNode() + " - " 
					+ this.getSelfAdaptationModels().getCurrentTrafficRole() + " | " 
					+ this.getScenario().toString() + " completed");   
		}
		
		//Deactivate this computation
		this.setActive(false);		
	}
	
	/**
	 * Send a computation transition message to a different local reflective computation, 
	 * but within the context of the same self-healing scenario.
	 * Useful for transitioning to target computations that that don't need additional input in
	 * order to execute.
	 */
	protected void transition(String targetComputationID){
		this.transition(targetComputationID, null);
	}
	
	public boolean hasReceivedComputationTransitionMessage(){
		return this.getCoordinationPoint().hasNextTransitionMessage();
	}
	
	/**
	 * @pre	This computation should have received a computation transition message
	 */
	public void handleTransitionMessage(){
		//Request the transition message from this computation's coordination point and store it
		this.transitionMessage = this.getCoordinationPoint().getNextTransitionMessage();
		
		//Activate this computation
		this.setActive(true);
	}
	
	/**
	 * If this reflective computation has become active after having received a computation
	 * transition message from another computation within the same self-healing scenario,
	 * the accompanying transition message can be consulted using this method.
	 * Return the null-reference otherwise
	 */
	public ComputationTransitionMessage<?> getComputationTransitionMessage(){
		return this.transitionMessage;
	}
	
	private ComputationTransitionMessage<?> transitionMessage = null;
	
	
	/**************************	 
	 * 
	 *	Remote Coordination
	 * (inter-loop coordination)
	 *
	 **************************/
	
	/**
	 * Used only for inter-loop coordination (to the reflective same computation as this one, just
	 * on a different node)
	 */
	public void sendRemoteMessage(NodeID destinationNode, SelfAdaptationMessage message) {
		if(message instanceof ComputationTransitionMessage<?>)
			throw new IllegalArgumentException("This method should only be used for inter-loop coordination!");
		
		//Set needed information
		message.setTargetMapeComputationID(this.getComputationID());
		message.setScenario(this.getScenario().getIdentifier());
		
		this.getCoordinationPoint().send(destinationNode, message);
	}
	
	public boolean hasNextRemoteMessage(){
		return this.getCoordinationPoint().hasNextRemoteMessage();
	}
	
	public SelfAdaptationMessage getNextRemoteMessage(){
		return this.getCoordinationPoint().getNextRemoteMessage();
	}
	
	public SelfAdaptationMessage getNextRemoteMessage(SelfAdaptationMessageSelector messageSelector){
		return this.getCoordinationPoint().getNextRemoteMessage(messageSelector);
	}
	
	
	/**************************	 
	 * 
	 *	Coordination Point
	 *
	 **************************/
	
	protected MapeCoordinationPoint getCoordinationPoint(){
		return this.communication;
	}
	
	public void setCoordinationPoint(MapeCoordinationPoint coordinationPoint){
		this.communication = coordinationPoint;
	}
	
	private MapeCoordinationPoint communication;
	
	
	/**************************	 
	 * 
	 *	Active Computation
	 *
	 **************************/	
	
	public boolean isActive(){
		return this.isActive;
	}
	
	public void setActive(boolean isActive){
		this.isActive = isActive;
	}
	
	private boolean isActive = false;
	
	
	/**************************	 
	 * 
	 *	Reflective Models
	 *
	 **************************/	
	
	public SelfAdaptationModels getSelfAdaptationModels(){
		return this.models;
	}
	
	private SelfAdaptationModels models;
	
	
	/**************************	 
	 * 
	 *	Scenario
	 *
	 **************************/	
	
	public SelfAdaptationScenario<?> getScenario(){
		return this.scenario;
	}
	
	private final SelfAdaptationScenario<?> scenario;
}