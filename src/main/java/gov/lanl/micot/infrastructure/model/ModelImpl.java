package gov.lanl.micot.infrastructure.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

/**
 * An implementation of some of the Model interface function calls
 * @author Russell Bent
 *
 */
public abstract class ModelImpl implements Model {

	private Set<ModelListener> listeners = null;
	
  //private Map<Class<? extends Violation>, Vector<Violation>>                            violations            = null;

//  private Map<Class<? extends Violation>, Map<Node, Collection<Violation>>>             violationsByNode      = null;
  
  private int                                                                           simulationQualityRank = 0;
  
  private Collection<Control>                                                           controls              = null;
  
  private Map<Asset,Asset>                                                         searchableAssets         = null;   
  
	/**
	 * Constructor
	 */
	public ModelImpl() {
		listeners = new HashSet<ModelListener>();		
    //violations = new HashMap<Class<? extends Violation>, Vector<Violation>>();
    //violationsByNode = new HashMap<Class<? extends Violation>, Map<Node, Collection<Violation>>>();
    //controls = new ArrayList<Control>();
    searchableAssets = new LinkedHashMap<Asset,Asset>();
	}
		
  @Override
  public void addModelListener(ModelListener listener) {
    listeners.add(listener);
  }
  
  @Override
  public void removeModelListener(ModelListener listener) {
    listeners.remove(listener);
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public <E extends Asset> Set<E> getAssets(Class<E> cls) {
    Set<E> assets = new HashSet<E>();
    if (Component.class.isAssignableFrom(cls)) {
      Class<? extends Component> temp = (Class<? extends Component>) cls;
      assets.addAll((Set<E>)getComponents(temp));
    }
    if (Connection.class.isAssignableFrom(cls)) {
      Class<? extends Connection> temp = (Class<? extends Connection>) cls;
      assets.addAll((Set<E>)getConnections(temp));
    }
    return assets;
  }

  @Override
  public <E extends Component> Set<E> getComponents(Class<E> cls) {
    Set<E> components = new HashSet<E>();
    for (Node node : getNodes()) {
      components.addAll(node.getComponents(cls));
    }    
    return components;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public <E extends Connection> Set<E> getConnections(Class<E> cls) {
    Set<E> edges = new HashSet<E>();    
    for (Connection edge : getConnections()) {
      if (cls.isAssignableFrom(edge.getClass())) {
        edges.add((E)edge);
      }
      //if (edge instanceof MetaConnection) {
        //for (Connection edge2 : ((MetaConnection) edge).getSubConnections()) {
          //if (cls.isAssignableFrom(edge2.getClass())) {
            //edges.add((E)edge2);
          //}   
        //}
      //}
    }    
    return edges;
  }
  
  @Override
  public Set<Asset> getAssets() {
    Set<Asset> assets = new HashSet<Asset>();
    assets.addAll(getComponents());
    assets.addAll(getConnections());
    //for (Connection edge : getConnections()) {
      //if (edge instanceof MetaConnection) {
        //assets.addAll(((MetaConnection) edge).getSubConnections());
      //}
    //}
    return assets;
  }
    
//  @Override
 // public int getNumberOfViolations() {
   // return getViolations(Violation.class).size();    
 // }

  //@SuppressWarnings("unchecked")
  //@Override
  ///public <E extends Violation> Vector<E> getViolations(Class<E> cls) {
    //if (violations.get(cls) == null) {
      //return new Vector<E>();
   // }
   // return (Vector<E>) violations.get(cls);
 // }

  //@SuppressWarnings("unchecked")
  //@Override
  //public <E extends Violation> Collection<E> getViolations(Class<E> cls, Node node) {
    //if (violationsByNode.get(cls) == null || violationsByNode.get(cls).get(node) == null) {
      //return new Vector<E>();
    //}
    //return (Collection<E>) violationsByNode.get(cls).get(node);
  //}

/*  @SuppressWarnings("unchecked")
  @Override
  public <E extends Violation> void addViolation(E violation) {
    HashSet<Node> nodes = new HashSet<Node>();
    for (Connection edge : violation.getConnectionsInViolation()) {
      nodes.addAll((Collection<? extends Node>) getNodes(edge));
    }
    for (Component component : violation.getComponentsInViolation()) {
      nodes.add(getNode(component));
    }

    Stack<Class<?>> toExpand = new Stack<Class<?>>();
    Set<Class<? extends Violation>> classes = new HashSet<Class<? extends Violation>>();
    toExpand.add(violation.getClass());
    while (toExpand.size() > 0) {
      Class<?> cls = toExpand.pop();
      classes.add((Class<? extends Violation>)cls);
      if (cls.getSuperclass() != null) {
        toExpand.add(cls.getSuperclass());
      }
      for (Class<?> i : cls.getInterfaces()) {
        toExpand.add(i);
      }
    }
    
    for (Class<? extends Violation> cls : classes) {
      if (violations.get(cls) == null) {
        violations.put(cls, new Vector<Violation>());
      }
      violations.get(cls).add(violation);

      for (Node node : nodes) {
        if (violationsByNode.get(cls) == null) {
          violationsByNode.put(cls, new HashMap<Node, Collection<Violation>>());
        }
        if (violationsByNode.get(cls).get(node) == null) {
          violationsByNode.get(cls).put(node, new Vector<Violation>());
        }
        violationsByNode.get(cls).get(node).add(violation);
      }
    }
  }*/

  /*@Override
  public void clearViolations() {
    violations = new HashMap<Class<? extends Violation>, Vector<Violation>>();
    violationsByNode = new HashMap<Class<? extends Violation>, Map<Node, Collection<Violation>>>();
  }*/
  
  //@Override
  //public Corridor getCorridor(Connection edge) {
    //if (getNodes(edge) != null) {
      //return getCorridor(getFirstNode(edge),getSecondNode(edge));
    //}
    //return null;
  //}

  //@Override
  //public Corridor getCorridor(Node node1, Node node2) {
    //return CorridorFactory.getInstance().getCorridor(node1, node2);
  //}
  
  //@Override
  //public CorridorFactory getCorridorFactory() {
    //return CorridorFactory.getInstance();
  //}
  
  @Override
  public void setSimulationQualityRank(int rank) {
    simulationQualityRank = rank;
  }
  
  @Override
  public int getSimulationQualityRank() {
    return simulationQualityRank;
  }

  @Override
  public Collection<Control> getControls() {
    return controls;
  }

  @Override
  public void addControl(Control control) {
    controls.add(control);
    for (ModelListener listener : listeners) {
      listener.addControl(control);
    }
  }
  
  @Override
  public void removeControl(Control control) {
    controls.remove(control);
    for (ModelListener listener : listeners) {
      listener.removeControl(control);
    }
  }
  
  /**
   * Get the model listeners
   * @return
   */
  protected Set<ModelListener> getModelListeners() {
    return listeners;
  }
  
  @Override
  public abstract Model clone();

  @Override
  public Collection<? extends FlowConnection> getFlowConnections(Node node) {
    ArrayList<FlowConnection> connections = new ArrayList<FlowConnection>();
    for (Edge edge : getEdges(node)) {
      connections.addAll(edge.getConnections(FlowConnection.class));
    }    
    return connections;
  }

  @Override
  public Collection<? extends Connection> getConnections(Node node) {
    ArrayList<Connection> connections = new ArrayList<Connection>();
    for (Edge edge : getEdges(node)) {
      connections.addAll(edge.getConnections(Connection.class));
    }    
    return connections;
  }
 
  @Override
  public Collection<? extends Connection> getConnections() {
   ArrayList<Connection> connections = new ArrayList<Connection>();
   for (Edge edge : getEdges()) {
     connections.addAll(edge.getConnections(Connection.class));
   }
   return connections;    
 }
  
  @Override
  public Collection<? extends FlowConnection> getFlowConnections() {
   ArrayList<FlowConnection> connections = new ArrayList<FlowConnection>();
   for (Edge edge : getEdges()) {
     connections.addAll(edge.getConnections(FlowConnection.class));
   }
   return connections;    
 }
  
  @Override
  public Collection<? extends FlowConnection> getFlowConnections(Node to, Node fro) {
    ArrayList<FlowConnection> connections = new ArrayList<FlowConnection>();
    for (Edge edge : getEdges(to,fro)) {
      connections.addAll(edge.getConnections(FlowConnection.class));
    }    
    return connections;
  }

  /**
   * General error checking on adding an asset
   * @param asset
   */
  protected void addAsset(Asset asset) {
    if (searchableAssets.containsKey(asset)) {
      throw new RuntimeException("Unrecoverable Error: Asset " + asset + " aready in model");
    }
    searchableAssets.put(asset, asset);
  }

  /**
   * Function for registering the removal of an asset
   * @param asset
   */
  protected void removeAsset(Asset asset) {
    searchableAssets.remove(asset);
  }

  @Override
  public Asset getAsset(Asset asset) {
    return searchableAssets.get(asset);
  }

}
