package node.selfAdaptationSystem.mapeManager.cameraIntroduction.caseB;

import java.util.ArrayList;
import java.util.List;

import utilities.NodeID;
import node.organizationMiddleware.contextManager.contextDirectories.Context;
import node.organizationMiddleware.contextManager.contextDirectories.Organization;
import node.organizationMiddleware.contextManager.contextDirectories.OrganizationBoundary;
import node.organizationMiddleware.contextManager.contextDirectories.RolePosition;
import node.selfAdaptationSystem.coordination.protocols.requestReply.RequestReplyProtocolHandler;
import node.selfAdaptationSystem.mapeManager.ComputationExecutionException;
import node.selfAdaptationSystem.mapeManager.PlanComputation;
import node.selfAdaptationSystem.mapeManager.cameraIntroduction.CameraIntroductionComputation;
import node.selfAdaptationSystem.mapeManager.cameraIntroduction.CameraIntroductionScenario;
import node.selfAdaptationSystem.selfAdaptationModels.SelfAdaptationModels;

public class P5B1 extends PlanComputation implements CameraIntroductionComputation {
	
	public P5B1(CameraIntroductionScenario scenario, SelfAdaptationModels models){
		super(scenario, models);
	}
	
	
	/**************************	 
	 * 
	 *	Execution
	 *
	 **************************/
	
	@Override
	/**
	 * As a camera node that has recently come back online after having failed earlier:
	 * - Because the participation in this case B computation implies that the local scenario subject
	 * is supposed to join a neighboring organization as a slave: send relevant local context information
	 * to this new master
	 */
	public void executeOnAliveSubjectNode(){		
		//Retrieve relevant computation input sent by previous computation
		NodeID newMaster = (NodeID) this.getComputationTransitionMessage().getTargetComputationInput();
		
		//If this is the first time this computation gets executed: initiate all needed objects
		if(this.protocolHandler == null){
			this.protocolHandler = new RequestReplyProtocolHandler<Context>(this);
			
			//Context with information to send to new master
			Context contextToSend = new Context(this.getSelfAdaptationModels().getHostNode());
			Organization dummy = new Organization(0, -1, null);
			contextToSend.setPersonalOrg(dummy);
			
			//Send local organization boundaries
			dummy.changeOrganizationBoundaries(this.getSelfAdaptationModels().calculateLocalOrganizationBoundaries());
			
			//Send up-to-date neighbor and traffic info
			// Note: these objects were created in an earlier computation of this scenario and are not an integral part
			// of the local traffic system; hence the "temp" qualifier
			RolePosition rp = new RolePosition(this.getSelfAdaptationModels().getTempTrafficInfo(), 
												this.getSelfAdaptationModels().getTempNeighborInfo(), "dummy");
			rp.setAgentId(this.getSelfAdaptationModels().getHostNode());
			dummy.addFilledRolePosition(rp);
			
			this.protocolHandler.setReplyPayload(contextToSend);
		}
		
		this.protocolHandler.execute();
		
		//If this subject has responded to a request from its future new master: move on the next computation E5B1
		// send along the new master id
		if(this.protocolHandler.hasCompleted()){
			this.transition("E5B1", newMaster);
		}
	}	
	
	@Override
	/**
	 * As the new master of the scenario subject:
	 * - Request and receive relevant context information from the new slave node, i.e. the scenario subject
	 */
	public void executeOnNewSubjectMasterNode(){		
		//If this is the first time this computation gets executed: initiate all needed objects
		if(this.protocolHandler == null){
			this.protocolHandler = new RequestReplyProtocolHandler<Context>(this, this.getScenario().getSubject());
		}
		
		this.protocolHandler.execute();
		
		//If the scenario subject has sent the required information:
		if(this.protocolHandler.hasCompleted()){
			Context receivedContext = this.protocolHandler.getReplyPayload();
			
			//Calculate new organization boundaries for the organization of this master node: 
			// if external agents of one of its current boundaries equals the external agent of a boundary provided by
			// the new slave subject camera, then this means that the subject is located on the edge of the organization and
			// that this new organization boundary should be use for the master's organization
			List<OrganizationBoundary> currentBoundaries = 
				this.getSelfAdaptationModels().getFullLocalOrganizationInformation().getOrganizationBoundaries();
			List<OrganizationBoundary> newSlaveBoundaries = receivedContext.getPersonalOrg().getOrganizationBoundaries();
			ArrayList<OrganizationBoundary> newBoundaries = new ArrayList<OrganizationBoundary>();
			for(OrganizationBoundary currentBoundary : currentBoundaries){
				boolean newBoundaryFound = false;
				
				for(OrganizationBoundary newSlaveBoundary : newSlaveBoundaries){
					if(currentBoundary.getExternalAgent().equals(newSlaveBoundary.getExternalAgent())){
						//Use new boundary sent by slave
						newBoundaries.add(newSlaveBoundary);
						
						newBoundaryFound = true;
					}
				}
				
				if(!newBoundaryFound){
					//Keep using the current boundary
					newBoundaries.add(currentBoundary);
				}
			}
			
			//Change received context for newly calculated boundaries
			receivedContext.getPersonalOrg().changeOrganizationBoundaries(newBoundaries);
			
			//Move on to next computation E5B1
			// send along the organization context containing data to be changed in the local base-level
			this.transition("E5B1", receivedContext);
		}
	}
	
	private RequestReplyProtocolHandler<Context> protocolHandler;
	
	
	
	@Override
	/**
	 * As a master of an organization neighboring on the scenario subject camera, but not to be become the
	 * scenario subject's new master node:
	 * - Do nothing. Technically shouldn't occur (This role does not participate in this computation)
	 * 
	 * @throws	ComputationExecutionException
	 * 			Always; this method should never be used.
	 */
	public void executeOnNeighborMasterNode() throws ComputationExecutionException {
		throw new ComputationExecutionException("P5B1 Computation on Other Neighbor Master Node");
	}

}
