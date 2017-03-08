package gov.lanl.micot.infrastructure.transportation.road.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import gov.lanl.micot.infrastructure.model.Component;
import gov.lanl.micot.infrastructure.model.Connection;
//import gov.lanl.micot.infrastructure.model.ConsumptionViolation;
import gov.lanl.micot.infrastructure.model.Edge;
import gov.lanl.micot.infrastructure.model.EdgeImpl;
import gov.lanl.micot.infrastructure.model.FlowConnection;
import gov.lanl.micot.infrastructure.model.ModelChangeException;
import gov.lanl.micot.infrastructure.model.ModelImpl;
import gov.lanl.micot.infrastructure.model.ModelListener;
import gov.lanl.micot.infrastructure.model.Node;
//import gov.lanl.micot.infrastructure.model.OverloadViolation;
//import gov.lanl.micot.infrastructure.model.ProductionViolation;
//import gov.lanl.micot.infrastructure.model.Violation;
import gov.lanl.micot.util.collection.Pair;
import gov.lanl.micot.util.graph.AbstractGraph;
import gov.lanl.micot.util.graph.GraphFactory;

/**
 * Abstraction of models. The contract with models is that the control the
 * editing and modification of components of the model. The component itself is
 * used as a key to find the particular state of that component within the model
 * 
 * @author Russell Bent
 */
public abstract class RoadModelImpl extends ModelImpl implements RoadModel, IntersectionChangeListener, RoadChangeListener, DestinationChangeListener, OriginChangeListener  {

  protected static final long                                                            serialVersionUID      = 0;

  protected AbstractGraph<RoadNode, Edge>                                                graph                 = null;

  private Set<RoadModelListener>                                                         modelListeners        = null;

  private Map<Road, Edge>                                                                  roads                 = null;

  private Map<Intersection, RoadNode>                                                intersections         = null;
  
  private Set<RoadNode>                                                              nodes                 = null;

  private Map<Destination, RoadNode>                                                 destinations          = null;
  
  private Map<Origin, RoadNode>                                                      origins               = null;
 
  private boolean                                                                        isSolved              = false;
  
  //private Map<Class<? extends Violation>, Vector<Violation>>                             violations            = null;

  //private Map<Class<? extends Violation>, Map<RoadNode, Collection<Violation>>>          violationsByNode      = null;
  
  private RoadFactory                                                                roadFactory                = null;

  private DestinationFactory                                                                destinationFactory  = null;

  private OriginFactory                                                              originFactory               = null;
 
  private IntersectionFactory                                                        intersectionFactory         = null;
  
  /**
   * Constructor
   * 
   * @param graph
   */
  public RoadModelImpl(AbstractGraph<RoadNode, Edge> graph) {
    super();
    init(graph);
  }

  /**
   * Constructor
   */
  public RoadModelImpl() {
    super();
    GraphFactory<RoadNode, Edge> factory = new GraphFactory<RoadNode, Edge>();
    init(factory.createGraph());
  }

  /**
   * Initialization routine
   */
  protected void init(AbstractGraph<RoadNode, Edge> graph) {
    this.graph = graph;
    modelListeners = new HashSet<RoadModelListener>();
    roads = new LinkedHashMap<Road, Edge>();
    destinations = new LinkedHashMap<Destination, RoadNode>();
    origins = new LinkedHashMap<Origin, RoadNode>();
    intersections = new LinkedHashMap<Intersection, RoadNode>();
    nodes = new HashSet<RoadNode>();
    isSolved = true;
    //violations = new HashMap<Class<? extends Violation>, Vector<Violation>>();
    //violationsByNode = new HashMap<Class<? extends Violation>, Map<RoadNode, Collection<Violation>>>();
  }

  @Override
  public Pair<RoadNode, RoadNode> getOrderedNodes(Road road) {
    Edge edge = getEdge(road);
    return getOrderedNodes(edge);
  }

  @Override
  public Pair<RoadNode, RoadNode> getOrderedNodes(Edge edge) {
    Collection<RoadNode> nodes = graph.getIncidentVertices(edge);
    if (nodes.size() == 0) {
      return null;
    }
    Iterator<RoadNode> it = nodes.iterator();
    return new Pair<RoadNode, RoadNode>(it.next(), it.next());
  }

  
  @Override
  public Collection<RoadNode> getNodes(Road road) {
    Edge edge = getEdge(road);
    return getNodes(edge);
  }

