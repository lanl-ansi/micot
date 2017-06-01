package gov.lanl.micot.infrastructure.ep.model.powerworld;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import gov.lanl.micot.infrastructure.ep.io.powerworld.PowerworldIOConstants;
import gov.lanl.micot.infrastructure.ep.io.powerworld.PowerworldModelFile;
import gov.lanl.micot.infrastructure.ep.model.Battery;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ControlArea;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModelImpl;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModelListener;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.FuelTypeEnum;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Intertie;
import gov.lanl.micot.infrastructure.ep.model.Line;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitor;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitorSwitch;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.infrastructure.ep.model.TransformerTypeEnum;
import gov.lanl.micot.infrastructure.ep.model.Zone;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.util.collection.Pair;
import gov.lanl.micot.util.collection.Triple;
import gov.lanl.micot.util.io.dcom.ComDataObject;
import gov.lanl.micot.util.io.dcom.ComObject;
import gov.lanl.micot.util.math.PolynomialFunction;

/**
 * Definition of a power world model
 *  
 * @author Russell Bent
 */
public class PowerworldModel extends ElectricPowerModelImpl implements ElectricPowerModelListener {
	
	private ComObject powerWorldModel = null;
		
	/**
	 * Constructor
	 */
	public PowerworldModel(ComObject powerWorldModel) {
		super();
		init(powerWorldModel);
		addModelListener(this);
	}
		
	public void finalize() {
	  powerWorldModel.callData(PowerworldIOConstants.CLOSE_CASE);    
	  powerWorldModel.close();
	}
	
