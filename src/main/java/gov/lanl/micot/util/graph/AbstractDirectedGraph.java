package gov.lanl.micot.util.graph;

import java.util.Collection;
import java.util.Map;

/**
 * Interface that serves as a wrapper for directed graph libraries
 * @author Russell Bent
 * @param <V>
 * @param <E>
 */
public interface AbstractDirectedGraph<V,E> {

  /**
   * Adds an edge between two pieces
   * @param edge
   * @param vertex1
   * @param vertex2
   */
  public boolean addEdge(E edge, V vertex1, V vertex2);
  
  /**
   * Adds a node to the graph
   * @param vertex
   */
  public void addNode(V vertex);
  
  /**
   * Get all edges between two vertices
   * @param vertex1
   * @param vertex2
   * @return
   */
  public Collection<E> getEdges(V vertex1, V vertex2);
    
  /**
   * Get all edges in the graph
   * @return
   */
  public Collection<E> getEdges();
  
  /**
   * Gets all the nodes in the graph
   * @return
   */
  public Collection<V> getNodes();
    
  /**
   * Removes a node from the graph
   * @param node
   */
  public void removeNode(V node);
  
  /**
   * Removes a link from the graph
   * @param link
   */
  public boolean removeEdge(E link);
  
  /**
   * Get all the neighbors of a node
   * (bi-directional call)
   * @param node
   * @return
   */
  public Collection<V> getNeighbors(V node);
  
  /**
   * Gets the incident vertices
   * @param edge
   * @return
   */
  public Collection<V> getIncidentVertices(E edge);
  
  /**
   * Gets the from edge
   * @param edge
   * @return
   */
  public V getSource(E edge);
  
  /**
   * Gets the to edge
   * @param edge
   * @return
   */
  public V getDestination(E edge);
  
  /**
   * Interface for finding shortest paths on the graph
   * @return
   */
  public Collection<E> getShortestPath(V n1, V n2);
  
  /**
   * Interface for finding shortest paths on the graph
   * with defined edge weights
   * @return
   */
  public Collection<E> getShortestPath(V n1, V n2, Map<E,Number> weights);
  
  /**
   * Get the predessors of a node 
   * @param n1
   * @return
   */
  public Collection<V> getPredecessors(V n1);
  
  /**
   * Get the successors of a node 
   * @param n1
   * @return
   */
  public Collection<V> getSuccessors(V n1);
  
  /**
   * Interface for computing maximum flow
   * @return
   */
  public Map<E,Number> computeMaxFlow(V source, V sink, Map<E,Number> capacities);
  
  /**
   * Get the incoming edges of a graph
   * @param vertex
   * @return
   */
  public Collection<E> getIncomingEdges(V vertex);
  
  /**
   * Get the outgoing edges of the graph
   * @param vertex
   * @return
   */
  public Collection<E> getOutgoingEdges(V vertex);
  
  /**
   * Get all edges attached to V
   * @param vertex
   * @return
   */
  public Collection<E> getEdges(V vertex);
 
}