  @Override
  public Collection<RoadNode> getNodes(Edge edge) {
    return graph.getIncidentVertices(edge);
  }
  
  @Override
  public RoadNode getFirstNode(Connection edge) {
    if (getNodes(edge).size() == 0) {
      System.err.println(edge);
    }
    return getNodes(edge).iterator().next();
  }

  @Override
  public RoadNode getSecondNode(Connection edge) {
    Iterator<RoadNode> it = getNodes(edge).iterator();
    it.next();
    return it.next();
  }

  //@Override
  public Collection<Edge> getEdges(RoadNode to, RoadNode fro) {
    return graph.getEdges(to, fro);
  }
  
  @Override
  public Collection<RoadNode> getNodes() {
    return graph.getNodes();
  }

  @Override
  public void addRoad(Road road, RoadNode fromNode, RoadNode toNode) {
    Edge edge = null;
    if (getEdges(fromNode, toNode).size() <= 0) {
      edge = new EdgeImpl(road);
      graph.addEdge(edge,fromNode,toNode);
    }
    else {
      edge = getEdges(fromNode, toNode).iterator().next();
      edge.addConnection(road);
    }
    roads.put(road, edge);        
    road.addRoadChangeListener(this);
    fireRoadAddedEvent(road);    
    addAsset(road);
  }

  /**
   * Fire a link added event
   * 
   * @param link
   */
  private void fireRoadAddedEvent(Road road) {
    for (RoadModelListener listener : modelListeners) {
      listener.roadAdded(road);
    }
  }

  @Override
  public void removeRoad(Road road) {
  	fireRoadRemovedEvent(road);

    Edge edge = getEdge(road);    
    if (road.equals(edge.getPrimaryConnection())) {
      if (edge.getConnections(Connection.class).size() > 1) {
        throw new ModelChangeException("Attempting to remove the prinary connection before all other connections are removed");
      }
    }
    
    edge.removeConnection(road);
    if (edge.getConnections(Connection.class).size() == 0) {
      if (!graph.removeEdge(edge)) {
        throw new ModelChangeException("Unable to find link " + road + " to remove");
      }      
    }

  	roads.remove(road);
    road.removeRoadChangeListener(this);  	
    removeAsset(road);
  }
  
  /**
   * Notify the listeners that the link has been removed
   * 
   * @param link
   */
  private void fireRoadRemovedEvent(Road link) {
    for (RoadModelListener listener : modelListeners) {
      listener.roadRemoved(link);
    }
  }

  @Override
  public Collection<RoadNode> getNeighbors(RoadNode node) {
    return graph.getNeighbors(node);
  }

  @Override
  public Collection<Road> getRoads(RoadNode node) {
    Vector<Road> lines = new Vector<Road>();
    Collection<RoadNode> neighbors = graph.getNeighbors(node);
    for (RoadNode node2 : neighbors) {
      Collection<Road> links = getRoads(node, node2);
      for (Road l : links) {
        lines.add(l);
      }
    }
    return lines;
  }

  @Override
  public Collection<Road> getRoads() {
    return roads.keySet();
  }

  @Override
  public void addModelListener(RoadModelListener listener) {
    modelListeners.add(listener);
  }

  @Override
  public void removeModelListener(RoadModelListener listener) {
    modelListeners.remove(listener);
  }

  @Override
  public void addIntersection(Intersection intersection) {
    RoadNode node = createNode(intersection);
    intersections.put(intersection, node);
    graph.addNode(node);
    nodes.add(node);
    fireIntersectionAddEvent(intersection);
    intersection.addIntersectionChangeListener(this);
    addAsset(intersection);
  }

  @Override
  public void addDestination(Destination destination, Intersection intersection) {
    RoadNode node = getNode(intersection);
    destinations.put(destination, node);
    destination.addDestinationChangeListener(this);
    node.addComponent(destination);
    fireDestinationAddEvent(destination);
    addAsset(destination);
  }

