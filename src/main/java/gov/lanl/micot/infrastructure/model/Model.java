package gov.lanl.micot.infrastructure.model;

import java.util.Collection;
import java.util.Set;

import gov.lanl.micot.util.collection.Pair;

/**
 * An interface for a model.  A model is intended to encapsulate 
 * the components and topology of a model
 * @author Russell Bent
 */
public interface Model extends Cloneable {
	
	/**
	 * Get the nodes of a connection
	 * @param link
	 * @return
	 */
	public Collection<? extends Node> getNodes(Connection connection);

	 /**
   * Get the nodes of an edge
   * @param link
   * @return
   */
  public Collection<? extends Node> getNodes(Edge edge);
	
	/**
	 * Get the nodes of a link in a consistent order
	 * @param link
	 * @return
	 */
	public Pair<? extends Node, ? extends Node> getOrderedNodes(Connection edge);

	
	 /**
   * Get the nodes of a link in a consistent order
   * @param link
   * @return
   */
  public Pair<? extends Node, ? extends Node> getOrderedNodes(Edge edge);

	
	/**
	 * Adds a connection to the model
	 * @param edge
	 */
	public void addConnection(Connection edge, Node fromNode, Node toNode);
		
	/**
	 * Remove a general connection from the model
	 * @param edge
	 */
	public void removeConnection(Connection edge);
	
	/**
	 * Get all the edges that bare flow for a node
	 * @param node
	 * @return
	 */
	public Collection<? extends FlowConnection> getFlowConnections(Node node);

	/**
   * Gets all the edges that carry flow
   * @return
   */
  public Collection<? extends FlowConnection> getFlowConnections(Node to, Node fro);
  
	/**
   * Get all the flow bearing edges
   * @return
   */
  public Collection<? extends FlowConnection> getFlowConnections();
    
  /**
	 * Get the neighbors of a node
	 * @param node
	 * @return
	 */
  public Collection<? extends Node> getNeighbors(Node node);
  
  /**
   * Get all the nodes
   * @return
   */
  public Collection<? extends Node> getNodes();
  
  /**
   * Get the first node of a edge
   * @param edge
   * @return
   */
  public Node getFirstNode(Connection edge);

  /**
   * Get the first node of a edge
   * @param edge
   * @return
   */
  public Node getFirstNode(Edge edge);
  
  /**
   * Get the first node of a edge
   * @param edge
   * @return
   */
  public Node getSecondNode(Connection edge);

  /**
   * Get the first node of a edge
   * @param edge
   * @return
   */
  public Node getSecondNode(Edge edge);
  
  /**
   * Gets all the edges
   * @return
   */
  public Collection<? extends Edge> getEdges(Node to, Node fro);

  /**
   * Gets all the edges
   * @return
   */
  public Collection<? extends Connection> getConnections(Node to, Node fro);

  
  	/**
   * Get the node of a component
   * @param shunt
   * @return
   */
  public Node getNode(Component component);
		
  /**
   * Get all links in the models
   * @return
   */
  public Collection<? extends Connection> getConnections();
  
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
  // TODO -- get rid of
  public boolean isSolved();
  
  /**
   * Set the solved state of the model
   * @param solved
   */
  // TODO -- get rid of
  public void setIsSolved(boolean solved);
             
  /**
   * Determine if a connection between n1 and n2 is critical (i.e. breaks
   * the graph into disconnected components)
   * @param n1
   * @param n2
   * @return
   */
  // TODO -- get rid of
 // public boolean isCriticalConnection(Node n1, Node n2);
  
  /**
   * Get all assets that are of type cls
   * @param <E>
   * @param cls
   * @return
   */
  public <E extends Asset> Set<E> getAssets(Class<E> cls);
  
  /**
   * Get all assets that are of type cls
   * @param <E>
   * @param cls
   * @return
   */
  public <E extends Component> Set<E> getComponents(Class<E> cls);
  
  /**
   * Get all assets that are of type cls
   * @param <E>
   * @param cls
   * @return
   */
  public <E extends Connection> Set<E> getConnections(Class<E> cls);
  
  /**
   * Get all the assets in the model
   * @return
   */
  public Set<Asset> getAssets();
  
  /**
   * Get the number of violations
   * @return
   */
  //public int getNumberOfViolations();

  /**
   * Add a violation to the state
   * @param violation
   */
  //public <E extends Violation> void addViolation(E violation);

  /**
   * Get all violations of a certain type
   * @param <E>
   * @param cls
   * @return
   */
  //public <E extends Violation> Collection<E> getViolations(Class<E> cls);
  
  /**
   * Get all the violations of a node
   * @param <E>
   * @param cls
   * @param node
   * @return
   */
  //public <E extends Violation> Collection<E> getViolations(Class<E> cls, Node node);

  /**
   * Clear out the violations
   */
  //public void clearViolations();

  /**
   * Set a ranking of simulation quality used to generator results for this model
   * Maybe not the right place for this function call
   * @param rank
   */
  // TODO -- get rid of
  public void setSimulationQualityRank(int rank);
  
  /**
   * Get the simulation quality rank
   * Maybe not the right place for this function call
   * @return
   */
  // TODO -- get rid of
  public int getSimulationQualityRank();

  /**
   * Get the controls associated with a model
   * @return
   */
  // TODO -- get rid of
  public Collection<Control> getControls();
  
  /**
   * Add a control
   * @param control
   */
  // TODO -- get rid of
  public void addControl(Control control);
  
  /**
   * Remove a control
   * @param control
   */
  // TODO -- get rid of
  public void removeControl(Control control);

  /**
   * Add a model listener
   * @param listener
   */
  public void addModelListener(ModelListener listener);

  /**
   * Remove a model listener
   * @param listener
   */
  public void removeModelListener(ModelListener listener);
  
  /**
   * Get the edges connected to a node
   * @param node
   * @return
   */
  public Collection<? extends Connection> getConnections(Node node);

  /**
   * Get the edges connected to a node
   * @param node
   * @return
   */
  public Collection<Edge> getEdges(Node node);
  
  /**
   * Clone the model
   * @return
   */
  public Model clone();
  
  /**
   * Get the edge associated with a connection
   * @param connection
   * @return
   */
  public Edge getEdge(Connection connection);
  
  /**
   * Get all the edges
   * @return
   */
  public Collection<Edge> getEdges();

  /**
   * Get the version of an asset that is associated with this model
   * 
   * We are using the asset itself as the indexing
   * 
   * @param asset
   * @return
   */
  public Asset getAsset(Asset asset);
}
