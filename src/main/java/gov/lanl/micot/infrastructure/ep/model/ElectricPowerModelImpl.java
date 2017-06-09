package gov.lanl.micot.infrastructure.ep.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.model.Component;
import gov.lanl.micot.infrastructure.model.Connection;
//import gov.lanl.micot.infrastructure.model.Corridor;
import gov.lanl.micot.infrastructure.model.Edge;
import gov.lanl.micot.infrastructure.model.EdgeImpl;
import gov.lanl.micot.infrastructure.model.ModelChangeException;
import gov.lanl.micot.infrastructure.model.ModelImpl;
import gov.lanl.micot.infrastructure.model.ModelListener;
import gov.lanl.micot.infrastructure.model.Node;
import gov.lanl.micot.util.collection.Pair;
import gov.lanl.micot.util.graph.AbstractGraph;
import gov.lanl.micot.util.graph.GraphFactory;

/**
 * Abstraction of power models. The contract with models is that the control the
 * editing and modification of components of the model. The component itself is
 * used as a key to find the particular state of that component within the model
 * 
 * @author Russell Bent
 */
public abstract class ElectricPowerModelImpl extends ModelImpl implements ElectricPowerModel, BusChangeListener, IntertieChangeListener, LineChangeListener, TransformerChangeListener, DCLineChangeListener, ShuntCapacitorChangeListener, ShuntCapacitorSwitchChangeListener, GeneratorChangeListener, LoadChangeListener, BatteryChangeListener {

  protected static final long                                                            serialVersionUID   = 0;

  protected AbstractGraph<ElectricPowerNode, Edge>                                       graph              = null;

  private Set<ElectricPowerModelListener>                                                modelListeners     = null;

  private Map<Line, Edge>                                                                lines              = null;

  private Map<Transformer, Edge>                                                         transformers       = null;

  private Map<DCLine, Edge>                                                              dcLines       = null;
  
  private Map<Intertie, Edge>                                                            interties          = null;

  private Map<ShuntCapacitor, ElectricPowerNode>                                         shunts             = null;

  private Map<ShuntCapacitorSwitch, ElectricPowerNode>                                   switchedShunts     = null;

  private Map<Bus, ElectricPowerNode>                                                    buses              = null;

  private Map<Generator, ElectricPowerNode>                                              generators         = null;

  private Map<Battery, ElectricPowerNode>                                                batteries         = null;
  
  private Map<Load, ElectricPowerNode>                                                   loads              = null;

  private Set<ControlArea>                                                               controlAreas       = null;

  private Set<Zone>                                                                      zones              = null;
  
  private Set<ElectricPowerNode>                                                         nodes              = null;

  private boolean                                                                        isSolved           = false;
  
  private double                                                                         mvaBase            = -1.0;
  
  private Set<Bus>                                                                       slackBuses         = null;
  
  private Map<Asset,ControlArea>                                                         assetAreas         = null;
  
  private Map<Asset, Zone>                                                               assetZones         = null;
  
  private Map<Intertie, ControlArea>                                                     meteredAreas       = null;
  
  private Map<Intertie, ControlArea>                                                     nonMeteredAreas    = null;
  
  private Map<ControlArea, Bus>                                                          areaSlackBuses     = null;
  
  /**
   * This is a map that says which asset controls a bus
   */
  private Map<Asset, Bus>                                                                controlBuses       = null;
  
  private BatteryFactory batteryFactory = null;
  
  private BusFactory busFactory = null;
  
  private ControlAreaFactory areaFactory = null;
  
  private GeneratorFactory generatorFactory = null;
  
  private IntertieFactory intertieFactory = null;
  
  private LineFactory lineFactory = null;
  
  private LoadFactory loadFactory = null;
  
  private ShuntCapacitorFactory capacitorFactory = null;
  
  private ShuntCapacitorSwitchFactory capacitorSwitchFactory = null;
  
  private TransformerFactory transformerFactory = null;
  
  private ZoneFactory zoneFactory = null;
  
  private DCLineFactory dcLineFactory = null;
        
  /**
   * Constructor
   * 
   * @param graph
   */
  public ElectricPowerModelImpl(AbstractGraph<ElectricPowerNode, Edge> graph) {
    super();
    init(graph);
  }

  /**
   * Constructor for default models
   */
  protected ElectricPowerModelImpl() {
    super();
    GraphFactory<ElectricPowerNode, Edge> factory = new GraphFactory<ElectricPowerNode, Edge>();
    init(factory.createGraph());
  }

  /**
   * Initialization routine
   */
  protected void init(AbstractGraph<ElectricPowerNode, Edge> graph) {
    this.graph = graph;
    modelListeners = new HashSet<ElectricPowerModelListener>();
    lines = new TreeMap<Line, Edge>();
    transformers = new LinkedHashMap<Transformer, Edge>();
    dcLines = new LinkedHashMap<DCLine, Edge>();
    interties = new TreeMap<Intertie, Edge>();
    switchedShunts = new TreeMap<ShuntCapacitorSwitch, ElectricPowerNode>();
    shunts = new TreeMap<ShuntCapacitor, ElectricPowerNode>();
    buses = new TreeMap<Bus, ElectricPowerNode>();
    generators = new TreeMap<Generator, ElectricPowerNode>();
    batteries = new TreeMap<Battery, ElectricPowerNode>();
    loads = new TreeMap<Load, ElectricPowerNode>();
    controlAreas = new TreeSet<ControlArea>();
    zones = new TreeSet<Zone>();
    nodes = new TreeSet<ElectricPowerNode>();
    isSolved = true;
    mvaBase = DEFAULT_MVA_BASE;
    slackBuses = new HashSet<Bus>();
    slackBuses = new HashSet<Bus>();
    assetAreas = new LinkedHashMap<Asset,ControlArea>();
    assetZones = new LinkedHashMap<Asset,Zone>();
    meteredAreas = new LinkedHashMap<Intertie, ControlArea>();
    nonMeteredAreas = new LinkedHashMap<Intertie, ControlArea>();
    areaSlackBuses = new LinkedHashMap<ControlArea, Bus>();
    controlBuses = new LinkedHashMap<Asset, Bus>();
  }

  @Override
  public Edge getEdge(Connection connection) {
    if (connection instanceof Line) {
      return lines.get(connection);
    }
    if (connection instanceof Transformer) {
      return transformers.get(connection);
    }
    if (connection instanceof Intertie) {
      return interties.get(connection);
    }
    if (connection instanceof DCLine) {
      return dcLines.get(connection);
    }
    return null;
  }
  
  @Override
  public Pair<ElectricPowerNode, ElectricPowerNode> getOrderedNodes(ElectricPowerConnection l) {
    Edge edge = getEdge(l);
    return getOrderedNodes(edge);    
  }

  @Override
  public Pair<ElectricPowerNode, ElectricPowerNode> getOrderedNodes(Edge l) {
    Collection<ElectricPowerNode> nodes = graph.getIncidentVertices(l);
    Iterator<ElectricPowerNode> it = nodes.iterator();
    return new Pair<ElectricPowerNode, ElectricPowerNode>(it.next(), it.next());
  }
  
  @Override
  public Collection<ElectricPowerNode> getNodes(ElectricPowerConnection link) {
    return graph.getIncidentVertices(getEdge(link));
  }

  @Override
  public Collection<ElectricPowerNode> getNodes(Edge edge) {
    return graph.getIncidentVertices(edge);
  }
  
