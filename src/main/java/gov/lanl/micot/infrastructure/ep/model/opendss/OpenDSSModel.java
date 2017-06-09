package gov.lanl.micot.infrastructure.ep.model.opendss;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.StringTokenizer;

import gov.lanl.micot.infrastructure.ep.io.opendss.OpenDSSData;
import gov.lanl.micot.infrastructure.ep.io.opendss.OpenDSSIOConstants;
import gov.lanl.micot.infrastructure.ep.model.Battery;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.DCLine;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModelImpl;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModelListener;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Intertie;
import gov.lanl.micot.infrastructure.ep.model.Line;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitor;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitorSwitch;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.util.geometry.PointImpl;
import gov.lanl.micot.util.io.dcom.ComObject;

/**
 * Definition of an OpenDSS model
 *  
 * @author Russell Bent
 */
public class OpenDSSModel extends ElectricPowerModelImpl implements ElectricPowerModelListener {
	
	private ComObject openDSSModel = null;
		
	/**
	 * Constructor
	 */
	public OpenDSSModel(ComObject openDSSModel) {
		super();
		init(openDSSModel);
		addModelListener(this);
	}
	
	 /**
   * Constructor
   */
  public OpenDSSModel(OpenDSSData openDSSModel) {
    super();
    init(openDSSModel);
  }
	
	/**
	 * Constructor
	 */
	private OpenDSSModel() {
	  super();
	  addModelListener(this);
	}
	
	public void finalize() {
	  openDSSModel.close();
	}
	
	/**
	 * Fill in all the attributes of the implementation
	 * @param opendss model
	 */
	private void init(ComObject openDSSModel) {
		this.openDSSModel = openDSSModel;

    setLineFactory(new OpenDSSLineFactory());
    setTransformerFactory(new OpenDSSTransformerFactory());
    setDCLineFactory(null);
    setIntertieFactory(new OpenDSSIntertieFactory());
    setBusFactory(new OpenDSSBusFactory());
    setLoadFactory(new OpenDSSLoadFactory());
    setGeneratorFactory(new OpenDSSGeneratorFactory());
    setCapacitorFactory(new OpenDSSShuntCapacitorFactory());
    setShuntCapacitorSwitchFactory(new OpenDSSShuntCapacitorSwitchFactory());
    setBatteryFactory(new OpenDSSBatteryFactory());
				
		ComObject circuit = openDSSModel.call(OpenDSSIOConstants.MODEL);
    Map<String,Bus> busMap = new HashMap<String,Bus>();   
	    
    int numBuses = circuit.getInteger(OpenDSSIOConstants.NUMBER_OF_BUSES);    
		// get the buses
		for (int i = 0; i < numBuses; ++i) {
		  ComObject localbus = circuit.call(OpenDSSIOConstants.BUSES, i);
		  ComObject activeElement = circuit.call(OpenDSSIOConstants.ELEMENT);     
		  Bus bus = getBusFactory().createBus(localbus, activeElement);
		  addBus(bus);
		  busMap.put(getBusId(bus), bus);		  
		}

		
    // get the source generator 
		/**
		 * OpenDSS Dcom does not have functionality to access the "vsource" which is the actual source. If we explict name the source bus
		 * "sourcebus" this will work.  The alternative is to create a generator in the dss file and add it there.
		 */		
		Bus sourceBus = busMap.get("sourcebus");
		if (sourceBus != null) {
		  Generator sourceGenerator = getGeneratorFactory().createSourceGenerator(sourceBus);
		  addGenerator(sourceGenerator, sourceBus);           
		}
		
    // get the generators
    ComObject generators = circuit.call(OpenDSSIOConstants.GENERATORS);
    int numGenerators = generators.getInteger(OpenDSSIOConstants.NUMBER_OF_GENERATORS);
    generators.getInteger(OpenDSSIOConstants.FIRST_GENERATOR);        
    for (long i = 0; i < numGenerators; ++i) {
      ComObject activeElement = circuit.call(OpenDSSIOConstants.ELEMENT);    
      ComObject property = activeElement.call(OpenDSSIOConstants.PROPERTIES, OpenDSSIOConstants.GENERATOR_BUS);
      String busName = property.getString(OpenDSSIOConstants.PROPERTY_VALUE);
      if (busName.contains(".")) {
        busName = busName.substring(0, busName.indexOf("."));
      }
      Bus bus = busMap.get(busName.toLowerCase());      
      Generator generator = getGeneratorFactory().createGenerator(generators, bus, activeElement);
      addGenerator(generator, bus);           
      generators.getInteger(OpenDSSIOConstants.NEXT_GENERATOR);          
    }    
		
		// get the loads
		ComObject loads = circuit.call(OpenDSSIOConstants.LOADS);
    int numLoads = loads.getInteger(OpenDSSIOConstants.NUMBER_OF_LOADS);
		for (long i = 1; i <= numLoads; ++i) {
		  loads.put(OpenDSSIOConstants.LOAD_IDX, i);
		  
		  ComObject activeElement = circuit.call(OpenDSSIOConstants.ELEMENT);		 
		  ComObject property = activeElement.call(OpenDSSIOConstants.PROPERTIES, OpenDSSIOConstants.LOAD_BUS);
		  String busName = property.getString(OpenDSSIOConstants.PROPERTY_VALUE);
		  if (busName.contains(".")) {
		    busName = busName.substring(0, busName.indexOf("."));
		  }
		  Bus bus = busMap.get(busName.toLowerCase());		  
		  Load load = getLoadFactory().createLoad(loads, bus, activeElement);
		  addLoad(load, bus);		  		  
		}    
		
		// TODO get the batteries
				
		// TODO get the capacitors
		
		// get the lines
    ComObject lines = circuit.call(OpenDSSIOConstants.LINES);
    int numLines = lines.getInteger(OpenDSSIOConstants.NUMBER_OF_LINES);
    lines.getInteger(OpenDSSIOConstants.FIRST_LINE);    
    for (int i = 0; i < numLines; ++i) {
      ComObject activeElement = circuit.call(OpenDSSIOConstants.ELEMENT);    
      String fromBusName = lines.getString(OpenDSSIOConstants.LINE_FROM_BUS);
      String toBusName = lines.getString(OpenDSSIOConstants.LINE_TO_BUS);

      if (fromBusName.contains(".")) {
        fromBusName = fromBusName.substring(0, fromBusName.indexOf("."));
      }
      if (toBusName.contains(".")) {
        toBusName = toBusName.substring(0, toBusName.indexOf("."));
      }

      Bus fromBus = busMap.get(fromBusName);
      Bus toBus = busMap.get(toBusName);
      Line line = getLineFactory().createLine(lines, fromBus, toBus, activeElement);
      addEdge(line, getNode(fromBus), getNode(toBus));
      lines.getInteger(OpenDSSIOConstants.NEXT_LINE);          
    }    
		
		// get the transformers
    ComObject transformers = circuit.call(OpenDSSIOConstants.TRANSFORMERS);
    int numTransformers = transformers.getInteger(OpenDSSIOConstants.NUMBER_OF_TRANSFORMERS);
    transformers.getInteger(OpenDSSIOConstants.FIRST_TRANSFORMER);    
    for (int i = 0; i < numTransformers; ++i) {
      ComObject activeElement = circuit.call(OpenDSSIOConstants.ELEMENT);    
      ComObject property = activeElement.call(OpenDSSIOConstants.PROPERTIES, OpenDSSIOConstants.TRANSFORMER_BUSES);     
      String busNames = property.getString(OpenDSSIOConstants.PROPERTY_VALUE);
      busNames = busNames.substring(1,busNames.length()-2);
      
      StringTokenizer tokenizer = new StringTokenizer(busNames, ",");
      String fromBusName = tokenizer.nextToken().trim();
      String toBusName = tokenizer.nextToken().trim();
   
      if (fromBusName.contains(".")) {
        fromBusName = fromBusName.substring(0, fromBusName.indexOf("."));
      }
      if (toBusName.contains(".")) {
        toBusName = toBusName.substring(0, toBusName.indexOf("."));
      }

      Bus fromBus = busMap.get(fromBusName);
      Bus toBus = busMap.get(toBusName);
      Transformer transformer = getTransformerFactory().createTransformer(transformers, fromBus, toBus, activeElement);
      addEdge(transformer, getNode(fromBus), getNode(toBus));
      transformers.getInteger(OpenDSSIOConstants.NEXT_TRANSFORMER);          
    }    		
    

	}

	
		
