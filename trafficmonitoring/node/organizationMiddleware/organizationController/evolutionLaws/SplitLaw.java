package node.organizationMiddleware.organizationController.evolutionLaws;

import java.util.ArrayList;
import java.util.Iterator;

import node.organizationMiddleware.contextManager.contextDirectories.Organization;
import node.organizationMiddleware.contextManager.contextDirectories.RolePosition;


import utilities.NodeID;

public class SplitLaw{

	public ArrayList<ArrayList<NodeID>> split(Organization org){
		
		ArrayList<RolePosition> congestedOnes = new ArrayList<RolePosition>();
		ArrayList<ArrayList<RolePosition>> finalResult = new ArrayList<ArrayList<RolePosition>>();
		
		for(RolePosition rp : org.getFilledRolePositions()){
			if(rp.getTrafficJamInfo().getAvgVelocity() >= org.getTrafficJamTreshhold() || rp.getTrafficJamInfo().getDensity() == 0 ){
				//no congestion => add as a sole entity
				ArrayList<RolePosition> soleEntity = new ArrayList<RolePosition>();
				soleEntity.add(rp);
				finalResult.add(soleEntity);
			}else{
				//roleposition sees congestion => should be put together with it's neighbouring
				//agents who also see congestion
				congestedOnes.add(rp);
			}
		}
		
		//put neighbouring rolepositions who see congestion together in groups
		finalResult.addAll(splitCongestedOnesInGroups(congestedOnes));
		return convert(finalResult);
	}
	
	private ArrayList<ArrayList<NodeID>> convert(ArrayList<ArrayList<RolePosition>> rolePositions){
		ArrayList<ArrayList<NodeID>> result = new ArrayList<ArrayList<NodeID>>();
		for(ArrayList<RolePosition> list : rolePositions){
			ArrayList<NodeID> agentList = new ArrayList<NodeID>();
			result.add(agentList);
			for(RolePosition rp : list){
				agentList.add(rp.getAgentId());
			}
		}
		return result;
	}
	
	private ArrayList<ArrayList<RolePosition>> splitCongestedOnesInGroups(ArrayList<RolePosition> rolePositions){
		ArrayList<ArrayList<RolePosition>> result = new ArrayList<ArrayList<RolePosition>>();
		while(rolePositions.size() > 0){
			RolePosition rp = rolePositions.get(0);
			rolePositions.remove(0);
			ArrayList<RolePosition> neighbours = getNeighbours(rp, rolePositions);
			rolePositions.removeAll(neighbours);
			result.add(neighbours);
		}
		return result;
	}
	
	private ArrayList<RolePosition> getNeighbours(RolePosition rp, ArrayList<RolePosition> rolePositions){
		ArrayList<RolePosition> result = new ArrayList<RolePosition>();
		result.add(rp);
		boolean neighboursFound = true;
		while(rolePositions.size() != 0 && neighboursFound){
			neighboursFound = false;
			Iterator<RolePosition> iterator = rolePositions.iterator();
			while(iterator.hasNext()){
				RolePosition rp2 = iterator.next();
				if(adjacent(result, rp2)){
					neighboursFound = true;
					result.add(rp2);
					iterator.remove();
				}
			}
		}
		return result;
	}

	private boolean adjacent(ArrayList<RolePosition> group, RolePosition rp) {
		for(RolePosition rp2 : group){
			for(NodeID neighbourOfRp2 : rp2.getNeighbourInfo().getNeighbours()){
				if(neighbourOfRp2.equals(rp.getAgentId()))
					return true;
			}
		}
		return false;
	}
	
}
