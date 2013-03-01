package node.selfAdaptationSystem.coordination.protocols;

import node.selfAdaptationSystem.mapeManager.MapeComputation;

/**
 * 
 * Note: should only be used to handle inter-loop coordination, not intra-loop (= computation transitions)
 */
public abstract class CoordinationProtocolHandler<R> {
	
	public CoordinationProtocolHandler(R protocolRole, MapeComputation localMapeComputation){
		this.protocolRole = protocolRole;
		this.mapeComputation = localMapeComputation;
	}
	
	/**************************	 
	 * 
	 *	Local Mape Computation
	 *
	 **************************/
	
	protected MapeComputation getMapeComputation(){
		return this.mapeComputation;
	}
	
	protected int getCurrentExecutionCycle(){
		return this.mapeComputation.getSelfAdaptationModels().getCurrentExecutionCycle();
	}
	
	private final MapeComputation mapeComputation;
	
	
	/**************************	 
	 * 
	 *	Protocol Role
	 *
	 **************************/
	
	public R getProtocolRole(){
		return this.protocolRole;
	}
	
	private R protocolRole;
	
	
	
	/**************************	 
	 * 
	 *	Execution
	 *
	 **************************/
	
	public abstract void execute();
	
	public abstract boolean hasCompleted();

}