  @Override
  public ElectricPowerNode getFirstNode(Connection edge) {
    return getNodes(edge).iterator().next();
  }

  @Override
  public ElectricPowerNode getSecondNode(Connection edge) {
    Iterator<ElectricPowerNode> it = getNodes(edge).iterator();
    it.next();
    return it.next();
  }

  @Override
  public ElectricPowerNode getFirstNode(Edge edge) {
    return getNodes(edge).iterator().next();
  }

  @Override
  public ElectricPowerNode getSecondNode(Edge edge) {
    Iterator<ElectricPowerNode> it = getNodes(edge).iterator();
    it.next();
    return it.next();
  }

  @Override
  public Collection<ElectricPowerConnection> getConnections(ElectricPowerNode to, ElectricPowerNode fro) {
    ArrayList<ElectricPowerConnection> connections = new ArrayList<ElectricPowerConnection>();
    for (Edge edge : getEdges(to,fro)) {
      connections.addAll(edge.getConnections(ElectricPowerConnection.class));
    }    
    return connections;
  }
  
  @Override
  public Collection<Edge> getEdges(Node to, Node fro) {
    return graph.getEdges((ElectricPowerNode)to, (ElectricPowerNode)fro);
  }
  
  @Override
  public Collection<ElectricPowerFlowConnection> getFlowEdges(ElectricPowerNode to, ElectricPowerNode fro) {
    Collection<ElectricPowerConnection> temp = getConnections(to, fro);
    Vector<ElectricPowerFlowConnection> lines = new Vector<ElectricPowerFlowConnection>();
    for (ElectricPowerConnection link : temp) {
      if (link instanceof ElectricPowerFlowConnection) {
        lines.add((ElectricPowerFlowConnection) link);
      }
    }
    return lines;
  }
  
  @Override
  public Collection<Intertie> getInterties(ElectricPowerNode to, ElectricPowerNode fro) {
    Collection<ElectricPowerConnection> temp = getConnections(to, fro);
    Vector<Intertie> lines = new Vector<Intertie>();
    for (ElectricPowerConnection link : temp) {
      if (link instanceof Intertie) {
        lines.add((Intertie) link);
      }
    }
    return lines;
  }

  /**
   * Return all the lines
   * 
   * @param to
   * @param fro
   * @return
   */
  public Collection<? extends Line> getLines(ElectricPowerNode to, ElectricPowerNode fro) {
    Collection<ElectricPowerConnection> temp = getConnections(to, fro);
    Vector<Line> lines = new Vector<Line>();
    for (ElectricPowerConnection link : temp) {
      if (link instanceof Line) {
        lines.add((Line) link);
      }
    }
    return lines;
  }

  /**
   * Return all the transformers
   * 
   * @param to
   * @param fro
   * @return
   */
  public Collection<? extends Transformer> getTransformers(ElectricPowerNode to, ElectricPowerNode fro) {
    Collection<ElectricPowerConnection> temp = getConnections(to, fro);
    Vector<Transformer> lines = new Vector<Transformer>();
    for (ElectricPowerConnection link : temp) {
      if (link instanceof Transformer) {
        lines.add((Transformer) link);
      }
    }
    return lines;
  }

  /**
   * Return all the DC lines
   * 
   * @param to
   * @param fro
   * @return
   */
  public Collection<? extends DCLine> getDCLines(ElectricPowerNode to, ElectricPowerNode fro) {
    Collection<ElectricPowerConnection> temp = getConnections(to, fro);
    Vector<DCLine> lines = new Vector<DCLine>();
    for (ElectricPowerConnection link : temp) {
      if (link instanceof DCLine) {
        lines.add((DCLine) link);
      }
    }
    return lines;
  }  
  
  @Override
  public Collection<ElectricPowerNode> getNodes() {
    return graph.getNodes();
  }

  @Override
  public void addConnection(Connection link, Node fromNode, Node toNode) {
    addEdge((ElectricPowerConnection)link, (ElectricPowerNode)fromNode, (ElectricPowerNode)toNode);
  }
  
