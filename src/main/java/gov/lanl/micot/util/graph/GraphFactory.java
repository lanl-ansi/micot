package gov.lanl.micot.util.graph;

/**
 * Factory for creating graphs. For right now it just creates JUNG
 * implementations
 * 
 * @author Russell Bent
 * 
 */
public class GraphFactory<V, E> {

  public static final String GRAPH_CLASS          = "gov.lanl.micot.util.graph.jung.JUNGGraph";
  public static final String DIRECTED_GRAPH_CLASS = "gov.lanl.micot.util.graph.jung.JUNGDirectedGraph";
  public static final String TREE_CLASS           = "gov.lanl.micot.util.graph.jung.JUNGTree";

  
  /**
   * Constructor
   */
  public GraphFactory() {
  }

  /**
   * Factory function for creating graphs
   * 
   * @return
   */
  @SuppressWarnings("unchecked")
  public AbstractGraph<V, E> createGraph() {
    try {
      return (AbstractGraph<V, E>) Class.forName(GRAPH_CLASS).newInstance();
    }
    catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Factory function for creating directed graphs
   * 
   * @return
   */
  @SuppressWarnings("unchecked")
  public AbstractDirectedGraph<V, E> createDirectedGraph() {
    try {
      return (AbstractDirectedGraph<V, E>) Class.forName(DIRECTED_GRAPH_CLASS).newInstance();
    }
    catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Factory function for creating trees
   * 
   * @return
   */
  @SuppressWarnings("unchecked")
  public AbstractTree<V, E> createTree() {
    try {
      return (AbstractTree<V, E>) Class.forName(TREE_CLASS).newInstance();
    }
    catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }
}
