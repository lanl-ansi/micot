package gov.lanl.micot.infrastructure.transportation.road.simulate.stub;

import java.util.ArrayList;
import java.util.Collection;

import gov.lanl.micot.infrastructure.transportation.road.model.Intersection;
import gov.lanl.micot.infrastructure.transportation.road.model.Road;
import gov.lanl.micot.infrastructure.transportation.road.model.RoadModel;
import gov.lanl.micot.infrastructure.transportation.road.model.RoadNode;
import gov.lanl.micot.infrastructure.transportation.road.simulate.RoadSimulatorImpl;
import gov.lanl.micot.util.collection.Pair;

/**
 * This is a a simulator the computes traffic based upon a constant value
 * quick and inaccurate
 * @author 236322
 */
public class StubRoadSimulator extends RoadSimulatorImpl {

//	private Collection<RoadNode> nodes = null;
  private Collection<Pair<String,Object>> nodeIds = null;

	
  /**
	 * Constructor
	 * @param nextGenerationPFWFilename
	 */
	protected StubRoadSimulator() {
		nodeIds = new ArrayList<Pair<String,Object>>();
	}

	@Override
	protected SimulatorSolveState simulateModel(RoadModel roadModel) {
	  if (nodeIds.size() == 0) {
	    return SimulatorSolveState.CONVERGED_SOLUTION;
	  }
	  
	  Collection<RoadNode> nodes = new ArrayList<RoadNode>(); 
	    
	  // could be quite inefficient
	  for (RoadNode node : roadModel.getNodes()) {
	    for (Pair<String,Object> id : nodeIds) {
	      if (node.getIntersection().getAttribute(id.getLeft()).equals(id.getRight())) {
	        nodes.add(node);
	      }
	    }     
	  }


	  
	  
//	  for (Road road : roadModel.getRoads()) {
	//  	road.setActualStatus(road.getDesiredStatus());
	 // }
	  
	  for (RoadNode node : roadModel.getNodes()) {
	  	Intersection intersection = node.getIntersection();
	  	//intersection.setActualStatus(intersection.getDesiredStatus());
	  	if (!intersection.getStatus()) {
	  		for (Road road : roadModel.getRoads(node)) {
	  			road.setStatus(false);
	  		}
	  	}
	  }
	  
	  for (RoadNode node1 : nodes) {
	  	for (RoadNode node2 : nodes) {
	  		double distance = 1.234;
	  		node1.getIntersection().setShortestDistance(node2.getIntersection(),distance);
	  		node1.getIntersection().setShortestTime(node2.getIntersection(),distance);
	  	}
	  }

    return SimulatorSolveState.CONVERGED_SOLUTION;	  
	}

	/**
	 * @param nodes the nodes to set
	 */
	protected void setNodes(Collection<Pair<String, Object>> nodeIds) {
		this.nodeIds = nodeIds;
	}

}

