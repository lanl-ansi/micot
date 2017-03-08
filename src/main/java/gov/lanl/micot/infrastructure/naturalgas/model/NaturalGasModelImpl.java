package gov.lanl.micot.infrastructure.naturalgas.model;

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
import gov.lanl.micot.infrastructure.model.Edge;
import gov.lanl.micot.infrastructure.model.EdgeImpl;
import gov.lanl.micot.infrastructure.model.ModelChangeException;
import gov.lanl.micot.infrastructure.model.ModelImpl;
import gov.lanl.micot.infrastructure.model.ModelListener;
import gov.lanl.micot.infrastructure.model.Node;
import gov.lanl.micot.infrastructure.naturalgas.model.impl.CityGateFactoryImpl;
import gov.lanl.micot.infrastructure.naturalgas.model.impl.CompressorFactoryImpl;
import gov.lanl.micot.infrastructure.naturalgas.model.impl.ControlValveFactoryImpl;
import gov.lanl.micot.infrastructure.naturalgas.model.impl.JunctionFactoryImpl;
import gov.lanl.micot.infrastructure.naturalgas.model.impl.PipeFactoryImpl;
import gov.lanl.micot.infrastructure.naturalgas.model.impl.ReservoirFactoryImpl;
import gov.lanl.micot.infrastructure.naturalgas.model.impl.ResistorFactoryImpl;
import gov.lanl.micot.infrastructure.naturalgas.model.impl.ShortPipeFactoryImpl;
import gov.lanl.micot.infrastructure.naturalgas.model.impl.ValveFactoryImpl;
import gov.lanl.micot.infrastructure.naturalgas.model.impl.WellFactoryImpl;
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
public abstract class NaturalGasModelImpl extends ModelImpl implements NaturalGasModel, JunctionChangeListener, ShortPipeChangeListener, PipeChangeListener, CompressorChangeListener, ReservoirChangeListener, WellChangeListener, CityGateChangeListener, ResistorChangeListener, ValveChangeListener, ControlValveChangeListener {

  protected static final long                                                            serialVersionUID     = 0;

  protected AbstractGraph<NaturalGasNode, Edge>                                          graph                = null;

  private Set<NaturalGasModelListener>                                                   modelListeners       = null;

  private Map<Pipe, Edge>                                                                pipes                = null;

  private Map<ShortPipe, Edge>                                                          shortPipes                = null;
  
  private Map<Compressor, Edge>                                                         compressors           = null;

  private Map<Valve, Edge>                                                              valves                = null;

  private Map<ControlValve, Edge>                                                       controlValves         = null;
  
  private Map<Resistor, Edge>                                                           resistors             = null;

  private Map<Reservoir, NaturalGasNode>                                                reservoirs            = null;

  private Map<Junction, NaturalGasNode>                                                 junctions             = null;

  private Map<Well, NaturalGasNode>                                                     wells                 = null;

  private Map<CityGate, NaturalGasNode>                                                 gates                 = null;
  
  private Set<NaturalGasNode>                                                           nodes                 = null;

  private boolean                                                                       isSolved              = false;
  
  private CityGateFactory cityGateFactory = null;

  private CompressorFactory compressorFactory = null;

  private ValveFactory valveFactory = null;
  
  private ControlValveFactory controlValveFactory = null;

  private ResistorFactory resistorFactory = null;

  private JunctionFactory junctionFactory = null;

  private PipeFactory pipeFactory = null;

  private ShortPipeFactory shortPipeFactory = null;

  private ReservoirFactory reservoirFactory = null;

  private WellFactory wellFactory = null;  
  
  /**
   * Constructor
   * 
   * @param graph
   */
  public NaturalGasModelImpl(AbstractGraph<NaturalGasNode, Edge> graph) {
    super();
    init(graph);
  }

  /**
   * Constructor
   */
  public NaturalGasModelImpl() {
    super();
    GraphFactory<NaturalGasNode, Edge> factory = new GraphFactory<NaturalGasNode, Edge>();
    init(factory.createGraph());
  }

  /**
   * Initialization routine
   */
  protected void init(AbstractGraph<NaturalGasNode, Edge> graph) {
    this.graph = graph;
    modelListeners = new HashSet<NaturalGasModelListener>();
    pipes = new LinkedHashMap<Pipe, Edge>();
    shortPipes = new LinkedHashMap<ShortPipe, Edge>();
    reservoirs = new LinkedHashMap<Reservoir, NaturalGasNode>();
    compressors = new LinkedHashMap<Compressor, Edge>();
    valves = new LinkedHashMap<Valve,Edge>();
    controlValves = new LinkedHashMap<ControlValve,Edge>();
    resistors = new LinkedHashMap<Resistor,Edge>();
    junctions = new LinkedHashMap<Junction, NaturalGasNode>();
    wells = new LinkedHashMap<Well, NaturalGasNode>();
    gates = new LinkedHashMap<CityGate, NaturalGasNode>();
    nodes = new HashSet<NaturalGasNode>();
    isSolved = true;
    cityGateFactory = new CityGateFactoryImpl();
    compressorFactory = new CompressorFactoryImpl();
    valveFactory = new ValveFactoryImpl();    
    controlValveFactory = new ControlValveFactoryImpl();
    resistorFactory = new ResistorFactoryImpl();
    junctionFactory = new JunctionFactoryImpl();
    pipeFactory = new PipeFactoryImpl();
    shortPipeFactory = new ShortPipeFactoryImpl();
    reservoirFactory = new ReservoirFactoryImpl();
    wellFactory = new WellFactoryImpl();
  }

  @Override
  public Pair<NaturalGasNode, NaturalGasNode> getOrderedNodes(NaturalGasConnection connection) {
    Edge edge = getEdge(connection);
    return getOrderedNodes(edge);
  }

  @Override
  public Pair<NaturalGasNode, NaturalGasNode> getOrderedNodes(Edge edge) {
    Collection<NaturalGasNode> nodes = graph.getIncidentVertices(edge);
    if (nodes.size() == 0) {
      return null;
    }
    Iterator<NaturalGasNode> it = nodes.iterator();
    return new Pair<NaturalGasNode, NaturalGasNode>(it.next(), it.next());
  }
  
  @Override
  public Collection<NaturalGasNode> getNodes(NaturalGasConnection link) {
    Edge edge = getEdge(link);
    return getNodes(edge);    
  }

