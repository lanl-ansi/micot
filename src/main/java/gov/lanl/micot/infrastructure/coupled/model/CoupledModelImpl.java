package gov.lanl.micot.infrastructure.coupled.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.model.Component;
import gov.lanl.micot.infrastructure.model.Connection;
//import gov.lanl.micot.infrastructure.model.Corridor;
import gov.lanl.micot.infrastructure.model.Edge;
import gov.lanl.micot.infrastructure.model.EdgeImpl;
import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.model.ModelChangeException;
import gov.lanl.micot.infrastructure.model.ModelImpl;
import gov.lanl.micot.infrastructure.model.ModelListener;
import gov.lanl.micot.infrastructure.model.Node;
import gov.lanl.micot.util.collection.Pair;
import gov.lanl.micot.util.graph.AbstractDirectedGraph;
import gov.lanl.micot.util.graph.AbstractGraph;
import gov.lanl.micot.util.graph.GraphFactory;

/**
 * Abstraction of coupled models. The contract with models is that the control the
 * editing and modification of components of the model. The component itself is
 * used as a key to find the particular state of that component within the model
 * 
 * @author Russell Bent
 */
public abstract class CoupledModelImpl extends ModelImpl implements CoupledModel, CouplingChangeListener {

  protected static final long                                                     serialVersionUID   = 0;

  protected AbstractDirectedGraph<CoupledNode, Edge>                              graph              = null;

  private Set<CoupledModelListener>                                                modelListeners     = null;

  private Map<Coupling, Edge>                                                      couplings          = null;

  private Map<CoupledComponent, CoupledNode>                                       components         = null;
  
  private Set<CoupledNode>                                                         nodes              = null;

  private boolean                                                                  isSolved           = false;
  
  private Set<Model>                                                               models             = null;
  
  private CouplingFactory couplingFactory = null;
  
  /**
   * Constructor
   * 
   * @param graph
   */
  public CoupledModelImpl(AbstractDirectedGraph<CoupledNode, Edge> graph) {
    super();
    init(graph);
  }

  /**
   * Constructor for default models
   */
  protected CoupledModelImpl() {
    super();
    GraphFactory<CoupledNode, Edge> factory = new GraphFactory<CoupledNode, Edge>();
    init(factory.createDirectedGraph());
  }

  /**
   * Initialization routine
   */
  protected void init(AbstractDirectedGraph<CoupledNode, Edge> graph) {
    this.graph = graph;
    modelListeners = new HashSet<CoupledModelListener>();
    couplings = new LinkedHashMap<Coupling, Edge>();
    components = new LinkedHashMap<CoupledComponent, CoupledNode>();
    nodes = new HashSet<CoupledNode>();
    isSolved = true;
    models = new LinkedHashSet<Model>();
  }

  @Override
  public Edge getEdge(Connection connection) {
    return couplings.get(connection);
  }
  
  @Override
  public Pair<CoupledNode, CoupledNode> getOrderedNodes(Coupling l) {
    Edge edge = getEdge(l);
    return getOrderedNodes(edge);    
  }

  @Override
  public Pair<CoupledNode, CoupledNode> getOrderedNodes(Edge l) {
    Collection<CoupledNode> nodes = graph.getIncidentVertices(l);
    Iterator<CoupledNode> it = nodes.iterator();
    return new Pair<CoupledNode, CoupledNode>(it.next(), it.next());
  }
  
  @Override
  public Collection<CoupledNode> getNodes(Coupling link) {
    return graph.getIncidentVertices(getEdge(link));
  }

  @Override
  public Collection<CoupledNode> getNodes(Edge edge) {
    return graph.getIncidentVertices(edge);
  }
  
  @Override
  public CoupledNode getFirstNode(Connection edge) {
    return getNodes(edge).iterator().next();
  }

  @Override
  public CoupledNode getSecondNode(Connection edge) {
    Iterator<CoupledNode> it = getNodes(edge).iterator();
    it.next();
    return it.next();
  }

  @Override
  public CoupledNode getFirstNode(Edge edge) {
    return getNodes(edge).iterator().next();
  }

  @Override
  public CoupledNode getSecondNode(Edge edge) {
    Iterator<CoupledNode> it = getNodes(edge).iterator();
    it.next();
    return it.next();
  }

  @Override
  public Collection<Edge> getEdges(Node to, Node fro) {
    return graph.getEdges((CoupledNode)to, (CoupledNode)fro);
  }
    