  @Override
  public void addEdge(ElectricPowerConnection link, ElectricPowerNode fromNode, ElectricPowerNode toNode) {
    if (link instanceof Line) {
      addLine((Line) link, fromNode, toNode);
    }
    else if (link instanceof Transformer) {
      addTransformer((Transformer) link, fromNode, toNode);
    }
    else if (link instanceof Intertie) {
      addIntertie((Intertie) link, fromNode, toNode);
    }
    else if (link instanceof DCLine) {
      addDCLine((DCLine) link, fromNode, toNode);
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
  private void addLine(Line line, ElectricPowerNode fromNode, ElectricPowerNode toNode) {
    Edge edge = null;
    if (getEdges(fromNode, toNode).size() <= 0) {
      edge = new EdgeImpl(line);
      graph.addEdge(edge,fromNode,toNode);
    }
    else {
      edge = getEdges(fromNode, toNode).iterator().next();
      edge.addConnection(line);
    }
    lines.put(line, edge);        
    line.addLineChangeListener(this);
    addAsset(line);
  }

  /**
   * Add a transformer to the model
   * 
   * @param line
   * @param data
   * @param node1
   * @param node2
   */
  private void addTransformer(Transformer transformer, ElectricPowerNode fromNode, ElectricPowerNode toNode) {
    Edge edge = null;
    if (getEdges(fromNode, toNode).size() <= 0) {
      edge = new EdgeImpl(transformer);
      graph.addEdge(edge,fromNode,toNode);
    }
    else {
      edge = getEdges(fromNode, toNode).iterator().next();
      edge.addConnection(transformer);
    }
    transformers.put(transformer, edge);    
    transformer.addTransformerChangeListener(this);
    addAsset(transformer);
  }

  
  /**
   * Add a DC line to the model
   * 
   * @param line
   * @param data
   * @param node1
   * @param node2
   */
  private void addDCLine(DCLine dcLine, ElectricPowerNode fromNode, ElectricPowerNode toNode) {
    Edge edge = null;
    if (getEdges(fromNode, toNode).size() <= 0) {
      edge = new EdgeImpl(dcLine);
      graph.addEdge(edge,fromNode,toNode);
    }
    else {
      edge = getEdges(fromNode, toNode).iterator().next();
      edge.addConnection(dcLine);
    }
    dcLines.put(dcLine, edge);    
    dcLine.addDCLineChangeListener(this);
    addAsset(dcLine);
  }

  
  
  
  /**
   * Add an intertie to the model
   * 
   * @param line
   * @param data
   * @param node1
   * @param node2
   */
  private void addIntertie(Intertie intertie, ElectricPowerNode fromNode, ElectricPowerNode toNode) {
    Edge edge = null;
    if (getEdges(fromNode, toNode).size() <= 0) {
      edge = new EdgeImpl(intertie);
      graph.addEdge(edge,fromNode,toNode);
    }
    else {
      edge = getEdges(fromNode, toNode).iterator().next();
      edge.addConnection(intertie);
    }
    interties.put(intertie, edge);                
    intertie.addIntertieChangeListener(this);
    addAsset(intertie);
  }

  /**
   * Adds a link to the model
   * 
   * @param ElectricPowerConnection
   */
  protected void addLink(ElectricPowerConnection link, ElectricPowerNode node1, ElectricPowerNode node2) {
  }

  /**
   * Fire a link added event
   * 
   * @param link
   */
  private void fireLinkAddedEvent(ElectricPowerConnection link) {
    for (ElectricPowerModelListener listener : modelListeners) {
      listener.linkAdded(link);
    }
    Collection<ElectricPowerNode> nodes = getNodes(link);
    Iterator<ElectricPowerNode> it = nodes.iterator();
    ElectricPowerNode node1 = it.next();
    ElectricPowerNode node2 = it.next();   
    for (ModelListener listener : getModelListeners()) {
      listener.addEdge(link, node1, node2);
    }

  }

  @Override
  public void removeConnection(Connection edge) {
  	removeEdge((ElectricPowerConnection)edge);
  }
  
  @Override
  public void removeEdge(ElectricPowerConnection link) {
    fireLinkRemovedEvent(link);    
    
    if (link instanceof Line) {
      removeLine((Line) link);
    }
    else if (link instanceof Transformer) {
      removeTransformer((Transformer) link);
    }
    else if (link instanceof Intertie) {
      removeIntertie((Intertie) link);
    }
    else if (link instanceof DCLine) {
      removeDCLine((DCLine) link);
    }
    else {
      throw new RuntimeException("Removal of unsupported link " + link);
    }
  }

  @Override
  public void removeLine(Line line) {
    Edge edge = getEdge(line);    
    if (line.equals(edge.getPrimaryConnection())) {
      if (edge.getConnections(Connection.class).size() > 1) {
        throw new ModelChangeException("Attempting to remove the prinary connection before all other connections are removed");
      }
    }
    
    edge.removeConnection(line);
    if (edge.getConnections(Connection.class).size() == 0) {
      if (!graph.removeEdge(edge)) {
        throw new ModelChangeException("Unable to find link " + line + " to remove");
      }      
    }
    
    lines.remove(line);
    line.removeLineChangeListener(this);
    removeAsset(line);
  }

  @Override
  public void removeTransformer(Transformer transformer) {
    Edge edge = getEdge(transformer);    
    if (transformer.equals(edge.getPrimaryConnection())) {
      if (edge.getConnections(Connection.class).size() > 1) {
        throw new ModelChangeException("Attempting to remove the prinary connection before all other connections are removed");
      }
    }
    
    edge.removeConnection(transformer);
    if (edge.getConnections(Connection.class).size() == 0) {
      if (!graph.removeEdge(edge)) {
        throw new ModelChangeException("Unable to find link " + transformer + " to remove");
      }      
    }
    
    transformers.remove(transformer);
    transformer.removeTransformerChangeListener(this);
    removeAsset(transformer);
  }

  @Override
  public void removeDCLine(DCLine dcLine) {
    Edge edge = getEdge(dcLine);    
    if (dcLine.equals(edge.getPrimaryConnection())) {
      if (edge.getConnections(Connection.class).size() > 1) {
        throw new ModelChangeException("Attempting to remove the prinary connection before all other connections are removed");
      }
    }
    
    edge.removeConnection(dcLine);
    if (edge.getConnections(Connection.class).size() == 0) {
      if (!graph.removeEdge(edge)) {
        throw new ModelChangeException("Unable to find link " + dcLine + " to remove");
      }      
    }
    
    dcLines.remove(dcLine);
    dcLine.removeDCLineChangeListener(this);
    removeAsset(dcLine);
  }

  
  @Override
  public void removeIntertie(Intertie intertie) {    
    Edge edge = getEdge(intertie);    
    if (intertie.equals(edge.getPrimaryConnection())) {
      if (edge.getConnections(Connection.class).size() > 1) {
        throw new ModelChangeException("Attempting to remove the prinary connection before all other connections are removed");
      }
    }
    
    edge.removeConnection(intertie);
    if (edge.getConnections(Connection.class).size() == 0) {
      if (!graph.removeEdge(edge)) {
        throw new ModelChangeException("Unable to find link " + intertie + " to remove");
      }      
    }

    interties.remove(intertie);
    intertie.removeIntertieChangeListener(this);
    removeAsset(intertie);
  }

  /**
   * Notify the listeners that the link has been removed
   * 
   * @param link
   */
  private void fireLinkRemovedEvent(ElectricPowerConnection link) {
    for (ElectricPowerModelListener listener : modelListeners) {
      listener.linkRemoved(link);
    }
    Collection<ElectricPowerNode> nodes = getNodes(link);
    Iterator<ElectricPowerNode> it = nodes.iterator();
    ElectricPowerNode node1 = it.next();
    ElectricPowerNode node2 = it.next();    
    for (ModelListener listener : getModelListeners()) {
      listener.removeEdge(link,node1, node2);
    }
  }

  @Override
  public void addShuntCapacitor(ShuntCapacitor shunt, Bus bus) {
    ElectricPowerNode node = getNode(bus);
    shunts.put(shunt, node);
    shunt.addShuntCapacitorChangeListener(this);
    node.addComponent(shunt);
    fireShuntAddEvent(shunt);
    addAsset(shunt);
  }
  
  /**
   * Notifies the listener that a shunt has been added the system
   * 
   * @param shunt
   * @param state
   */
  private void fireShuntAddEvent(ShuntCapacitor shunt) {
    for (ElectricPowerModelListener listener : modelListeners) {
      listener.shuntCapacitorAdded(shunt);
    }
    ElectricPowerNode node = getNode(shunt);
    for (ModelListener listener : getModelListeners()) {
      listener.attributeUpdated(shunt, node, ShuntCapacitor.REACTIVE_COMPENSATION_KEY, shunt.getReactiveCompensation());
    }
  }

  @Override
  public void removeShuntCapacitor(ShuntCapacitor shunt) {
    ElectricPowerNode node = getNode(shunt);
    fireShuntRemoveEvent(shunt);
    shunt.removeShuntCapacitorChangeListener(this);
    shunts.remove(shunt);
    node.removeComponent(shunt);    
    removeAsset(shunt);
  }

  /**
   * Notifies the listener that a shunt has been added the system
   * 
   * @param shunt
   * @param state
   */
  private void fireShuntRemoveEvent(ShuntCapacitor shunt) {
    for (ElectricPowerModelListener listener : modelListeners) {
      listener.shuntCapacitorRemoved(shunt);
    }
    ElectricPowerNode node = getNode(shunt);
    for (ModelListener listener : getModelListeners()) {
      listener.attributeUpdated(shunt, node, ShuntCapacitor.REACTIVE_COMPENSATION_KEY, 0.0);
    }
  }

  @Override
  public void addShuntCapacitorSwitch(ShuntCapacitorSwitch shunt, Bus bus) {
    ElectricPowerNode node = getNode(bus);
    switchedShunts.put(shunt, node);
    shunt.addShuntCapacitorSwitchChangeListener(this);
    node.addComponent(shunt);
    fireShuntCapacitorSwitchAddEvent(shunt);
    addAsset(shunt);
  }

  /**
   * Notifies the listener that a shunt has been added the system
   * 
   * @param shunt
   * @param state
   */
  private void fireShuntCapacitorSwitchAddEvent(ShuntCapacitorSwitch shunt) {
    for (ElectricPowerModelListener listener : modelListeners) {
      listener.shuntCapacitorSwitchAdded(shunt);
    }
  }

  @Override
  public void removeShuntCapacitorSwitch(ShuntCapacitorSwitch shunt) {
    ElectricPowerNode node = getNode(shunt);
    shunt.removeShuntCapacitorSwitchChangeListener(this);
    switchedShunts.remove(shunt);
    node.removeComponent(shunt);
    fireShuntCapacitorSwitchRemoveEvent(shunt);
    removeAsset(shunt);
  }

  /**
   * Notifies the listener that a shunt has been added the system
   * 
   * @param shunt
   * @param state
   */
  private void fireShuntCapacitorSwitchRemoveEvent(ShuntCapacitorSwitch shunt) {
    for (ElectricPowerModelListener listener : modelListeners) {
      listener.shuntCapacitorSwitchRemoved(shunt);
    }
  }

  @Override
  public Collection<ElectricPowerNode> getNeighbors(ElectricPowerNode node) {
    return graph.getNeighbors(node);
  }

  @Override
  public Collection<? extends Line> getLines(ElectricPowerNode node) {
    Vector<Line> lines = new Vector<Line>();
    Collection<ElectricPowerNode> neighbors = graph.getNeighbors(node);
    for (ElectricPowerNode node2 : neighbors) {
      Collection<ElectricPowerConnection> links = getConnections(node, node2);
      for (ElectricPowerConnection l : links) {
        if (l instanceof Line) {
          lines.add((Line) l);
        }
      }
    }
    return lines;
  }

  @Override
  public Collection<ElectricPowerFlowConnection> getFlowEdges(ElectricPowerNode node) {
    Vector<ElectricPowerFlowConnection> links = new Vector<ElectricPowerFlowConnection>();
    links.addAll(getLines(node));
    links.addAll(getTransformers(node));
    links.addAll(getDCLines(node));
    return links;
  }

  @Override
  public Collection<? extends Transformer> getTransformers(ElectricPowerNode node) {
    Vector<Transformer> lines = new Vector<Transformer>();
    Collection<ElectricPowerNode> neighbors = graph.getNeighbors(node);
    for (ElectricPowerNode node2 : neighbors) {
      Collection<ElectricPowerConnection> links = getConnections(node, node2);
      for (ElectricPowerConnection l : links) {
        if (l instanceof Transformer) {
          lines.add((Transformer) l);
        }
      }
    }
    return lines;
  }

  @Override
  public Collection<? extends DCLine> getDCLines(ElectricPowerNode node) {
    Vector<DCLine> lines = new Vector<DCLine>();
    Collection<ElectricPowerNode> neighbors = graph.getNeighbors(node);
    for (ElectricPowerNode node2 : neighbors) {
      Collection<ElectricPowerConnection> links = getConnections(node, node2);
      for (ElectricPowerConnection l : links) {
        if (l instanceof DCLine) {
          lines.add((DCLine) l);
        }
      }
    }
    return lines;
  }

  
  @Override
  public Collection<? extends ShuntCapacitor> getShuntCapacitors() {
    return shunts.keySet();
  }

  @Override
  public Collection<? extends ShuntCapacitorSwitch> getShuntCapacitorSwitches() {
    return switchedShunts.keySet();
  }

  @Override
  public Collection<? extends Line> getLines() {
    return lines.keySet();
  }

  @Override
  public Collection<? extends Transformer> getTransformers() {
    return transformers.keySet();
  }

  @Override
  public Collection<? extends DCLine> getDCLines() {
    return dcLines.keySet();
  }
  
  @Override
  public Collection<? extends DCTwoTerminalLine> getDCTwoTerminalLines() {
    ArrayList<DCTwoTerminalLine> lines = new ArrayList<DCTwoTerminalLine>();
    for (DCLine line : dcLines.keySet()) {
      if (line instanceof DCTwoTerminalLine) {
        lines.add((DCTwoTerminalLine)line);
      }
    }
    return lines;
  }

  @Override
  public Collection<? extends DCMultiTerminalLine> getDCMultiTerminalLines() {
    ArrayList<DCMultiTerminalLine> lines = new ArrayList<DCMultiTerminalLine>();
    for (DCLine line : dcLines.keySet()) {
      if (line instanceof DCMultiTerminalLine) {
        lines.add((DCMultiTerminalLine)line);
      }
    }
    return lines;
  }
  
  @Override
  public Collection<? extends DCVoltageSourceLine> getDCVoltageSourceLines() {
    ArrayList<DCVoltageSourceLine> lines = new ArrayList<DCVoltageSourceLine>();
    for (DCLine line : dcLines.keySet()) {
      if (line instanceof DCVoltageSourceLine) {
        lines.add((DCVoltageSourceLine)line);
      }
    }
    return lines;
  }
  
  @Override
  public Collection<? extends ElectricPowerFlowConnection> getFlowConnections() {
    Vector<ElectricPowerFlowConnection> links = new Vector<ElectricPowerFlowConnection>();
    links.addAll(getLines());
    links.addAll(getTransformers());
    links.addAll(getDCLines());
    return links;
  }

  @Override
  public void addModelListener(ElectricPowerModelListener listener) {
    modelListeners.add(listener);
  }

  @Override
  public void removeModelListener(ElectricPowerModelListener listener) {
    modelListeners.remove(listener);
  }

  @Override
  public Collection<? extends Intertie> getInterties() {
    return interties.keySet();
  }

  @Override
  public void addBus(Bus bus) {
    ElectricPowerNode node = createNode(bus);
    buses.put(bus, node);
    graph.addNode(node);
    nodes.add(node);
    fireBusAddEvent(bus);
    bus.addBusChangeListener(this);
    addAsset(bus);
  }

  /**
   * Notifies the listener that a bus has been added to the system
   * 
   * @param shunt
   * @param state
   */
  private void fireBusAddEvent(Bus bus) {
    for (ElectricPowerModelListener listener : modelListeners) {
      listener.busAdded(bus);
    }
  }

  @Override
  public void removeBus(Bus bus) {
    ElectricPowerNode node = getNode(bus);
    buses.remove(bus);
    nodes.remove(node);
    graph.removeNode(node);
    destroyNode(node);
    fireBusRemoveEvent(bus);
    bus.removeBusChangeListener(this);
    removeAsset(bus);
  }

  /**
   * Notifies the listener that a bus has been removed
   * 
   * @param shunt
   * @param state
   */
  private void fireBusRemoveEvent(Bus bus) {
    for (ElectricPowerModelListener listener : modelListeners) {
      listener.busRemoved(bus);
    }
  }

  @Override
  public Collection<? extends Bus> getBuses() {
    return buses.keySet();
  }

  @Override
  public void addGenerator(Generator generator, Bus bus) {
    ElectricPowerNode node = getNode(bus);
    generators.put(generator, node);
    generator.addGeneratorDataListener(this);
    node.addComponent(generator);
    fireGeneratorAddEvent(generator);
    if (generator.getType() != null && generator.getType().equals(GeneratorTypeEnum.REFERENCE_BUS_TYPE)) {
    	slackBuses.add(node.getBus());
    }
    addAsset(generator);
  }

  @Override
  public void addBattery(Battery battery, Bus bus) {
    ElectricPowerNode node = getNode(bus);
    batteries.put(battery, node);
    battery.addBatteryDataListener(this);
    node.addComponent(battery);
    fireBatteryAddEvent(battery);
    addAsset(battery);
  }

  /**
   * Notify the listeners a generator has been added
   * 
   * @param data
   * @param generator
   */
  private void fireGeneratorAddEvent(Generator generator) {
    for (ElectricPowerModelListener listener : modelListeners) {
      listener.generatorAdded(generator);      
    }
    for (ModelListener listener : getModelListeners()) {
      listener.attributeUpdated(generator, getNode(generator), Generator.ACTUAL_REAL_GENERATION_KEY, generator.getActualRealGeneration()); 
      listener.attributeUpdated(generator, getNode(generator), Generator.MAXIMUM_PRODUCTION_KEY, generator.getDesiredRealGenerationMax()); 
    }
  }

  /**
   * Notify the listeners a battery has been added
   * 
   * @param data
   * @param generator
   */
  private void fireBatteryAddEvent(Battery battery) {
    for (ElectricPowerModelListener listener : modelListeners) {
      listener.batteryAdded(battery);      
    }
    for (ModelListener listener : getModelListeners()) {
      listener.attributeUpdated(battery, getNode(battery), Battery.ACTUAL_REAL_GENERATION_KEY, battery.getActualRealGeneration()); 
      listener.attributeUpdated(battery, getNode(battery), Battery.ENERGY_CAPACITY_KEY, battery.getEnergyCapacity().doubleValue());           
    }
  }

  @Override
  public void removeGenerator(Generator generator) {
    ElectricPowerNode node = getNode(generator);
    generators.remove(generator);
    generator.removeGeneratorDataListener(this);
    node.removeComponent(generator);
    fireGeneratorRemoveEvent(generator);
    if (generator.getType().equals(GeneratorTypeEnum.REFERENCE_BUS_TYPE)) {
    	slackBuses.remove(node.getBus());
    }
    removeAsset(generator);
  }

  @Override
  public void removeBattery(Battery battery) {
    ElectricPowerNode node = getNode(battery);
    batteries.remove(battery);
    battery.removeBatteryDataListener(this);
    node.removeComponent(battery);
    fireBatteryRemoveEvent(battery);    
    removeAsset(battery);
  }
  
  /**
   * Notify that a generator has been removed
   * 
   * @param generator
   */
  private void fireGeneratorRemoveEvent(Generator generator) {
    for (ElectricPowerModelListener listener : modelListeners) {
      listener.generatorRemove(generator);
    }
    for (ModelListener listener : getModelListeners()) {
      listener.attributeUpdated(generator, getNode(generator), Generator.MAXIMUM_PRODUCTION_KEY, 0.0); 
      listener.attributeUpdated(generator, getNode(generator), Generator.ACTUAL_REAL_GENERATION_KEY, 0.0);       
    }
  }

  /**
   * Notify that a battery has been removed
   * 
   * @param generator
   */
  private void fireBatteryRemoveEvent(Battery battery) {
    for (ElectricPowerModelListener listener : modelListeners) {
      listener.batteryRemove(battery);
    }
    for (ModelListener listener : getModelListeners()) {
      listener.attributeUpdated(battery, getNode(battery), Battery.ACTUAL_REAL_GENERATION_KEY, 0.0);
      listener.attributeUpdated(battery, getNode(battery), Battery.ENERGY_CAPACITY_KEY, 0.0);         
    }
  }
  
  @Override
  public Collection<? extends Generator> getGenerators() {
    return generators.keySet();
  }

  @Override
  public Collection<? extends Battery> getBatteries() {
    return batteries.keySet();
  }
  
  @Override
  public Collection<? extends ControlArea> getControlAreas() {
    return controlAreas;
  }
  
  @Override
  public Collection<? extends Zone> getZones() {
    return zones;
  }

  @Override
  public void addLoad(Load load, Bus bus) {
    ElectricPowerNode node = getNode(bus);
    loads.put(load, node);
    load.addLoadChangeListener(this);
    node.addComponent(load);
    fireLoadAddEvent(load);
    addAsset(load);
  }

  /**
   * Fires a load added event
   * 
   * @param load
   * @param data
   */
  private void fireLoadAddEvent(Load load) {
    for (ElectricPowerModelListener listener : modelListeners) {
      listener.loadAdded(load);
    }
  }

  @Override
  public void removeLoad(Load load) {
    ElectricPowerNode node = getNode(load);
    loads.remove(load);
    load.removeLoadChangeListener(this);
    node.removeComponent(load);
    fireLoadRemoveEvent(load);
    removeAsset(load);
  }

  /**
   * Notification that a load has been removed
   * 
   * @param load
   */
  private void fireLoadRemoveEvent(Load load) {
    for (ElectricPowerModelListener listener : modelListeners) {
      listener.loadRemoved(load);
    }
  }

  @Override
  public Collection<? extends Load> getLoads() {
    return loads.keySet();
  }

  /**
   * Gets a load from a node
   * 
   * @param id
   * @return
   */
  @Override
  public Collection<? extends Load> getLoads(ElectricPowerNode node) {
    return (Collection<Load>) node.getComponents(Load.class);
  }

  /**
   * Gets a shunt
   * 
   * @param id
   * @return
   */
  public Collection<? extends ShuntCapacitor> getShuntCapacitors(ElectricPowerNode node) {
    return (Collection<ShuntCapacitor>) node.getComponents(ShuntCapacitor.class);
  }

  @Override
  public Collection<? extends ShuntCapacitorSwitch> getShuntCapacitorSwitches(ElectricPowerNode node) {
    return (Collection<ShuntCapacitorSwitch>) node.getComponents(ShuntCapacitorSwitch.class);
  }
  
  /**
   * Gets generators of a node
   * 
   * @param id
   * @return
   */
  @Override
  public Collection<? extends Generator> getGenerators(ElectricPowerNode node) {
    return (Collection<Generator>) node.getComponents(Generator.class);
  }

  /**
   * Gets batteries of a node
   * 
   * @param id
   * @return
   */
  @Override
  public Collection<? extends Battery> getBatteries(ElectricPowerNode node) {
    return (Collection<Battery>) node.getComponents(Battery.class);
  }
  
  @Override
  public Collection<ElectricPowerConnection> getConnections() {
    ArrayList<ElectricPowerConnection> connections = new ArrayList<ElectricPowerConnection>();
    for (Edge edge : getEdges()) {
      connections.addAll(edge.getConnections(ElectricPowerConnection.class));
    }
    return connections;    
   // return graph.getEdges();
  }

  @Override
  public Collection<Edge> getEdges() {
    return graph.getEdges();
  }
  
  @Override
  public Collection<ElectricPowerNode> createVoltageIsland(ElectricPowerNode startNode, int islandExtent) {
    // construct the island
    AbstractGraph<ElectricPowerNode, Edge> tempGraph = graph.constructIsland(startNode, islandExtent);
    Bus bus = startNode.getBus();

    // remove any nodes without the same voltage as bus
    Vector<ElectricPowerNode> nodesToRemove = new Vector<ElectricPowerNode>();
    for (ElectricPowerNode node : tempGraph.getNodes()) {
      if (node.getBus().getSystemVoltageKV() != bus.getSystemVoltageKV()) {
        nodesToRemove.add(node);
      }
    }
    for (ElectricPowerNode node : nodesToRemove) {
      tempGraph.removeNode(node);
    }

    // remove disconnected pieces
    return tempGraph.constructIsland(startNode, islandExtent).getNodes();
  }

  @Override
  public Collection<Component> getComponents() {
    Vector<Component> components = new Vector<Component>();
    components.addAll(getGenerators());
    components.addAll(getBatteries());
    components.addAll(getBuses());
    components.addAll(getLoads());
    components.addAll(getShuntCapacitors());
    components.addAll(getShuntCapacitorSwitches());
    return components;
  }

  @Override
  public Collection<ElectricPowerFlowConnection> getFlowConnections(Node to, Node fro) {
    return getFlowEdges((ElectricPowerNode) to, (ElectricPowerNode) fro);
  }

  @Override
  public Collection<ElectricPowerFlowConnection> getFlowConnections(Node node) {
    return getFlowEdges((ElectricPowerNode) node);
  }

  @Override
  public Collection<ElectricPowerNode> getNeighbors(Node node) {
    return getNeighbors((ElectricPowerNode) node);
  }

  @Override
  public Collection<? extends Connection> getConnections(Node to, Node fro) {
    return getConnections((ElectricPowerNode) to, (ElectricPowerNode) fro);
  }

  @Override
  public Collection<Connection> getConnections(Node node) {
    ArrayList<Connection> connections = new ArrayList<Connection>();
    for (Edge edge : getEdges(node)) {
      connections.addAll(edge.getConnections(Connection.class));
    }    
    return connections;
  }
 
  @Override
  public Collection<Edge> getEdges(Node node) {
    return graph.getEdges((ElectricPowerNode)node);
  }
 
  
  @Override
  public Pair<? extends Node, ? extends Node> getOrderedNodes(Connection link) {
    return getOrderedNodes((ElectricPowerConnection) link);
  }

  @Override
  public Collection<ElectricPowerNode> getNodes(Connection link) {
    return getNodes((ElectricPowerConnection) link);
  }

  @Override
  public void busDataChanged(Bus data, Object attribute) {
    fireBusChangeEvent(data, attribute);
  }

  /**
   * Tell the listeners about this change event
   * 
   * @param bus
   * @param kv
   */
  private void fireBusChangeEvent(Bus bus, Object attribute) {
    for (ModelListener listener : getModelListeners()) {
      listener.attributeUpdated(bus, getNode(bus), attribute, bus.getAttribute(attribute));
    }
    for (ElectricPowerModelListener listener : modelListeners) {
      listener.busDataChange(bus);
    }
  }

  @Override
  public void generatorDataChanged(Generator data, Object attribute) {
    if (data.getType() != null && data.getType().equals(GeneratorTypeEnum.REFERENCE_BUS_TYPE)) {
    	slackBuses.add(getNode(data).getBus());
    }
    else {
    	slackBuses.remove(getNode(data).getBus());
    }    
    fireGenerationChangeEvent(data, attribute);
  }
  
  @Override
  public void batteryDataChanged(Battery data, Object attribute) {
    fireBatteryChangeEvent(data, attribute);
  }

  /**
   * Notification to the listener that a generator value has changed
   * 
   * @param generator
   */
  private void fireGenerationChangeEvent(Generator generator, Object attribute) {
    for (ModelListener listener : getModelListeners()) {
      listener.attributeUpdated(generator, getNode(generator), attribute, generator.getAttribute(attribute));      
    }
    for (ElectricPowerModelListener listener : modelListeners) {
      listener.generatorDataChange(generator);
    }
  }

  /**
   * Notification to the listener that a battery value has changed
   * 
   * @param generator
   */
  private void fireBatteryChangeEvent(Battery battery, Object attribute) {
    for (ModelListener listener : getModelListeners()) {
      listener.attributeUpdated(battery, getNode(battery), attribute, battery.getAttribute(attribute));
    }
    for (ElectricPowerModelListener listener : modelListeners) {
      listener.batteryDataChange(battery);
    }
  }

  
  @Override
  public void loadDataChanged(Load data, Object attribute) {
    fireLoadChangeEvent(data, attribute);
  }

  /**
   * Notification to the listener that a load value has changed
   * 
   * @param load
   */
  private void fireLoadChangeEvent(Load load, Object attribute) {
    for (ModelListener listener : getModelListeners()) {
      listener.attributeUpdated(load, getNode(load), attribute, load.getAttribute(attribute));
    }
    for (ElectricPowerModelListener listener : modelListeners) {
      listener.loadDataChange(load);
    }
  }

  @Override
  public void shuntCapacitorDataChanged(ShuntCapacitor data, Object attribute) {
    fireShuntCapacitorChangeEvent(data, attribute);
  }

  /**
   * Notifies the listener that a shunt's compensation has changed
   * 
   * @param shunt
   * @param state
   */
  private void fireShuntCapacitorChangeEvent(ShuntCapacitor shunt, Object attribute) {
    for (ModelListener listener : getModelListeners()) {
      listener.attributeUpdated(shunt, getNode(shunt), attribute, shunt.getAttribute(attribute));
    }
    for (ElectricPowerModelListener listener : modelListeners) {
      listener.shuntCapacitorDataChange(shunt);
    }
  }

  @Override
  public void shuntCapacitorSwitchDataChanged(ShuntCapacitorSwitch data, Object attribute) {
    fireShuntCapacitorSwitchChangeEvent(data, attribute);
  }

  /**
   * Notifies the listener that a shunt's switch compensation has changed
   * 
   * @param shunt
   * @param state
   */
  private void fireShuntCapacitorSwitchChangeEvent(ShuntCapacitorSwitch shunt, Object attribute) {
    for (ModelListener listener : getModelListeners()) {
      listener.attributeUpdated(shunt, getNode(shunt), attribute, shunt.getAttribute(attribute));
    }
    for (ElectricPowerModelListener listener : modelListeners) {
      listener.shuntCapacitorSwitchDataChange(shunt);
    }
  }

  @Override
  public void intertieDataChanged(Intertie data, Object attribute) {
    fireIntertieChangeEvent(data, attribute);
  }

  /**
   * Tell the listeners about this change event
   * 
   * @param bus
   * @param kv
   */
  private void fireIntertieChangeEvent(Intertie intertie, Object attribute) {
    for (ModelListener listener : getModelListeners()) {
      listener.attributeUpdated(intertie, /*null,*/ getFirstNode(intertie), getSecondNode(intertie), attribute, intertie.getAttribute(attribute));
    }

    for (ElectricPowerModelListener listener : modelListeners) {
      listener.intertieDataChange(intertie);
    }
  }

  @Override
  public void lineDataChanged(Line line, Object attribute) {
    fireLineChangeEvent(line, attribute);
  }

  /**
   * Tell the listeners about this change event
   * 
   * @param bus
   * @param kv
   */
  private void fireLineChangeEvent(Line line, Object attribute) {
    for (ModelListener listener : getModelListeners()) {
      listener.attributeUpdated(line, getFirstNode(line), getSecondNode(line), attribute, line.getAttribute(attribute));
    }
    for (ElectricPowerModelListener listener : modelListeners) {
      listener.lineDataChange(line);
    }
  }

  @Override
  public void transformerDataChanged(Transformer line, Object attribute) {
    fireTransformerChangeEvent(line, attribute);
  }

  @Override
  public void dcLineDataChanged(DCLine line, Object attribute) {
    fireDCLineChangeEvent(line, attribute);
  }
  
  /**
   * Tell the listeners about this change event
   * 
   * @param bus
   * @param kv
   */
  private void fireTransformerChangeEvent(Transformer transformer, Object attribute) {
    for (ModelListener listener : getModelListeners()) {
      listener.attributeUpdated(transformer, getFirstNode(transformer), getSecondNode(transformer), attribute, transformer.getAttribute(attribute));
    }
    for (ElectricPowerModelListener listener : modelListeners) {
      listener.transformerDataChange(transformer);
    }
  }
  
  /**
   * Tell the listeners about this change event
   * 
   * @param bus
   * @param kv
   */
  private void fireDCLineChangeEvent(DCLine line, Object attribute) {
    for (ModelListener listener : getModelListeners()) {
      listener.attributeUpdated(line, getFirstNode(line), getSecondNode(line), attribute, line.getAttribute(attribute));
    }
    for (ElectricPowerModelListener listener : modelListeners) {
      listener.dcLineDataChange(line);
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
  protected abstract ElectricPowerModelImpl constructClone();
  
  @Override
  public ElectricPowerModel clone() {
    ElectricPowerModelImpl newModel = constructClone();
    newModel.setIsSolved(isSolved());
    newModel.setSimulationQualityRank(getSimulationQualityRank());
    
    Map<Bus,Bus> oldToNew = new HashMap<Bus,Bus>();
    Map<Intertie,Intertie> oldToNewIntertie = new HashMap<Intertie,Intertie>();
    Map<ControlArea,ControlArea> oldToNewArea = new HashMap<ControlArea,ControlArea>();
    Map<Zone,Zone> oldToNewZone = new HashMap<Zone,Zone>();
    Map<Asset,Asset> oldToNewAssets = new HashMap<Asset,Asset>();
    
    for (Bus bus : getBuses()) { 
      Bus newBus = bus.clone();
      newModel.addBus(newBus);
      oldToNew.put(bus, newBus);
      oldToNewAssets.put(bus,newBus);
    }
    
    for (Generator generator : getGenerators()) {
      Generator newGenerator = generator.clone();
      newModel.addGenerator(newGenerator, oldToNew.get(getNode(generator).getBus()));
      oldToNewAssets.put(generator,newGenerator);
    }

    for (Load load : getLoads()) {
      Load newLoad = load.clone();
      newModel.addLoad(newLoad, oldToNew.get(getNode(load).getBus()));
      oldToNewAssets.put(load,newLoad);
    }
    
    for (ShuntCapacitor capacitor : getShuntCapacitors()) {
      ShuntCapacitor newCapacitor = capacitor.clone();
      newModel.addShuntCapacitor(newCapacitor, oldToNew.get(getNode(capacitor).getBus()));
      oldToNewAssets.put(capacitor,newCapacitor);

    }
    
    for (ShuntCapacitorSwitch capacitor : getShuntCapacitorSwitches()) {
      ShuntCapacitorSwitch newCapacitor = capacitor.clone();
      newModel.addShuntCapacitorSwitch(newCapacitor, oldToNew.get(getNode(capacitor).getBus()));
      oldToNewAssets.put(capacitor,newCapacitor);
    }

    for (Line line : getLines()) {
      Line newLine = line.clone();
      ElectricPowerNode node1 = newModel.getNode(oldToNew.get(getFirstNode(line).getBus()));
      ElectricPowerNode node2 = newModel.getNode(oldToNew.get(getSecondNode(line).getBus()));      
      newModel.addEdge(newLine,node1,node2);
      oldToNewAssets.put(line,newLine);
    }
    
    for (Transformer transformer : getTransformers()) {
      Transformer newTransformer = transformer.clone();
      ElectricPowerNode node1 = newModel.getNode(oldToNew.get(getFirstNode(transformer).getBus()));
      ElectricPowerNode node2 = newModel.getNode(oldToNew.get(getSecondNode(transformer).getBus()));
      newModel.addEdge(newTransformer,node1,node2);
      oldToNewAssets.put(transformer,newTransformer);
    }

    for (DCLine line : getDCLines()) {
      DCLine newLine = line.clone();
      ElectricPowerNode node1 = newModel.getNode(oldToNew.get(getFirstNode(line).getBus()));
      ElectricPowerNode node2 = newModel.getNode(oldToNew.get(getSecondNode(line).getBus()));
      newModel.addEdge(newLine,node1,node2);
      oldToNewAssets.put(line,newLine);
    }
    
    for (Intertie intertie : getInterties()) {
      Intertie newIntertie = intertie.clone();
      ElectricPowerNode node1 = newModel.getNode(oldToNew.get(getFirstNode(intertie).getBus()));
      ElectricPowerNode node2 = newModel.getNode(oldToNew.get(getSecondNode(intertie).getBus()));
      newModel.addEdge(newIntertie,node1,node2);
      oldToNewIntertie.put(intertie, newIntertie);
      oldToNewAssets.put(intertie,newIntertie);
    }
    
    for (Battery battery : getBatteries()) {
      Battery newBattery = battery.clone();
      newModel.addBattery(newBattery, oldToNew.get(getNode(battery).getBus()));
      oldToNewAssets.put(battery,newBattery);
    }
    
    for (ControlArea area : getControlAreas()) {
      ControlArea newArea = area.clone();
      newModel.addArea(newArea);
      oldToNewArea.put(area, newArea);
    }
    
    for (Zone zone : getZones()) {
      Zone newZone = zone.clone();
      newModel.addZone(newZone);
      oldToNewZone.put(zone, newZone);
    }
    
    newModel.setMVABase(getMVABase());

    for (Asset asset : assetAreas.keySet()) {
      newModel.setControlArea(oldToNewAssets.get(asset), oldToNewArea.get(assetAreas.get(asset)));
    }
    
    for (Asset asset : assetZones.keySet()) {
      newModel.setZone(oldToNewAssets.get(asset), oldToNewZone.get(assetZones.get(asset)));
    }

    for (Intertie intertie : meteredAreas.keySet()) {
      newModel.setMeteredArea(oldToNewIntertie.get(intertie), oldToNewArea.get(meteredAreas.get(intertie)));
    }
    
    for (Intertie intertie : nonMeteredAreas.keySet()) {
      newModel.setNonMeteredArea(oldToNewIntertie.get(intertie), oldToNewArea.get(nonMeteredAreas.get(intertie)));
    }

    for (ControlArea area : areaSlackBuses.keySet()) {
      newModel.setSlackBus(oldToNewArea.get(area), oldToNew.get(areaSlackBuses.get(area)));
    }

    for (Asset asset : controlBuses.keySet()) {
      newModel.setControlBus(oldToNewAssets.get(asset), oldToNew.get(controlBuses.get(asset)));
    }
    
    newModel.setBatteryFactory(getBatteryFactory());
    newModel.setBusFactory(getBusFactory());
    newModel.setControlAreaFactory(getControlAreaFactory());
    newModel.setGeneratorFactory(getGeneratorFactory());
    newModel.setIntertieFactory(getIntertieFactory());
    newModel.setLineFactory(getLineFactory());
    newModel.setLoadFactory(getLoadFactory());
    newModel.setCapacitorFactory(getShuntCapacitorFactory());
    newModel.setShuntCapacitorSwitchFactory(getShuntCapacitorSwitchFactory());
    newModel.setTransformerFactory(getTransformerFactory());
    newModel.setDCLineFactory(getDCLineFactory());
    newModel.setZoneFactory(getZoneFactory());
    
    return newModel; 
  }
  
  @Override
  public void addArea(ControlArea area) {
    controlAreas.add(area);
    addAsset(area);
  }

  @Override
  public void addZone(Zone zone) {
    zones.add(zone);
    addAsset(zone);
  }

  /**
   * Get the MVA Base
   * @return
   */
  public double getMVABase() {
    return mvaBase;
  }
  
  /**
   * Set the MVA Base
   * @param mvaBase
   */
  public void setMVABase(double mvaBase) {
    this.mvaBase = mvaBase;
  }
  
  @Override
  public Set<Bus> getSlackBuses() {
  	return slackBuses;
  }
  
  @Override
  public ControlArea getControlArea(Asset asset) {
    return assetAreas.get(asset);
  }
  
  @Override 
  public Zone getZone(Asset asset) {
    return assetZones.get(asset);
  }
  
  @Override
  public Bus getSlackBus(ControlArea area) {
    return areaSlackBuses.get(area);
  }
  
  @Override
  public ControlArea getMeteredArea(Intertie intertie) {
    return meteredAreas.get(intertie);
  }
  
  @Override
  public ControlArea getNonMeteredArea(Intertie intertie) {
    return nonMeteredAreas.get(intertie);
  }
  
  @Override
  public void setControlArea(Asset asset, ControlArea area) {
    assetAreas.put(asset, area);
  }
  
  @Override 
  public void setZone(Asset asset, Zone zone) {
    assetZones.put(asset, zone);
  }
  
  @Override
  public void setSlackBus(ControlArea area, Bus slackBus) {
    areaSlackBuses.put(area, slackBus);
  }
  
  @Override
  public void setMeteredArea(Intertie intertie, ControlArea area) {
    meteredAreas.put(intertie, area);
  }
  
  @Override
  public void setNonMeteredArea(Intertie intertie, ControlArea area) {
    nonMeteredAreas.put(intertie, area);
  }

  @Override
  public void setControlBus(Asset asset, Bus bus) {
    controlBuses.put(asset, bus);
  }
  
  @Override
  public Bus getControlBus(Asset asset) {
    return controlBuses.get(asset);
  }

  /**
   * Create a node for a bus
   * 
   * @param bus
   * @return
   */
  protected ElectricPowerNode createNode(Bus bus) {
    ElectricPowerNode node = new ElectricPowerNodeImpl(bus);
    return node;
  }

  /**
   * Destroy a node
   * 
   * @param node
   * @return
   */
  protected void destroyNode(ElectricPowerNode node) {    
    if (node.getComponents(Component.class).size() > 1) {
      throw new RuntimeException("Error: Destroying an electric power node that still contains some components.");
    }
  }
  
  @Override
  public ElectricPowerNode getNode(Component component) {
    if (component instanceof Bus) {
      return buses.get(component);
    }
    if (component instanceof Generator) {
      return generators.get(component);      
    }
    if (component instanceof Load) {
      return loads.get(component);
    }
    if (component instanceof Battery) {
      return batteries.get(component);
    }
    if (component instanceof ShuntCapacitor) {
      return shunts.get(component);
    }
    if (component instanceof ShuntCapacitorSwitch) {
      return switchedShunts.get(component);
    }

    return null;
  }

  /**
   * Get the asset areas
   * @return
   */
  protected Map<Asset,ControlArea> getAssetAreas() {
    return assetAreas;
  }
  
  /**
   * Get the asset zones
   * @return
   */
  protected Map<Asset,Zone> getAssetZones() {
    return assetZones;
  }
  
  /**
   * Get the metereed areas
   */
  protected Map<Intertie,ControlArea> getMeteredAreas() {
    return meteredAreas;
  }
  
  /**
   * Get the metereed areas
   */
  protected Map<Intertie,ControlArea> getNonMeteredAreas() {
    return nonMeteredAreas;
  }
  
  /**
   * Get the area slack buses
   * @return
   */
  protected Map<ControlArea, Bus> getAreaSlackBuses() {
    return areaSlackBuses;
  }
  
  /**
   * Get control buses
   * @return
   */
  protected Map<Asset, Bus> getControlBuses() {
    return controlBuses;
  }
  
  @Override
  public BatteryFactory getBatteryFactory() {
    return batteryFactory;
  }

  @Override
  public BusFactory getBusFactory() {
    return busFactory;
  }

  @Override
  public ControlAreaFactory getControlAreaFactory() {
    return areaFactory;
  }
  
  @Override
  public GeneratorFactory getGeneratorFactory() {
    return generatorFactory;
  }
 
  @Override
  public IntertieFactory getIntertieFactory() {
    return intertieFactory;
  }
 
  @Override
  public LineFactory getLineFactory() {
    return lineFactory;
  }
  
  @Override
  public LoadFactory getLoadFactory() {
    return loadFactory;
  }
  
  @Override
  public ShuntCapacitorFactory getShuntCapacitorFactory() {
    return capacitorFactory;
  }

  @Override
  public ShuntCapacitorSwitchFactory getShuntCapacitorSwitchFactory() {
    return capacitorSwitchFactory;
  }
  
  @Override
  public TransformerFactory getTransformerFactory() {
    return transformerFactory;
  }

  @Override
  public DCLineFactory getDCLineFactory() {
    return dcLineFactory;
  }
  
  @Override
  public ZoneFactory getZoneFactory() {
    return zoneFactory;
  }
  
  /**
   * Set the battery factory
   * @param batteryFactory
   */
  protected void setBatteryFactory(BatteryFactory batteryFactory) {
    this.batteryFactory = batteryFactory;
  }
  
  /**
   * Set the bus factory
   * @param busFactory
   */
  protected void setBusFactory(BusFactory busFactory) {
    this.busFactory = busFactory;
  }
  
  /**
   * Set the control area factory
   * @param areaFactory
   */
  protected void setControlAreaFactory(ControlAreaFactory areaFactory) {
    this.areaFactory = areaFactory;
  }
  
  /**
   * Set the genrator factory
   * @param generatorFactory
   */
  protected void setGeneratorFactory(GeneratorFactory generatorFactory) {
    this.generatorFactory = generatorFactory;
  }
  
  /**
   * Set the intertie factory
   * @param intertieFactory
   */
  protected void setIntertieFactory(IntertieFactory intertieFactory) {
    this.intertieFactory = intertieFactory;
  }
  
  /**
   * Set the load factory
   * @param loadFactory
   */
  protected void setLineFactory(LineFactory lineFactory) {
    this.lineFactory = lineFactory;
  }
  
  /**
   * Set the load factory
   * @param loadFactory
   */
  protected void setLoadFactory(LoadFactory loadFactory) {
    this.loadFactory =loadFactory;
  }
  
  /**
   * Set the capacitor factory
   * @param capacitorFactory
   */
  protected void setCapacitorFactory(ShuntCapacitorFactory capacitorFactory) {
    this.capacitorFactory = capacitorFactory;
  }
  
  /**
   * Set the capacitor switch factory
   * @param caacitor switch Factory
   */
  protected void setShuntCapacitorSwitchFactory(ShuntCapacitorSwitchFactory capacitorSwitchFactory) {
    this.capacitorSwitchFactory = capacitorSwitchFactory;
  }
  
  /**
   * Set the transformer factory
   * @param transformerFactory
   */
  protected void setTransformerFactory(TransformerFactory transformerFactory) {
    this.transformerFactory = transformerFactory;
  }

  /**
   * Set the dc line factory
   * @param transformerFactory
   */
  protected void setDCLineFactory(DCLineFactory dcLineFactory) {
    this.dcLineFactory = dcLineFactory;
  }
  
  /**
   * Set the zone factory
   * @param zoneFactory
   */
  protected void setZoneFactory(ZoneFactory zoneFactory) {
    this.zoneFactory = zoneFactory;
  }








}