  @Override
  public void addOrigin(Origin origin, Intersection intersection) {
    RoadNode node = getNode(intersection);
    origins.put(origin, node);
    origin.addOriginDataListener(this);
    node.addComponent(origin);
    fireOriginAddEvent(origin);
    addAsset(origin);
  }

  
  /**
   * Notifies the listener that a bus has been added to the system
   * 
   * @param shunt
   * @param state
   */
  private void fireIntersectionAddEvent(Intersection intersection) {
    for (RoadModelListener listener : modelListeners) {
      listener.intersectionAdded(intersection);
    }
  }

  /**
   * Notifies the listener that a bus has been added to the system
   * 
   * @param shunt
   * @param state
   */
  private void fireOriginAddEvent(Origin origin) {
    for (RoadModelListener listener : modelListeners) {
      listener.originAdded(origin);
    }
  }
  
  /**
   * Notifies the listener that a bus has been added to the system
   * 
   * @param shunt
   * @param state
   */
  private void fireDestinationAddEvent(Destination destination) {
    for (RoadModelListener listener : modelListeners) {
      listener.destinationAdded(destination);
    }
  }
  
  @Override
  public void removeIntersection(Intersection intersection) {
    RoadNode node = getNode(intersection);
    intersections.remove(intersection);
    nodes.remove(node);
    graph.removeNode(node);
    destroyNode(node);
    fireIntersectionRemoveEvent(intersection);
    intersection.removeIntersectionChangeListener(this);
    removeAsset(intersection);
  }

  @Override
  public void removeDestination(Destination destination) {
    destinations.remove(destination);
    destination.removeDestinationChangeListener(this);
    Node node = getNode(destination);
    node.removeComponent(destination);
    fireDestinationRemoveEvent(destination);
    removeAsset(destination);
  }
  
  @Override
  public void removeOrigin(Origin origin) {
    origins.remove(origin);
    origin.removeOriginDataListener(this);
    Node node = getNode(origin);
    node.removeComponent(origin);
    fireOriginRemoveEvent(origin);
    removeAsset(origin);
  }
  
  /**
   * Notifies the listener that a bus has been removed
   * 
   * @param shunt
   * @param state
   */
  private void fireIntersectionRemoveEvent(Intersection intersection) {
    for (RoadModelListener listener : modelListeners) {
      listener.intersectionRemoved(intersection);
    }
  }

  /**
   * Notifies the listener that a bus has been removed
   * 
   * @param shunt
   * @param state
   */
  private void fireDestinationRemoveEvent(Destination destination) {
    for (RoadModelListener listener : modelListeners) {
      listener.destinationRemoved(destination);
    }
  }

  
  /**
   * Notifies the listener that a bus has been removed
   * 
   * @param shunt
   * @param state
   */
  private void fireOriginRemoveEvent(Origin origin) {
    for (RoadModelListener listener : modelListeners) {
      listener.originRemoved(origin);
    }
  }

  @Override
  public Collection<? extends Intersection> getIntersections() {
    return intersections.keySet();
  }

  @Override
  public Collection<? extends Destination> getDestinations() {
    return destinations.keySet();
  }

  @Override
  public Collection<? extends Origin> getOrigins() {
    return origins.keySet();
  }
  
  @Override
  public Collection<Component> getComponents() {
    Vector<Component> components = new Vector<Component>();
    components.addAll(getIntersections());
    components.addAll(getOrigins());
    components.addAll(getDestinations());
    return components;
  }

  /**
   * Gets a load from a node
   * 
   * @param id
   * @return
   */
  @Override
  public Collection<? extends Destination> getDestinations(RoadNode node) {
    return (Collection<Destination>) node.getComponents(Destination.class);
  }

  /**
   * Gets a load from a node
   * 
   * @param id
   * @return
   */
  @Override
  public Collection<? extends Origin> getOrigins(RoadNode node) {
    return (Collection<Origin>) node.getComponents(Origin.class);
  }

  @Override
  public Collection<RoadNode> getNeighbors(Node node) {
    return getNeighbors((RoadNode) node);
  }

  @Override
  public Collection<? extends Connection> getConnections(Node to, Node fro) {
    return getConnections((RoadNode) to, (RoadNode) fro);
  }