  @Override
  public Collection<NaturalGasNode> getNodes(Edge link) {
    return graph.getIncidentVertices(link);
  }

  
  @Override
  public NaturalGasNode getFirstNode(Connection edge) {
    if (getNodes(edge).size() == 0) {
      System.err.println(edge);
    }
    return getNodes(edge).iterator().next();
  }

  @Override
  public NaturalGasNode getSecondNode(Connection edge) {
    Iterator<NaturalGasNode> it = getNodes(edge).iterator();
    it.next();
    return it.next();
  }

  @Override
  public Collection<Pipe> getPipes(NaturalGasNode to, NaturalGasNode fro) {
    ArrayList<Pipe> pipes = new ArrayList<Pipe>();
    Collection<Edge> edges = graph.getEdges(to,fro);    
    for (Edge edge : edges) {
      pipes.addAll(edge.getConnections(Pipe.class));
    }
    return pipes;
  }
  
  @Override
  public Collection<ShortPipe> getShortPipes(NaturalGasNode to, NaturalGasNode fro) {
    ArrayList<ShortPipe> shortPipes = new ArrayList<ShortPipe>();
    Collection<Edge> edges = graph.getEdges(to,fro);    
    for (Edge edge : edges) {
      shortPipes.addAll(edge.getConnections(ShortPipe.class));
    }
    return shortPipes;
  }

  @Override
  public Collection<Compressor> getCompressors(NaturalGasNode to, NaturalGasNode fro) {
    ArrayList<Compressor> compressors = new ArrayList<Compressor>();
    Collection<Edge> edges = graph.getEdges(to,fro);    
    for (Edge edge : edges) {
      compressors.addAll(edge.getConnections(Compressor.class));
    }
    return compressors;
  }

  @Override
  public Collection<Valve> getValves(NaturalGasNode to, NaturalGasNode fro) {
    ArrayList<Valve> valves = new ArrayList<Valve>();
    Collection<Edge> edges = graph.getEdges(to,fro);    
    for (Edge edge : edges) {
      valves.addAll(edge.getConnections(Valve.class));
    }
    return valves;
  }

  @Override
  public Collection<ControlValve> getControlValves(NaturalGasNode to, NaturalGasNode fro) {
    ArrayList<ControlValve> valves = new ArrayList<ControlValve>();
    Collection<Edge> edges = graph.getEdges(to,fro);    
    for (Edge edge : edges) {
      valves.addAll(edge.getConnections(ControlValve.class));
    }
    return valves;
  }

  
  @Override
  public Collection<Resistor> getResistors(NaturalGasNode to, NaturalGasNode fro) {
    ArrayList<Resistor> r = new ArrayList<Resistor>();
    Collection<Edge> edges = graph.getEdges(to,fro);    
    for (Edge edge : edges) {
      r.addAll(edge.getConnections(Resistor.class));
    }
    return r;
  }
  
  @Override
  public Collection<NaturalGasNode> getNodes() {
    return graph.getNodes();
  }

  @Override
  public void addPipe(Pipe pipe, NaturalGasNode fromNode, NaturalGasNode toNode) {
    Edge edge = null;
    if (getEdges(fromNode, toNode).size() <= 0) {
      edge = new EdgeImpl(pipe);
      graph.addEdge(edge,fromNode,toNode);
    }
    else {
      edge = getEdges(fromNode, toNode).iterator().next();
      edge.addConnection(pipe);
    }
    pipes.put(pipe, edge);        
    
    pipe.addPipeChangeListener(this);
    firePipeAddedEvent(pipe);    
    addAsset(pipe);
  }

  @Override
  public void addShortPipe(ShortPipe pipe, NaturalGasNode fromNode, NaturalGasNode toNode) {
    Edge edge = null;
    if (getEdges(fromNode, toNode).size() <= 0) {
      edge = new EdgeImpl(pipe);
      graph.addEdge(edge,fromNode,toNode);
    }
    else {
      edge = getEdges(fromNode, toNode).iterator().next();
      edge.addConnection(pipe);
    }
    shortPipes.put(pipe, edge);        
    
    pipe.addShortPipeChangeListener(this);
    fireShortPipeAddedEvent(pipe);    
    addAsset(pipe);
  }

  
  @Override
  public void addCompressor(Compressor compressor, NaturalGasNode fromNode, NaturalGasNode toNode) {
    Edge edge = null;
    if (getEdges(fromNode, toNode).size() <= 0) {
      edge = new EdgeImpl(compressor);
      graph.addEdge(edge,fromNode,toNode);
    }
    else {
      edge = getEdges(fromNode, toNode).iterator().next();
      edge.addConnection(compressor);
    }
    compressors.put(compressor, edge);            
    compressor.addCompressorChangeListener(this);
    fireCompressorAddEvent(compressor);    
    addAsset(compressor);
  }
  
  @Override
  public void addValve(Valve valve, NaturalGasNode fromNode, NaturalGasNode toNode) {
    Edge edge = null;
    if (getEdges(fromNode, toNode).size() <= 0) {
      edge = new EdgeImpl(valve);
      graph.addEdge(edge,fromNode,toNode);
    }
    else {
      edge = getEdges(fromNode, toNode).iterator().next();
      edge.addConnection(valve);
    }
    valves.put(valve, edge);            
    valve.addValveChangeListener(this);
    fireValveAddEvent(valve);    
    addAsset(valve);
  }

  
  @Override
  public void addControlValve(ControlValve valve, NaturalGasNode fromNode, NaturalGasNode toNode) {
    Edge edge = null;
    if (getEdges(fromNode, toNode).size() <= 0) {
      edge = new EdgeImpl(valve);
      graph.addEdge(edge,fromNode,toNode);
    }
    else {
      edge = getEdges(fromNode, toNode).iterator().next();
      edge.addConnection(valve);
    }
    controlValves.put(valve, edge);            
    valve.addControlValveChangeListener(this);
    fireControlValveAddEvent(valve);    
    addAsset(valve);

  }

  @Override
  public void addResistor(Resistor resistor, NaturalGasNode fromNode, NaturalGasNode toNode) {
    Edge edge = null;
    if (getEdges(fromNode, toNode).size() <= 0) {
      edge = new EdgeImpl(resistor);
      graph.addEdge(edge,fromNode,toNode);
    }
    else {
      edge = getEdges(fromNode, toNode).iterator().next();
      edge.addConnection(resistor);
    }
    resistors.put(resistor, edge);            
    resistor.addResistorChangeListener(this);
    fireResistorAddEvent(resistor);   
    addAsset(resistor);
  }
 
  /**
   * Fire a link added event
   * 
   * @param link
   */
  private void firePipeAddedEvent(Pipe pipe) {
    for (NaturalGasModelListener listener : modelListeners) {
      listener.pipeAdded(pipe);
    }
  }

