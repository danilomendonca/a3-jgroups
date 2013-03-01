package node.selfAdaptationSystem.mapeManager.singleMasterNodeFailureScenario;

import node.selfAdaptationSystem.mapeManager.SelfHealingScenario;

/**
 * A scenario meant to detect and deal with the failure of a master of a single member organization.
 * 
 * Scenario roles:
 * - Subject single master node
 * - Masters of neighboring organizations (which will afterwards become direct neighbor organizations
 * of each other)
 * 
 * Implementation-specific note: this type of scenario is not really needed.
 * The direct neighbor nodes of a failing single master node exchange information, in the context of
 * NeighborNodeFailureScenarios, with the other neighbors of the dead subject node. These direct neighbor 
 * nodes are also members of the new neighboring organizations of the organization the local node belongs to.
 * Relying on the base-level functionality (specifically NeighbourInfoPerceptionMechanism,
 * NeighbourInfoSyncMechanism, NeighbourInfoAggregationMechanism and Neighbours_SyncMechanism), the
 * necessary information is exchanged between the new neighboring organizations and this single master
 * node failure event is therefore handled automatically.
 */
public abstract class SingleMasterNodeFailureScenario extends SelfHealingScenario<Void> {

	private SingleMasterNodeFailureScenario() {
		super(null, null, null);
	}	

}
