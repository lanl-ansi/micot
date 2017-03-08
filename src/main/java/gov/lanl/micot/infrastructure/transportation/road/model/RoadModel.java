package gov.lanl.micot.infrastructure.transportation.road.model;

import gov.lanl.micot.infrastructure.model.Component;
import gov.lanl.micot.infrastructure.model.Connection;
import gov.lanl.micot.infrastructure.model.Edge;
import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.util.collection.Pair;

import java.util.Collection;

/**
 * An interface for a model.  A model is intended to encapsulate 
 * the components and topology of a model
 * @author Russell Bent
 */
public interface RoadModel extends Model {
	
	/**
	 * Add a listener to the model
	 * @param listener
	 */
	public void addModelListener(RoadModelListener listener);
	
	/**
	 * Remove a listener from the model
	 * @param listener
	 */
	public void removeModelListener(RoadModelListener listener);

	/**
	 * Get the nodes of a link
	 * @param link
	 * @return
	 */
	public Collection<RoadNode> getNodes(Road road);
	
	/**
	 * Get the nodes of a link in a consistent order
	 * @param link
	 * @return
	 */
	public Pair<RoadNode,RoadNode> getOrderedNodes(Road road);
		
	/**
	 * Adds a link to the model
	 * @param link
	 */
	public void addRoad(Road road, RoadNode fromNode, RoadNode toNode);
		
	/**
	 * Remove a road from the model
	 * @param link
	 */
	public void removeRoad(Road road);
			
	/**
	 * Get all the links that bare flow for a node
	 * @param node
	 * @return
	 */
	public Collection<Road> getRoads(RoadNode node);

	/**
   * Gets all the pipes that carry flow
   * @return
   */
  public Collection<Road> getRoads(RoadNode to, RoadNode fro);
  
	/**
   * Get all the flow bearing links
   * @return
   */
  public Collection<Road> getRoads();
    
  /**
	 * Get the neighbors of a node
	 * @param node
	 * @return
	 */
  public Collection<RoadNode> getNeighbors(RoadNode node);
  
  /**
   * Get all the nodes
   * @return
   */
  public Collection<RoadNode> getNodes();
      
 /**
	 * Get the factory used for making pipes
	 * @return
	 */
	public RoadFactory getRoadFactory();
			
	/**
   * Get the node of a component
   * @param shunt
   * @return
   */
  public RoadNode getNode(Component component);
		
	/**
	 * Add a intersection to the model
	 * @param bus
	 * @param data
	 */
	public void addIntersection(Intersection intersection);
	
	/**
	 * get all the junctions
	 * @return
	 */
	public Collection<? extends Intersection> getIntersections();

	/**
	 * Remove the bus from the model
	 * @param bus
	 */
	void removeIntersection(Intersection intersection);

	/**
	 * Get the factory for creating junctions
	 * @return
	 */
	public IntersectionFactory getIntersectionFactory();
		
	/**
	 * Get the edges between nodes
	 * @param node1
	 * @param node2
	 * @return
	 */
	public Collection<Road> getConnections(RoadNode node1, RoadNode node2);
	
  /**
   * Get all the components in the model
   * @return
   */
  public Collection<Component> getComponents();

  /**
   * Returns an answer as to whether or not the model
   * is a solvable state
   * @return
   */
  public boolean isSolved();
  
  /**
   * Set the solved state of the model
   * @param solved
   */
  public void setIsSolved(boolean solved);
    
  /**
   * Get the number of violations
   * @return
   */
 // public int getNumberOfViolations();
  
  /**
   * Create a new model state that is populated by data in "state"
   * @param state
   * @return
   */
  public RoadModel createNewModel(RoadModel state);

  @Override
  public RoadNode getFirstNode(Connection edge);

  @Override
  public RoadNode getSecondNode(Connection edge);

  @Override
  public RoadNode getFirstNode(Edge edge);

  @Override
  public RoadNode getSecondNode(Edge edge);

  
  /**
   * Clear out the violations
   */
	//public void clearViolations();
	
	/**
	 * Get all the city gates in the model
	 * @return
	 */
	public Collection<? extends Destination> getDestinations();

	/**
	 * Add a load to the model
	 * @param load
	 * @param data
	 */
	public void addDestination(Destination destination, Intersection intersection);
	
	/**
	 * Remove a load from the model
	 * @param load
	 */
	public void removeDestination(Destination destination);

	/**
	 * Return the factory used for creating loads
	 * @return
	 */
	public DestinationFactory getDestinationFactory();
	
  /**
	 * Get all the city gates in the model
	 * @return
	 */
	public Collection<? extends Origin> getOrigins();

	/**
	 * Add a load to the model
	 * @param load
	 * @param data
	 */
	public void addOrigin(Origin origin, Intersection intersection);
	
	/**
	 * Remove a load from the model
	 * @param load
	 */
	public void removeOrigin(Origin origin);

	/**
	 * Return the factory used for creating loads
	 * @return
	 */
	public OriginFactory getOriginFactory();
	
  /**
	 * Get all the destinations in the model
	 * @return
	 */
	public Collection<? extends Destination> getDestinations(RoadNode node);
	
  /**
	 * Get all the origins in the model
	 * @return
	 */
	public Collection<? extends Origin> getOrigins(RoadNode node);
	
  /**
   * Clone the road model
   * @return
   */
  public RoadModel clone();
}