  @Override
  public Collection<RoadNode> getNodes(Connection link) {
    return getNodes((Road) link);
  }

  @Override
  public void intersectionDataChanged(Intersection data, Object attribute) {
    fireIntersectionChangeEvent(data, attribute);
  }

  @Override
  public void destinationDataChanged(Destination data, Object attribute) {
    fireDestinationChangeEvent(data, attribute);
  }

  @Override
  public void originDataChanged(Origin data, Object attribute) {
    fireOriginChangeEvent(data, attribute);
  }
  
  /**
   * Tell the listeners about this change event
   * 
   * @param bus
   * @param kv
   */
  private void fireIntersectionChangeEvent(Intersection intersection, Object attribute) {
    for (ModelListener listener : getModelListeners()) {
      listener.attributeUpdated(intersection, getNode(intersection), attribute, intersection.getAttribute(attribute));
    }    
    for (RoadModelListener listener : modelListeners) {
      listener.intersectionDataChange(intersection);
    }
  }
  
  /**
   * Tell the listeners about this change event
   * 
   * @param bus
   * @param kv
   */
  private void fireDestinationChangeEvent(Destination destination, Object attribute) {
    for (ModelListener listener : getModelListeners()) {
      listener.attributeUpdated(destination, getNode(destination), attribute, destination.getAttribute(attribute));
    }    
    for (RoadModelListener listener : modelListeners) {
      listener.destinationDataChange(destination);
    }
  }

  /**
   * Tell the listeners about this change event
   * 
   * @param bus
   * @param kv
   */
  private void fireOriginChangeEvent(Origin origin, Object attribute) {
    for (ModelListener listener : getModelListeners()) {
      listener.attributeUpdated(origin, getNode(origin), attribute, origin.getAttribute(attribute));
    }    
    for (RoadModelListener listener : modelListeners) {
      listener.originDataChange(origin);
    }
  }

  
  @Override
  public void roadDataChanged(Road road, Object attribute) {
    fireRoadChangeEvent(road, attribute);
  }