	 /**
   * Fill in all the attributes of the implementation
   * @param opendss model
   */
  private void init(OpenDSSData openDSSModel) {
    this.openDSSModel = null;

    // there is no explict declaration of a bus (node) data, for now I am going to assume
    // there is always coordinate data which gives us bus data.  If this breaks somewhere down
    // the line, we will need to infer bus existence from the edge data
    Map<String,Bus> busMap = new HashMap<String,Bus>(); 
    for (String id : openDSSModel.getBusCoordIds()) {
      Bus bus = getBusFactory().createBus(id, openDSSModel.getBusCoordData(id));
      addBus(bus);
      busMap.put(getBusId(bus), bus);
    }

    // get the source generators 
    for (String id : openDSSModel.getVsourceIds()) {
      String generatorData = openDSSModel.getVSourceData(id);
      String busid = OpenDSSIOConstants.getData(generatorData, OpenDSSIOConstants.GENERATOR_BUS, "=");
      Bus sourceBus = busMap.get(busid);      
      Generator sourceGenerator = getGeneratorFactory().createSourceGenerator(sourceBus, generatorData);
      addGenerator(sourceGenerator, sourceBus);       
      double systemKV = (OpenDSSIOConstants.getData(generatorData, OpenDSSIOConstants.BUS_KVBASE, "=") == null ? -1.0 : Double.parseDouble(OpenDSSIOConstants.getData(generatorData, OpenDSSIOConstants.BUS_KVBASE, "=")));      
      sourceBus.setSystemVoltageKV(systemKV);      
    }
        
    // get the generators
    // TODO removed the following, it is because of IEEE 37 not having Generators.DSS
    if (openDSSModel.getGeneratorIds() != null) {
	    for (String id : openDSSModel.getGeneratorIds()) {
	      String generatorData = openDSSModel.getGeneratorData(id);
	      String busid = OpenDSSIOConstants.getData(generatorData, OpenDSSIOConstants.GENERATOR_BUS, "=");
	      Bus bus = busMap.get(busid.toLowerCase());      
	      Generator generator = getGeneratorFactory().createGenerator(generatorData, bus);
	      addGenerator(generator, bus);           
	      double systemKV = (OpenDSSIOConstants.getData(generatorData, OpenDSSIOConstants.BUS_KVBASE, "=") == null ? -1.0 : Double.parseDouble(OpenDSSIOConstants.getData(generatorData, OpenDSSIOConstants.BUS_KVBASE, "=")));      
	      bus.setSystemVoltageKV(systemKV);            
	    }   
    }
    
    // get the loads
    for (String id : openDSSModel.getLoadIds()) {
      String loadData = openDSSModel.getLoadData(id);
      String busid = OpenDSSIOConstants.getData(loadData, OpenDSSIOConstants.LOAD_BUS, "=");
      if (busid == null) {
    	  continue;
      }
      // need to strip off the trailing "."'s that indicate the phase the load is connected to
      int idx = busid.indexOf(".");
      if (idx >= 0) {
    	// idx-1 omits the previous char before "."
        busid = busid.substring(0, idx);
      }
      // TODO following returns null for bus
      Bus bus = busMap.get(busid.toLowerCase());
      Load load = getLoadFactory().createLoad(loadData, bus);
      addLoad(load, bus);           
      double systemKV = (OpenDSSIOConstants.getData(loadData, OpenDSSIOConstants.BUS_KVBASE, "=") == null ? -1.0 : Double.parseDouble(OpenDSSIOConstants.getData(loadData, OpenDSSIOConstants.BUS_KVBASE, "=")));      
      bus.setSystemVoltageKV(systemKV);            
    }    
    
    
    // get the lines
    for (String id : openDSSModel.getLineIds()) {
      String lineData = openDSSModel.getLineData(id);
      String busfromid = OpenDSSIOConstants.getData(lineData, OpenDSSIOConstants.LINE_FROM_BUS, "=");
      String bustoid = OpenDSSIOConstants.getData(lineData, OpenDSSIOConstants.LINE_TO_BUS, "=");
 
      if (busfromid == null || bustoid == null) {
    	  continue;
      }
      int idx = busfromid.indexOf(".");
      if (idx >= 0) {
        busfromid = busfromid.substring(0, idx);
      }
      
      idx = bustoid.indexOf(".");
      if (idx >= 0) {
        bustoid = bustoid.substring(0, idx);
      }

      Bus frombus = busMap.get(busfromid.toLowerCase());      
      Bus tobus = busMap.get(bustoid.toLowerCase());      
            
      String linecode = openDSSModel.getLineCodeData(OpenDSSIOConstants.getData(lineData,OpenDSSIOConstants.LINE_CODE, "="));      
      Line line = getLineFactory().createLine(lineData, frombus, tobus, linecode);
      addEdge(line, getNode(frombus), getNode(tobus));
    }    
    
    // get the transformers
    for (String id : openDSSModel.getTransformerIds()) {
      String transformerData = openDSSModel.getTransformerData(id);
      //String busfromid = getData(transformerData, OpenDSSIOConstants.LINE_FROM_BUS);
      //String bustoid = getData(transformerData, OpenDSSIOConstants.LINE_TO_BUS);
      // TODO following is hardcoded as the string comes out with buses=(frombus tobus) with a space in the middle
      String buses = transformerData.substring(transformerData.indexOf("buses=")+6, transformerData.length());
      int idx_space = buses.indexOf(" ");
      int idx_end = (buses.charAt(0) == '[' ? buses.indexOf("]") : buses.indexOf(")"));
      String busfromid = buses.substring(1, idx_space);
      String bustoid = buses.substring(idx_space+1, idx_end);
 
      int idx = busfromid.indexOf(".");
      if (idx >= 0) {
        busfromid = busfromid.substring(0, idx);
      }
      
      idx = bustoid.indexOf(".");
      if (idx >= 0) {
        bustoid = bustoid.substring(0, idx);
      }
      
      idx = busfromid.indexOf(",");
      if (idx >= 0) {
        busfromid = busfromid.substring(0, idx);
      }
      
      idx = bustoid.indexOf(",");
      if (idx >= 0) {
        bustoid = bustoid.substring(0, idx);
      }      

      Bus frombus = busMap.get(busfromid.toLowerCase());      
      Bus tobus = busMap.get(bustoid.toLowerCase());      
      // Following handles if the transformer ends do not exists in the busMap
      if (frombus == null) {
          frombus = getBusFactory().createBus(busfromid, new PointImpl(0.0,0.0));
          addBus(frombus);
          busMap.put(getBusId(frombus), frombus);
      }
      if (tobus == null) {
          tobus = getBusFactory().createBus(bustoid, new PointImpl(0.0,0.0));
          addBus(tobus);
          busMap.put(getBusId(tobus), tobus);    	  
      }
      
      String linecode = openDSSModel.getLineCodeData(OpenDSSIOConstants.getData(transformerData,OpenDSSIOConstants.LINE_CODE, "="));      
      Transformer line = getTransformerFactory().createTransformer(transformerData, frombus, tobus, linecode);
      addEdge(line, getNode(frombus), getNode(tobus));
    }       
  }
	