	/**
	 * Fill in all the attributes of the implementation
	 * @param opendss model
	 */
	private void init(ComObject powerWorldModel) {
		this.powerWorldModel = powerWorldModel;
		ComDataObject busesObject = powerWorldModel.callData(PowerworldIOConstants.LIST_OF_DEVICES, PowerworldIOConstants.BUS, "");
    ComDataObject branchObject = powerWorldModel.callData(PowerworldIOConstants.LIST_OF_DEVICES_AS_VARIANT_STRINGS, PowerworldIOConstants.BRANCH, "");
    ComDataObject genObject = powerWorldModel.callData(PowerworldIOConstants.LIST_OF_DEVICES_AS_VARIANT_STRINGS, PowerworldIOConstants.GENERATOR, "");
    ComDataObject areaObject = powerWorldModel.callData(PowerworldIOConstants.LIST_OF_DEVICES, PowerworldIOConstants.AREA, "");
    ComDataObject zoneObject = powerWorldModel.callData(PowerworldIOConstants.LIST_OF_DEVICES, PowerworldIOConstants.ZONE, "");
    ComDataObject loadObject = powerWorldModel.callData(PowerworldIOConstants.LIST_OF_DEVICES_AS_VARIANT_STRINGS, PowerworldIOConstants.LOAD, "");
    ComDataObject shuntObject = powerWorldModel.callData(PowerworldIOConstants.LIST_OF_DEVICES_AS_VARIANT_STRINGS, PowerworldIOConstants.SHUNT, "");

    
    // get some system level data
    String simFields[] = new String[]{PowerworldIOConstants.MVA_BASE}; 
    String simValues[] = new String[] {""};        
    ComDataObject simObject = powerWorldModel.callData(PowerworldIOConstants.GET_PARAMETERS_SINGLE_ELEMENT, PowerworldIOConstants.SIM_SOLUTION_OPTIONS, simFields, simValues);
    ArrayList<ComDataObject> simData = simObject.getArrayValue();
    String simErrorString = simData.get(0).getStringValue();
    if (!simErrorString.equals("")) {
      System.err.println("Error getting powerworld simulator parameter data: " + simErrorString);                
    }        
    ArrayList<ComDataObject> sData = simData.get(1).getArrayValue();                       
    String mvaBaseStr = sData.get(0).getStringValue();
    double mvaBase = Double.parseDouble(mvaBaseStr);

    setLineFactory(new PowerworldLineFactory());
    setTransformerFactory(new PowerworldTransformerFactory());
    setIntertieFactory(new PowerworldIntertieFactory());
    setBusFactory(new PowerworldBusFactory());
    setLoadFactory(new PowerworldLoadFactory());
    setGeneratorFactory(new PowerworldGeneratorFactory());
    setCapacitorFactory(new PowerworldShuntCapacitorFactory());
    setShuntCapacitorSwitchFactory(new PowerworldShuntCapacitorSwitchFactory());
    setBatteryFactory(new PowerworldBatteryFactory());
    setControlAreaFactory(new PowerworldAreaFactory());
    setZoneFactory(new PowerworldZoneFactory());
    
    PowerworldBusFactory busFactory = getBusFactory();
    PowerworldGeneratorFactory genFactory = getGeneratorFactory();
    PowerworldLoadFactory loadFactory = getLoadFactory();
    PowerworldShuntCapacitorFactory shuntFactory = getShuntCapacitorFactory();
    PowerworldShuntCapacitorSwitchFactory switchedShuntFactory = getShuntCapacitorSwitchFactory();
    PowerworldLineFactory lineFactory = getLineFactory();
    PowerworldTransformerFactory transformerFactory = getTransformerFactory();
    PowerworldAreaFactory areaFactory = getControlAreaFactory();
    PowerworldZoneFactory zoneFactory = getZoneFactory();

    Map<Integer,Bus> busMap = new HashMap<Integer,Bus>();   
    Map<Asset, String> areas = new HashMap<Asset,String>();   
    Map<Bus, String> slacks = new HashMap<Bus,String>();   
    Map<Asset, String> zones = new HashMap<Asset,String>();
    Map<Asset, Bus> regulated = new HashMap<Asset,Bus>();
        
    Map<Integer, ControlArea> areaMap = new HashMap<Integer,ControlArea>();
    Map<Integer, Zone> zoneMap = new HashMap<Integer,Zone>();

    // get all the buses
    ArrayList<ComDataObject> buses = busesObject.getArrayValue();
    String errorString = buses.get(0).getStringValue();
    if (errorString.equals("")) {
      ArrayList<ComDataObject> data = buses.get(1).getArrayValue();
      ArrayList<Integer> ids = data.get(0).getIntArrayValue();
            
      for (int i = 0; i < ids.size(); ++i) {
        Bus bus = busFactory.createBus(powerWorldModel, ids.get(i));
        addBus(bus);
        busMap.put(ids.get(i), bus);

        String fields[] = new String[]{PowerworldIOConstants.BUS_NUM, PowerworldIOConstants.BUS_AREA, PowerworldIOConstants.BUS_ZONE, PowerworldIOConstants.BUS_SLACK}; 
        String values[] = new String[] {ids.get(i)+"", "","", ""};        
        ComDataObject dataObject = powerWorldModel.callData(PowerworldIOConstants.GET_PARAMETERS_SINGLE_ELEMENT, PowerworldIOConstants.BUS, fields, values);
        ArrayList<ComDataObject> busData = dataObject.getArrayValue();
        String errorString2 = busData.get(0).getStringValue();
        if (!errorString2.equals("")) {
          System.err.println("Error getting powerworld bus data: " + errorString2);                
        }        
        ArrayList<ComDataObject> bData = busData.get(1).getArrayValue();                       
        String area = bData.get(1).getStringValue();
        String zone = bData.get(2).getStringValue();
        String slack = bData.get(3).getStringValue();        
        areas.put(bus, area);
        zones.put(bus, zone);
        
        if (slack.equalsIgnoreCase(PowerworldIOConstants.BUS_YES)) {
          slacks.put(bus, area);
        }
      }      
    }
    else {
      System.err.println("Error getting powerworld bus data: " + errorString);      
    }

    // get all the generators
    ArrayList<ComDataObject> generators = genObject.getArrayValue();
    errorString = generators.get(0).getStringValue();
    if (errorString.equals("")) {
      ArrayList<ComDataObject> data = generators.get(1).getArrayValue();
      ArrayList<Integer> busids = data.get(0).getIntArrayValue();
      ArrayList<String> genids = data.get(1).getStringArrayValue();
            
      for (int i = 0; i < busids.size(); ++i) {
        int busid = busids.get(i);
        String genid = genids.get(i).trim();
        Bus bus = busMap.get(busid);
        
        Generator generator = genFactory.createGenerator(powerWorldModel, bus, new Pair<Integer,String>(busid, genid));
        addGenerator(generator,bus);

        String fields[] = new String[]{PowerworldIOConstants.BUS_NUM, PowerworldIOConstants.GEN_NUM, PowerworldIOConstants.GEN_AREA, PowerworldIOConstants.GEN_ZONE, PowerworldIOConstants.GEN_MVA_BASE, PowerworldIOConstants.REMOTE_BUS}; 
        String values[] = new String[] {busid+"", genid+"", "","","",""};        
        ComDataObject dataObject = powerWorldModel.callData(PowerworldIOConstants.GET_PARAMETERS_SINGLE_ELEMENT, PowerworldIOConstants.GENERATOR, fields, values);
        ArrayList<ComDataObject> genData = dataObject.getArrayValue();
        String errorString2 = genData.get(0).getStringValue();
        if (!errorString2.equals("")) {
          System.err.println("Error getting powerworld generator data: " + errorString2);                
        }        
        ArrayList<ComDataObject> bData = genData.get(1).getArrayValue();                       
        String area = bData.get(2).getStringValue();
        String zone = bData.get(3).getStringValue();
        String remote = bData.get(5).getStringValue();
        
        int remoteId = Integer.parseInt(remote.trim());
        
        regulated.put(bus, busMap.get(remoteId));
        areas.put(generator, area);
        zones.put(generator, zone);        
      }      
    }
    else {
      System.out.println("Error getting powerworld generator data: " + errorString);      
    }


    // get all the loads
    ArrayList<ComDataObject> loads = loadObject.getArrayValue();
    errorString = loads.get(0).getStringValue();
    if (errorString.equals("")) {
      ArrayList<ComDataObject> data = loads.get(1).getArrayValue();
      ArrayList<Integer> busids = data.get(0).getIntArrayValue();
      ArrayList<String> loadids = data.get(1).getStringArrayValue();
            
      for (int i = 0; i < busids.size(); ++i) {
        int busid = busids.get(i);
        String loadid = loadids.get(i).trim();
        Bus bus = busMap.get(busid);
        
        Load load = loadFactory.createLoad(powerWorldModel, bus, new Pair<Integer,String>(busid, loadid));
        addLoad(load,bus);

        String fields[] = new String[]{PowerworldIOConstants.BUS_NUM, PowerworldIOConstants.LOAD_NUM, PowerworldIOConstants.LOAD_AREA, PowerworldIOConstants.LOAD_ZONE}; 
        String values[] = new String[] {busid+"", loadid+"", "",""};        
        ComDataObject dataObject = powerWorldModel.callData(PowerworldIOConstants.GET_PARAMETERS_SINGLE_ELEMENT, PowerworldIOConstants.LOAD, fields, values);
        ArrayList<ComDataObject> loadData = dataObject.getArrayValue();
        String errorString2 = loadData.get(0).getStringValue();
        if (!errorString2.equals("")) {
          System.err.println("Error getting powerworld load data: " + errorString2);                
        }        
        ArrayList<ComDataObject> bData = loadData.get(1).getArrayValue();                       
        String area = bData.get(2).getStringValue();
        String zone = bData.get(3).getStringValue();
        areas.put(load, area);
        zones.put(load, zone);
      }      
    }
    else {
      System.out.println("Error getting powerworld load data: " + errorString);      
    }

    // get all the shunts
    ArrayList<ComDataObject> shuntdata = buses.get(1).getArrayValue();
    ArrayList<Integer> shuntids = shuntdata.get(0).getIntArrayValue();
          
    for (int i = 0; i < shuntids.size(); ++i) {
      String fields[] = new String[]{ PowerworldIOConstants.BUS_NUM, PowerworldIOConstants.SHUNT_MW, PowerworldIOConstants.SHUNT_MVAR,
          PowerworldIOConstants.SHUNT_SS_MW, PowerworldIOConstants.SHUNT_SS_MVAR, PowerworldIOConstants.SHUNT_MAX_MW, PowerworldIOConstants.SHUNT_MAX_MVAR,
          PowerworldIOConstants.SHUNT_MIN_MW, PowerworldIOConstants.SHUNT_MIN_MVAR};
      String values[] = new String[] {shuntids.get(i)+"", "","","","","","","",""};                
      ComDataObject dataObject = powerWorldModel.callData(PowerworldIOConstants.GET_PARAMETERS_SINGLE_ELEMENT, PowerworldIOConstants.BUS, fields, values);
      ArrayList<ComDataObject> busData = dataObject.getArrayValue();
      String errorString2 = busData.get(0).getStringValue();
      if (!errorString2.equals("")) {
        System.err.println("Error getting powerworld shunt data: " + errorString2);                
      }        
      ArrayList<ComDataObject> bData = busData.get(1).getArrayValue();                       
      String mw = bData.get(1).getStringValue();
      String mvar = bData.get(2).getStringValue();
      String ssmw = bData.get(3).getStringValue();
      String ssmvar = bData.get(4).getStringValue();
      String ssmaxmw = bData.get(5).getStringValue();
      String ssmaxmvar = bData.get(6).getStringValue();
      String ssminmw = bData.get(7).getStringValue();
      String ssminmvar = bData.get(8).getStringValue();
      
      Bus bus = busMap.get(shuntids.get(i));
      
      if ( (mw != null || mvar != null) && Double.parseDouble(mw) != 0.0 && Double.parseDouble(mvar) != 0.0) {
        ShuntCapacitor capacitor = shuntFactory.createShuntCapacitor(powerWorldModel, bus, shuntids.get(i));
        addShuntCapacitor(capacitor,bus);
        areas.put(capacitor, areas.get(bus));
        zones.put(capacitor, zones.get(bus));
      }

      if (ssmw != null || ssmvar != null || ssmaxmw != null || ssmaxmvar != null || ssminmw != null || ssminmvar != null) {
        ShuntCapacitorSwitch capacitor = switchedShuntFactory.createShuntCapacitorSwitch(powerWorldModel, bus, shuntids.get(i));
        addShuntCapacitorSwitch(capacitor,bus);
        areas.put(capacitor, areas.get(bus));
        zones.put(capacitor, zones.get(bus));
      }
       
    }
    
        
   /* ArrayList<ComDataObject> shunts = shuntObject.getArrayValue();
    errorString = shunts.get(0).getStringValue();
    if (errorString.equals("")) {
      ArrayList<ComDataObject> data = shunts.get(1).getArrayValue();
      ArrayList<Integer> ids = data.get(0).getIntArrayValue();
            
      for (int i = 0; i < ids.size(); ++i) {
        String fields[] = new String[]{PowerworldIOConstants.BUS_NUM, PowerworldIOConstants.SHUNT_AREA, PowerworldIOConstants.SHUNT_ZONE, PowerworldIOConstants.SHUNT_MODE}; 
        String values[] = new String[] {ids.get(i)+"", "","",""};                
        ComDataObject dataObject = powerWorldModel.callData(PowerworldIOConstants.GET_PARAMETERS_SINGLE_ELEMENT, PowerworldIOConstants.BUS, fields, values);
        ArrayList<ComDataObject> busData = dataObject.getArrayValue();
        String errorString2 = busData.get(0).getStringValue();
        if (!errorString2.equals("")) {
          System.err.println("Error getting powerworld shunt data: " + errorString2);                
        }        
        ArrayList<ComDataObject> bData = busData.get(1).getArrayValue();                       
        String area = bData.get(1).getStringValue();
        String zone = bData.get(2).getStringValue();
        String mode = bData.get(3).getStringValue();

        Bus bus = busMap.get(ids.get(i));
        System.out.println(bus + " " + mode);
        
        
        /*Asset asset = null;
        if (mode != null && (mode.equalsIgnoreCase(PowerworldIOConstants.SHUNT_FIXED) || mode.equalsIgnoreCase(PowerworldIOConstants.SHUNT_BUS_SHUNT))) {
          int k = numShunts.get(bus) == null ? 0 : numShunts.get(bus);          
          ShuntCapacitor capacitor = shuntFactory.createShuntCapacitor(powerWorldModel, bus, ids.get(i), k);
          addShuntCapacitor(capacitor,bus);
          asset = capacitor;
          numShunts.put(bus, k+1);
        }
        else {
          int k = numSwitchedShunts.get(bus) == null ? 0 : numSwitchedShunts.get(bus);          
          ShuntCapacitorSwitch capacitor = switchedShuntFactory.createShuntCapacitorSwitch(powerWorldModel, bus, ids.get(i), k);
          addShuntCapacitorSwitch(capacitor,bus);
          asset = capacitor;
          numSwitchedShunts.put(bus, k+1);          
        }
        areas.put(asset, area);
        zones.put(asset, zone);
      }      
    }
    else {
      System.out.println("Error getting powerworld shunt data: " + errorString);      
    }*/

        
    // get all the lines
    ArrayList<ComDataObject> branches = branchObject.getArrayValue();
    errorString = branches.get(0).getStringValue();
    if (errorString.equals("")) {
      ArrayList<ComDataObject> data = branches.get(1).getArrayValue();
      ArrayList<Integer> fromids = data.get(0).getIntArrayValue();
      ArrayList<Integer> toids = data.get(1).getIntArrayValue();
      ArrayList<String> branchids = data.get(2).getStringArrayValue();
            
      for (int i = 0; i < branchids.size(); ++i) {
        int fromid = fromids.get(i);
        int toid = toids.get(i);
        String id = branchids.get(i).trim();

        String fields[] = new String[]{PowerworldIOConstants.BRANCH_BUS_FROM_NUM, PowerworldIOConstants.BRANCH_BUS_TO_NUM, PowerworldIOConstants.BRANCH_NUM, PowerworldIOConstants.BRANCH_AREA, PowerworldIOConstants.BRANCH_ZONE, PowerworldIOConstants.BRANCH_TYPE, PowerworldIOConstants.BRANCH_REGULATED_BUS}; 
        String values[] = new String[] {fromid+"", toid+"", id+"", "","","",""};
        ComDataObject dataObject = powerWorldModel.callData(PowerworldIOConstants.GET_PARAMETERS_SINGLE_ELEMENT, PowerworldIOConstants.BRANCH, fields, values);
        ArrayList<ComDataObject> branchData = dataObject.getArrayValue();
        String errorString2 = branchData.get(0).getStringValue();
        if (!errorString2.equals("")) {
          System.err.println("Error getting powerworld branch data: " + errorString2);                
        }
        ArrayList<ComDataObject> bData = branchData.get(1).getArrayValue();                       

        String type = bData.get(5).getStringValue();
        Bus fBus = busMap.get(fromid);
        Bus tBus = busMap.get(toid);

        ElectricPowerFlowConnection connection = null;        
        if (type.equalsIgnoreCase(PowerworldIOConstants.BRANCH_TRANSFORMER)) {
          connection = transformerFactory.createTransformer(powerWorldModel, fBus, tBus, new Triple<Integer,Integer,String>(fromid,toid,id));
        }
        else {
          connection = lineFactory.createLine(powerWorldModel, fBus, tBus, new Triple<Integer,Integer,String>(fromid,toid,id));
        }

        addEdge(connection,getNode(fBus),getNode(tBus));
        String area = bData.get(3).getStringValue();
        String zone = bData.get(4).getStringValue();
        String regulatedBus = bData.get(6).getStringValue();
        
        areas.put(connection, area);
        zones.put(connection, zone);
        
        if (regulatedBus != null) {
          Integer reg = Integer.parseInt(regulatedBus.trim());
          regulated.put(connection, busMap.get(reg));
        }
      }      
    }
    else {
      System.out.println("Error getting powerworld branch data: " + errorString);      
    }
    
    // get all the areas
    ArrayList<ComDataObject> as = areaObject.getArrayValue();
    errorString = as.get(0).getStringValue();
    if (errorString.equals("")) {
      ArrayList<ComDataObject> data = as.get(1).getArrayValue();
      ArrayList<Integer> ids = data.get(0).getIntArrayValue();
            
      for (int i = 0; i < ids.size(); ++i) {
        ControlArea area = areaFactory.createArea(powerWorldModel, ids.get(i));
        addArea(area);
        areaMap.put(ids.get(i), area);
      }      
    }
    else {
      System.out.println("Error getting powerworld area data: " + errorString);      
    }

    
    // get all the zones
    ArrayList<ComDataObject> zs = zoneObject.getArrayValue();
    errorString = zs.get(0).getStringValue();
    if (errorString.equals("")) {
      ArrayList<ComDataObject> data = zs.get(1).getArrayValue();
      ArrayList<Integer> ids = data.get(0).getIntArrayValue();
            
      for (int i = 0; i < ids.size(); ++i) {
        Zone zone = zoneFactory.createZone(powerWorldModel, ids.get(i));
        addZone(zone);
        zoneMap.put(ids.get(i), zone);
      }      
    }
    else {
      System.out.println("Error getting powerworld zone data: " + errorString);      
    }
    
    setMVABase(mvaBase);
    
    for (Asset asset : areas.keySet()) {
      setControlArea(asset, areaMap.get(areas.get(asset)));
      setZone(asset, zoneMap.get(zones.get(asset)));      
    }

    for (Bus bus : slacks.keySet()) {
      setSlackBus(areaMap.get(areas.get(bus)), bus);      
    }
    
    for (Asset asset : regulated.keySet()) {
      setControlBus(asset, regulated.get(asset));
    }    
	}

