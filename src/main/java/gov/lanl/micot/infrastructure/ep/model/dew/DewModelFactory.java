package gov.lanl.micot.infrastructure.ep.model.dew;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Line;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitor;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.model.ModelFactory;

/**
 * Factory class for creating dew model states from other model states
 * 
 * @author Russell Bent
 */
public class DewModelFactory implements ModelFactory<ElectricPowerModel> {

  private static DewModelFactory INSTANCE = null;

  public static DewModelFactory getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new DewModelFactory();
    }
    return INSTANCE;
  }

  // scratch data structures
  private Set<DewLegacyId> substations = null;
  private Set<DewLegacyId> circuits = null;
  private Set<DewLegacyId> busbars = null;
  private Set<DewLegacyId> singleEndedDevices = null;
  private Set<DewLegacyId> transformers = null;
  private Set<DewLegacyId> capacitors = null;
  private Set<DewLegacyId> lines = null;
  private Set<DewLegacyId> switches = null;
  private Set<DewLegacyId> protectiveDevices = null;
  private Set<DewLegacyId> loads = null;
  private Map<DewLegacyId, Integer> types = null;

  private Map<DewLegacyId, DewLegacyId> nodeCluster = null;
  private Map<DewLegacyId, DewLegacyId> nodeN1 = null;
  private Map<DewLegacyId, DewLegacyId> nodeN2 = null;
  
  private Map<Integer, String> feederNames = null;
  private Map<Integer, String> feederSerials = null;

  /**
   * Constructor
   */
  private DewModelFactory() {
  }

  @Override
  public DewModelImpl constructModel(Model model) {
    DewModelImpl dewModel = (model instanceof DewModelImpl) ? (DewModelImpl) model : constructDefaultDEWModel((ElectricPowerModel) model);
    return dewModel;
  }

  /**
   * Method for constructing a model state
   * 
   * @param state
   * @return
   */
  public ElectricPowerModel constructDEWModelState(ElectricPowerModel state) {
    DewModel model = constructModel(state);
    return model;
  }

  /**
   * Constructor a default dew model
   * 
   * @param model
   * @return
   */
  private DewModelImpl constructDefaultDEWModel(ElectricPowerModel model) {
    throw new RuntimeException("Error: DEWModelFactory::constructDefaultDEWModel not implemented");
  }

  /**
   * Set up the component data into sets
   * 
   * @throws DewException
   */
  private void setupComponentData(Dew engine) throws DewException {
    Collection<DewLegacyId> ids = engine.getComponentIds();

    // organize the component types
    substations = new HashSet<DewLegacyId>();
    busbars = new HashSet<DewLegacyId>();
    singleEndedDevices = new HashSet<DewLegacyId>();
    transformers = new HashSet<DewLegacyId>();
    capacitors = new HashSet<DewLegacyId>();
    lines = new HashSet<DewLegacyId>();
    switches = new HashSet<DewLegacyId>();
    protectiveDevices = new HashSet<DewLegacyId>();
    loads = new HashSet<DewLegacyId>();
    circuits = new HashSet<DewLegacyId>();
    
    types = new HashMap<DewLegacyId, Integer>();
    

    for (DewLegacyId key : ids) {      
      
      String type = engine.getComponentData(DewVariables.DEW_COMPONENT_TYPE_KEY, key, null).toString();
      int iType = Integer.parseInt(type);
      types.put(key,iType);
      
      switch (iType) {
      case 0: // circuit
        circuits.add(key);
        break;
      case 2: { // sectionalizer
        switches.add(key);
        break;
      }
      case 6: { // protective device
        protectiveDevices.add(key);
        break;
      }
      case 8: { // single ended device
        singleEndedDevices.add(key);
        break;
      }
      case 9: { // load
        loads.add(key);
        break;
      }
      case 16: { // transformer
        transformers.add(key);
        break;
      }
      case 32: { // capacitor
        capacitors.add(key);
        break;
      }
      case 40: { // capacitor
        capacitors.add(key);
        break;
      }
      case 65: { // line
        lines.add(key);
        break;
      }
      case 129: { // cable
        lines.add(key);
        break;
      }
      case 258: { // switch
        switches.add(key);
        break;
      }
      case 513: { // busbar
        busbars.add(key);
        break;
      }
      case 1024: { // circuit
        circuits.add(key);
        break;
      }
      case 1032: { // substation
        substations.add(key);
        break;
      }
      default: {
        System.err.println("Not handling component of type " + iType + " yet. The key is " + key);
        break;
      }
      }
    }
  }
  
  /**
   * Get feeder name information out of the data
   * @param engine
   * @throws DewException 
   */
  private void setupFeederNames(Dew engine) throws DewException {
    feederNames = new HashMap<Integer, String>();
    feederSerials = new HashMap<Integer, String>();

    for (DewLegacyId id : circuits) {
      Object fn = engine.getComponentData(DewVariables.DEW_BUS_NAME_KEY, id, null);
      Object s = engine.getComponentData(Bus.NAME_KEY, id, null);      
      String feederName = fn == null ? "" : fn.toString();
      String serial = s == null ? "" : s.toString();
      feederNames.put(id.getOne(), feederName);
      feederSerials.put(id.getOne(), serial);      
    }    
    
  }

  /**
   * DEW sets up its components where everything is connected to one or two
   * things, which causes buses to be split into many components so, here, we
   * are going to cluster everything back together
   * 
   * @throws DewException
   * @throws NumberFormatException
   */
  private void setupNodeClusters(Dew engine) throws NumberFormatException, DewException {
    nodeCluster = new HashMap<DewLegacyId, DewLegacyId>();
    nodeN1 = new HashMap<DewLegacyId, DewLegacyId>();
    nodeN2 = new HashMap<DewLegacyId, DewLegacyId>();

    HashSet<DewLegacyId> buses = new HashSet<DewLegacyId>();
    buses.addAll(busbars);
    buses.addAll(loads);
    buses.addAll(singleEndedDevices);
    buses.addAll(capacitors);
    buses.addAll(substations);
    buses.addAll(circuits);

    for (DewLegacyId id : buses) {
      nodeCluster.put(id, id);
      int n1 = Integer.parseInt(engine.getComponentData(DewVariables.DEW_CONNECTION1_KEY, id, "").toString());
      int n2 = Integer.parseInt(engine.getComponentData(DewVariables.DEW_CONNECTION2_KEY, id, "").toString());
      if (buses.contains(n1)) {
        nodeN1.put(id, new DewLegacyId(id.getOne(),n1));
      }

      if (buses.contains(n2)) {
        nodeN2.put(id, new DewLegacyId(id.getOne(),n2));
      }
    }
    
    // clustering algorithm.... could be done more efficiently I suppose
    boolean setChanged = true;
    while (setChanged) {
      setChanged = false;
      for (DewLegacyId id : nodeCluster.keySet()) {
        if (nodeN1.get(id) != null && nodeCluster.get(nodeN1.get(id)).getTwo() < nodeCluster.get(id).getTwo()) {
          setChanged = true;
          nodeCluster.put(id, nodeCluster.get(nodeN1.get(id)));
        }

        if (nodeN2.get(id) != null && nodeCluster.get(nodeN2.get(id)).getTwo() < nodeCluster.get(id).getTwo()) {
          setChanged = true;
          nodeCluster.put(id, nodeCluster.get(nodeN2.get(id)));
        }
      }
    }
  }

  /**
   * Creates a DEW model
   * 
   * @param engine
   * @return
   * @throws DewException
   */
  @SuppressWarnings("unchecked")
  public DewModelImpl createModel(Dew engine, Collection<DewPtlinespcData> ld,  Collection<DewPtlinecondData> lcd, Collection<DewPtcabcondData> ccd) throws DewException {
    DewModelImpl model = new DewModelImpl(engine);

    setupComponentData(engine);
    setupNodeClusters(engine);
    setupFeederNames(engine);
    
    // some book keep data structures for the line data
    Map<Integer, DewPtlinespcData> lineData = new HashMap<Integer, DewPtlinespcData>();
    for (DewPtlinespcData d : ld) {
      lineData.put(d.getIptrow(),d);
    }
    
    Map<Integer, DewPtlinecondData> lineCondData = new HashMap<Integer, DewPtlinecondData>();
    for (DewPtlinecondData d : lcd) {
      lineCondData.put(d.getIwire(),d);
    }

    Map<Integer, DewPtcabcondData> cabCondData = new HashMap<Integer, DewPtcabcondData>();
    for (DewPtcabcondData d : ccd) {
      cabCondData.put(d.getIcab(),d);
    }
    
    
    // some book-keeping data structures
    Map<DewLegacyId, Bus> buses = new HashMap<DewLegacyId, Bus>();

    // Factories
    DewBusFactory busFactory = model.getBusFactory();
    DewGeneratorFactory generatorFactory = model.getGeneratorFactory();
    DewLoadFactory loadFactory = model.getLoadFactory();
    DewTransformerFactory transformerFactory = model.getTransformerFactory();
    DewLineFactory lineFactory = model.getLineFactory();
    DewShuntFactory capacitorFactory = model.getShuntCapacitorFactory();

    // create buses for the clusters
//    System.out.println("Cluster Buses");

    for (DewLegacyId id : nodeCluster.keySet()) {
      if (nodeCluster.get(id).equals(id)) {
        Bus bus = busFactory.constructClusterBus(id, engine, lineData);
        model.addBus(bus);
        buses.put(id, bus);   
      }
    }

    // pull in substation related data
  //  System.out.println("substations");

    for (DewLegacyId id : substations) {
      Bus bus = buses.get(nodeCluster.get(id));
      Generator generator = generatorFactory.constructGeneratorFromSubstationData(id, engine, lineData);
      model.addGenerator(generator, bus);
    }
    
    // mark some busbars as being clustered
    for (DewLegacyId id : nodeCluster.keySet()) {
      if (!nodeCluster.get(id).equals(id)) {
        Bus bus = buses.get(nodeCluster.get(id));
        bus.getAttribute(DewVariables.DEW_LEGACY_IDS_KEY, Set.class).add(id);
      }
    }

    // these can be generators
    for (DewLegacyId id : singleEndedDevices) {
      Generator generator = generatorFactory.constructGenerator(id, engine, lineData);
      Bus bus = buses.get(nodeCluster.get(id));
      model.addGenerator(generator, bus);
    }

   // System.out.println("Loads");

    for (DewLegacyId id : loads) {
      Load load = loadFactory.constructLoad(id, engine, lineData);
      Bus bus = buses.get(nodeCluster.get(id));
      model.addLoad(load, bus);
    }

  //  System.out.println("Capacitors");

    for (DewLegacyId id : capacitors) {
      ShuntCapacitor capacitor = capacitorFactory.constructCapacitor(id, engine, lineData);
      Bus bus = buses.get(nodeCluster.get(id));
      model.addShuntCapacitor(capacitor, bus);
    }
        
    // note that transformers that have no connection should be considered
    // loads, and that data should come from the power flow analysis
    // ratings and impedances need to come from calculations
   // System.out.println("transformers");

    for (DewLegacyId id : transformers) {
                 
      int connection1 = Integer.parseInt(engine.getComponentData(DewVariables.DEW_CONNECTION_KEY, id, "").toString());      
      int connection2 = Integer.parseInt(engine.getComponentData(DewVariables.DEW_CONNECTION2_KEY, id, "").toString());
      
      DewLegacyId b1 = new DewLegacyId(id.getOne(), connection1);
      DewLegacyId b2 = new DewLegacyId(id.getOne(), connection2);
            
      
      if (nodeCluster.containsKey(b1)) {
        b1 = nodeCluster.get(b1);
      }
      if (nodeCluster.containsKey(b2)) {
        b2 = nodeCluster.get(b2);
      }

      if (b2.getTwo() >= 0) {
        b2 = id;
      }
            
      Bus bus1 = buses.get(b1);
      Bus bus2 = buses.get(b2);
      
      // connected upstream to a line.... need to create an upstream node for
      // b1 (the upstream)
      if (bus1 == null) {
          bus1 = busFactory.createVirtualBus(b1, engine, lineData);
          buses.put(b1, bus1);
          model.addBus(bus1);
          
          Load load = loadFactory.constructLoad(b1, engine, lineData);
          if (load.getDesiredRealLoad().doubleValue() > 0.0 || load.getDesiredReactiveLoad().doubleValue() > 0.0) {
            model.addLoad(load, bus1);
          }
          
      }

      // connected downstream to a line... need to create an upstream bus for
      // id (this line)
      if (bus2 == null) {
          bus2 = busFactory.createVirtualBus(id, engine, lineData);
          buses.put(id, bus2);
          model.addBus(bus2);
          
          Load load = loadFactory.constructLoad(id, engine, lineData);
          if (load.getDesiredRealLoad().doubleValue() > 0.0 || load.getDesiredReactiveLoad().doubleValue() > 0.0) {
            model.addLoad(load, bus2);
          }
          
        }

      Transformer transformer = transformerFactory.createTransformer(id, engine, lineData);
      model.addEdge(transformer, model.getNode(bus1), model.getNode(bus2));
      
      boolean hasSwitch = (protectiveDevices.contains(id) || switches.contains(id)) ? true : false;          
      transformer.setAttribute(Transformer.HAS_SWITCH_KEY, hasSwitch);      
    }


    // for now, all of these are treated the same....
    HashSet<DewLegacyId> lineCollection = new HashSet<DewLegacyId>();
    lineCollection.addAll(lines);
    lineCollection.addAll(switches);
    lineCollection.addAll(protectiveDevices);

   // System.out.println("lines");

    int i = 0;
    //System.out.println(lineCollection.size());
    for (DewLegacyId id : lineCollection) {
      ++i;
      int connection1 = Integer.parseInt(engine.getComponentData(DewVariables.DEW_CONNECTION_KEY, id, "").toString());  
      int connection2 = Integer.parseInt(engine.getComponentData(DewVariables.DEW_CONNECTION2_KEY, id, "").toString());
      
      int feederConnection = Integer.parseInt(engine.getComponentData(DewVariables.DEW_FEEDER_CONNECTION_KEY, id, "").toString());
      int feederConnectionId = Integer.parseInt(engine.getComponentData(DewVariables.DEW_FEEDER_CONNECTION_ID_KEY, id, "").toString());
            
      DewLegacyId b1 = new DewLegacyId(id.getOne(), connection1);
      DewLegacyId b2 = new DewLegacyId(id.getOne(), connection2);

      
      if (nodeCluster.containsKey(b1)) {
        b1 = nodeCluster.get(b1);
      }
      if (nodeCluster.containsKey(b2)) {
        b2 = nodeCluster.get(b2);
      }
      
      if (b2.getTwo() >= 0) {
        b2 = id;
      }
      
      Bus bus1 = buses.get(b1);
      Bus bus2 = buses.get(b2);
   
      
      // connected upstream to a line.... need to create an upstream node for
      // b1 (the upstream)
      if (bus1 == null && b1.getTwo() >= 0) {
          bus1 = busFactory.createVirtualBus(b1, engine, lineData);
          buses.put(b1, bus1);
          model.addBus(bus1);
          
          Load load = loadFactory.constructLoad(b1, engine, lineData);
          if (load.getDesiredRealLoad().doubleValue() > 0.0 || load.getDesiredReactiveLoad().doubleValue() > 0.0) {
            model.addLoad(load, bus1);
          }
      }

      // connected downstream to a line... need to create an upstream bus for
      // id (this line)
      if (bus2 == null && b2.getTwo() >= 0) {
          bus2 = busFactory.createVirtualBus(id, engine, lineData);
          buses.put(id, bus2);
          model.addBus(bus2);
          Load load = loadFactory.constructLoad(b2, engine, lineData);
          if (load.getDesiredRealLoad().doubleValue() > 0.0 || load.getDesiredReactiveLoad().doubleValue() > 0.0) {
            model.addLoad(load, bus2);
          }
          
      }

      // this is a connection between feeders.  This will get added twice, so only add if it is a switch or protective device
      
      if (feederConnection != -1) {
        if (switches.contains(id) || protectiveDevices.contains(id)) {
          DewLegacyId feeder = new DewLegacyId(feederConnection, feederConnectionId);
          Bus side1 = bus1 == null ? bus2 : bus1;
          Bus side2 = buses.get(feeder);
          if (side2 == null) {
            side2 = busFactory.createVirtualBus(feeder, engine, lineData);
            buses.put(feeder, side2);
            model.addBus(side2);   
            
            
            Load load = loadFactory.constructLoad(feeder, engine, lineData);
            if (load.getDesiredRealLoad().doubleValue() > 0.0 || load.getDesiredReactiveLoad().doubleValue() > 0.0) {
              model.addLoad(load, side2);
            }
            
          }
          
          // this adds the switch between the feeders
          Line line = lineFactory.createLine(id, engine, lineData, lineCondData, cabCondData, types.get(id));
          model.addEdge(line, model.getNode(side1), model.getNode(side2));
          
          boolean hasSwitch = (protectiveDevices.contains(id) || switches.contains(id)) ? true : false;          
          line.setAttribute(Transformer.HAS_SWITCH_KEY, hasSwitch);      
        }
        else {
          Bus side1 = bus1 == null ? bus2 : bus1;
          Bus side2 = buses.get(id);
          if (side2 == null) {
            side2 = busFactory.createVirtualBus(id, engine, lineData);
            buses.put(id, side2);
            model.addBus(side2);       
            
            
            Load load = loadFactory.constructLoad(id, engine, lineData);
            if (load.getDesiredRealLoad().doubleValue() > 0.0 || load.getDesiredReactiveLoad().doubleValue() > 0.0) {
              model.addLoad(load, side2);
            }
            
          }
          
          // this adds the line going back to the other node
          Line line = lineFactory.createLine(id, engine, lineData, lineCondData, cabCondData, types.get(id));
          model.addEdge(line, model.getNode(side1), model.getNode(side2));
          boolean hasSwitch = (protectiveDevices.contains(id) || switches.contains(id)) ? true : false;          
          line.setAttribute(Transformer.HAS_SWITCH_KEY, hasSwitch);      
        }
      } 
      
      
      // this is an end point, so just add it as a load
      else if (b1.getTwo() == -1 || b2.getTwo() == -1) {
        Bus bus = bus1 == null ? bus2 : bus1;
        if (model.getNode(bus).getLoad() == null) {
          Load load = loadFactory.constructEstimatedLoad(id, engine, lineData);
          model.addLoad(load, bus);
        }
      }
      
      else {
        Line line = lineFactory.createLine(id, engine, lineData, lineCondData, cabCondData, types.get(id));
        model.addEdge(line, model.getNode(bus1), model.getNode(bus2));
        boolean hasSwitch = (protectiveDevices.contains(id) || switches.contains(id)) ? true : false;          
        line.setAttribute(Transformer.HAS_SWITCH_KEY, hasSwitch);      
      }      
    } 
    
    // now connect nodes to line-nodes
    for (DewLegacyId id : nodeCluster.keySet()) {
      int connection1 = Integer.parseInt(engine.getComponentData(DewVariables.DEW_CONNECTION_KEY, id, "").toString());  

      DewLegacyId b1 = new DewLegacyId(id.getOne(), connection1);
      DewLegacyId b2 = id; //new DewLegacyId(id.getOne(), connection2);

      
      if (b1.getTwo() < 0) {
        continue;
      }
      
      if (nodeCluster.containsKey(b1)) {
        b1 = nodeCluster.get(b1);
      }
      if (nodeCluster.containsKey(b2)) {
        b2 = nodeCluster.get(b2);
      }
      
       
      Bus bus1 = buses.get(b1);
      Bus bus2 = buses.get(b2);
      
      if (bus1 == null) {
        System.err.println("Warning: Missing bus " + b1 + ". Skipping adding a line, may have unintended consequences,");
        continue;
      }
      
      if (bus2 == null) {
        System.err.println("Warning: Missing bus " + b2 + ". Skipping adding a line, may have unintended consequences,");
        continue;
      }

      
      if (bus1.equals(bus2)) {
        continue;
      }
      
      Line line = lineFactory.createLine(id, engine, lineData, lineCondData, cabCondData, types.get(id));
      model.addEdge(line, model.getNode(bus1), model.getNode(bus2));
      boolean hasSwitch = (protectiveDevices.contains(id) || switches.contains(id)) ? true : false;          
      line.setAttribute(Transformer.HAS_SWITCH_KEY, hasSwitch);      
    }
    
    
    // here comes a hack.... it appears that the first entry (labled 1) should be connected back to the substation
    // and that connection does not appear explicitly in the the .dew files... so let us hack it in here 
    for (DewLegacyId id : nodeCluster.keySet()) {
      if (id.getTwo() == 1) {
        DewLegacyId b1 = id;        
        
        int feeder = Integer.parseInt(engine.getComponentData(DewVariables.DEW_SUBSTATION_KEY, id, "").toString());;
        
        DewLegacyId b2 = new DewLegacyId(feeder, 0); // this should be the substation indicator....
        
        if (nodeCluster.containsKey(b1)) {
          b1 = nodeCluster.get(b1);
        }
        if (nodeCluster.containsKey(b2)) {
          b2 = nodeCluster.get(b2);
        }
        
         
        Bus bus1 = buses.get(b1);
        Bus bus2 = buses.get(b2);

        Line line = lineFactory.createLine(bus1, bus2, new DewLegacyId(id.getOne(), 0));
        model.addEdge(line, model.getNode(bus1), model.getNode(bus2));
        boolean hasSwitch = (protectiveDevices.contains(id) || switches.contains(id)) ? true : false;          
        line.setAttribute(Transformer.HAS_SWITCH_KEY, hasSwitch);      
      }
    }
    
    // here comes another hack to get feeder level labeling onto the data
    for (Asset asset : model.getAssets()) {
      String feederName = feederNames.get(asset.getAttribute(DewVariables.DEW_LEGACY_ID_KEY, DewLegacyId.class).getOne());
      String feederSerial = feederSerials.get(asset.getAttribute(DewVariables.DEW_LEGACY_ID_KEY, DewLegacyId.class).getOne());
      asset.setAttribute(DewVariables.DEW_FEEDER_NAME_KEY, feederName);
      asset.setAttribute(DewVariables.DEW_FEEDER_SERIAL_KEY, feederSerial);      
    }
    
    
    return model;
  }

}
