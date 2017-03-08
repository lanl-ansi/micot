package gov.lanl.micot.util.graph;

import java.util.Collection;
import java.util.Map;

/**
 * Interface that serves as a wrapper for graph/network libraries
 * @author Russell Bent
 * @param <V>
 * @param <E>
 */
public interface AbstractGraph<V,E> {

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
   * Creates a subgraph of graph from start of all nodes
   * within a certain hop count (island extent) of start
   * @param start
   * @param islandExtent
   * @return
   */
  public AbstractGraph<V,E> constructIsland(V start, int islandExtent);
  
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
   * Determine if edge is critical, i.e. it splits the network into two components
   * @param edge
   * @return
   */
  public boolean isCriticalEdge(E edge);
  
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
   * Get all the edges associated with a node
   * @param node
   * @return
   */
  public Collection<E> getEdges(V node);
    
  
}
