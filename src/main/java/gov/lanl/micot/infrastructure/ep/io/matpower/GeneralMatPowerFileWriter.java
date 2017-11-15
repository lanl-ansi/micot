package gov.lanl.micot.infrastructure.ep.io.matpower;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import gov.lanl.micot.infrastructure.ep.model.Battery;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ControlArea;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Line;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitor;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.infrastructure.ep.model.Zone;
import gov.lanl.micot.infrastructure.ep.model.matpower.DefaultMatPowerHeaderFactory;
import gov.lanl.micot.infrastructure.ep.model.matpower.MatPowerModelConstants;
import gov.lanl.micot.infrastructure.ep.model.matpower.MatPowerModelImpl;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.model.Component;
import gov.lanl.micot.infrastructure.model.Connection;

/**
 * This class is intended to write MatPower files for general non MatPower models
 * 
 * @author Russell Bent
 */
public class GeneralMatPowerFileWriter extends MatPowerFileWriterImpl {

	private Map<Bus, Integer>										ids				= null;
	private int																	area			= -1;
	private int																	zone			= -1;
	private Bus																	slackBus	= null;
	private MatPowerModelImpl                   tempModel = null;

	/**
	 * Constructor
	 */
	protected GeneralMatPowerFileWriter() {
		area = 1;
		zone = 1;
		slackBus = null;
		tempModel = new MatPowerModelImpl();
	}

	@Override
	protected void writeBuses(ElectricPowerModel model, StringBuffer buffer) {
		ids = new HashMap<Bus, Integer>();
//		area = MatPowerAreaFactory.getInstance().getUnusedId();
 //   zone = MatPowerZoneFactory.getInstance().findUnusedId();
    area = tempModel.getControlAreaFactory().getUnusedId();
    zone = tempModel.getZoneFactory().findUnusedId();
		
    setupAreas(model);
    setupZones(model);
    setupBuses(model);
    setupLoads(model);
    setupGenerators(model);
    setupCapacitors(model);
    setupLines(model);
    setupTransformers(model);
		
    TreeSet<ElectricPowerNode> set = new TreeSet<ElectricPowerNode>();
    set.addAll(model.getNodes());
    
		// fill in bus data
		for (ElectricPowerNode node : set) {
		  Bus bus = node.getBus();
		  Load load = node.getLoad();
		  ShuntCapacitor shunt = node.getShunt();
	    Generator generator = node.getGenerator();
      String line = getMatPowerBusLine(bus, generator, load, shunt, getArea(bus,model), getZone(bus,model));
      buffer.append(line);
      buffer.append(System.getProperty("line.separator"));
		}		
	}

