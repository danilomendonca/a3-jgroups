package node.selfAdaptationSystem.selfAdaptationController;

import node.selfAdaptationSystem.baseLevel.BaseLevelConnector;
import node.selfAdaptationSystem.mapeManager.MapeManager;

public class SelfAdaptationController {
	
	public enum LocalTrafficSystemRoleType { SINGLE_MASTER, MASTER_WITH_SLAVES, SLAVE, ROLE_TRANSITION, NO_ROLE }
	
	
	public SelfAdaptationController(BaseLevelConnector baseLevel, MapeManager mapeManager){
		this.baseLevel = baseLevel;
		this.mapeManager = mapeManager;
	}
	
	public void execute(){		
		this.updateCurrentTrafficSystemRole();
		
		//Start execution cycle for self-adaptation scenarios
		this.mapeManager.execute(this.getCurrentLocalSystemRole());
	}
	
	private void updateCurrentTrafficSystemRole(){
		if(this.baseLevel.isRoleless())
			this.setCurrentLocalSystemRole(LocalTrafficSystemRoleType.NO_ROLE);
		else if(this.baseLevel.isInTransition())
			this.setCurrentLocalSystemRole(LocalTrafficSystemRoleType.ROLE_TRANSITION);		
		else if(!this.baseLevel.isMasterNode())
				this.setCurrentLocalSystemRole(LocalTrafficSystemRoleType.SLAVE);
		else if(this.baseLevel.getLocalOrganizationContext().getPersonalOrg().getAgents().size() > 1)
				this.setCurrentLocalSystemRole(LocalTrafficSystemRoleType.MASTER_WITH_SLAVES);
		else
			this.setCurrentLocalSystemRole(LocalTrafficSystemRoleType.SINGLE_MASTER);
	}
	
	private BaseLevelConnector baseLevel;
	private MapeManager mapeManager;
	
	


	public LocalTrafficSystemRoleType getCurrentLocalSystemRole() {
		return this.currentLocalSystemRole;
	}

	public void setCurrentLocalSystemRole(
			LocalTrafficSystemRoleType currentLocalSystemRole) {
		this.currentLocalSystemRole = currentLocalSystemRole;
	}	
	
	private LocalTrafficSystemRoleType currentLocalSystemRole;

}