  /**
   * Fire a link added event
   * 
   * @param link
   */
  private void fireShortPipeAddedEvent(ShortPipe pipe) {
    for (NaturalGasModelListener listener : modelListeners) {
      listener.shortPipeAdded(pipe);
    }
  }
  
  @Override
  public void removePipe(Pipe pipe) {
    firePipeRemovedEvent(pipe);    
    Edge edge = getEdge(pipe);    
    if (pipe.equals(edge.getPrimaryConnection())) {
      if (edge.getConnections(Connection.class).size() > 1) {
        throw new ModelChangeException("Attempting to remove the prinary connection before all other connections are removed");
      }
    }
    
    edge.removeConnection(pipe);
    if (edge.getConnections(Connection.class).size() == 0) {
      if (!graph.removeEdge(edge)) {
        throw new ModelChangeException("Unable to find link " + pipe + " to remove");
      }      
    }
  	
    pipes.remove(pipe);
    pipe.removePipeChangeListener(this);  	
    removeAsset(pipe);
  }

  @Override
  public void removeShortPipe(ShortPipe pipe) {
    fireShortPipeRemovedEvent(pipe);    
    Edge edge = getEdge(pipe);    
    if (pipe.equals(edge.getPrimaryConnection())) {
      if (edge.getConnections(Connection.class).size() > 1) {
        throw new ModelChangeException("Attempting to remove the prinary connection before all other connections are removed");
      }
    }
    
    edge.removeConnection(pipe);
    if (edge.getConnections(Connection.class).size() == 0) {
      if (!graph.removeEdge(edge)) {
        throw new ModelChangeException("Unable to find link " + pipe + " to remove");
      }      
    }
    
    shortPipes.remove(pipe);
    pipe.removeShortPipeChangeListener(this);    
    removeAsset(pipe);
  }
  
  @Override
  public void removeCompressor(Compressor compressor) {
    fireCompressorRemoveEvent(compressor);        
    Edge edge = getEdge(compressor);    
    if (compressor.equals(edge.getPrimaryConnection())) {
      if (edge.getConnections(Connection.class).size() > 1) {
        throw new ModelChangeException("Attempting to remove the prinary connection before all other connections are removed");
      }
    }
    
    edge.removeConnection(compressor);
    if (edge.getConnections(Connection.class).size() == 0) {
      if (!graph.removeEdge(edge)) {
        throw new ModelChangeException("Unable to find link " + compressor + " to remove");
      }      
    }
    
    compressors.remove(compressor);
    compressor.removeCompressorChangeListener(this);    
    removeAsset(compressor);
  }

  @Override
  public void removeValve(Valve valve) {
    fireValveRemoveEvent(valve);        
    Edge edge = getEdge(valve);    
    if (valve.equals(edge.getPrimaryConnection())) {
      if (edge.getConnections(Connection.class).size() > 1) {
        throw new ModelChangeException("Attempting to remove the prinary connection before all other connections are removed");
      }
    }
    
    edge.removeConnection(valve);
    if (edge.getConnections(Connection.class).size() == 0) {
      if (!graph.removeEdge(edge)) {
        throw new ModelChangeException("Unable to find link " + valve + " to remove");
      }      
    }
    
    valves.remove(valve);
    valve.removeValveChangeListener(this);    
    removeAsset(valve);
  }

  
  @Override
  public void removeControlValve(ControlValve valve) {
    fireControlValveRemoveEvent(valve);        
    Edge edge = getEdge(valve);    
    if (valve.equals(edge.getPrimaryConnection())) {
      if (edge.getConnections(Connection.class).size() > 1) {
        throw new ModelChangeException("Attempting to remove the prinary connection before all other connections are removed");
      }
    }
    
    edge.removeConnection(valve);
    if (edge.getConnections(Connection.class).size() == 0) {
      if (!graph.removeEdge(edge)) {
        throw new ModelChangeException("Unable to find link " + valve + " to remove");
      }      
    }
    
    controlValves.remove(valve);
    valve.removeControlValveChangeListener(this);    
    removeAsset(valve);
  }
  
  @Override
  public void removeResistor(Resistor resistor) {
    fireResisitorRemoveEvent(resistor);        
    Edge edge = getEdge(resistor);    
    if (resistor.equals(edge.getPrimaryConnection())) {
      if (edge.getConnections(Connection.class).size() > 1) {
        throw new ModelChangeException("Attempting to remove the prinary connection before all other connections are removed");
      }
    }
    
    edge.removeConnection(resistor);
    if (edge.getConnections(Connection.class).size() == 0) {
      if (!graph.removeEdge(edge)) {
        throw new ModelChangeException("Unable to find link " + resistor + " to remove");
      }      
    }
    
    resistors.remove(resistor);
    resistor.removeResistorChangeListener(this);    
    removeAsset(resistor);
  }

  /**
   * Notify the listeners that the link has been removed
   * 
   * @param link
   */
  private void firePipeRemovedEvent(Pipe link) {
    for (NaturalGasModelListener listener : modelListeners) {
      listener.pipeRemoved(link);
    }
  }

  /**
   * Notify the listeners that the link has been removed
   * 
   * @param link
   */
  private void fireShortPipeRemovedEvent(ShortPipe link) {
    for (NaturalGasModelListener listener : modelListeners) {
      listener.shortPipeRemoved(link);
    }
  }
  
  /**
   * Notifies the listener that a compressor has been added the system
   * 
   * @param shunt
   * @param state
   */
  private void fireCompressorAddEvent(Compressor compressor) {
    for (NaturalGasModelListener listener : modelListeners) {
      listener.compressorAdded(compressor);
    }
  }

  /**
   * Notifies the listener that a valve has been added the system
   * 
   * @param shunt
   * @param state
   */
  private void fireValveAddEvent(Valve valve) {
    for (NaturalGasModelListener listener : modelListeners) {
      listener.valveAdded(valve);
    }
  }

  
  /**
   * Notifies the listener that a valve has been added the system
   * 
   * @param shunt
   * @param state
   */
  private void fireControlValveAddEvent(ControlValve valve) {
    for (NaturalGasModelListener listener : modelListeners) {
      listener.controlValveAdded(valve);
    }
  }

  
  /**
   * Notifies the listener that a valve has been added the system
   * 
   * @param shunt
   * @param state
   */
  private void fireResistorAddEvent(Resistor resistor) {
    for (NaturalGasModelListener listener : modelListeners) {
      listener.resistorAdded(resistor);
    }
  }