  /**
   * Tell the listeners about this change event
   * 
   * @param bus
   * @param kv
   */
  private void fireRoadChangeEvent(Road road, Object attribute) {
    for (ModelListener listener : getModelListeners()) {
      listener.attributeUpdated(road, /*getCorridor(road),*/ getFirstNode(road), getSecondNode(road), attribute, road.getAttribute(attribute));
    }    
    for (RoadModelListener listener : modelListeners) {
      listener.roadDataChange(road);
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

  /*@SuppressWarnings("unchecked")
  @Override
  public <E extends Violation> Vector<E> getViolations(Class<E> cls) {
    if (violations.get(cls) == null) {
      return new Vector<E>();
    }
    return (Vector<E>) violations.get(cls);
  }*/

/*  @SuppressWarnings("unchecked")
  @Override
  public <E extends Violation> Collection<E> getViolations(Class<E> cls, RoadNode node) {
    if (violationsByNode.get(cls) == null || violationsByNode.get(cls).get(node) == null) {
      return new Vector<E>();
    }
    return (Collection<E>) violationsByNode.get(cls).get(node);
  }

  @Override
  public int getNumberOfViolations() {
    int size = 0;
    for (Class<? extends Violation> key : violations.keySet()) {
      size += violations.get(key).size();
    }
    return size;
  }
  
  @Override
  public <E extends Violation> void addViolation(E violation) {
    HashSet<RoadNode> nodes = new HashSet<RoadNode>();
    for (Connection edge : violation.getConnectionsInViolation()) {
      nodes.addAll((Collection<? extends RoadNode>) getNodes(edge));
    }
    for (Component component : violation.getComponentsInViolation()) {
      nodes.add(getNode(component));
    }

    if (violations.get(violation.getClass()) == null) {
      violations.put(violation.getClass(), new Vector<Violation>());
    }
    violations.get(violation.getClass()).add(violation);

    for (RoadNode node : nodes) {
      if (violationsByNode.get(violation.getClass()) == null) {
        violationsByNode.put(violation.getClass(), new HashMap<RoadNode, Collection<Violation>>());
      }
      if (violationsByNode.get(violation.getClass()).get(node) == null) {
        violationsByNode.get(violation.getClass()).put(node, new Vector<Violation>());
      }
      violationsByNode.get(violation.getClass()).get(node).add(violation);
    }
  }*/
  
  /**
   * construct the local clone
   * @return
   */
  protected abstract RoadModelImpl constructClone();

  @Override
  public RoadModel createNewModel(RoadModel oldModel) {
    return oldModel.clone();
  }
  
  @Override
  public RoadModel clone() {
    RoadModelImpl newModel = constructClone();
    newModel.setIsSolved(newModel.isSolved());
    
    Map<Intersection, Intersection> oldToNew = new HashMap<Intersection,Intersection>();    
    for (Intersection intersection : getIntersections()) {
      Intersection newIntersection = intersection.clone();
      newModel.addIntersection(newIntersection);
      oldToNew.put(intersection, newIntersection);
    }

    for (Origin origin : getOrigins()) {
      Origin newOrigin = origin.clone();
      Intersection intersection = oldToNew.get(getNode(origin).getIntersection());
      newModel.addOrigin(newOrigin, intersection);
    }
    
    for (Destination destination : getDestinations()) {
      Destination newDestination = destination.clone();
      Intersection intersection = oldToNew.get(getNode(destination).getIntersection());
      newModel.addDestination(newDestination, intersection);
    }
    
    for (Road road : getRoads()) {
      Road newRoad = road.clone();
      newModel.addRoad(newRoad, newModel.getNode(getFirstNode(road).getIntersection()), newModel.getNode(getSecondNode(road).getIntersection()));
    }
    
    // create line violations
    /*for (Road link : newModel.getRoads()) { 
      double flow = Math.abs(link.getFlow().doubleValue());
      if (flow > link.getCapacity().doubleValue()) {
        newModel.addViolation(new OverloadViolation(link,  link.getFlow().doubleValue(),
        link.getCapacity().doubleValue()));
      }      
    }

    // create load violations
    for (Destination load : newModel.getDestinations()) {
      double consumption = load.getActualConsumption().doubleValue();
      if (consumption < load.getDesiredConsumption().doubleValue()) {
        newModel.addViolation(new ConsumptionViolation(load, load.getDesiredConsumption().doubleValue(), consumption));
      }       
    }
        
    // well overloads
    for (Producer producer : newModel.getComponents(Producer.class)) {
      double production = producer.getActualProduction().doubleValue();
      if (production > producer.getMaximumProduction().doubleValue() || production < producer.getMinimumProduction().doubleValue()) {
        newModel.addViolation(new ProductionViolation(producer, production, producer.getMinimumProduction().doubleValue(), producer.getMaximumProduction().doubleValue()));
      }   
    }*/

    newModel.setDestinationFactory(getDestinationFactory());
    newModel.setOriginFactory(getOriginFactory());
    newModel.setRoadFactory(getRoadFactory());
    newModel.setIntersectionFactory(getIntersectionFactory());
    
    return newModel;    
  }
      
  //@Override
  //public void clearViolations() {
  	//violations = new HashMap<Class<? extends Violation>, Vector<Violation>>();
    //violationsByNode = new HashMap<Class<? extends Violation>, Map<RoadNode, Collection<Violation>>>();
  //}
  
  @Override
  public Pair<? extends Node, ? extends Node> getOrderedNodes(Connection link) {
    return getOrderedNodes((Road) link);
  }

  @Override
  public Collection<Road> getConnections(RoadNode node1, RoadNode node2) {
    Collection<Edge> edges = getEdges(node1, node2);
    ArrayList<Road> roads = new ArrayList<Road>();
    for (Edge edge : edges) {
      roads.addAll(edge.getConnections(Road.class));
    }
    return roads;    
  }

  @Override
  public Collection<? extends FlowConnection> getFlowConnections() {
    Vector<FlowConnection> links = new Vector<FlowConnection>();
    links.addAll(getRoads());
    return links;
  }
  
  @Override
  public Collection<? extends FlowConnection> getFlowConnections(Node node) {
    return getRoads((RoadNode)node);
  }

  @Override
  public Collection<? extends FlowConnection> getFlowConnections(Node to, Node fro) {
    return getRoads((RoadNode)to,(RoadNode)fro);
  }
  
  @Override
  public Collection<? extends Connection> getConnections() {
    return getRoads();
  }

  /*@Override
  public boolean isCriticalConnection(Node n1, Node n2) {
    Road pipe = getRoads((RoadNode)n1,(RoadNode)n2).iterator().next();
    Edge edge = getEdge(pipe);    
    return graph.isCriticalEdge(edge);
  }*/

  @Override
  public void addConnection(Connection edge, Node fromNode, Node toNode) {
    addRoad((Road)edge, (RoadNode)fromNode, (RoadNode)toNode);
  }
  
  @Override
  public void removeConnection(Connection edge) {
    removeRoad((Road)edge);
  }
  
  @Override
  public Collection<? extends Connection> getConnections(Node node) {
    Collection<Edge> edges = getEdges(node);
    ArrayList<Connection> connections = new ArrayList<Connection>();
    for (Edge edge : edges) {
      connections.addAll(edge.getConnections(Connection.class));
    }    
    return connections;
  }
  
  /**
   * Create a node for a bus
   * 
   * @param bus
   * @return
   */
  protected RoadNode createNode(Intersection intersection) {
    RoadNode node = new RoadNodeImpl(intersection);
    return node;
  }

  /**
   * Destroy a node
   * 
   * @param node
   * @return
   */
  protected void destroyNode(RoadNode node) {    
    if (node.getComponents(Component.class).size() > 1) {
      throw new RuntimeException("Error: Destroying a road node that still contains some components.");
    }
  }
  
  @Override
  public RoadNode getNode(Component component) {
    if (component instanceof Origin) {
      return origins.get(component);
    }
    if (component instanceof Intersection) {
      return intersections.get(component);      
    }
    if (component instanceof Destination) {
      return destinations.get(component);
    }
    return null;
  }

  @Override
  public Collection<? extends Edge> getEdges(Node to, Node fro) {
    return graph.getEdges((RoadNode)to,(RoadNode)fro);
  }

  @Override
  public Collection<Edge> getEdges(Node node) {
    return graph.getEdges((RoadNode)node);
  }

  @Override
  public Edge getEdge(Connection connection) {
    if (connection instanceof Road) {
      return roads.get(connection);
    }
    return null;
  }

  @Override
  public Collection<Edge> getEdges() {
    return graph.getEdges();
  }

  @Override
  public Collection<Road> getRoads(RoadNode to, RoadNode fro) {
    Collection<Edge> edges = getEdges(to,fro);
    ArrayList<Road> roads = new ArrayList<Road>();
    for (Edge edge : edges) {
      roads.addAll(edge.getConnections(Road.class));
    }    
    return roads;
  }

  @Override
  public RoadNode getFirstNode(Edge edge) {
    return getNodes(edge).iterator().next();
  }

  @Override
  public RoadNode getSecondNode(Edge edge) {
    Iterator<RoadNode> it = getNodes(edge).iterator();
    it.next();
    return it.next();
  }

  /**
   * Set the road factory
   * @param c
   */
  protected void setRoadFactory(RoadFactory roadFactory) {
    this.roadFactory = roadFactory;
  }
  
  /**
   * Set the intersection factory
   */
  protected void setIntersectionFactory(IntersectionFactory intersectionFactory) {
    this.intersectionFactory = intersectionFactory;    
  }

  /**
   * Set the origin factory
   */
  protected void setOriginFactory(OriginFactory originFactory) {
    this.originFactory = originFactory;    
  }

  /**
   * Set the destination factory
   */
  protected void setDestinationFactory(DestinationFactory destinationFactory) {
    this.destinationFactory = destinationFactory;    
  }

  @Override
  public RoadFactory getRoadFactory() {
    return roadFactory;
  }

  @Override
  public IntersectionFactory getIntersectionFactory() {
    return intersectionFactory;
  }

  @Override
  public OriginFactory getOriginFactory() {
    return originFactory;
  }

  @Override
  public DestinationFactory getDestinationFactory() {
    return destinationFactory;
  }
}