	@Override
	public PowerworldZoneFactory getZoneFactory() {
	  return (PowerworldZoneFactory) super.getZoneFactory();
	}

	@Override
  public PowerworldAreaFactory getControlAreaFactory() {
    return (PowerworldAreaFactory) super.getControlAreaFactory();
  }	
	
	@Override
	public PowerworldBusFactory getBusFactory() {
		return (PowerworldBusFactory) super.getBusFactory();
	}

	@Override
	public PowerworldGeneratorFactory getGeneratorFactory() {		
    return (PowerworldGeneratorFactory) super.getGeneratorFactory();
	}

	@Override
	public PowerworldBatteryFactory getBatteryFactory() {		
    return (PowerworldBatteryFactory) super.getBatteryFactory();
	}

	@Override
	public PowerworldIntertieFactory getIntertieFactory() {
    return (PowerworldIntertieFactory) super.getIntertieFactory();
	}

	@Override
	public PowerworldLineFactory getLineFactory() {
    return (PowerworldLineFactory) super.getLineFactory();
	}

	@Override
	public PowerworldLoadFactory getLoadFactory() {
    return (PowerworldLoadFactory) super.getLoadFactory();
	}

	@Override
	public PowerworldShuntCapacitorFactory getShuntCapacitorFactory() {
    return (PowerworldShuntCapacitorFactory) super.getShuntCapacitorFactory();
	}