	/**
	 * Create the matpower data that we might need
	 * @param id
	 * @param bus
	 * @return
	 */
	private int fillWithMatPowerData(int id, Bus bus) {
	//  MatPowerBusFactory.getInstance().updateBus(bus, id);	  
	  tempModel.getBusFactory().updateBus(bus, id);    
	  return Math.max(id, bus.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, Integer.class) + 1);
	}

  /**
   * Create the pfw data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithMatPowerData(Bus id, ShuntCapacitor shunt) {
//    MatPowerShuntFactory.getInstance().updateShunt(shunt, id);
    tempModel.getShuntCapacitorFactory().updateShunt(shunt, id);
  }

  /**
   * Create the pfw data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithMatPowerData(Bus id1, Bus id2, Line line) {
//    MatPowerLineFactory.getInstance().updateLine(line, id1, id2);
    tempModel.getLineFactory().updateLine(line, id1, id2);
  }

  /**
   * Create the pfw data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithMatPowerData(Bus id1, Bus id2, Transformer line) {
//    MatPowerTransformerFactory.getInstance().updateTransformer(line, id1, id2);
    tempModel.getTransformerFactory().updateTransformer(line, id1, id2);
  }
  
  /**
   * Create the pfw data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithMatPowerData(Bus bus, Load load) {
//    MatPowerLoadFactory.getInstance().updateLoad(load, bus);
    tempModel.getLoadFactory().updateLoad(load, bus);
  }
  
  /**
   * Create the pfw data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithMatPowerData(Bus id, Generator generator) {
//    MatPowerGeneratorFactory.getInstance().updateGenerator(generator, id);
    tempModel.getGeneratorFactory().updateGenerator(generator, id);

  }
  
  /**
   * Create the matpower data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithMatPowerData(Bus bus, Battery battery) {
//    MatPowerBatteryFactory.getInstance().updateBattery(battery, bus);
    tempModel.getBatteryFactory().updateBattery(battery, bus);
  }
  
  /**
   * Create the pfw data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithMatPowerData(int id, ControlArea area) {
//    MatPowerAreaFactory.getInstance().updateArea(area, id);
    tempModel.getControlAreaFactory().updateArea(area, id);
  }
  
  /**
   * Create the pfw data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithMatPowerData(int id, Zone zone) {
//    MatPowerZoneFactory.getInstance().updateZone(zone,id);
    tempModel.getZoneFactory().updateZone(zone,id);
  }
  
	@Override
	protected void writeAreas(ElectricPowerModel model, StringBuffer buffer) {
	  for (ControlArea controlArea : model.getControlAreas()) {
	    Bus sb = model.getSlackBus(controlArea) != null ? model.getSlackBus(controlArea) : slackBus;     
	    String line = getMatPowerAreaLine(controlArea, sb);      
	    buffer.append(line);
	    buffer.append(System.getProperty("line.separator"));
	  }	  
	}

	@Override
	protected void writeFlowLines(ElectricPowerModel model, StringBuffer buffer) {
		for (ElectricPowerFlowConnection line : model.getFlowConnections()) {
      String string = null;
      if (line instanceof Line) {
	      string = getMatPowerLineLine(line, model.getFirstNode(line).getBus(), model.getSecondNode(line).getBus()); 
      }
      else {
        string = getMatPowerLineLine((Transformer)line, model.getFirstNode(line).getBus(), model.getSecondNode(line).getBus()); 
      }
			buffer.append(string);
			buffer.append(System.getProperty("line.separator"));
		}
	}

	@Override
	protected String getVersion(ElectricPowerModel model) {
		return DefaultMatPowerHeaderFactory.getInstance().getVersion();
	}

	@Override
	protected String getCase(ElectricPowerModel model) {
	  return DefaultMatPowerHeaderFactory.getInstance().getCase();
	}
	
	@Override
	protected void writeCoordinates(ElectricPowerModel model, StringBuffer buffer) {
		for (Bus bus : model.getBuses()) {
			buffer.append(getCoordinateLine(ids.get(bus),bus));
			buffer.append(System.getProperty("line.separator"));
		}		
	}

  @Override
  protected void writeExtraGeneratorInfo(ElectricPowerModel model, StringBuffer buffer) {
    for (Generator generator : model.getGenerators()) {
      ElectricPowerNode node = model.getNode(generator);
      buffer.append(getExtraGeneratorInfoLine(ids.get(node.getBus()),generator));
      buffer.append(System.getProperty("line.separator"));
    }   
  }

  
  @Override
  protected void writeBatteryInfo(ElectricPowerModel model, StringBuffer buffer) {
    for (Battery battery : model.getBatteries()) {
      ElectricPowerNode node = model.getNode(battery);
      buffer.append(getBatteryLine(ids.get(node.getBus()),battery));
      buffer.append(System.getProperty("line.separator"));
    }   
  }

	@Override
	protected void writeGenerators(ElectricPowerModel model, StringBuffer buffer) {
    TreeSet<ElectricPowerNode> set = new TreeSet<ElectricPowerNode>();
    set.addAll(model.getNodes());

    for (ElectricPowerNode node : set) {
      for (Generator generator : node.getComponents(Generator.class)) {
        int id = ids.get(model.getNode(generator).getBus());
        String  string = getMatPowerGeneratorLine(id, generator, node.getBus()); 
        buffer.append(string);
        buffer.append(System.getProperty("line.separator"));
      }
    }
	}
	
  /**
   * Try to figure out what the control area is
   * @param asset
   * @return
   */
  private ControlArea getArea(Asset asset, ElectricPowerModel model) {
    if (asset instanceof Bus) {
      return model.getControlArea(asset) != null ? model.getControlArea(asset) : model.getControlAreas().iterator().next();
    }
    else if (asset instanceof Component) {
      return model.getControlArea(asset) != null ? model.getControlArea(asset) : getArea(model.getNode((Component)asset).getBus(), model);
    }
    else if (asset instanceof Connection) {
      return model.getControlArea(asset) != null ? model.getControlArea(asset) : getArea(model.getFirstNode((Connection)asset).getBus(), model);
    }    
    return null;
  }
  
  /**
   * Try to figure out what the zone is
   * @param asset
   * @return
   */
  private Zone getZone(Asset asset, ElectricPowerModel model) {
    if (asset instanceof Bus) {
      return model.getZone(asset) != null ? model.getZone(asset) : model.getZones().iterator().next();
    }
    else if (asset instanceof Component) {
      return model.getZone(asset) != null ? model.getZone(asset) : getZone(model.getNode((Component)asset).getBus(), model);
    }
    else if (asset instanceof Connection) {
      return model.getZone(asset) != null ? model.getZone(asset) : getZone(model.getFirstNode((Connection)asset).getBus(), model);
    }    
    return null;
  }

  /**
   * Set up the buses with matpower data
   * @param model
   */
  private void setupBuses(ElectricPowerModel model) {
//    int id = MatPowerBusFactory.getInstance().findUnusedId();
    int id = tempModel.getBusFactory().findUnusedId();

    for (Bus bus : model.getBuses()) {
      id = fillWithMatPowerData(id,bus);
      ids.put(bus, bus.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, Integer.class));
    }
  }

  /**
   * Set up the buses with matpoer data
   * @param model
   */
  private void setupLoads(ElectricPowerModel model) {
    for (Load load : model.getLoads()) {
      Bus id = model.getNode(load).getBus();
      fillWithMatPowerData(id, load);
    }
  }

  /**
   * Set up the capacitors with pfw data
   * @param model
   */
  private void setupCapacitors(ElectricPowerModel model) {
    for (ShuntCapacitor capacitor : model.getShuntCapacitors()) {
      Bus id = model.getNode(capacitor).getBus();
      fillWithMatPowerData(id, capacitor);     
    }
  }

  /**
   * Set up the buses with pfw data
   * @param model
   */
  private void setupGenerators(ElectricPowerModel model) {
    double maxReal = 0;
    for (Generator generator : model.getGenerators()) {
      if (slackBus == null || maxReal < generator.getRealGeneration().doubleValue()) {
        slackBus = model.getNode(generator).getBus();
        maxReal = generator.getRealGeneration().doubleValue();
      }
    }
    
    // generators
    for (Generator generator : model.getGenerators()) {
      Bus id = model.getNode(generator).getBus();
      fillWithMatPowerData(id, generator);
    }

    // batteries
    for (Battery battery : model.getBatteries()) {
      Bus id = model.getNode(battery).getBus();
      fillWithMatPowerData(id, battery);
    }
  }  

  /**
   * Sets up the areas with matpower data
   * @param model
   */
  private void setupAreas(ElectricPowerModel model) {
    if (model.getControlAreas().size() == 0) {
//      model.addArea(MatPowerAreaFactory.getInstance().createArea(area));
      model.addArea(tempModel.getControlAreaFactory().createArea(area));
    }
    
    int tempArea = area;
    for (ControlArea controlArea : model.getControlAreas()) {
      fillWithMatPowerData(tempArea, controlArea);
      ++tempArea;
    }
  }

  /**
   * Set up the lines with matpower data
   * @param model
   */
  private void setupLines(ElectricPowerModel model) {
    for (Line line : model.getLines()) {
      Bus id1 = model.getFirstNode(line).getBus();
      Bus id2 = model.getSecondNode(line).getBus();
      fillWithMatPowerData(id1, id2, line);      
    }
  }
  
  /**
   * Set up the transformers with matpower data
   * @param model
   */
  private void setupTransformers(ElectricPowerModel model) {
    for (Transformer line : model.getTransformers()) {
      Bus id1 = model.getFirstNode(line).getBus();
      Bus id2 = model.getSecondNode(line).getBus();
      fillWithMatPowerData(id1, id2, line);      
    }  
  }  

  /**
   * Sets up the zones with pfw data
   * @param model
   */
  private void setupZones(ElectricPowerModel model) {
    if (model.getZones().size() == 0) {
//      model.addZone(MatPowerZoneFactory.getInstance().createZone(zone));
      model.addZone(tempModel.getZoneFactory().createZone(zone));
    }
    
    int tempZone = zone;
    for (Zone zone : model.getZones()) {
      fillWithMatPowerData(tempZone, zone);
      ++tempZone;
    }
  }

  
}
