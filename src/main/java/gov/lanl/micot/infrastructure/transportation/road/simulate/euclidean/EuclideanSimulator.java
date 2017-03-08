package gov.lanl.micot.infrastructure.transportation.road.simulate.euclidean;

import java.util.ArrayList;
import java.util.Collection;

import gov.lanl.micot.infrastructure.transportation.road.model.Intersection;
import gov.lanl.micot.infrastructure.transportation.road.model.Road;
import gov.lanl.micot.infrastructure.transportation.road.model.RoadModel;
import gov.lanl.micot.infrastructure.transportation.road.model.RoadNode;
import gov.lanl.micot.infrastructure.transportation.road.simulate.RoadSimulatorImpl;
import gov.lanl.micot.util.collection.Pair;

/**
 * This is a a simulator the computes traffic based on Euclidean distances
 * @author Russell Bent
 */
public class EuclideanSimulator extends RoadSimulatorImpl {

  private Collection<RoadNode> allPairNodes = null;
  private Collection<Pair<RoadNode,RoadNode>> individualPairNodes = null;
	
	private static final double MILES_PER_DEGREE = 69.69;
	private static final double AVG_MILES_PER_HOUR = 30;
  
  /**
	 * Constructor
	 * @param nextGenerationPFWFilename
	 */
	protected EuclideanSimulator() {
    allPairNodes = new ArrayList<RoadNode>();
    individualPairNodes = new ArrayList<Pair<RoadNode,RoadNode>>();
	}
	
	@Override
	protected SimulatorSolveState simulateModel(RoadModel roadModel) {		
	  for (Road road : roadModel.getRoads()) {
	  	road.setActualStatus(road.getDesiredStatus());
	  }
	  
	  for (RoadNode node : roadModel.getNodes()) {
	  	Intersection intersection = node.getIntersection();
	  	intersection.setActualStatus(intersection.getDesiredStatus());
	  	if (!intersection.getActualStatus()) {
	  		for (Road road : roadModel.getRoads(node)) {
	  			road.setActualStatus(false);
	  		}
	  	}
	  }
	  	  
	  for (RoadNode node1 : allPairNodes) {
	  	for (RoadNode node2 : allPairNodes) {
	  		double distance = node1.getIntersection().getCoordinate().distance(node2.getIntersection().getCoordinate()) * MILES_PER_DEGREE;
	  		node1.getIntersection().setShortestTime(node2.getIntersection(),distance*AVG_MILES_PER_HOUR);
	      node1.getIntersection().setShortestDistance(node2.getIntersection(),distance);
	  	}
	  }
	  	  
	   // do individual pairs
    for (Pair<RoadNode,RoadNode> pair : individualPairNodes) {
      RoadNode node1 = pair.getOne();
      RoadNode node2 = pair.getTwo();
      double distance = node1.getIntersection().getCoordinate().distance(node2.getIntersection().getCoordinate()) * MILES_PER_DEGREE;
      node1.getIntersection().setShortestTime(node2.getIntersection(),distance*AVG_MILES_PER_HOUR);
      node1.getIntersection().setShortestDistance(node2.getIntersection(),distance);
    }

    //return new RoadSimulatorImplTuple(SimulatorSolveState.CONVERGED_SOLUTION, model);	  
    return SimulatorSolveState.CONVERGED_SOLUTION;    
    
	}

  /**
   * @param nodes the nodes to set
   */
  protected void setAllPairNodes(Collection<RoadNode> nodes) {
    this.allPairNodes = nodes;
  }

  /**
  * @param nodes the nodes to set
  */
 protected void setIndividualNodes(Collection<Pair<RoadNode,RoadNode>> nodes) {
   this.individualPairNodes = nodes;
 }

}