	@Override
	public OpenDSSBusFactory getBusFactory() {
	  return (OpenDSSBusFactory) super.getBusFactory();
	}

	@Override
	public OpenDSSGeneratorFactory getGeneratorFactory() {		
    return (OpenDSSGeneratorFactory) super.getGeneratorFactory();
	}

	@Override
	public OpenDSSBatteryFactory getBatteryFactory() {		
    return (OpenDSSBatteryFactory) super.getBatteryFactory();
	}

	@Override
	public OpenDSSIntertieFactory getIntertieFactory() {
    return (OpenDSSIntertieFactory) super.getIntertieFactory();
	}

	@Override
	public OpenDSSLineFactory getLineFactory() {
    return (OpenDSSLineFactory) super.getLineFactory();
	}

	@Override
	public OpenDSSLoadFactory getLoadFactory() {
    return (OpenDSSLoadFactory) super.getLoadFactory();
	}

	@Override
	public OpenDSSShuntCapacitorFactory getShuntCapacitorFactory() {
    return (OpenDSSShuntCapacitorFactory) super.getShuntCapacitorFactory();
	}

	@Override
	public OpenDSSShuntCapacitorSwitchFactory getShuntCapacitorSwitchFactory() {
    return (OpenDSSShuntCapacitorSwitchFactory) super.getShuntCapacitorSwitchFactory();
	}