  /**
   * Notifies the listener that a shunt has been added the system
   * 
   * @param shunt
   * @param state
   */
  private void fireCompressorRemoveEvent(Compressor compressor) {
    for (NaturalGasModelListener listener : modelListeners) {
      listener.compressorRemoved(compressor);
    }
  }

  /**
   * Notifies the listener that a shunt has been added the system
   * 
   * @param shunt
   * @param state
   */
  private void fireValveRemoveEvent(Valve valve) {
    for (NaturalGasModelListener listener : modelListeners) {
      listener.valveRemoved(valve);
    }
  }

  /**
   * Notifies the listener that a shunt has been added the system
   * 
   * @param shunt
   * @param state
   */
  private void fireControlValveRemoveEvent(ControlValve valve) {
    for (NaturalGasModelListener listener : modelListeners) {
      listener.controlValveRemoved(valve);
    }
  }

  
  /**
   * Notifies the listener that a shunt has been added the system
   * 
   * @param shunt
   * @param state
   */
  private void fireResisitorRemoveEvent(Resistor resisitor) {
    for (NaturalGasModelListener listener : modelListeners) {
      listener.resistorRemoved(resisitor);
    }
  }

  
  
  @Override
  public void addReservoir(Reservoir reservoir, Junction junction) {
    NaturalGasNode node = getNode(junction);
    reservoirs.put(reservoir, node);
    reservoir.addReservoirChangeListener(this);
    node.addComponent(reservoir);
    fireReservoirAddEvent(reservoir);
    addAsset(reservoir);

  }

  /**
   * Notifies the listener that a shunt has been added the system
   * 
   * @param shunt
   * @param state
   */
  private void fireReservoirAddEvent(Reservoir reservoir) {
    for (NaturalGasModelListener listener : modelListeners) {
      listener.reservoirAdded(reservoir);
    }
  }

  @Override
  public void removeReservoir(Reservoir reservoir) {
    reservoir.removeReservoirChangeListener(this);
    Node node = getNode(reservoir);    
    reservoirs.remove(reservoir);
    node.removeComponent(reservoir);
    fireReservoirRemoveEvent(reservoir);
    removeAsset(reservoir);
  }

  /**
   * Notifies the listener that a shunt has been added the system
   * 
   * @param shunt
   * @param state
   */
  private void fireReservoirRemoveEvent(Reservoir reservoir) {
    for (NaturalGasModelListener listener : modelListeners) {
      listener.reservoirRemoved(reservoir);
    }
  }

  @Override
  public Collection<NaturalGasNode> getNeighbors(NaturalGasNode node) {
    return graph.getNeighbors(node);
  }

  @Override
  public Collection<Pipe> getPipes(NaturalGasNode node) {
    Vector<Pipe> lines = new Vector<Pipe>();
    Collection<NaturalGasNode> neighbors = graph.getNeighbors(node);
    for (NaturalGasNode node2 : neighbors) {
      Collection<Pipe> links = getPipes(node, node2);
      for (Pipe l : links) {
        lines.add(l);
      }
    }
    return lines;
  }

  
  @Override
  public Collection<ShortPipe> getShortPipes(NaturalGasNode node) {
    Vector<ShortPipe> lines = new Vector<ShortPipe>();
    Collection<NaturalGasNode> neighbors = graph.getNeighbors(node);
    for (NaturalGasNode node2 : neighbors) {
      Collection<ShortPipe> links = getShortPipes(node, node2);
      for (ShortPipe l : links) {
        lines.add(l);
      }
    }
    return lines;
  }
  
  @Override
  public Collection<? extends Compressor> getCompressors(NaturalGasNode node) {
    Vector<Compressor> lines = new Vector<Compressor>();
    Collection<NaturalGasNode> neighbors = graph.getNeighbors(node);
    for (NaturalGasNode node2 : neighbors) {
      Collection<Compressor> links = getCompressors(node, node2);
      for (Compressor l : links) {
        lines.add(l);
      }
    }
    return lines;
  }

  @Override
  public Collection<? extends Valve> getValves(NaturalGasNode node) {
    Vector<Valve> lines = new Vector<Valve>();
    Collection<NaturalGasNode> neighbors = graph.getNeighbors(node);
    for (NaturalGasNode node2 : neighbors) {
      Collection<Valve> links = getValves(node, node2);
      for (Valve l : links) {
        lines.add(l);
      }
    }
    return lines;
  }
  
  @Override
  public Collection<? extends ControlValve> getControlValves(NaturalGasNode node) {
    Vector<ControlValve> lines = new Vector<ControlValve>();
    Collection<NaturalGasNode> neighbors = graph.getNeighbors(node);
    for (NaturalGasNode node2 : neighbors) {
      Collection<ControlValve> links = getControlValves(node, node2);
      for (ControlValve l : links) {
        lines.add(l);
      }
    }
    return lines;
  }
  
  @Override
  public Collection<? extends Resistor> getResistors(NaturalGasNode node) {
    Vector<Resistor> lines = new Vector<Resistor>();
    Collection<NaturalGasNode> neighbors = graph.getNeighbors(node);
    for (NaturalGasNode node2 : neighbors) {
      Collection<Resistor> links = getResistors(node, node2);
      for (Resistor l : links) {
        lines.add(l);
      }
    }
    return lines;
  }

  
  @Override
  public Collection<? extends Compressor> getCompressors() {
    return compressors.keySet();
  }

  @Override
  public Collection<? extends Valve> getValves() {
    return valves.keySet();
  }

  @Override
  public Collection<? extends ControlValve> getControlValves() {
    return controlValves.keySet();
  }
  
  @Override
  public Collection<? extends Resistor> getResistors() {
    return resistors.keySet();
  }

  
  @Override
  public Collection<? extends Reservoir> getReservoirs() {
    return reservoirs.keySet();
  }

  @Override
  public Collection<Pipe> getPipes() {
    return pipes.keySet();
  }

  @Override
  public Collection<ShortPipe> getShortPipes() {
    return shortPipes.keySet();
  }
  
  @Override
  public Edge getEdge(Connection connection) {
    if (connection instanceof Pipe) {
      return pipes.get(connection);
    }
    if (connection instanceof ShortPipe) {
      return shortPipes.get(connection);
    }
    if (connection instanceof Compressor) {
      return compressors.get(connection);
    }
    if (connection instanceof Valve) {
      return valves.get(connection);
    }
    if (connection instanceof ControlValve) {
      return controlValves.get(connection);
    }
    if (connection instanceof Resistor) {
      return resistors.get(connection);
    }
    return null;
  }
  
  
  @Override
  public void addModelListener(NaturalGasModelListener listener) {
    modelListeners.add(listener);
  }