 /**
   * Return all the lines
   * 
   * @param to
   * @param fro
   * @return
   */
  public Collection<Coupling> getCouplings(CoupledNode to, CoupledNode fro) {
    Collection<? extends Connection> temp = getConnections(to, fro);
    Vector<Coupling> lines = new Vector<Coupling>();
    for (Connection link : temp) {
      if (link instanceof Coupling) {
        lines.add((Coupling) link);
      }
    }
    return lines;
  }

  @Override
  public Collection<CoupledNode> getNodes() {
    return graph.getNodes();
  }

  @Override
  public void addConnection(Connection link, Node fromNode, Node toNode) {
    addEdge((Coupling)link, (CoupledNode)fromNode, (CoupledNode)toNode);
  }
  
  @Override
  public void addEdge(Coupling link, CoupledNode fromNode, CoupledNode toNode) {
    if (link instanceof Coupling) {
      addCoupling((Coupling) link, fromNode, toNode);
    }
    fireLinkAddedEvent(link);      
  }

  /**
   * Add a line to the model
   * 
   * @param line
   * @param data
   * @param node1
   * @param node2
   */
  private void addCoupling(Coupling line, CoupledNode fromNode, CoupledNode toNode) {
    Edge edge = null;
    if (getEdges(fromNode, toNode).size() <= 0) {
      edge = new EdgeImpl(line);
      graph.addEdge(edge,fromNode,toNode);
    }
    else {
      edge = getEdges(fromNode, toNode).iterator().next();
      edge.addConnection(line);
    }
    couplings.put(line, edge);        
    line.addCouplingChangeListener(this);
    addAsset(line);
  }

  /**
   * Fire a link added event
   * 
   * @param link
   */
  private void fireLinkAddedEvent(Coupling link) {
    for (CoupledModelListener listener : modelListeners) {
      listener.linkAdded(link);
    }
    Collection<CoupledNode> nodes = getNodes(link);
    Iterator<CoupledNode> it = nodes.iterator();
    CoupledNode node1 = it.next();
    CoupledNode node2 = it.next();   
    for (ModelListener listener : getModelListeners()) {
      listener.addEdge(link, /*tc,*/ node1, node2);
    }
  }

  @Override
  public void removeConnection(Connection edge) {
  	removeEdge((Coupling)edge);
  }
  
  @Override
  public void removeEdge(Coupling link) {
    fireLinkRemovedEvent(link);    
    removeCoupling(link);
  }

  @Override
  public void removeCoupling(Coupling coupling) {
    Edge edge = getEdge(coupling);    
    if (coupling.equals(edge.getPrimaryConnection())) {
      if (edge.getConnections(Connection.class).size() > 1) {
        throw new ModelChangeException("Attempting to remove the prinary connection before all other connections are removed");
      }
    }
    
    edge.removeConnection(coupling);
    if (edge.getConnections(Connection.class).size() == 0) {
      if (!graph.removeEdge(edge)) {
        throw new ModelChangeException("Unable to find link " + coupling + " to remove");
      }      
    }
    
    couplings.remove(coupling);
    coupling.removeCouplingChangeListener(this);
    removeAsset(coupling);
  }

  /**
   * Notify the listeners that the link has been removed
   * 
   * @param link
   */
  private void fireLinkRemovedEvent(Coupling link) {
    for (CoupledModelListener listener : modelListeners) {
      listener.linkRemoved(link);
    }
    Collection<CoupledNode> nodes = getNodes(link);
    Iterator<CoupledNode> it = nodes.iterator();
    CoupledNode node1 = it.next();
    CoupledNode node2 = it.next();    
//    Corridor tc = getCorridor(node1,node2);     
    for (ModelListener listener : getModelListeners()) {
      listener.removeEdge(link,/*tc,*/ node1, node2);
    }
  }

  @Override
  public Collection<CoupledNode> getNeighbors(CoupledNode node) {
    return graph.getNeighbors(node);
  }

  @Override
  public Collection<Coupling> getCouplings(CoupledNode node) {
    Vector<Coupling> lines = new Vector<Coupling>();
    Collection<CoupledNode> neighbors = graph.getNeighbors(node);
    for (CoupledNode node2 : neighbors) {
      Collection<? extends Connection> links = getConnections(node, node2);
      for (Connection l : links) {
        if (l instanceof Coupling) {
          lines.add((Coupling) l);
        }
      }
    }
    return lines;
  }