	@Override
	public OpenDSSTransformerFactory getTransformerFactory() {
    return (OpenDSSTransformerFactory) super.getTransformerFactory();
	}

	/**
	 * Finds a particular link
	 * @param node1
	 * @param node2
	 * @param circuit
	 * @return
	 */
	protected ElectricPowerFlowConnection getFlowLink(ElectricPowerNode node1, ElectricPowerNode node2, Object id) {
	  Collection<ElectricPowerFlowConnection> links = getFlowEdges(node1, node2);
	  for (ElectricPowerFlowConnection line : links) {
	    if (line instanceof Transformer) {
	      if (((Transformer)line).getCircuit().equals(id)) {
	        return line;
	      }
	    }
	    else if (line instanceof Line) {
	       if (((Line)line).getCircuit().equals(id)) {
	         return line;
	       }
	     }
	  }
	  return null;
	}

	@Override
	public void busAdded(Bus bus) {
	  throw new RuntimeException("OpenDSSModel::busAdded");
	  // TODO
		//ieissModel.addEntity(getIeissBus(bus));
	}

	@Override
	public void busRemoved(Bus bus) {
	  throw new RuntimeException("OpenDSSModel::busRemoved");
	  // TODO
//		ieissModel.removeEntity(getBusId(bus));		
	}

	@Override
	public void generatorAdded(Generator generator) {
	   throw new RuntimeException("OpenDSSModel::generatorAdded");
	   // TODO
	  /*ieiss.domains.base.entities.Producer producer = getIeissGenerator(generator);
	  NodeAttachment attachment = (NodeAttachment)producer;
    ieissModel.addEntity(attachment);
    Bus ieissBus = getNode(generator).getBus();
    Entity connect = ieissModel.getEntityById(getBusId(ieissBus));
    if (!connect.neighbors().contains(producer)) {
      Entity.connect(getIeissBus(ieissBus), attachment, Entity.ANY_SIDE);
    } */    	  
	}

	@Override
	public void batteryAdded(Battery battery) {
	   throw new RuntimeException("OpenDSSModel::batteryAdded");
	   //TODO
	  
	  /*ieiss.domains.base.entities.Producer producer = getIeissBattery(battery);
	  NodeAttachment attachment = (NodeAttachment)producer;
    ieissModel.addEntity(attachment);
    Bus ieissBus = getNode(battery).getBus();
    Entity connect = ieissModel.getEntityById(getBusId(ieissBus));
    if (!connect.neighbors().contains(producer)) {
      Entity.connect(getIeissBus(ieissBus), attachment, Entity.ANY_SIDE);
    } */    	  
	}
	
	@Override
	public void generatorRemove(Generator generator) {
	   throw new RuntimeException("OpenDSSModel::generatorRemove");
	   // TODO
		//ieissModel.removeEntity(getGeneratorId(generator));		
	}

	@Override
	public void batteryRemove(Battery battery) {
	  throw new RuntimeException("OpenDSSModel::batteryRemove");
	  //TODO
		//ieissModel.removeEntity(getBatteryId(battery));		
	}
	
