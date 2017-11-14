package gov.lanl.micot.infrastructure.transportation.road.simulate.shortestpath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import gov.lanl.micot.infrastructure.transportation.road.model.Intersection;
import gov.lanl.micot.infrastructure.transportation.road.model.Road;
import gov.lanl.micot.infrastructure.transportation.road.model.RoadModel;
import gov.lanl.micot.infrastructure.transportation.road.model.RoadNode;
import gov.lanl.micot.infrastructure.transportation.road.simulate.RoadSimulatorImpl;
import gov.lanl.micot.util.collection.Pair;
import gov.lanl.micot.util.graph.AbstractGraph;
import gov.lanl.micot.util.graph.GraphFactory;

/**
 * This is a a simulator the computes traffic based upon shortest paths
 * packages
 * @author Russell Bent
 */
public class ShortestPathSimulator extends RoadSimulatorImpl {

  private Collection<Pair<String,Object>> allPairNodeIds = null;
  private Collection<Pair<Pair<String,Object>,Pair<String,Object>>> individualPairNodeIds = null;

//  private Collection<RoadNode> allPairNodes = null;
 // private Collection<Pair<RoadNode,RoadNode>> individualPairNodes = null;

  
  /**
	 * Constructor
	 * @param nextGenerationPFWFilename
	 */
	protected ShortestPathSimulator() {
    allPairNodeIds = new ArrayList<Pair<String,Object>>();
    individualPairNodeIds = new ArrayList<Pair<Pair<String,Object>,Pair<String,Object>>>();
	}

	@Override
	protected SimulatorSolveState simulateModel(RoadModel roadModel) {
	  Collection<RoadNode> allPairNodes = new ArrayList<RoadNode>(); 
	  Collection<Pair<RoadNode,RoadNode>> individualPairNodes = new ArrayList<Pair<RoadNode,RoadNode>>();
	  
	  // could be quite inefficient
	  for (RoadNode node : roadModel.getNodes()) {
	    for (Pair<String,Object> id : allPairNodeIds) {
	      if (node.getIntersection().getAttribute(id.getLeft()).equals(id.getRight())) {
	        allPairNodes.add(node);
	      }
	    }	    
	  }

    for (Pair<Pair<String, Object>, Pair<String, Object>> id : individualPairNodeIds) {
      RoadNode node1 = null;      
      RoadNode node2 = null;
   
      Pair<String, Object> id1 = id.getLeft();
      Pair<String, Object> id2 = id.getRight();
      
      for (RoadNode node : roadModel.getNodes()) {
        if (node.getIntersection().getAttribute(id1.getLeft()).equals(id1.getRight())) {
          node1 = node;
        }
        if (node.getIntersection().getAttribute(id2.getLeft()).equals(id2.getRight())) {
          node2 = node;
        }
      }      
      individualPairNodes.add(new Pair<RoadNode,RoadNode>(node1,node2));
    }
	  	  
	  
//	  for (Road road : roadModel.getRoads()) {
	//  	road.setActualStatus(road.getDesiredStatus());
	 // }
	  
	  for (RoadNode node : roadModel.getNodes()) {
	  	Intersection intersection = node.getIntersection();
	  //	intersection.setActualStatus(intersection.getDesiredStatus());
	  	if (!intersection.getStatus()) {
	  		for (Road road : roadModel.getRoads(node)) {
	  			road.setStatus(false);
	  		}
	  	}
	  }
	  
	  // create the shortest path graph
	  GraphFactory<RoadNode,Road> factory = new GraphFactory<RoadNode,Road>();
	  AbstractGraph<RoadNode,Road> graph = factory.createGraph();
	  Map<Road,Number> weights = new HashMap<Road,Number>();
	  
	  for (RoadNode node : roadModel.getNodes()) {
	  	graph.addNode(node);
	  }
	  
	  // TODO should these be bi-directional?
	  for (Road road : roadModel.getRoads()) {
	  	if (road.getStatus()) {
	  		graph.addEdge(road, roadModel.getFirstNode(road), roadModel.getSecondNode(road));
	  		weights.put(road, road.getLength());
	  	}
	  }
	  	  
	  for (RoadNode node1 : allPairNodes) {
	  	for (RoadNode node2 : allPairNodes) {
	  		Collection<Road> roads = graph.getShortestPath(node1, node2, weights);
	  		double distance = 0;
	  		for (Road road : roads) {
	  			distance += road.getLength();
	  		}
	  		if (!node1.equals(node1) && roads.size() == 0) {
	  			distance = Double.POSITIVE_INFINITY;
	  		}
	  		node1.getIntersection().setShortestDistance(node2.getIntersection(),distance);	  		
	  	}
	  }
	  
	   // do individual pairs
    for (Pair<RoadNode,RoadNode> pair : individualPairNodes) {
      RoadNode node1 = pair.getOne();
      RoadNode node2 = pair.getTwo();
      Collection<Road> roads = graph.getShortestPath(node1, node2, weights);
      double time = 0;
      for (Road road : roads) {
        time += road.getLength() / road.getSpeedLimit();
      }
      if (!node1.equals(node1) && roads.size() == 0) {
        time = Double.POSITIVE_INFINITY;
      }
      node1.getIntersection().setShortestTime(node2.getIntersection(),time);
    }
	  
    return SimulatorSolveState.CONVERGED_SOLUTION;
	}

  /**
   * @param nodes the nodes to set
   */
  protected void setAllPairNodes(Collection<Pair<String, Object>> nodes) {
    this.allPairNodeIds = nodes;
  }

  /**
  * @param nodes the nodes to set
  */
 protected void setIndividualNodes(Collection<Pair<Pair<String, Object>, Pair<String, Object>>> nodes) {
   this.individualPairNodeIds = nodes;
 }

}