  @Override
  public Collection<Coupling> getCouplings() {
    return couplings.keySet();
  }

  @Override
  public void addModelListener(CoupledModelListener listener) {
    modelListeners.add(listener);
  }

  @Override
  public void removeModelListener(CoupledModelListener listener) {
    modelListeners.remove(listener);
  }
 
  @Override
  public void addComponent(CoupledComponent asset) {
    if (components.containsKey(asset)) {
      return;
    }
   
    CoupledNode node = createNode(asset);
    components.put(asset, node);
    graph.addNode(node);
    nodes.add(node);
    fireComponentAddEvent(asset);
    addAsset(asset);
  }

  /**
   * Notifies the listener that a bus has been added to the system
   * 
   * @param shunt
   * @param state
   */
  private void fireComponentAddEvent(CoupledComponent component) {
    for (CoupledModelListener listener : modelListeners) {
      listener.componentAdded(component);
    }
  }

  @Override
  public void removeComponent(CoupledComponent component) {
    CoupledNode node = getNode(component);
    components.remove(component);
    nodes.remove(node);
    graph.removeNode(node);
    destroyNode(node);
    fireComponentRemoveEvent(component);
    removeAsset(component);
  }

  /**
   * Notifies the listener that a bus has been removed
   * 
   * @param shunt
   * @param state
   */
  private void fireComponentRemoveEvent(CoupledComponent component) {
    for (CoupledModelListener listener : modelListeners) {
      listener.componentRemoved(component);
    }
  }

  @Override
  public Collection<Edge> getEdges() {
    return graph.getEdges();
  }
  
  @Override
  public Collection<Component> getComponents() {
    Vector<Component> c = new Vector<Component>();
    c.addAll(components.keySet());
    return c;
  }

  @Override
  public Collection<CoupledNode> getNeighbors(Node node) {
    return getNeighbors((CoupledNode) node);
  }

  @Override
  public Collection<? extends Connection> getConnections(Node to, Node fro) {
    return getConnections((CoupledNode) to, (CoupledNode) fro);
  }

  @Override
  public Collection<Edge> getEdges(Node node) {
    return graph.getEdges((CoupledNode)node);
  }
   
  @Override
  public Pair<? extends Node, ? extends Node> getOrderedNodes(Connection link) {
    return getOrderedNodes((Coupling) link);
  }

  @Override
  public Collection<CoupledNode> getNodes(Connection link) {
    return getNodes((Coupling) link);
  }

  @Override
  public void couplingDataChanged(Coupling coupling, Object attribute) {
    fireCouplingChangeEvent(coupling, attribute);
  }

  /**
   * Tell the listeners about this change event
   * 
   * @param bus
   * @param kv
   */
  private void fireCouplingChangeEvent(Coupling coupling, Object attribute) {
    //Corridor corridor = getCorridor(coupling);
    for (ModelListener listener : getModelListeners()) {
      listener.attributeUpdated(coupling, /*corridor,*/ getFirstNode(coupling), getSecondNode(coupling), attribute, coupling.getAttribute(attribute));
    }
    for (CoupledModelListener listener : modelListeners) {
      listener.couplingDataChange(coupling);
    }
  }

  @Override
  public boolean isSolved() {
    return isSolved;
  }

  @Override
  public void setIsSolved(boolean solved) {
    isSolved = solved;
  }
  
  /**
   * construct the local clone
   * @return
   */
  protected abstract CoupledModelImpl constructClone();
  