	@Override
	public void linkAdded(ElectricPowerConnection link) {	
	  throw new RuntimeException("OpenDSSModel::linkAdded");
    //TODO
    
	  
	/*	Pair<ElectricPowerNode,ElectricPowerNode> nodes = getOrderedNodes(link);
		Bus ieissBus1 = nodes.getOne().getBus();
		Bus ieissBus2 = nodes.getTwo().getBus();
		
		  // do the connections
		  ElectricPowerLink ieissLink = link.getAttribute(IeissModelConstants.IEISS_COMPONENT_KEY, ElectricPowerLink.class);
      ieissModel.addEntity(ieissLink);
      Entity connect1 = ieissModel.getEntityById(getBusId(ieissBus1));
      Entity connect2 = ieissModel.getEntityById(getBusId(ieissBus2));
      if (!connect1.neighbors().contains(ieissLink)) {
        Entity.connect(connect1, ieissLink, Entity.LEFT_SIDE);
        Entity.connect(connect2, ieissLink, Entity.RIGHT_SIDE);
      }*/
	}

	@Override
	public void linkRemoved(ElectricPowerConnection link) {
	  throw new RuntimeException("OpenDSSModel::linkRemoved");
    //TODO
    
	  
	    /*ElectricPowerLink ieissLink = link.getAttribute(IeissModelConstants.IEISS_COMPONENT_KEY, ElectricPowerLink.class);
      ieissModel.removeEntity(ieissLink.getId());
      ieissLink.getNodes()[Entity.LEFT_SIDE] = null;
      ieissLink.getNodes()[Entity.RIGHT_SIDE] = null;*/
	}

	@Override
	public void loadAdded(Load load) {
	  throw new RuntimeException("OpenDSSModel::loadAdded");
    //TODO
    
	  
		/*ieissModel.addEntity(getIeissLoad(load));
		Bus ieissBus = getNode(load).getBus();
		Entity connect = ieissModel.getEntityById(getBusId(ieissBus));
		if (!connect.neighbors().contains(getIeissLoad(load))) {
			Entity.connect(getIeissBus(ieissBus), getIeissLoad(load), Entity.ANY_SIDE);
		}*/
	}

	@Override
	public void loadRemoved(Load load) {
	  throw new RuntimeException("OpenDSSModel::loadRemoved");
    //TODO    	  
		//ieissModel.removeEntity(getLoadId(load));		
	}

	@Override
	public void shuntCapacitorAdded(ShuntCapacitor shunt) {
	  throw new RuntimeException("OpenDSSModel::shuntCapacitorAdded");
    //TODO
     
		/*ieissModel.addEntity(getIeissCapacitor(shunt));
		Bus ieissBus = getNode(shunt).getBus();
		Entity connect = ieissModel.getEntityById(getBusId(ieissBus));
		if (!connect.neighbors().contains(getIeissCapacitor(shunt))) {
			Entity.connect(getIeissBus(ieissBus), getIeissCapacitor(shunt), Entity.ANY_SIDE);
		}*/
	}

	@Override
	public void shuntCapacitorRemoved(ShuntCapacitor shunt) {
	   throw new RuntimeException("OpenDSSModel::shuntCapacitorRemoved");
	    //TODO
		//ieissModel.removeEntity(getCapacitorId(shunt));				
	}

	@Override
	public void shuntCapacitorSwitchAdded(ShuntCapacitorSwitch shunt) {
	}

	@Override
	public void shuntCapacitorSwitchRemoved(ShuntCapacitorSwitch shunt) {
	}

  /**
   * @return the ieissModel
   */
  public ComObject getOpenDSSModel() {
    return openDSSModel;
  }

  /**
   * Sync the underling IEISS model with the attributes stored in the date and the top
   */
  public void syncModel() {
    syncTopology();
    
    // sync the models
    for (Generator generator : getGenerators()) {
      sync(generator);
    }
    for (Battery battery : getBatteries()) {
      sync(battery);
    }
    for (Load load : getLoads()) {
      sync(load);
    }
    for (ShuntCapacitor capacitor : getShuntCapacitors()) {
      sync(capacitor);
    }
    for (Line line : getLines()) {
      sync(line);
    }
    for (Transformer transformer : getTransformers()) {
      sync(transformer);
    }
    for (Bus bus : getBuses()) {
      sync(bus);
    }

  }
  
  /**
   * Sync the bus with its underlying ieiss model
   * @param bus
   */
  private void sync(Bus bus) {
    String name = getBusId(bus);
    ComObject circuit = openDSSModel.call(OpenDSSIOConstants.MODEL);
    
    ComObject co = null;
    ComObject activeElement = null;
    
    int numBuses = circuit.getInteger(OpenDSSIOConstants.NUMBER_OF_BUSES);    
    // get the buses
    for (int i = 0; i < numBuses; ++i) {
      ComObject localbus = circuit.call(OpenDSSIOConstants.BUSES, i);
      String thisName = localbus.getString(OpenDSSIOConstants.LOAD_NAME); 
      activeElement = circuit.call(OpenDSSIOConstants.ELEMENT);  
      
      if (thisName.equals(name)) {
        co = localbus;
        break;
      }
    }
    
    boolean status = bus.getDesiredStatus();

    activeElement.put(OpenDSSIOConstants.BUS_STATUS, status);
  }
  
