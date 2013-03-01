package node.selfAdaptationSystem.mapeManager.cameraIntroduction;

import java.util.ArrayList;
import java.util.Collection;

import utilities.NodeID;
import utilities.Pair;
import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.coordination.protocols.aggregation.AggregationProtocolHandler;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.MonitorComputation;
import node.selfAdaptationSystem.selfAdaptationModels.OrganizationSnapshot;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class M52 extends MonitorComputation implements CameraIntroductionComputation {
	
	public M52(CameraIntroductionScenario scenario, SelfAdaptationModels models, BaseLevelConnector baseLevel){
		super(scenario, models, baseLevel);
	}
	
	
	/**************************	 
	 * 
	 *	Execution
	 *
	 **************************/
	
	@Override
	/**
	 * As a camera node that has recently come back online after having failed earlier:
	 * - Collect information from the masters of all neighboring organizations on whether or not they are
	 * currently noticing traffic congestion for their organization
	 */
	public void executeOnAliveSubjectNode(){
		//Create protocol handler for the role of AGGREGATOR, collecting information from the masters of all
		// neighboring organizations
		if(this.protocolHandler == null){
			ArrayList<NodeID> senders = new ArrayList<NodeID>();
			for(OrganizationSnapshot neighborOrg : this.getSelfAdaptationModels().getAllNeighborNodeOrganizationInformation()){
				senders.add(neighborOrg.getMasterNode());
			}
			
			this.protocolHandler = new AggregationProtocolHandler<Pair<NodeID, Boolean>>(this, senders);
		}
		
		this.protocolHandler.execute();
		
		//Check if every neighbor master has sent the relevant data
		if(this.protocolHandler.hasCompleted){
			Collection<Pair<NodeID, Boolean>> collectedInfo = this.protocolHandler.getAggregatedInformation();
			boolean localCongestion = this.getBaseLevelConnector().localCameraSeesTrafficCongestion();
			
			//Move on to next computation: A52
			// send along the local traffic info plus the collected traffic information from all neighbor masters
			Pair<Boolean, ?> info = new Pair<Boolean, Collection<Pair<NodeID, Boolean>>>(localCongestion, collectedInfo);
			this.transition("A52", info);
		}
		else{
			//Not all data received yet; try again in next execution cycle
			return;
		}	
	}
	
	@Override
	/**
	 * As a master of an organization neighboring on the scenario subject camera:
	 * - Send the scenario subject information on whether or not the organization of this local master is currently
	 * noticing traffic congestion
	 */
	public void executeOnNeighborMasterNode(){
		//Check whether the organization of this local master currently sees a traffic jam
		boolean seesCongestion = this.getBaseLevelConnector().localOrganizationSeesTrafficCongestion();		
		
		//Send traffic information to subject camera
		Pair<NodeID,Boolean> trafficInfo = 
			new Pair<NodeID,Boolean>(this.getSelfAdaptationModels().getHostNode(), seesCongestion);
		this.protocolHandler = 
			new AggregationProtocolHandler<Pair<NodeID,Boolean>>(this, this.getScenario().getSubject(), trafficInfo);
		this.protocolHandler.execute();
		
		//Move on to next computation: A52
		this.transition("A52");
	}
	
	private AggregationProtocolHandler<Pair<NodeID, Boolean>> protocolHandler;
	
	
	
	
	@Override
	/**
	 * As the new master of the scenario subject:
	 *- Do nothing. Technically shouldn't occur (This role does not participate in this computation)
	 * 
	 * @throws	ComputationExecutionException
	 * 			Always; this method should never be used.
	 */
	public void executeOnNewSubjectMasterNode() throws ComputationExecutionException {
		throw new ComputationExecutionException("M52 Computation on New Subject Master Node");
	}
}