  @Override
  public void removeModelListener(NaturalGasModelListener listener) {
    modelListeners.remove(listener);
  }

  @Override
  public void addJunction(Junction junction) {
    NaturalGasNode node = createNode(junction);
    junctions.put(junction, node);
    graph.addNode(node);
    nodes.add(node);
    fireJunctionAddEvent(junction);
    junction.addJunctionChangeListener(this);
    addAsset(junction);

  }

  /**
   * Notifies the listener that a bus has been added to the system
   * 
   * @param shunt
   * @param state
   */
  private void fireJunctionAddEvent(Junction junction) {
    for (NaturalGasModelListener listener : modelListeners) {
      listener.junctionAdded(junction);
    }
  }

  @Override
  public void removeJunction(Junction junction) {
    NaturalGasNode node = getNode(junction);
    junctions.remove(junction);
    nodes.remove(node);
    graph.removeNode(node);
    destroyNode(node);
    fireJunctionRemoveEvent(junction);
    junction.removeJunctionChangeListener(this);
    removeAsset(junction);
  }

  /**
   * Notifies the listener that a bus has been removed
   * 
   * @param shunt
   * @param state
   */
  private void fireJunctionRemoveEvent(Junction junction) {
    for (NaturalGasModelListener listener : modelListeners) {
      listener.junctionRemoved(junction);
    }
  }

  @Override
  public Collection<? extends Junction> getJunctions() {
    return junctions.keySet();
  }

  @Override
  public void addWell(Well well, Junction junction) {
    NaturalGasNode node = getNode(junction);
    wells.put(well, node);
    well.addWellDataListener(this);
    node.addComponent(well);
    fireWellAddEvent(well);
    addAsset(well);
  }

  /**
   * Notify the listeners a generator has been added
   * 
   * @param data
   * @param generator
   */
  private void fireWellAddEvent(Well well) {
    for (NaturalGasModelListener listener : modelListeners) {
      listener.wellAdded(well);
    }
  }

  @Override
  public void removeWell(Well well) {
    Node node = getNode(well);
    wells.remove(well);
    well.removeWellDataListener(this);
    node.removeComponent(well);
    fireWellRemoveEvent(well);
    removeAsset(well);
  }

  /**
   * Notify that a generator has been removed
   * 
   * @param generator
   */
  private void fireWellRemoveEvent(Well well) {
    for (NaturalGasModelListener listener : modelListeners) {
      listener.wellRemoved(well);
    }
  }

  @Override
  public Collection<? extends Well> getWells() {
    return wells.keySet();
  }
  
   @Override
  public void addCityGate(CityGate gate, Junction junction) {
    NaturalGasNode node = getNode(junction);
    gates.put(gate, node);
    gate.addCityGateChangeListener(this);
    node.addComponent(gate);
    fireCityGateAddEvent(gate);
    addAsset(gate);
  }

  /**
   * Fires a load added event
   * 
   * @param load
   * @param data
   */
  private void fireCityGateAddEvent(CityGate gate) {
    for (NaturalGasModelListener listener : modelListeners) {
      listener.cityGateAdded(gate);
    }
  }

  @Override
  public void removeCityGate(CityGate gate) {
    Node node = getNode(gate);
    gates.remove(gate);
    gate.removeCityGateChangeListener(this);
    node.removeComponent(gate);
    fireCityGateRemoveEvent(gate);
    removeAsset(gate);
  }

  /**
   * Notification that a load has been removed
   * 
   * @param load
   */
  private void fireCityGateRemoveEvent(CityGate gate) {
    for (NaturalGasModelListener listener : modelListeners) {
      listener.cityGateRemoved(gate);
    }
  }

  @Override
  public Collection<? extends CityGate> getCityGates() {
    return gates.keySet();
  }

  /**
   * Create a node for a bus
   * 
   * @param bus
   * @return
   */
  protected NaturalGasNode createNode(Junction junction) {
    NaturalGasNode node = new NaturalGasNodeImpl(junction);
    return node;
  }

  /**
   * Destroy a node
   * 
   * @param node
   * @return
   */
  protected void destroyNode(NaturalGasNode node) {    
    if (node.getComponents(Component.class).size() > 1) {
      throw new RuntimeException("Error: Destroying a natural gas node that still contains some components.");
    }
  }

  /**
   * Gets a load from a node
   * 
   * @param id
   * @return
   */
  @Override
  public Collection<? extends CityGate> getCityGates(NaturalGasNode node) {
    return (Collection<CityGate>) node.getComponents(CityGate.class);
  }

  /**
   * Gets generators of a node
   * 
   * @param id
   * @return
   */
  @Override
  public Collection<? extends Well> getWells(NaturalGasNode node) {
    return (Collection<Well>) node.getComponents(Well.class);
  }

  /**
   * Gets reservoirs of a node
   * 
   * @param id
   * @return
   */
  @Override
  public Collection<Reservoir> getReservoirs(NaturalGasNode node) {
    return (Collection<Reservoir>) node.getComponents(Reservoir.class);
  }
  
  @Override
  public Collection<Component> getComponents() {
    Vector<Component> components = new Vector<Component>();
    components.addAll(getWells());
    components.addAll(getJunctions());
    components.addAll(getCityGates());
    components.addAll(getReservoirs());
    return components;
  }

  @Override
  public Collection<NaturalGasNode> getNeighbors(Node node) {
    return getNeighbors((NaturalGasNode) node);
  }

  @Override
  public Collection<? extends Connection> getConnections(Node to, Node fro) {
    return getConnections((NaturalGasNode) to, (NaturalGasNode) fro);
  }

  @Override
  public Collection<NaturalGasNode> getNodes(Connection link) {
    if (link instanceof Pipe) {
      return getNodes((Pipe) link);
    }
    else if (link instanceof ShortPipe) {
      return getNodes((ShortPipe)link);
    }
    else if (link instanceof Compressor) {
      return getNodes((Compressor)link);
    }
    else if (link instanceof Resistor) {
      return getNodes((Resistor)link);
    }
    else if (link instanceof ControlValve) {
      return getNodes((ControlValve)link);
    }
    else {
      return getNodes((Valve)link);
    }
  }

  @Override
  public void junctionDataChanged(Junction data, Object attribute) {
    fireJunctionChangeEvent(data, attribute);
  }