	@Override
	public PowerworldShuntCapacitorSwitchFactory getShuntCapacitorSwitchFactory() {
    return (PowerworldShuntCapacitorSwitchFactory) super.getShuntCapacitorSwitchFactory();
	}

	@Override
	public PowerworldTransformerFactory getTransformerFactory() {
    return (PowerworldTransformerFactory) super.getTransformerFactory();
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
  	enterEditMode();
  	String fields[] = getBusFields();
  	Object values[] = getBusValues(bus);
  	String command = PowerworldIOConstants.createCreateDataCommand(PowerworldIOConstants.BUS, fields, values);
  	executeScript(command);
	}

	@Override
	public void busRemoved(Bus bus) {
    enterEditMode();
    String filtername = "busremove" + bus;
    String command = getBusRemoveFilter(bus, filtername);
    executeScript(command);
    command = PowerworldIOConstants.createDeleteCommand(PowerworldIOConstants.BUS, filtername);
    executeScript(command);
	}

	@Override
	public void generatorAdded(Generator generator) {
  	enterEditMode();
  	String fields[] = getGeneratorFields();
  	Object values[] = getGeneratorValues(generator);
  	String command = PowerworldIOConstants.createCreateDataCommand(PowerworldIOConstants.GENERATOR, fields, values);
  	executeScript(command);
	}

	@Override
	public void batteryAdded(Battery battery) {
	}
	
	@Override
	public void generatorRemove(Generator generator) {
    enterEditMode();
    String filtername = "generatorremove" + generator;
    String command = getGeneratorRemoveFilter(generator, filtername);
    executeScript(command);
    command = PowerworldIOConstants.createDeleteCommand(PowerworldIOConstants.GENERATOR, filtername);
    executeScript(command);
	}

	@Override
	public void batteryRemove(Battery battery) {
	}
	
	@Override
	public void linkAdded(ElectricPowerConnection link) {	
  	enterEditMode();
  	String fields[] = link instanceof Line ? getLineFields() : getTransformerFields();
  	Object values[] = link instanceof Transformer ? getLineValues((Line)link) : getTransformerValues((Transformer)link);
  	String command = PowerworldIOConstants.createCreateDataCommand(PowerworldIOConstants.BRANCH, fields, values);
  	executeScript(command);
	}

	@Override
	public void linkRemoved(ElectricPowerConnection link) {
    enterEditMode();
    String filtername = "linkremove" + link;
    String command = getBranchRemoveFilter(link, filtername);
    executeScript(command);
    command = PowerworldIOConstants.createDeleteCommand(PowerworldIOConstants.BRANCH, filtername);
    executeScript(command);
	}

	@Override
	public void loadAdded(Load load) {
  	enterEditMode();
  	String fields[] = getLoadFields();
  	Object values[] = getLoadValues(load);
  	String command = PowerworldIOConstants.createCreateDataCommand(PowerworldIOConstants.LOAD, fields, values);
  	executeScript(command);
	}

	@Override
	public void loadRemoved(Load load) {
    enterEditMode();
    String filtername = "loadremove" + load;
    String command = getLoadRemoveFilter(load, filtername);
    executeScript(command);
    command = PowerworldIOConstants.createDeleteCommand(PowerworldIOConstants.LOAD, filtername);
    executeScript(command);

	}

	@Override
	public void shuntCapacitorAdded(ShuntCapacitor shunt) {
  	enterEditMode();
  	String fields[] = getCapacitorFields();
  	Object values[] = getCapacitorValues(shunt);
  	String command = PowerworldIOConstants.createCreateDataCommand(PowerworldIOConstants.SHUNT, fields, values);
  	executeScript(command);

	}

	@Override
	public void shuntCapacitorRemoved(ShuntCapacitor shunt) {
    enterEditMode();
    String filtername = "capacitorremove" + shunt;
    String command = getCapacitorRemoveFilter(shunt, filtername);
    executeScript(command);
    command = PowerworldIOConstants.createDeleteCommand(PowerworldIOConstants.SHUNT, filtername);
    executeScript(command);
	}

	@Override
	public void shuntCapacitorSwitchAdded(ShuntCapacitorSwitch shunt) {
  	enterEditMode();
  	String fields[] = getCapacitorSwitchFields();
  	Object values[] = getCapacitorSwitchValues(shunt);
  	String command = PowerworldIOConstants.createCreateDataCommand(PowerworldIOConstants.SHUNT, fields, values);
  	executeScript(command);
	}

	@Override
	public void shuntCapacitorSwitchRemoved(ShuntCapacitorSwitch shunt) {
    enterEditMode();
    String filtername = "capacitorremove" + shunt;
    String command = getCapacitorRemoveFilter(shunt, filtername);
    executeScript(command);
    command = PowerworldIOConstants.createDeleteCommand(PowerworldIOConstants.SHUNT, filtername);
    executeScript(command);
	}

	/**
	 * Generic function for updating data
	 * @param fields
	 * @param values
	 */
	private void updateData(String[] fields, String[] values, String type) {	  
		ComDataObject object = powerWorldModel.callData(PowerworldIOConstants.CHANGE_PARAMETERS_SINGLE_ELEMENT, type, fields, values);
	  ArrayList<ComDataObject> data = object.getArrayValue();
	  String errorString = data.get(0).getStringValue();
	  if (!errorString.equals("")) {
	  	System.err.println("Error changing data of " + type + ": " + errorString);                
	  }
	}
	
