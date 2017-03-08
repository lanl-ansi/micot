package gov.lanl.micot.util.graph;

import java.util.Collection;

/**
 * Interface that serves as a wrapper for graph/network libraries
 * Tree implementation
 * @author Russell Bent
 * @param <V>
 * @param <E>
 */
public interface AbstractTree<V,E> {

  /**
   * Sets the root variable
   * @param root
   */
  public void setRoot(V root);
  
  /**
   * Gets the root node
   * @return
   */
  public V getRoot();
  
  /**
   * Adds a child to the tree
   * @param edge
   * @param parent
   * @param child
   */
  public boolean addChild(E edge, V parent, V child);
  
  /**
   * Remvoes a child from the tree
   * @param edge
   * @param parent
   * @param child
   */
  public boolean removeChild(V child);
  
  
  /**
   * Gets all the children of a node
   * @param parent
   * @return
   */
  public Collection<V> getChildren(V parent);
  
}