  /**
   * Tell the listeners about this change event
   * 
   * @param bus
   * @param kv
   */
  private void fireJunctionChangeEvent(Junction junction, Object attribute) {
    for (ModelListener listener : getModelListeners()) {
      listener.attributeUpdated(junction, getNode(junction), attribute, junction.getAttribute(attribute));
    }
    for (NaturalGasModelListener listener : modelListeners) {
      listener.junctionDataChange(junction);
    }
  }

  @Override
  public void wellDataChanged(Well data, Object attribute) {
    fireWellChangeEvent(data, attribute);
  }

  /**
   * Notification to the listener that a generator value has changed
   * 
   * @param generator
   */
  private void fireWellChangeEvent(Well well, Object attribute) {
    for (ModelListener listener : getModelListeners()) {
      listener.attributeUpdated(well, getNode(well), attribute, well.getAttribute(attribute));
    }    
    for (NaturalGasModelListener listener : modelListeners) {
      listener.wellDataChange(well);
    }
  }

  @Override
  public void cityGateDataChanged(CityGate data, Object attribute) {
    fireCityGateChangeEvent(data, attribute);
  }

  /**
   * Notification to the listener that a load value has changed
   * 
   * @param generator
   */
  private void fireCityGateChangeEvent(CityGate gate, Object attribute) {
    for (ModelListener listener : getModelListeners()) {
      listener.attributeUpdated(gate, getNode(gate), attribute, gate.getAttribute(attribute));
    }    
    for (NaturalGasModelListener listener : modelListeners) {
      listener.cityGateDataChange(gate);
    }
  }

  @Override
  public void compressorDataChanged(Compressor data, Object attribute) {
    fireCompressorChangeEvent(data, attribute);
  }

  @Override
  public void valveDataChanged(Valve data, Object attribute) {
    fireValveChangeEvent(data, attribute);
  }
  
  @Override
  public void controlValveDataChanged(ControlValve data, Object attribute) {
    fireControlValveChangeEvent(data, attribute);
  }

  @Override
  public void resistorDataChanged(Resistor data, Object attribute) {
    fireResistorChangeEvent(data, attribute);
  }

  /**
   * Notifies the listener that a shunt's compensation has changed
   * 
   * @param shunt
   * @param state
   */
  private void fireCompressorChangeEvent(Compressor compressor, Object attribute) {
    for (ModelListener listener : getModelListeners()) {
      listener.attributeUpdated(compressor, getFirstNode(compressor), getSecondNode(compressor), attribute, compressor.getAttribute(attribute));
    }    
    for (NaturalGasModelListener listener : modelListeners) {
      listener.compressorDataChange(compressor);
    }
  }
  
  /**
   * Notifies the listener that a shunt's compensation has changed
   * 
   * @param shunt
   * @param state
   */
  private void fireValveChangeEvent(Valve valve, Object attribute) {
    for (ModelListener listener : getModelListeners()) {
      listener.attributeUpdated(valve,  getFirstNode(valve), getSecondNode(valve), attribute, valve.getAttribute(attribute));
    }    
    for (NaturalGasModelListener listener : modelListeners) {
      listener.valveDataChange(valve);
    }
  }

  /**
   * Notifies the listener that a shunt's compensation has changed
   * 
   * @param shunt
   * @param state
   */
  private void fireControlValveChangeEvent(ControlValve valve, Object attribute) {
    for (ModelListener listener : getModelListeners()) {
      listener.attributeUpdated(valve,  getFirstNode(valve), getSecondNode(valve), attribute, valve.getAttribute(attribute));
    }    
    for (NaturalGasModelListener listener : modelListeners) {
      listener.controlValveDataChange(valve);
    }
  }
  
  /**
   * Notifies the listener that a shunt's compensation has changed
   * 
   * @param shunt
   * @param state
   */
  private void fireResistorChangeEvent(Resistor resistor, Object attribute) {
    for (ModelListener listener : getModelListeners()) {
      listener.attributeUpdated(resistor,  getFirstNode(resistor), getSecondNode(resistor), attribute, resistor.getAttribute(attribute));
    }    
    for (NaturalGasModelListener listener : modelListeners) {
      listener.resistorDataChange(resistor);
    }
  }

  
  @Override
  public void reservoirDataChanged(Reservoir data, Object attribute) {
    fireReservoirChangeEvent(data, attribute);
  }

  /**
   * Notifies the listener that a shunt's switch compensation has changed
   * 
   * @param shunt
   * @param state
   */
  private void fireReservoirChangeEvent(Reservoir reservoir, Object attribute) {
    for (ModelListener listener : getModelListeners()) {
      listener.attributeUpdated(reservoir, getNode(reservoir), attribute, reservoir.getAttribute(attribute));
    }
    for (NaturalGasModelListener listener : modelListeners) {
      listener.reservoirDataChange(reservoir);
    }
  }

  @Override
  public void pipeDataChanged(Pipe pipe, Object attribute) {
    firePipeChangeEvent(pipe, attribute);
  }

  @Override
  public void shortPipeDataChanged(ShortPipe pipe, Object attribute) {
    fireShortPipeChangeEvent(pipe, attribute);
  }
  
  /**
   * Tell the listeners about this change event
   * 
   * @param bus
   * @param kv
   */
  private void firePipeChangeEvent(Pipe pipe, Object attribute) {
    for (ModelListener listener : getModelListeners()) {
      listener.attributeUpdated(pipe, getFirstNode(pipe), getSecondNode(pipe), attribute, pipe.getAttribute(attribute));
    }
    
    for (NaturalGasModelListener listener : modelListeners) {
      listener.pipeDataChange(pipe);
    }
  }
  