  @Override
  public void busDataChange(Bus bus) {
  	enterEditMode();  	
    String fields[] = getBusFields(); 
    String values[] = convertToStringArray(getBusValues(bus));
    updateData(fields, values, PowerworldIOConstants.BUS);   
    
    // one of the bus fields get stored in generator data (remote voltage), so need to do that update too...
    for (Generator generator : getNode(bus).getComponents(Generator.class)) {
      generatorDataChange(generator);
    }
  }

  /**
   * Get the bus fields
   * @return
   */
  private String[] getBusFields() {
  	return new String[]{PowerworldIOConstants.BUS_NUM,
    		PowerworldIOConstants.BUS_PU_VOLTAGE, PowerworldIOConstants.BUS_ANGLE, 
    		PowerworldIOConstants.BUS_LATITUDE, PowerworldIOConstants.BUS_LONGITUDE, 
    		PowerworldIOConstants.BUS_KV, PowerworldIOConstants.BUS_MAX_VOLTAGE, 
    		PowerworldIOConstants.BUS_MIN_VOLTAGE, PowerworldIOConstants.BUS_STATUS, PowerworldIOConstants.BUS_CAT};
  }
  
  /**
   * Get the bus values
   * @param bus
   * @return
   */
  private Object[] getBusValues(Bus bus) {
    String status = bus.getActualStatus() && bus.getDesiredStatus() ? PowerworldIOConstants.BUS_CONNECTED : PowerworldIOConstants.BUS_DISCONNECTED;
    String cat = bus.getActualStatus() && bus.getDesiredStatus() ? bus.getAttribute(PowerworldModelConstants.POWERWORLD_BUS_CATEGORY_KEY).toString() : PowerworldIOConstants.BUS_DEAD;    
  	return new Object[] {getBusId(bus), bus.getVoltagePU(), bus.getPhaseAngle(), 
    		bus.getCoordinate().getX(), bus.getCoordinate().getY(), bus.getSystemVoltageKV(), 
    		bus.getMaximumVoltagePU(), bus.getMinimumVoltagePU(), status, cat};
  }
  
  /**
   * convert all the objects into strings
   * @param array
   * @return
   */
  private String[] convertToStringArray(Object[] array) {
  	String[] values = new String[array.length];
  	for (int i = 0; i < array.length; ++i) {
  		values[i] = array[i].toString();
  	}  	
  	return values;
  }
  
  @Override
  public void generatorDataChange(Generator generator) {
  	enterEditMode();
    String fields[] = getGeneratorFields();
    String values[] = convertToStringArray(getGeneratorValues(generator));  	
    updateData(fields, values, PowerworldIOConstants.GENERATOR);   
  }
  
  /**
   * Get the generator fields
   * @return
   */
  private String[] getGeneratorFields() {
  	return new String[]{PowerworldIOConstants.BUS_NUM, PowerworldIOConstants.GEN_NUM, 
    		PowerworldIOConstants.GEN_FUEL_TYPE, PowerworldIOConstants.GEN_COST_CONSTANT, 
    		PowerworldIOConstants.GEN_COST_LINEAR, PowerworldIOConstants.GEN_COST_SQUARE, 
    		PowerworldIOConstants.GEN_COST_CUBE, PowerworldIOConstants.GEN_MVA_BASE, 
    		PowerworldIOConstants.GEN_MVAR, PowerworldIOConstants.GEN_MW, 
    		PowerworldIOConstants.GEN_MVAR_MAX, PowerworldIOConstants.GEN_MW_MAX, 
    		PowerworldIOConstants.GEN_MVAR_MIN, PowerworldIOConstants.GEN_MW_MIN, 
    		PowerworldIOConstants.GEN_STATUS, PowerworldIOConstants.GEN_VOLTAGE}; 
  }
 
  /**
   * Get the generator values
   * @return
   */
  private Object[] getGeneratorValues(Generator generator) {
  	Pair<Integer,Integer> id = getGeneratorId(generator);
    String ft = getPowerworldFuelType(generator.getAttribute(Generator.FUEL_TYPE_KEY, FuelTypeEnum.class));
    PolynomialFunction fcn = generator.getAttribute(Generator.ECONOMIC_COST_KEY, PolynomialFunction.class);
    Double[] datapoints = fcn.getPolynomialCoefficients().toArray(new Double[0]);
    double costConstant = datapoints.length >= 1 ? datapoints[0] : 0;
    double linearConstant = datapoints.length >= 2 ? datapoints[1] : 0;
    double quadConstant = datapoints.length >= 3 ? datapoints[2] : 0;
    double cubeConstant = datapoints.length >= 4 ? datapoints[3] : 0;
    String status = generator.getActualStatus() && generator.getDesiredStatus() ? PowerworldIOConstants.GEN_CLOSED : PowerworldIOConstants.GEN_OPEN;
    
    Bus bus = getNode(generator).getBus();
    double remoteVoltage = bus.getRemoteVoltagePU();
    
    return new Object[] {id.getLeft(), id.getRight(), 
      ft, costConstant, linearConstant, quadConstant, cubeConstant, generator.getAttribute(Generator.MVA_BASE_KEY), 
      generator.getActualReactiveGeneration(), generator.getActualRealGeneration(), 
      generator.getDesiredReactiveMax(), generator.getDesiredRealGenerationMax(), 
      generator.getReactiveMin(), generator.getRealGenerationMin(), status, remoteVoltage};  		
  }

  @Override
  public void batteryDataChange(Battery battery) {
  }
  
  @Override
  public void intertieDataChange(Intertie intertie) {
  }

  @Override
  public void lineDataChange(Line line) {
  	enterEditMode();  	
    String fields[] = getLineFields();
    String values[] = convertToStringArray(getLineValues(line));    
    updateData(fields, values, PowerworldIOConstants.BRANCH);   
  }

  /**
   * Get the line values associated with the line
   * @param line
   * @return
   */
  private Object[] getLineValues(Line line) {
    Triple<Integer,Integer,Integer> id = getConnectionId(line);
    String status = line.getActualStatus() && line.getDesiredStatus() ? PowerworldIOConstants.BRANCH_CLOSED : PowerworldIOConstants.BRANCH_OPEN;
    return new Object[] {
      id.getOne(), 
      id.getTwo(), 
      id.getThree(), 
      line.getResistance(), 
      line.getReactance(), 
      line.getLineCharging(), 
      line.getCapacityRating(), 
      line.getShortTermEmergencyCapacityRating(), 
      line.getLongTermEmergencyCapacityRating(), 
      status, 
      line.getAttribute(Line.LENGTH_KEY),
      line.getReactiveLoss(), 
      line.getRealLoss(), 
      line.getAttribute(Line.MVAR_FLOW_SIDE1_KEY), 
      line.getAttribute(Line.MVAR_FLOW_SIDE2_KEY), 
      line.getAttribute(Line.MW_FLOW_SIDE1_KEY), 
      line.getAttribute(Line.MW_FLOW_SIDE2_KEY)};     
  }
  