  /**
   * Sync the shunt with its underlying ieiss model
   * @param shunt
   */
  public void sync(ShuntCapacitor shunt) {
    throw new RuntimeException("OpenDSSModel::Sync");
    //TODO

    
   /* ieiss.domains.ep.entities.Capacitor ieissCapacitor = getIeissCapacitor(shunt);

    ieissCapacitor.setProductionRate(new ComplexNumber(shunt.getRealCompensation(),shunt.getReactiveCompensation()));
    ieissCapacitor.setInitialProductionRate(new ComplexNumber(shunt.getRealCompensation(),shunt.getReactiveCompensation()));
    ieissCapacitor.setStatus(shunt.getDesiredStatus() ? Status.operating : Status.off);
    ieissCapacitor.setActive(shunt.getDesiredStatus());      
    Point3d[] points = new Point3d[1];
    points[0] = new Point3d(shunt.getCoordinate().getX(),shunt.getCoordinate().getY(),0);
    ieissCapacitor.setLocation(points);*/
  }
  
  /**
   * Sync the load with its underlying load model
   * @param shunt
   */
  public void sync(Load load) {        
    String name = getLoadId(load);
    ComObject circuit = openDSSModel.call(OpenDSSIOConstants.MODEL);
    
    ComObject loads = circuit.call(OpenDSSIOConstants.LOADS);
    int numLoads = loads.getInteger(OpenDSSIOConstants.NUMBER_OF_LOADS);
    ComObject co = null;
    ComObject activeElement = null;
    
    for (long i = 1; i <= numLoads; ++i) {
      loads.put(OpenDSSIOConstants.LOAD_IDX, i);
      String thisName = loads.getString(OpenDSSIOConstants.LOAD_NAME); 
      activeElement = circuit.call(OpenDSSIOConstants.ELEMENT);    
      if (thisName.equals(name)) {
        co = loads;
        break;
      }
    }    
    
    double reactive = load.getDesiredReactiveLoad().doubleValue() * 1000.0;
    double real = load.getDesiredRealLoad().doubleValue() * 1000.0;
    boolean status = load.getDesiredStatus();
          
    co.put(OpenDSSIOConstants.LOAD_KVAR, reactive);
    co.put(OpenDSSIOConstants.LOAD_KW, real);
    activeElement.put(OpenDSSIOConstants.LOAD_STATUS, status);
  }

  /**
   * Sync the line with the underlying line model
   * @param line
   */
  public void sync(Line line) {
    String name = getLineId(line);
    ComObject circuit = openDSSModel.call(OpenDSSIOConstants.MODEL);
        
    ComObject lines = circuit.call(OpenDSSIOConstants.LINES);
    int numLines = lines.getInteger(OpenDSSIOConstants.NUMBER_OF_LINES);
    lines.getInteger(OpenDSSIOConstants.FIRST_LINE);
    ComObject co = null;
    ComObject activeElement = null;
    
    for (int i = 0; i < numLines; ++i) {
      String thisName = lines.getString(OpenDSSIOConstants.LINE_NAME); 
      if (thisName.equals(name)) {
        co = lines;
        activeElement = circuit.call(OpenDSSIOConstants.ELEMENT);
      }
      lines.getInteger(OpenDSSIOConstants.NEXT_LINE);          
    }    
    
    
    boolean status = line.getDesiredStatus();
    
    
    // TODO, set some of the other attributes
    activeElement.put(OpenDSSIOConstants.LINE_STATUS, status);
  }
  
  /**
   * Sync the line with the underlying line model
   * @param line
   */
  public void sync(Transformer transformer) {
    String name = getTransformerId(transformer);
    ComObject circuit = openDSSModel.call(OpenDSSIOConstants.MODEL);
    
    ComObject co = null;
    ComObject activeElement = null;
    
    ComObject transformers = circuit.call(OpenDSSIOConstants.TRANSFORMERS);
    int numTransformers = transformers.getInteger(OpenDSSIOConstants.NUMBER_OF_TRANSFORMERS);
    transformers.getInteger(OpenDSSIOConstants.FIRST_TRANSFORMER);    
    for (int i = 0; i < numTransformers; ++i) {
      String thisName = transformers.getString(OpenDSSIOConstants.TRANSFORMER_NAME); 
      if (thisName.equals(name)) {
        co = transformers;
        activeElement = circuit.call(OpenDSSIOConstants.ELEMENT);
      }
      transformers.getInteger(OpenDSSIOConstants.NEXT_TRANSFORMER);          
    }       

    boolean status = transformer.getDesiredStatus();
    // TODO, set some of the other attributes
    activeElement.put(OpenDSSIOConstants.TRANSFORMER_STATUS, status);
 
   }
  