  /**
   * Tell the listeners about this change event
   * 
   * @param bus
   * @param kv
   */
  private void fireShortPipeChangeEvent(ShortPipe pipe, Object attribute) {
    for (ModelListener listener : getModelListeners()) {
      listener.attributeUpdated(pipe, getFirstNode(pipe), getSecondNode(pipe), attribute, pipe.getAttribute(attribute));
    }
    
    for (NaturalGasModelListener listener : modelListeners) {
      listener.shortPipeDataChange(pipe);
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
  protected abstract NaturalGasModelImpl constructClone();
  
  @Override
  public NaturalGasModel clone() {
    NaturalGasModelImpl newModel = constructClone();
    newModel.setIsSolved(isSolved());
    newModel.setSimulationQualityRank(getSimulationQualityRank());
    
    Map<Junction,Junction> oldToNewJunctions = new HashMap<Junction,Junction>();
    
    for (Junction junction : getJunctions()) { 
      Junction newJunction = junction.clone();
      newModel.addJunction(newJunction);      
      oldToNewJunctions.put(junction,newJunction);
    }

    for (Well well : getWells()) {
      Well newWell = well.clone();
      Junction newJunction = oldToNewJunctions.get(getNode(well).getJunction());
      newModel.addWell(newWell, newJunction);
    }

    for (CityGate gate : getCityGates()) {
      CityGate newGate = gate.clone();
      Junction newJunction = oldToNewJunctions.get(getNode(gate).getJunction());      
      newModel.addCityGate(newGate, newJunction);
    }
    
    for (Compressor compressor : getCompressors()) {
      Compressor newCompressor = compressor.clone();
      Junction newJunction1 = oldToNewJunctions.get(getFirstNode(compressor).getJunction());      
      Junction newJunction2 = oldToNewJunctions.get(getSecondNode(compressor).getJunction());      
      newModel.addCompressor(newCompressor, newModel.getNode(newJunction1), newModel.getNode(newJunction2));
    }
    
    for (Valve valve : getValves()) {
      Valve newValve = valve.clone();
      Junction newJunction1 = oldToNewJunctions.get(getFirstNode(valve).getJunction());      
      Junction newJunction2 = oldToNewJunctions.get(getSecondNode(valve).getJunction());      
      newModel.addValve(newValve, newModel.getNode(newJunction1), newModel.getNode(newJunction2));
    }
    
    for (ControlValve valve : getControlValves()) {
      ControlValve newValve = valve.clone();
      Junction newJunction1 = oldToNewJunctions.get(getFirstNode(valve).getJunction());      
      Junction newJunction2 = oldToNewJunctions.get(getSecondNode(valve).getJunction());      
      newModel.addControlValve(newValve, newModel.getNode(newJunction1), newModel.getNode(newJunction2));
    }
    
    for (Resistor resistor : getResistors()) {
      Resistor newResistor = resistor.clone();
      Junction newJunction1 = oldToNewJunctions.get(getFirstNode(resistor).getJunction());      
      Junction newJunction2 = oldToNewJunctions.get(getSecondNode(resistor).getJunction());      
      newModel.addResistor(newResistor, newModel.getNode(newJunction1), newModel.getNode(newJunction2));
    }
    
    for (Pipe pipe : getPipes()) {
      Pipe newPipe = pipe.clone();
      Junction newJunction1 = oldToNewJunctions.get(getFirstNode(pipe).getJunction());      
      Junction newJunction2 = oldToNewJunctions.get(getSecondNode(pipe).getJunction());      
      newModel.addPipe(newPipe, newModel.getNode(newJunction1), newModel.getNode(newJunction2));
    }
    
    for (ShortPipe pipe : getShortPipes()) {
      ShortPipe newPipe = pipe.clone();
      Junction newJunction1 = oldToNewJunctions.get(getFirstNode(pipe).getJunction());      
      Junction newJunction2 = oldToNewJunctions.get(getSecondNode(pipe).getJunction());      
      newModel.addShortPipe(newPipe, newModel.getNode(newJunction1), newModel.getNode(newJunction2));
    }
    
    for (Reservoir reservoir : getReservoirs()) {
      Reservoir newReservoir = reservoir.clone();
      Junction newJunction = oldToNewJunctions.get(getNode(reservoir).getJunction());         
      newModel.addReservoir(newReservoir, newJunction);
    }
    
//    ViolationCalculator calculator = ViolationCalculator.getInstance();
    
    // create line violations
  //  calculator.calculateOverloads(newModel);

    // create load violations
    //calculator.calculateLoadSheds(newModel);
    
    // create pressure violations
    //calculator.calculatePressureViolations(newModel);    
    
    // generator overloads
    //calculator.calculateProducerViolations(newModel);
    
    newModel.setCityGateFactory(getCityGateFactory());
    newModel.setCompressorFactory(getCompressorFactory());
    newModel.setValveFactory(getValveFactory());
    newModel.setControlValveFactory(getControlValveFactory());
    newModel.setResistorFactory(getResistorFactory());
    newModel.setJunctionFactory(getJunctionFactory());
    newModel.setPipeFactory(getPipeFactory());
    newModel.setShortPipeFactory(getShortPipeFactory());
    newModel.setResistorFactory(getResistorFactory());
    newModel.setWellFactory(getWellFactory());
        
    return newModel;     
  }
  
  @Override
  public Pair<? extends Node, ? extends Node> getOrderedNodes(Connection link) {
    return getOrderedNodes((Pipe) link);
  }

  @Override
  public Collection<NaturalGasConnection> getConnections(NaturalGasNode node1, NaturalGasNode node2) {
    Collection<Edge> edges = getEdges(node1, node2);
    ArrayList<NaturalGasConnection> collections = new ArrayList<NaturalGasConnection>();
    for (Edge edge : edges) {
      collections.addAll(edge.getConnections(NaturalGasConnection.class));
    }
    return collections;    
  }

  @Override
  public Collection<Edge> getEdges(NaturalGasNode node1, NaturalGasNode node2) {
    return graph.getEdges(node1, node2);
  }
  
  @Override
  public Collection<NaturalGasConnection> getFlowConnections() {
    Vector<NaturalGasConnection> links = new Vector<NaturalGasConnection>();
    links.addAll(getPipes());
    links.addAll(getShortPipes());
    links.addAll(getCompressors());
    links.addAll(getValves());
    links.addAll(getControlValves());
    links.addAll(getResistors());
    return links;
  }
  
  @Override
  public Collection<NaturalGasConnection> getFlowConnections(Node node) {
    Vector<NaturalGasConnection> links = new Vector<NaturalGasConnection>();
    links.addAll(getPipes((NaturalGasNode)node));
    links.addAll(getShortPipes((NaturalGasNode)node));
    links.addAll(getCompressors((NaturalGasNode)node));
    links.addAll(getValves((NaturalGasNode)node));
    links.addAll(getControlValves((NaturalGasNode)node));
    links.addAll(getResistors((NaturalGasNode)node));
    return links;
  }

  @Override
  public Collection<NaturalGasConnection> getFlowConnections(Node to, Node fro) {
    Vector<NaturalGasConnection> links = new Vector<NaturalGasConnection>();
    links.addAll(getPipes((NaturalGasNode)to,(NaturalGasNode)fro));
    links.addAll(getShortPipes((NaturalGasNode)to,(NaturalGasNode)fro));
    links.addAll(getCompressors((NaturalGasNode)to,(NaturalGasNode)fro));
    links.addAll(getValves((NaturalGasNode)to,(NaturalGasNode)fro));
    links.addAll(getControlValves((NaturalGasNode)to,(NaturalGasNode)fro));
    links.addAll(getResistors((NaturalGasNode)to,(NaturalGasNode)fro));
    return links;
  }
  
  @Override
  public Collection<NaturalGasConnection> getConnections() {
    Vector<NaturalGasConnection> links = new Vector<NaturalGasConnection>();
    links.addAll(getPipes());
    links.addAll(getShortPipes());
    links.addAll(getCompressors());
    links.addAll(getValves());
    links.addAll(getControlValves());
    links.addAll(getResistors());
    return links;
  }

  /*@Override
  public boolean isCriticalConnection(Node n1, Node n2) {
    Edge edge = getEdges((NaturalGasNode)n1,(NaturalGasNode)n2).iterator().next();
    return graph.isCriticalEdge(edge);
  }*/

  @Override
  public void addConnection(Connection edge, Node fromNode, Node toNode) {
    if (edge instanceof Pipe) {
      addPipe((Pipe)edge, (NaturalGasNode)fromNode, (NaturalGasNode)toNode);
    }
    else if (edge instanceof Compressor){
      addCompressor((Compressor)edge, (NaturalGasNode)fromNode, (NaturalGasNode)toNode);
    }
    else if (edge instanceof ShortPipe){
      addShortPipe((ShortPipe)edge, (NaturalGasNode)fromNode, (NaturalGasNode)toNode);
    }
    else if (edge instanceof Resistor){
      addResistor((Resistor)edge, (NaturalGasNode)fromNode, (NaturalGasNode)toNode);
    }
    else if (edge instanceof ControlValve){
      addControlValve((ControlValve)edge, (NaturalGasNode)fromNode, (NaturalGasNode)toNode);
    }
    else {
      addValve((Valve)edge, (NaturalGasNode)fromNode, (NaturalGasNode)toNode);
    }
  }

  @Override
  public void removeConnection(Connection edge) {
    if (edge instanceof Pipe) {
      removePipe((Pipe)edge);
    }
    else if (edge instanceof Compressor){
      removeCompressor((Compressor)edge);
    }
    else if (edge instanceof ShortPipe){
      removeShortPipe((ShortPipe)edge);
    }
    else if (edge instanceof Resistor){
      removeResistor((Resistor)edge);
    }
    else if (edge instanceof ControlValve){
      removeControlValve((ControlValve)edge);
    }
    else {
      removeValve((Valve)edge);
    }

  }
  
  @Override
  public Collection<? extends Connection> getConnections(Node node) {
    Collection<Edge> edges = graph.getEdges((NaturalGasNode)node);
    ArrayList<Connection> connections = new ArrayList<Connection>();
    for (Edge edge : edges) {
      connections.addAll(edge.getConnections(Connection.class));
    }
    
    return connections;
  }
 
  @Override
  public CityGateFactory getCityGateFactory() {
    return cityGateFactory;
  }

  @Override
  public CompressorFactory getCompressorFactory() {
    return compressorFactory;
  }

  @Override
  public ValveFactory getValveFactory() {
    return valveFactory;
  }
  
  @Override
  public ControlValveFactory getControlValveFactory() {
    return controlValveFactory;
  }

  @Override
  public ResistorFactory getResistorFactory() {
    return resistorFactory;
  }
  
  @Override
  public JunctionFactory getJunctionFactory() {
    return junctionFactory;
  }

  @Override
  public PipeFactory getPipeFactory() {
    return pipeFactory;
  }
  
  @Override
  public ShortPipeFactory getShortPipeFactory() {
    return shortPipeFactory;
  }

  @Override
  public ReservoirFactory getReservoirFactory() {
    return reservoirFactory;
  }

  @Override
  public WellFactory getWellFactory() {
    return wellFactory;
  }
  
  /**
   * Set the city gate factpru
   * @param cf
   */
  protected void setCityGateFactory(CityGateFactory cf) {
    cityGateFactory = cf;;
  }

  /**
   * Sets the compressor factor
   * @param f
   */
  protected void setCompressorFactory(CompressorFactory f) {
    compressorFactory = f;
  }

  /**
   * Set the valve factory
   * @param f
   */
  protected void setValveFactory(ValveFactory f) {
    valveFactory = f;
  }
  
  /**
   * Set the control valve factory
   * @param f
   */
  protected void setControlValveFactory(ControlValveFactory f) {
    controlValveFactory = f;
  }

  /**
   * Set the resistor factory
   * @param f
   */
  protected void setResistorFactory(ResistorFactory f) {
    resistorFactory = f ;
  }
  
  /**
   * Set the jucntionfactory
   * @param f
   */
  protected void setJunctionFactory(JunctionFactory f) {
    junctionFactory = f;
  }

  /**
   * Set the pipe factory
   * @param f
   */
  protected void setPipeFactory(PipeFactory f) {
    pipeFactory = f;
  }
  
  /**
   * Set the short pipe factory
   * @param f
   */
  protected void setShortPipeFactory(ShortPipeFactory f) {
    shortPipeFactory = f;
  }

  /**
   * Set the reservoir factory
   * @param f
   */
  protected void setReservoirFactory(ReservoirFactory f) {
    reservoirFactory = f;
  }

  /**
   * Set the well factory
   * @param f
   */
  protected void setWellFactory(WellFactory f) {
    wellFactory = f;
  }

  @Override
  public NaturalGasNode getNode(Component component) {
    if (component instanceof Junction) {
      return junctions.get(component);
    }
    if (component instanceof Well) {
      return wells.get(component);      
    }
    if (component instanceof CityGate) {
      return gates.get(component);
    }
    if (component instanceof Reservoir) {
      return reservoirs.get(component);
    }
    return null;
  }
  
  @Override
  public Collection<Edge> getEdges(Node to, Node fro) {
    return getEdges((NaturalGasNode)to, (NaturalGasNode)fro);
  }

  @Override
  public Collection<Edge> getEdges(Node node) {
    return graph.getEdges((NaturalGasNode)node);
  }

  @Override
  public Collection<Edge> getEdges() {
    return graph.getEdges();
  }

  @Override
  public NaturalGasNode getFirstNode(Edge edge) {
    return getNodes(edge).iterator().next();
  }

  @Override
  public NaturalGasNode getSecondNode(Edge edge) {
    Iterator<NaturalGasNode> it = getNodes(edge).iterator();
    it.next();
    return it.next();
  }
  
}