  @Override
  public CoupledModel clone() {
    CoupledModelImpl newModel = constructClone();
    newModel.setIsSolved(isSolved());
    newModel.setSimulationQualityRank(getSimulationQualityRank());
    
    for (Model model : getModels()) {
      Model nm = model.clone();
      newModel.addModel(nm);      
    }
    
    Map<Asset,Asset> allAssets = new HashMap<Asset,Asset>();
    for (Model model : newModel.getModels()) {
      for (Asset asset : model.getAssets()) {
        allAssets.put(asset,asset);
      }
    }
    
    Map<CoupledComponent,CoupledComponent> oldToNew = new HashMap<CoupledComponent,CoupledComponent>();
    
    for (Component component : getComponents()) {
      CoupledComponent cc = (CoupledComponent)component;
      Asset newAsset = allAssets.get(cc.getAsset());
      CoupledComponent newComponent = new CoupledComponent(newAsset) ;
      newModel.addComponent(newComponent);
      oldToNew.put(cc,newComponent);
    }
    
    for (Coupling coupling : getCouplings()) {
      Coupling newCoupling = coupling.clone();
      CoupledNode node1 = newModel.getNode((CoupledComponent)oldToNew.get(getFirstNode(coupling).getPrimaryComponent()));
      CoupledNode node2 = newModel.getNode((CoupledComponent)oldToNew.get(getSecondNode(coupling).getPrimaryComponent()));
      
      newModel.addEdge(newCoupling, node1, node2);
    }
    
    newModel.setCouplingFactory(getCouplingFactory());
    
    return newModel;
  }
    
 /* @SuppressWarnings("unchecked")
  @Override
  public boolean isCriticalConnection(Node n1, Node n2) {
  	AbstractGraph<CoupledNode, Corridor> transmissionGraph = new GraphFactory<CoupledNode, Corridor>().createGraph();
		Collection<Coupling> edges = (Collection<Coupling>) getFlowConnections(n1,n2);
  	if (edges == null || edges.size() == 0) {
  		return false;
  	}
    Corridor corridor = new Corridor(n1,n2);  	
		
  	for (CoupledNode node : getNodes()) {
  		transmissionGraph.addNode(node);
  	}
  	for (Coupling edge : getCouplings()) {
  		CoupledNode node1 = getFirstNode(edge);
  		CoupledNode node2 = getSecondNode(edge);
  		Corridor newCorridor = new Corridor(node1,node2);      
  		if (transmissionGraph.getEdges(node1,node2) == null || transmissionGraph.getEdges(node1,node2).size() == 0) {
  			transmissionGraph.addEdge(newCorridor, node1, node2);
  		}
  	}  	
		return transmissionGraph.isCriticalEdge(corridor);
  }*/
  
  /**
   * Create a node for a bus
   * 
   * @param bus
   * @return
   */
  protected CoupledNode createNode(CoupledComponent asset) {
    CoupledNode node = new CoupledNodeImpl(asset);
    return node;
  }

  /**
   * Destroy a node
   * 
   * @param node
   * @return
   */
  protected void destroyNode(CoupledNode node) {    
    if (node.getComponents(CoupledComponent.class).size() > 1) {
      throw new RuntimeException("Error: Destroying an electric power node that still contains some components.");
    }
  }
  
  @Override
  public CoupledNode getNode(CoupledComponent component) {
    return components.get(component);
  }

  @Override
  public void removeModel(Model model) {
    models.remove(model);
    System.err.println("Function removeModel for coupled networks not properly thought out.  Should we also remove the couplings to that model?");
  }
  
  @Override
  @SuppressWarnings("unchecked")
  public <E extends Model> Collection<E> getModels(Class<E> cls) {
    Collection<E> ms = new ArrayList<E>();
    for (Model model : models) {
      if (cls.isAssignableFrom(model.getClass())) {
        ms.add((E)model);
      }
    }    
    return ms;
  }

  @Override
  public void addModel(Model model) {
    models.add(model);
  }
  
  @Override
  public Collection<Model> getModels() {
    return models;
  }
  
  public Collection<Connection> getConnections(CoupledNode to, CoupledNode fro) {
    ArrayList<Connection> connections = new ArrayList<Connection>();
    for (Edge edge : getEdges(to,fro)) {
      connections.addAll(edge.getConnections(Connection.class));
    }    
    return connections;
  }

  @Override
  public Node getNode(Component component) {
    if (component instanceof CoupledComponent) {
      return components.get(component);
    }
    
    for (Node node : getNodes()) {
      if (node.getPrimaryComponent().equals(component)) {
        return node;
      }
    }    
    return null;
  }
  
  @Override
  public Asset getAsset(Asset asset) {
    Asset a = super.getAsset(asset);
    while (a == null) {
      for (Model model : getModels()) {
        a = model.getAsset(asset);
      }
    }
    return a;
  }

  @Override
  public CouplingFactory getCouplingFactory() {
    return couplingFactory;
  }

  /**
   * Sets the coupling factory
   * @param f
   */
  protected void setCouplingFactory(CouplingFactory f) {
    couplingFactory = f;
  }
  
}