  /**
   * Sync the generator with its underlying generator model
   * @param shunt
   */
  public void sync(Generator generator) {
    String name = getGeneratorId(generator);
    
    // nothing to sync if this is the slack bus
    if (!name.equals("source-sourcebus")) {
      ComObject circuit = openDSSModel.call(OpenDSSIOConstants.MODEL);
      ComObject generators = circuit.call(OpenDSSIOConstants.GENERATORS);
      int numGenerators = generators.getInteger(OpenDSSIOConstants.NUMBER_OF_GENERATORS);
      generators.getInteger(OpenDSSIOConstants.FIRST_GENERATOR);
      ComObject co = null;
      ComObject activeElement = null;
      for (long i = 0; i < numGenerators; ++i) {
        String thisName = generators.getString(OpenDSSIOConstants.GENERATOR_NAME);
        activeElement = circuit.call(OpenDSSIOConstants.ELEMENT);    
        if (thisName.equals(name)) {
          co = generators;
          break;
        }
        generators.getInteger(OpenDSSIOConstants.NEXT_GENERATOR);          
      }    
      
      double reactive = generator.getDesiredReactiveGeneration().doubleValue() * 1000.0;
      double real = generator.getDesiredRealGeneration().doubleValue() * 1000.0;
      boolean status = generator.getDesiredStatus();
      
      co.put(OpenDSSIOConstants.GENERATOR_KVAR, reactive);
      co.put(OpenDSSIOConstants.GENERATOR_KW, real);
      activeElement.put(OpenDSSIOConstants.GENERATOR_STATUS, status);
    }
  }

  /**
   * Sync the battery with its underlying generator model
   * @param shunt
   */
  public void sync(Battery battery) {
    throw new RuntimeException("OpenDSSModel::sync battery");
  }

  
  /**
   * Side effect of the ieiss models.. the entities store the connectivity, so sometimes we need to ensure
   * that the connectivity is in sync with the model
   */
	private void syncTopology() {
	  ComObject circuit = openDSSModel.call(OpenDSSIOConstants.MODEL);
	  
	  // check buses
	  HashSet<String> busNames = new HashSet<String>();
    int numBuses = circuit.getInteger(OpenDSSIOConstants.NUMBER_OF_BUSES);    
    for (int i = 0; i < numBuses; ++i) {
      ComObject localbus = circuit.call(OpenDSSIOConstants.BUSES, i);
      String name = localbus.getString(OpenDSSIOConstants.BUS_NAME, new Object[0]);    
      busNames.add(name);
    }	  
    HashMap<String, Bus> containedBuses = new HashMap<String,Bus>();
    for (Bus bus : getBuses()) {
      if (!busNames.contains(getBusId(bus))) {
        throw new RuntimeException("Currently can't sync topology by adding buses to a com object");
      }      
      containedBuses.put(getBusId(bus), bus);
    }
    for (String name : busNames) {
      if (!containedBuses.containsKey(name)) {
        throw new RuntimeException("Currently can't sync topology by removing buses to a com object");
      }
    }
    
    
    // check the generators
    HashSet<String> generatorNames = new HashSet<String>();
    ComObject generators = circuit.call(OpenDSSIOConstants.GENERATORS);
    int numGenerators = generators.getInteger(OpenDSSIOConstants.NUMBER_OF_GENERATORS);
    generators.getInteger(OpenDSSIOConstants.FIRST_GENERATOR);        
    for (long i = 0; i < numGenerators; ++i) {          
      String name = generators.getString(OpenDSSIOConstants.GENERATOR_NAME);
      generatorNames.add(name);      
      generators.getInteger(OpenDSSIOConstants.NEXT_GENERATOR);          
    }
    // add the sourcebus
    generatorNames.add("source-sourcebus");
    
    HashMap<String, Generator> containedGenerators = new HashMap<String,Generator>();
    for (Generator generator : getGenerators()) {
      if (!generatorNames.contains(getGeneratorId(generator))) {
        throw new RuntimeException("Currently can't sync topology by adding generators to a com object");
      }      
      containedGenerators.put(getGeneratorId(generator), generator);
    }
    for (String name : generatorNames) {
      if (!containedGenerators.containsKey(name)) {
        throw new RuntimeException("Currently can't sync topology by removing generators to a com object");
      }
    }
    
    
    // check the loads
    HashSet<String> loadNames = new HashSet<String>();
    ComObject loads = circuit.call(OpenDSSIOConstants.LOADS);
    int numLoads = loads.getInteger(OpenDSSIOConstants.NUMBER_OF_LOADS);
    for (long i = 1; i <= numLoads; ++i) {
      loads.put(OpenDSSIOConstants.LOAD_IDX, i);
      String name = loads.getString(OpenDSSIOConstants.LOAD_NAME); 
      loadNames.add(name);
    }
    HashMap<String, Load> containedLoads = new HashMap<String,Load>();
    for (Load load : getLoads()) {
      if (!loadNames.contains(getLoadId(load))) {
        System.err.println(getLoadId(load));
        throw new RuntimeException("Currently can't sync topology by adding loads to a com object");
      }      
      containedLoads.put(getLoadId(load), load);
    }
    for (String name : loadNames) {
      if (!containedLoads.containsKey(name)) {
        throw new RuntimeException("Currently can't sync topology by removing loads to a com object");
      }
    }
    

    // check the lines
    HashSet<String> lineNames = new HashSet<String>();
    ComObject lines = circuit.call(OpenDSSIOConstants.LINES);
    int numLines = lines.getInteger(OpenDSSIOConstants.NUMBER_OF_LINES);
    lines.getInteger(OpenDSSIOConstants.FIRST_LINE);   
    for (int i = 0; i < numLines; ++i) {
      String name = lines.getString(OpenDSSIOConstants.LINE_NAME);    
      lineNames.add(name);
      lines.getInteger(OpenDSSIOConstants.NEXT_LINE);        
    }
    HashMap<String, Line> containedLines = new HashMap<String,Line>();
    for (Line line : getLines()) {
      if (!lineNames.contains(getLineId(line))) {
        throw new RuntimeException("Currently can't sync topology by adding lines to a com object");
      }      
      containedLines.put(getLineId(line), line);
    }
    for (String name : lineNames) {
      if (!containedLines.containsKey(name)) {
        throw new RuntimeException("Currently can't sync topology by removing lines to a com object");
      }
    }
    
    // check the transformers
    HashSet<String> transformerNames = new HashSet<String>();    
    ComObject transformers = circuit.call(OpenDSSIOConstants.TRANSFORMERS);
    int numTransformers = transformers.getInteger(OpenDSSIOConstants.NUMBER_OF_TRANSFORMERS);
    transformers.getInteger(OpenDSSIOConstants.FIRST_TRANSFORMER);    
    for (int i = 0; i < numTransformers; ++i) {
      String name = transformers.getString(OpenDSSIOConstants.TRANSFORMER_NAME);          
      transformerNames.add(name);
      transformers.getInteger(OpenDSSIOConstants.NEXT_TRANSFORMER);          
    }    
    HashMap<String, Transformer> containedTransformers = new HashMap<String,Transformer>();
    for (Transformer transformer : getTransformers()) {
      if (!transformerNames.contains(getTransformerId(transformer))) {
        throw new RuntimeException("Currently can't sync topology by adding transformers to a com object");
      }      
      containedTransformers.put(getTransformerId(transformer), transformer);
    }
    for (String name : transformerNames) {
      if (!containedTransformers.containsKey(name)) {
        throw new RuntimeException("Currently can't sync topology by removing transformers to a com object");
      }
    }
    


	}

