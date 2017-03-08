package gov.lanl.micot.util.graph.jung;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.FactoryUtils;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.TransformerUtils;

import edu.uci.ics.jung.algorithms.flows.EdmondsKarpMaxFlow;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import gov.lanl.micot.util.graph.AbstractDirectedGraph;

/**
 * Implementation of the abstract directed graph that use the JUNG 
 * package
 * @author Russell Bent
 *
 * @param <V>
 * @param <E>
 */
public class JUNGDirectedGraph<V,E> extends DirectedSparseMultigraph<V,E> implements AbstractDirectedGraph<V, E>, Serializable {
  protected static final long serialVersionUID = 0;
  
  /**
   * Constructor
   */
  public JUNGDirectedGraph() {
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
  	return super.getIncidentVertices(edge);
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
	public Collection<V> getPredecessors(V n1) {
		return super.getPredecessors(n1);
	}
 
 @Override
 public Collection<V> getSuccessors(V n1) {
	 return super.getSuccessors(n1);
 }
 
 @SuppressWarnings("unchecked")
 @Override
 public Map<E,Number> computeMaxFlow(V source, V sink, Map<E,Number> capacities) {
	 // protection if there are no edges
	 if (getEdges().size() <= 0) {
		 return new HashMap<E,Number>();
	 }
	 
	 JUNGDirectedGraph<V,JUNGMaxFlowEdge> flowGraph = new JUNGDirectedGraph<V,JUNGMaxFlowEdge>();
	 Map<JUNGMaxFlowEdge,Number> localFlows = new HashMap<JUNGMaxFlowEdge,Number>();
	 Map<JUNGMaxFlowEdge,Number> newCapacities = new HashMap<JUNGMaxFlowEdge,Number>();
	 
	 for (V v : getNodes()) {
		 flowGraph.addNode(v);
	 }
	 for (E e : getEdges()) {
		 JUNGMaxFlowEdge edge = new JUNGMaxFlowEdge(e);
		 Collection<V> vertices = getIncidentVertices(e);
		 Iterator<V> it = vertices.iterator();
		 V v1 = it.next();
		 V v2 = it.next();
		 flowGraph.addEdge(edge, v1, v2);
		 newCapacities.put(edge, capacities.get(e));
	 }
	 
	 Transformer<JUNGMaxFlowEdge, Number> transformer = TransformerUtils.mapTransformer(newCapacities); 
	 Factory<JUNGMaxFlowEdge> edgeFactory = (Factory<JUNGMaxFlowEdge>) FactoryUtils.instantiateFactory(JUNGMaxFlowEdge.class);
	 	 
	 EdmondsKarpMaxFlow<V,JUNGMaxFlowEdge> ek = new EdmondsKarpMaxFlow<V,JUNGMaxFlowEdge>(flowGraph, source, sink, transformer, localFlows, edgeFactory);
	 ek.evaluate();
	 
	 // translate
	 Map<E,Number> flows = new HashMap<E,Number>();
	 for (JUNGMaxFlowEdge<E> edge : localFlows.keySet()) {
		 flows.put(edge.getWrappedEdge(), localFlows.get(edge));
	 }
	 	 
	 return flows;
	 
 }

 	@Override
 	public Collection<E> getIncomingEdges(V vertex) {
	  return this.getInEdges(vertex);
 	}

 	@Override
 	public Collection<E> getOutgoingEdges(V vertex) {
 		return this.getOutEdges(vertex);
 	}

	@Override
	public V getSource(E edge) {
		return super.getSource(edge);
	}

	@Override
	public V getDestination(E edge) {
		return super.getDest(edge);
	}

  @Override
  public Collection<E> getEdges(V vertex) {
    ArrayList<E> edges = new ArrayList<E>();
    edges.addAll(getIncomingEdges(vertex));
    edges.addAll(getOutgoingEdges(vertex));
    return edges;
  }

}
