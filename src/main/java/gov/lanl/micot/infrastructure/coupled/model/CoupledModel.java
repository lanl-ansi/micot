package gov.lanl.micot.infrastructure.coupled.model;

import gov.lanl.micot.infrastructure.model.Connection;
import gov.lanl.micot.infrastructure.model.Edge;
import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.util.collection.Pair;

import java.util.Collection;


/**
 * An interface for a coupled model.  A model is intended to encapsulate 
 * the components and topology of a coupled model
 * @author Russell Bent
 */
public interface CoupledModel extends Model, Cloneable {
	
	/**
	 * Add a listener to the model
	 * @param listener
	 */
	public void addModelListener(CoupledModelListener listener);
	
	/**
	 * Remove a listener from the model
	 * @param listener
	 */
	public void removeModelListener(CoupledModelListener listener);

	/**
	 * Get the nodes of a edge
	 * @param edge
	 * @return
	 */
	public Collection<CoupledNode> getNodes(Coupling edge);
	
	/**
	 * Get the nodes of a edge in a consistent order
	 * @param edge
	 * @return
	 */
	public Pair<CoupledNode,CoupledNode> getOrderedNodes(Coupling edge);
		
	/**
	 * Adds a edge to the model
	 * @param edge
	 */
	public void addEdge(Coupling edge, CoupledNode fromNode, CoupledNode toNode);
		
	/**
	 * Remove a general edge from the model
	 * @param edge
	 */
	public void removeEdge(Coupling edge);
	    
  /**
	 * Get the neighbors of a node
	 * @param node
	 * @return
	 */
  public Collection<CoupledNode> getNeighbors(CoupledNode node);
  
  /**
   * Get all the nodes
   * @return
   */
  public Collection<CoupledNode> getNodes();
    
	/**
	 * Get the factory used for making lines
	 * @return
	 */
	public CouplingFactory getCouplingFactory();
	
	/**
   * Get the node of a component
   * @param shunt
   * @return
   */
  public CoupledNode getNode(CoupledComponent component);
	
  /**
   * Construct a copy of this model
   * @return
   */
  public CoupledModel clone();
    
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
      
  @Override
  public CoupledNode getFirstNode(Connection edge);

  @Override
  public CoupledNode getSecondNode(Connection edge);

  @Override
  public CoupledNode getFirstNode(Edge edge);

  @Override
  public CoupledNode getSecondNode(Edge edge);

  /**
   * Remove a coupling
   * @param coupling
   */
  public void removeCoupling(Coupling coupling);

  /**
   * Get the couplings in the model
   * @return
   */
  public Collection<Coupling> getCouplings();

  /**
   * Add a model to a system
   * @param model
   */
  public void addModel(Model model);
  
  /**
   * Remove the model
   * @param model
   */
  public void removeModel(Model model);
  
  /**
   * Get a model
   * @param cls
   * @return
   */
  public <E extends Model> Collection<E> getModels(Class<E> cls);

  /**
   * Get couplings
   * @param node
   * @return
   */
  public Collection<Coupling> getCouplings(CoupledNode node);
  
  /**
   * Get all the models
   * @return
   */
  public Collection<Model> getModels();

  /**
   * Add a coupled component
   * @param component
   */
  public void addComponent(CoupledComponent asset);

  /**
   * Remove a coupled component
   * @param component
   */
  public void removeComponent(CoupledComponent asset);
}


