package node.selfAdaptationSystem.mapeManager.masterWithSlavesNodeFailureScenario;

import java.util.ArrayList;
import java.util.Collection;

import utilities.NodeID;
import node.organizationMiddleware.contextManager.contextDirectories.Context;
import node.organizationMiddleware.contextManager.contextDirectories.Organization;
import node.organizationMiddleware.contextManager.contextDirectories.OrganizationBoundary;
import node.selfAdaptationSystem.coordination.protocols.aggregation.AggregationProtocolHandler;
import node.selfAdaptationSystem.coordination.protocols.election.ElectionInformation;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.PlanComputation;
import node.selfAdaptationSystem.selfAdaptationModels.OrganizationSnapshot;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class P23 extends PlanComputation implements MasterWithSlavesNodeFailureComputation {

	public P23(MasterWithSlavesNodeFailureScenario scenario, SelfAdaptationModels models) {
		super(scenario, models);
	}
	
	
	/**************************	 
	 * 
	 *	Execution
	 *
	 **************************/	
	
	@Override
	/**
	 * As a remaining slave to this scenario's subject master node:
	 * - send relevant local organization context to the newly elected master
	 */
	public void executeOnSlaveNode(){
		//Retrieve relevant computation input sent by previous computation
		ElectionInformation input = (ElectionInformation) this.getComputationTransitionMessage().getTargetComputationInput();
		NodeID newlyElectedMaster = input.getNewlyElectedMaster();
		
		//Prepare local organization context
		Context context = new Context(this.getSelfAdaptationModels().getHostNode());
		context.setPersonalOrg(new Organization(this.getSelfAdaptationModels().getTrafficJamTreshold(), 
							this.getSelfAdaptationModels().getLocalOrganization().getOrganizationId(), newlyElectedMaster));
		
		//Add agent, just for this local node
		ArrayList<NodeID> agents = new ArrayList<NodeID>();
		agents.add(this.getSelfAdaptationModels().getHostNode());
		context.getPersonalOrg().changeAgents(agents);
		
		//Add local up-to-date role position of this local node: contains local traffic jam and neighbor node information
		context.getPersonalOrg().addFilledRolePosition(this.getSelfAdaptationModels().getLocalRoleposition().copy());
		
		//If this slave node was located at the edge of the original organization: provide local organization boundaries
		context.getPersonalOrg().changeOrganizationBoundaries(this.getSelfAdaptationModels().calculateLocalOrganizationBoundaries());
		
		//Set local neighbor organizations
		for(OrganizationSnapshot neighbor : this.getSelfAdaptationModels().getAllNeighborNodeOrganizationInformation()){
			//Just provide information on the organization ID and its master, not on members of the organization
			Organization newOrganization = new Organization(0, neighbor.getOrganizationId(), neighbor.getMasterNode());
			context.addNeighbourOrganization(newOrganization);
		}
		
		//Send the constructed context to the newly elected master
		AggregationProtocolHandler<Context> aggregationHandler = 
			new AggregationProtocolHandler<Context>(this, newlyElectedMaster, context);		
		aggregationHandler.execute();
		
		//Move on to next computation: E23
		// note: provide the constructed context as an argument
		this.transition("E23", context);
	}
	
	@Override
	/**
	 * As the newly elected master of the organization of which this scenario's subject was the original master:
	 * - receive relevant organization context from all remaining slaves of this organization
	 */
	public void executeOnNewMasterNode() throws ComputationExecutionException {
		//Retrieve relevant computation input sent by previous computation
		ElectionInformation input = (ElectionInformation) this.getComputationTransitionMessage().getTargetComputationInput();
		ArrayList<NodeID> otherRemainingSlaves = input.getOtherRemainingSlaves();
		
		//If no slaves will be a member of this organization: move on to next computation: E23
		if(otherRemainingSlaves.size() == 0){
			//Move on to next computation: E23
			// note: provide the constructed context as an argument
			this.transition("E23", this.constructNewLocalMasterContext());
			
			return;
		}		
		//Otherwise: collect Context information from all remaining slaves
		
		if(this.aggregationHandler == null){
			this.aggregationHandler = new AggregationProtocolHandler<Context>(this, otherRemainingSlaves);
		}	
			
		aggregationHandler.execute();
		
		//Continue working only when all slaves have sent their local contexts
		if(this.aggregationHandler.hasCompleted()){
			//Retrieve all collected slave contexts
			this.localSlaveContexts = this.aggregationHandler.getAggregatedInformation();
			
			//Move on to next computation: E23
			// note: provide the constructed context as an argument
			this.transition("E23", this.constructNewLocalMasterContext());
		}
		else{
			//Stay in this computation for now
			return;
		}
	}
	
	private AggregationProtocolHandler<Context> aggregationHandler;
	private Collection<Context> localSlaveContexts = new ArrayList<Context>();
	
	private Context constructNewLocalMasterContext(){
		//Construct local organization information
		//Take needed information from any of the slave contexts (received earlier)
		float trafficJamTreshold = this.getSelfAdaptationModels().getTrafficJamTreshold();
		//Keep the same organization ID
		int orgId = this.getSelfAdaptationModels().getLocalOrganization().getOrganizationId();
		Organization localOrg = new Organization(trafficJamTreshold, orgId, this.getSelfAdaptationModels().getHostNode());
		
		//Set agents		
		ArrayList<NodeID> agents = new ArrayList<NodeID>();
		//... add this master node
		agents.add(this.getSelfAdaptationModels().getHostNode());
		//... add slaves (if any)
		for(Context slaveCont : this.localSlaveContexts){
			agents.add(slaveCont.getPersonalID());
		}
		localOrg.changeAgents(agents);
		
		//Add up-to-date rolepositions: contain local traffic jam and neighbor node information
		//... for local master nose
		localOrg.addFilledRolePosition(this.getSelfAdaptationModels().getLocalRoleposition());
		//... for all slaves (if any)
		for(Context slaveContext : this.localSlaveContexts){
			//Assume that each slave has sent exactly one RolePosition-object: its own
			localOrg.addFilledRolePosition(slaveContext.getPersonalOrg().getFilledRolePositions().get(0));
		}
		
		//Set organization boundaries
		ArrayList<OrganizationBoundary> boundaries = new ArrayList<OrganizationBoundary>();
		//... from local information
		for(OrganizationBoundary boundary : this.getSelfAdaptationModels().calculateLocalOrganizationBoundaries())
			boundaries.add(boundary);
		//... and from slave information (if any)
		for(Context slaveCont : this.localSlaveContexts){
			for(OrganizationBoundary boundary : slaveCont.getPersonalOrg().getOrganizationBoundaries()){
				if(!boundaries.contains(boundary))
					boundaries.add(boundary);
			}
		}
		localOrg.changeOrganizationBoundaries(boundaries);
		
		//Note: explicit neighbor organization is not set; this information will be gathered by the base-level,
		// following the adaptation of the organization boundaries (calculated above and exacted during E23)
		
		//Construct local context
		Context result = new Context(this.getSelfAdaptationModels().getHostNode());
		result.setPersonalOrg(localOrg);
		
		return result;
	}
	
	
	
	@Override
	/**
	 * As a master of an organization adjacent to the organization of this scenario's subject master node:
	 * - Do nothing. Technically shouldn't occur (a master node of a neighboring organization is not involved
	 * in this particular computation)
	 */
	public void executeOnNeighborMasterNode() throws ComputationExecutionException {
		throw new ComputationExecutionException("P23 Computation on Master of Neighboring Organization");
	}
	
	@Override
	/**
	 * As a master node and subject of this particular Self-Healing Scenario:
	 * - Do nothing. Technically shouldn't occur (at this stage in the scenario, the subject node has failed)
	 * 
	 * @throws	ComputationExecutionException
	 * 			Always; this method should never be used.
	 */
	public void executeOnSubjectMasterNode() throws ComputationExecutionException{
		throw new ComputationExecutionException("P23 Computation on Subject Master Node");
	}
	
}