  /**
   * Get the fields associated lines
   * @return
   */
  private String[] getLineFields() {
    return new String[]{ PowerworldIOConstants.BRANCH_BUS_FROM_NUM, 
        PowerworldIOConstants.BRANCH_BUS_TO_NUM, 
        PowerworldIOConstants.BRANCH_NUM, 
        PowerworldIOConstants.BRANCH_RESISTANCE, 
        PowerworldIOConstants.BRANCH_REACTANCE, 
        PowerworldIOConstants.BRANCH_CHARGING, 
        PowerworldIOConstants.BRANCH_THERMAL_LIMIT_A, 
        PowerworldIOConstants.BRANCH_THERMAL_LIMIT_B, 
        PowerworldIOConstants.BRANCH_THERMAL_LIMIT_C, 
        PowerworldIOConstants.BRANCH_STATUS, 
        PowerworldIOConstants.BRANCH_LENGTH,
        PowerworldIOConstants.BRANCH_REACTIVE_LOSS, 
        PowerworldIOConstants.BRANCH_REAL_LOSS, 
        PowerworldIOConstants.BRANCH_FROM_REACTIVE_FLOW, 
        PowerworldIOConstants.BRANCH_TO_REACTIVE_FLOW, 
        PowerworldIOConstants.BRANCH_FROM_REAL_FLOW, 
        PowerworldIOConstants.BRANCH_TO_REAL_FLOW}; 
  }

  @Override
  public void loadDataChange(Load load) {
  	enterEditMode();
    String fields[] = getLoadFields();  
    String values[] = convertToStringArray(getLoadValues(load));
    updateData(fields, values, PowerworldIOConstants.LOAD);   
  }

  /**
   * Get all the fields associated with loads
   * @return
   */
  private String[] getLoadFields() {
    return new String[] {PowerworldIOConstants.BUS_NUM, 
        PowerworldIOConstants.LOAD_NUM, PowerworldIOConstants.LOAD_MVAR, 
        PowerworldIOConstants.LOAD_MW, PowerworldIOConstants.LOAD_STATUS};
  }

  /**
   * Get the load values
   * @param load
   * @return
   */
  private Object[] getLoadValues(Load load) {
    Pair<Integer,Integer> id = getLoadId(load);
    String status = load.getActualStatus() && load.getDesiredStatus() ? PowerworldIOConstants.LOAD_CLOSED : PowerworldIOConstants.LOAD_OPEN;
    return new Object[] {id.getLeft(), id.getRight(),load.getActualReactiveLoad(),load.getActualRealLoad(),status};    
  }
  
  @Override
  public void shuntCapacitorDataChange(ShuntCapacitor shunt) {
  	enterEditMode();
    String fields[] = getCapacitorFields();  
    String values[] = convertToStringArray(getCapacitorValues(shunt));
    updateData(fields, values, PowerworldIOConstants.BUS);   
  }
  
  /**
   * Get the fields associated with a shunt capacitor
   * @return
   */
  private String[] getCapacitorFields() {
    return new String[]{PowerworldIOConstants.BUS_NUM, 
      PowerworldIOConstants.SHUNT_MVAR, 
      PowerworldIOConstants.SHUNT_MW};
  }
  
  /**
   * Get the capacitor values
   * @param shunt
   * @return
   */
  private Object[] getCapacitorValues(ShuntCapacitor shunt) {
    int id = getCapacitorId(shunt);
//    System.out.println(id + " " + shunt.getRealCompensation());

    
    // power world needs everything in their actual power units... models assumes these are in per unit (this is different than other formats
    return new Object[] {id, shunt.getReactiveCompensation(),shunt.getRealCompensation()};
    
  }
  
  @Override
  public void shuntCapacitorSwitchDataChange(ShuntCapacitorSwitch shunt) {
  	enterEditMode();
  	String fields[] = getCapacitorSwitchFields();
    String values[] = convertToStringArray(getCapacitorSwitchValues(shunt));
    updateData(fields, values, PowerworldIOConstants.BUS);   
  }

  /**
   * Get the fields associated with a capacitor switch
   * @return
   */
  private String[] getCapacitorSwitchFields() {
    return new String[]{PowerworldIOConstants.BUS_NUM,
        PowerworldIOConstants.SHUNT_SS_MW,
        PowerworldIOConstants.SHUNT_SS_MVAR,
        PowerworldIOConstants.SHUNT_MAX_MW,
        PowerworldIOConstants.SHUNT_MIN_MW,
        PowerworldIOConstants.SHUNT_MAX_MVAR,
        PowerworldIOConstants.SHUNT_MIN_MVAR,
        };
  }
  
  /**
   * Get capacitor switch values
   * @param shunt
   * @return
   */
  private Object[] getCapacitorSwitchValues(ShuntCapacitorSwitch shunt) {
    int id = getCapacitorId(shunt);
    return new Object[] {id, 
        shunt.getRealCompensation().doubleValue(), 
        shunt.getReactiveCompensation().doubleValue(),
        shunt.getMaxMW(),
        shunt.getMinMW(),
        shunt.getMaxMVar(),
        shunt.getMinMVar()};
  }
  
  /**
   * Get the fields associated with transformers
   * @return
   */
  private String[] getTransformerFields() {
    return new String[]{
      PowerworldIOConstants.BRANCH_BUS_FROM_NUM, 
      PowerworldIOConstants.BRANCH_BUS_TO_NUM, 
      PowerworldIOConstants.BRANCH_NUM, 
      PowerworldIOConstants.BRANCH_RESISTANCE, 
      PowerworldIOConstants.BRANCH_REACTANCE, 
      PowerworldIOConstants.BRANCH_CHARGING, 
      PowerworldIOConstants.BRANCH_THERMAL_LIMIT_A, 
      PowerworldIOConstants.BRANCH_THERMAL_LIMIT_B, 
      PowerworldIOConstants.BRANCH_THERMAL_LIMIT_C, 
      PowerworldIOConstants.BRANCH_STATUS, 
      PowerworldIOConstants.BRANCH_LENGTH,
      PowerworldIOConstants.BRANCH_REACTIVE_LOSS, 
      PowerworldIOConstants.BRANCH_REAL_LOSS, 
      PowerworldIOConstants.BRANCH_FROM_REACTIVE_FLOW, 
      PowerworldIOConstants.BRANCH_TO_REACTIVE_FLOW, 
      PowerworldIOConstants.BRANCH_FROM_REAL_FLOW, 
      PowerworldIOConstants.BRANCH_TO_REAL_FLOW, 
      PowerworldIOConstants.BRANCH_TAP_RATIO, 
      PowerworldIOConstants.BRANCH_TAP_PHASE,
      PowerworldIOConstants.BRANCH_MAX_TAP_RATIO, 
      PowerworldIOConstants.BRANCH_MIN_TAP_RATIO, 
      PowerworldIOConstants.BRANCH_TAP_RATIO_STEPS, 
      PowerworldIOConstants.BRANCH_TRANSFORMER_TYPE}; 
  }

