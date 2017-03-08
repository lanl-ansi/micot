package gov.lanl.micot.util.graph.jung;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.TransformerUtils;

import edu.uci.ics.jung.algorithms.filters.KNeighborhoodFilter;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import gov.lanl.micot.util.graph.AbstractGraph;

/**
 * Implementation of the abstract graph that use the JUNG 
 * package
 * @author Russell Bent
 *
 * @param <V>
 * @param <E>
 */
public class JUNGGraph<V,E> extends UndirectedSparseMultigraph<V,E> implements AbstractGraph<V, E>, Serializable {
  protected static final long serialVersionUID = 0;
  
  /**
   * Constructor
   */
  public JUNGGraph() {
  	super();
  }
  
  @Override
  public boolean addEdge(E edge, V vertex1, V vertex2) {
    return super.addEdge(edge,vertex1,vertex2);
  }
  
  @Override
  public void addNode(V vertex) {
    super.addVertex(vertex);
  }
  
  @Override
  public Collection<E> getEdges(V vertex1, V vertex2) {
    return super.findEdgeSet(vertex1, vertex2);
  }
 
  @Override
  public Collection<E> getEdges() {
    return super.getEdges();
  }

  @Override
  public Collection<V> getNodes() {
    return super.getVertices();
  }
  
  @Override
  public AbstractGraph<V,E> constructIsland(V start, int islandExtent) {
    Set<V> set = new HashSet<V>();
    set.add(start);
    KNeighborhoodFilter<V,E> filter = new KNeighborhoodFilter<V,E>(set, islandExtent, KNeighborhoodFilter.EdgeType.IN_OUT);
    return (JUNGGraph<V,E>)filter.transform(this);    
  }
  
  @Override
  public void removeNode(V node) {
    super.removeVertex(node);
  }
  
  @Override
  public boolean removeEdge(E link) {    
    return super.removeEdge(link);
  }
  
  @Override
  public Collection<V> getNeighbors(V node) {
  	return super.getNeighbors(node);
  }
  
  @Override
  public Collection<V> getIncidentVertices(E edge) {
  	if (super.getIncidentCount(edge) == 0) {
  		return new Vector<V>();
  	}
  	return super.getIncidentVertices(edge);
  }

	@Override
	public boolean isCriticalEdge(E edge) {
		Collection<V> vertices = getIncidentVertices(edge);
		Iterator<V> it = vertices.iterator();
		V v1 = it.next();
		V v2 = it.next();
		
		removeEdge(edge);
		DijkstraShortestPath<V,E> alg = new DijkstraShortestPath<V,E>(this);
		Collection<E> path = alg.getPath(v1, v2);	
		boolean returnValue = path == null || path.size() == 0;		
		addEdge(edge, vertices);
		
		return returnValue;
	}
	
	@Override
	public Collection<E> getShortestPath(V n1, V n2) {
		DijkstraShortestPath<V,E> alg = new DijkstraShortestPath<V,E>(this);
		Collection<E> path = alg.getPath(n1, n2);		
		return path;
	}
	
	@Override
	public Collection<E> getShortestPath(V n1, V n2, Map<E,Number> weights) {
		Transformer<E,Number> transformer = TransformerUtils.mapTransformer(weights);
		DijkstraShortestPath<V,E> alg = new DijkstraShortestPath<V,E>(this, transformer);
		Collection<E> path = alg.getPath(n1, n2);		
		return path;
	}


  @Override
  public Collection<E> getEdges(V node) {
    Collection<V> nodes = getNeighbors(node);
    Collection<E> edges = new ArrayList<E>();
    for (V v : nodes) {
      edges.addAll(getEdges(node, v));
    }
    return edges;
  }

 /* @Override
  public Map<V, Point> calculateSpringDirectedLayout(Map<E,Double> distances, Map<V, Point> initialPoints) {
        
    Map<E, Integer> d = new HashMap<E, Integer>();
    for (E e : distances.keySet()) {
      d.put(e, distances.get(e).intValue());
    }
        
    SpringLayout<V,E> layout = new SpringLayout<V,E>(this, new SpringDistance(d));
    layout.setInitializer(new SpringInitializer(initialPoints));
    
    layout.initialize();
    layout.setSize(new Dimension(100,100));
    for (V v : initialPoints.keySet()) {
      Point p = initialPoints.get(v);
      layout.setLocation(v, p.getX(), p.getY());
    }
        
    for (int i = 0; i < 1000; ++i) {
      layout.step();
    }
    
    Map<V, Point> points = new HashMap<V, Point>();    
    for (V node : getNodes()) {
      Point2D p = layout.transform(node);
      PointImpl point = new PointImpl(p.getX(), p.getY());
      points.put(node, point);
    }
    
    return points;
  }*/
  
  /**
   * Spring distance class
   * @author Russell Bent
   *
   */
/*  private class SpringDistance implements org.apache.commons.collections15.Transformer<E, Integer> {

    private Map<E,Integer> map = null;
    
    public SpringDistance(Map<E,Integer> map) {
      this.map = map;
    }
    
    @Override
    public Integer transform(Object arg0) {
      return map.get(arg0);
    }
    
  }*/
  
  
  /*private class SpringInitializer implements org.apache.commons.collections15.Transformer<V,Point2D> {

    private Map<V, Point> map = null;
    
    /**
     * Constructor
     * @param map
     */
    /*public SpringInitializer(Map<V, Point> map) {
      super();
      this.map = map;
    }

    @Override
    public Point2D transform(V arg0) {
      Point2D point = new Point2D.Double(map.get(arg0).getX(), map.get(arg0).getY());
      return point;
    }
    
    
  }*/
  
}