  @Override
  public void busDataChange(Bus bus) {
  }

  @Override
  public void generatorDataChange(Generator generator) {
  }

  @Override
  public void batteryDataChange(Battery battery) {
  }
  
  @Override
  public void intertieDataChange(Intertie intertie) {
  }

  @Override
  public void lineDataChange(Line line) {
  }

  @Override
  public void loadDataChange(Load load) {
  }

  @Override
  public void shuntCapacitorDataChange(ShuntCapacitor shunt) {
  }

  @Override
  public void shuntCapacitorSwitchDataChange(ShuntCapacitorSwitch shunt) {
  }

  @Override
  public void transformerDataChange(Transformer transformer) {
  }

  @Override
  public void dcLineDataChange(DCLine line) {
  }
  
  @Override
  protected ElectricPowerModelImpl constructClone() {
    syncModel();
    OpenDSSModel model = new OpenDSSModel();
    model.openDSSModel = openDSSModel;
    return model;
  }
  
  /**
   * Get the ieiss bus of a bus
   * @param bus
   * @return
   */
  private String getBusId(Bus bus) {
    return bus.getAttribute(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY, String.class);
  }

  /**
   * Get the ieiss bus of a bus
   * @param bus
   * @return
   */
  private String getCapacitorId(ShuntCapacitor capacitor) {
    return capacitor.getAttribute(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY, String.class);
  }

  /**
   * Get the ieiss load of a load
   * @param bus
   * @return
   */
  private String getLoadId(Load load) {
    return load.getAttribute(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY, String.class);
  }

  /**
   * Get the ieiss generator of a generator
   * @param bus
   * @return
   */
  private String getGeneratorId(Generator generator) {
    return generator.getAttribute(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY, String.class);
  }

  /**
   * Get the ieiss generator of a generator
   * @param bus
   * @return
   */
  private String getBatteryId(Battery battery) {
    return battery.getAttribute(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY, String.class);
  }
  
  /**
   * Get the ieiss generator of a generator
   * @param bus
   * @return
   */
  private String getLineId(Line line) {
    return line.getAttribute(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY, String.class);
  }
  
  /**
   * Get the ieiss generator of a generator
   * @param bus
   * @return
   */
  private String getTransformerId(Transformer line) {
    return line.getAttribute(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY, String.class);
  }
  
  /**
   * Get the opendss com object
   * @return
   */
  public ComObject getOpenDSS() {
    return openDSSModel;
  }
  
}