  /**
   * Get the values associated with transformers
   * @param transformer
   * @return
   */
  private Object[] getTransformerValues(Transformer transformer) {
    Triple<Integer,Integer,Integer> id = getConnectionId(transformer);
    String status = transformer.getActualStatus() && transformer.getDesiredStatus() ? PowerworldIOConstants.BRANCH_CLOSED : PowerworldIOConstants.BRANCH_OPEN;
    String type = getPowerworldTransformerType(transformer.getAttribute(Transformer.TYPE_KEY, TransformerTypeEnum.class)); 
    double tapAngle = transformer.getAttribute(Transformer.TAP_ANGLE_KEY, Number.class).doubleValue();
    double maxAngle = transformer.getAttribute(Transformer.MAX_TAP_ANGLE_KEY,Number.class).doubleValue();         
    double minAngle = transformer.getAttribute(Transformer.MIN_TAP_ANGLE_KEY,Number.class).doubleValue();         
    if (type.equals(PowerworldIOConstants.BRANCH_TRANSFORMER_PHASE)) {
      tapAngle = Math.toDegrees(tapAngle);
      maxAngle = Math.toDegrees(maxAngle);
      minAngle = Math.toDegrees(minAngle);
    }    
    return new Object[] {
        id.getOne(), 
        id.getTwo(), 
        id.getThree(), 
        transformer.getResistance(), 
        transformer.getReactance(), 
        transformer.getLineCharging(), 
        transformer.getCapacityRating(), 
        transformer.getShortTermEmergencyCapacityRating(), 
        transformer.getLongTermEmergencyCapacityRating(), 
        status, 
        transformer.getAttribute(Line.LENGTH_KEY),
        transformer.getReactiveLoss(), 
        transformer.getRealLoss(), 
        transformer.getAttribute(Line.MVAR_FLOW_SIDE1_KEY), 
        transformer.getAttribute(Line.MVAR_FLOW_SIDE2_KEY), 
        transformer.getAttribute(Line.MW_FLOW_SIDE1_KEY), 
        transformer.getAttribute(Line.MW_FLOW_SIDE2_KEY),
        transformer.getAttribute(Transformer.TAP_RATIO_KEY),
        tapAngle,         
        maxAngle,         
        minAngle,         
        transformer.getAttribute(Transformer.STEP_SIZE_KEY),         
        type
        };

  }
  
  @Override
  public void transformerDataChange(Transformer transformer) {
  	enterEditMode();
    String fields[] = getTransformerFields();
    String values[] = convertToStringArray(getTransformerValues(transformer));   	
    updateData(fields, values, PowerworldIOConstants.BRANCH);   
  }

  @Override
  public synchronized ElectricPowerModel clone() {
    ElectricPowerModel newModel = constructClone();
    newModel.setIsSolved(isSolved());
    newModel.setSimulationQualityRank(getSimulationQualityRank());

    // most everything should have been propogated to the model, and this will ensure it
    HashMap<Class<?>, HashMap<Object, Asset>> assetMap = new HashMap<Class<?>, HashMap<Object, Asset>>();     
    for (Asset asset : newModel.getAssets()) {
      if (assetMap.get(asset.getClass()) == null) {
        assetMap.put(asset.getClass(), new HashMap<Object, Asset>());
      }
      assetMap.get(asset.getClass()).put(asset.getAttribute(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY), asset);
    }    
    
    for (Asset oldAsset : getAssets()) {
      Asset newAsset = assetMap.get(oldAsset.getClass()).get(oldAsset.getAttribute(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY));      
      for (Object key : oldAsset.getAttributeKeys()) {
        if (!key.equals(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY) && !key.equals(Asset.ASSET_ID_KEY)) {
          newAsset.setAttribute(key,oldAsset.getAttribute(key));
        }
      }
    }
    return newModel; 
  }
  
  @Override
  protected synchronized ElectricPowerModelImpl constructClone() {
    PowerworldModel model = null;
    PowerworldModelFile file = new PowerworldModelFile();
    try {
      String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
      String fileName = processName+"temp.pwb";
      file.saveFile(fileName, model);
      model = (PowerworldModel) file.readModel(fileName);
      File f = new File(fileName);
      f.delete();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return model;
  }
  
  /**
   * Get the ieiss bus of a bus
   * @param bus
   * @return
   */
  public Integer getBusId(Bus bus) {
    return bus.getAttribute(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY, Integer.class);
  }

  /**
   * Get the ieiss bus of a bus
   * @param bus
   * @return
   */
	@SuppressWarnings("unchecked")
  private Integer getCapacitorId(ShuntCapacitor capacitor) {
    return capacitor.getAttribute(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY, Integer.class);
  }

  /**
   * Get the ieiss bus of a bus
   * @param bus
   * @return
   */
	@SuppressWarnings("unchecked")
  private Integer getCapacitorId(ShuntCapacitorSwitch capacitor) {
    return capacitor.getAttribute(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY, Integer.class);
  }

  
  /**
   * Get the power world load of a load
   * @param bus
   * @return
   */
  @SuppressWarnings("unchecked")
	private Pair<Integer,Integer> getLoadId(Load load) {
    return load.getAttribute(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY, Pair.class);
  }

  /**
   * Get the ieiss generator of a generator
   * @param bus
   * @return
   */
  @SuppressWarnings("unchecked")
	public Pair<Integer,Integer> getGeneratorId(Generator generator) {
    return generator.getAttribute(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY, Pair.class);
  }

  /**
   * Get the ieiss generator of a generator
   * @param bus
   * @return
   */
  private String getBatteryId(Battery battery) {
    return battery.getAttribute(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY, String.class);
  }
  
  /**
   * Get the ieiss generator of a generator
   * @param bus
   * @return
   */
  @SuppressWarnings("unchecked")
	public Triple<Integer,Integer,Integer> getConnectionId(ElectricPowerConnection line) {
    return line.getAttribute(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY, Triple.class);
  }
    
  /**
   * Get the power world com object
   * @return
   */
  public ComObject getPowerworld() {
    return powerWorldModel;
  }
  
  /**
   * Put power world in edit mode
   */
  private void enterEditMode() {
  	String scriptcommand = PowerworldIOConstants.EDIT_MODE;
  	ComDataObject object = powerWorldModel.callData(PowerworldIOConstants.RUN_SCRIPT_COMMAND, scriptcommand);
  	ArrayList<ComDataObject> o = object.getArrayValue();
    String errorString = o.get(0).getStringValue();
    if (!errorString.equals("")) {
      System.err.println("Error getting into edit mode: " + errorString);                
    }
  }

  /**
   * Execute an arbitrary script
   * @param command
   */
  private void executeScript(String command) {
  	ComDataObject object = powerWorldModel.callData(PowerworldIOConstants.RUN_SCRIPT_COMMAND, command);
  	ArrayList<ComDataObject> o = object.getArrayValue();
    String errorString = o.get(0).getStringValue();
    if (!errorString.equals("")) {
      System.err.println("Error with script: " + command + "--" + errorString);                
    }  	
  }
  
  /**
   * Do the reverse look up on powerworld fuel types
   * @param ft
   * @return
   */
  private String getPowerworldFuelType(FuelTypeEnum ft) {
  	if (ft.equals(FuelTypeEnum.COAL)) {
  		return PowerworldIOConstants.COAL;
    }
    if (ft.equals(FuelTypeEnum.NATURAL_GAS)) {
    	return PowerworldIOConstants.NATURAL_GAS;
    }
    if (ft.equals(FuelTypeEnum.HYDRO)) {
    	return PowerworldIOConstants.HYDRO;
    }
    if (ft.equals(FuelTypeEnum.NUCLEAR)) {
    	return PowerworldIOConstants.NUCLEAR;
    }
    if (ft.equals(FuelTypeEnum.SOLAR)) {
    	return PowerworldIOConstants.SOLAR;
    }
    if (ft.equals(FuelTypeEnum.WIND)) {
    	return PowerworldIOConstants.WIND;
    }
    if (ft.equals(FuelTypeEnum.OIL)) {
    	return PowerworldIOConstants.OIL;
    }
    if (ft.equals(FuelTypeEnum.DIESEL)) {
    	return PowerworldIOConstants.DIESEL;
    }
    if (ft.equals(FuelTypeEnum.GEOTHERMOL)) {
    	return PowerworldIOConstants.GEOTHERMAL;
    }
    if (ft.equals(FuelTypeEnum.BIOMASS)) {
    	return PowerworldIOConstants.BIOMASS;
    }
    return "";
  }

  /**
   * Get the type of the transformer
   * @param type
   * @return
   */
  private String getPowerworldTransformerType(TransformerTypeEnum type) {
  	if (type.equals(TransformerTypeEnum.TRANSMISSION_FIXED_ANGLE_VARIABLE_TAP_VOLTAGE_CONTROL_TYPE)) {
  		return PowerworldIOConstants.BRANCH_TRANSFORMER_LTC;
  	}
  	
  	if (type.equals(TransformerTypeEnum.TRANSMISSION_FIXED_ANGLE_VARIABLE_TAP_REACTIVE_FLOW_CONTROL_TYPE)) {
  		return PowerworldIOConstants.BRANCH_TRANSFORMER_MVAR;
  	}

   	if (type.equals(TransformerTypeEnum.TRANSMISSION_FIXED_ANGLE_VARIABLE_TAP_REAL_FLOW_CONTROL_TYPE)) {
  		return PowerworldIOConstants.BRANCH_TRANSFORMER_PHASE;
  	}
   	return PowerworldIOConstants.BRANCH_TRANSFORMER_FIXED;
  }
  
  /**
   * create a filter command for removing a single bus
   * @param bus
   * @return
   */
  private String getBusRemoveFilter(Bus bus, String filtername) {
    String command = PowerworldIOConstants.CREATE_FILTER;
    command += "{";
    command += PowerworldIOConstants.createRemoveHeader(PowerworldIOConstants.BUS, filtername);
    command += PowerworldIOConstants.SUBDATA_HEADER;
    command += PowerworldIOConstants.BUS_NUM + " = " + getBusId(bus);
    command += PowerworldIOConstants.SUBDATA_FOOTER;
    command += "}";
    return command;    
  }
  
  /**
   * create a filter command for removing a single generator
   * @param bus
   * @return
   */
  private String getGeneratorRemoveFilter(Generator generator, String filtername) {
    Pair<Integer,Integer> id = getGeneratorId(generator);
    
    String command = PowerworldIOConstants.CREATE_FILTER;
    command += "{";
    command += PowerworldIOConstants.createRemoveHeader(PowerworldIOConstants.GENERATOR, filtername);
    command += PowerworldIOConstants.SUBDATA_HEADER;
    command += PowerworldIOConstants.BUS_NUM + " = " + id.getLeft();
    command += PowerworldIOConstants.GEN_NUM + " = " + id.getRight();
    command += PowerworldIOConstants.SUBDATA_FOOTER;
    command += "}";
    return command;    
  }

  /**
   * create a filter command for removing a single generator
   * @param bus
   * @return
   */
  private String getLoadRemoveFilter(Load load, String filtername) {
    Pair<Integer,Integer> id = getLoadId(load);
    
    String command = PowerworldIOConstants.CREATE_FILTER;
    command += "{";
    command += PowerworldIOConstants.createRemoveHeader(PowerworldIOConstants.LOAD, filtername);
    command += PowerworldIOConstants.SUBDATA_HEADER;
    command += PowerworldIOConstants.BUS_NUM + " = " + id.getLeft();
    command += PowerworldIOConstants.LOAD_NUM + " = " + id.getRight();
    command += PowerworldIOConstants.SUBDATA_FOOTER;
    command += "}";
    return command;    
  }


  /**
   * create a filter command for removing a single generator
   * @param bus
   * @return
   */
  private String getCapacitorRemoveFilter(ShuntCapacitor capacitor, String filtername) {
    int id = getCapacitorId(capacitor);
    
    String command = PowerworldIOConstants.CREATE_FILTER;
    command += "{";
    command += PowerworldIOConstants.createRemoveHeader(PowerworldIOConstants.SHUNT, filtername);
    command += PowerworldIOConstants.SUBDATA_HEADER;
    command += PowerworldIOConstants.BUS_NUM + " = " + id;
    command += PowerworldIOConstants.SUBDATA_FOOTER;
    command += "}";
    return command;    
  }

  /**
   * create a filter command for removing a single generator
   * @param bus
   * @return
   */
  private String getCapacitorRemoveFilter(ShuntCapacitorSwitch capacitor, String filtername) {
    int id = getCapacitorId(capacitor);
    
    String command = PowerworldIOConstants.CREATE_FILTER;
    command += "{";
    command += PowerworldIOConstants.createRemoveHeader(PowerworldIOConstants.SHUNT, filtername);
    command += PowerworldIOConstants.SUBDATA_HEADER;
    command += PowerworldIOConstants.BUS_NUM + " = " + id;
    command += PowerworldIOConstants.SUBDATA_FOOTER;
    command += "}";
    return command;    
  }
  
  /**
   * create a filter command for removing a single branch
   * @param bus
   * @return
   */
  private String getBranchRemoveFilter(ElectricPowerConnection line, String filtername) {
    Triple<Integer,Integer, Integer> id = getConnectionId(line);
    
    String command = PowerworldIOConstants.CREATE_FILTER;
    command += "{";
    command += PowerworldIOConstants.createRemoveHeader(PowerworldIOConstants.BRANCH, filtername);
    command += PowerworldIOConstants.SUBDATA_HEADER;
    command += PowerworldIOConstants.BRANCH_BUS_FROM_NUM + " = " + id.getOne();
    command += PowerworldIOConstants.BRANCH_BUS_TO_NUM + " = " + id.getTwo();
    command += PowerworldIOConstants.BRANCH_NUM + " = " + id.getThree();
    command += PowerworldIOConstants.SUBDATA_FOOTER;
    command += "}";
    return command;    
  }

  
  /**
   * Remove zones
   * @deprecated
   */
  public void removeZones() {
    enterEditMode();
    for (Bus bus : getBuses()) {
      String fields[] = new String[]{PowerworldIOConstants.BUS_NUM, PowerworldIOConstants.BUS_ZONE};
      Object values[] = new Object[] { getBusId(bus), 1 };
      String command = PowerworldIOConstants.createCreateDataCommand(PowerworldIOConstants.BUS, fields, values);
      executeScript(command);      
    }
    
    
  }

  
}
