package gov.lanl.micot.infrastructure.ep.io.pfw;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import gov.lanl.micot.infrastructure.ep.model.Battery;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ControlArea;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Intertie;
import gov.lanl.micot.infrastructure.ep.model.Line;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitor;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitorSwitch;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.infrastructure.ep.model.Zone;
import gov.lanl.micot.infrastructure.ep.model.pfw.PFWHeader;
import gov.lanl.micot.infrastructure.ep.model.pfw.PFWModelConstants;
import gov.lanl.micot.infrastructure.ep.model.pfw.PFWModelImpl;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.model.Component;
import gov.lanl.micot.infrastructure.model.Connection;

/**
 * This class is intended to write PFW files for general non PFW models
 * 
 * @author Russell Bent
 */
public class GeneralPFWFileWriter extends PFWFileWriterImpl {

	private Map<Bus, Integer>										ids				= null;
	private Map<Bus, Map<Bus, Integer>>	circuits	= null;
	private int																	area			= -1;
	private int																	zone			= -1;
	private Bus																	slackBus	= null;
	private PFWModelImpl                        tempModel = null;
	
	/**
	 * Constructor
	 */
	protected GeneralPFWFileWriter() {
		area = 1;
		zone = 1;
		slackBus = null;
		tempModel = new PFWModelImpl();
	}

	@Override
	protected void writeBuses(ElectricPowerModel model, StringBuffer buffer) {
		ids = new HashMap<Bus, Integer>();
		circuits = new HashMap<Bus, Map<Bus, Integer>>();
    setupAreas(model);
    setupZones(model);
    setupBuses(model);
    setupLoads(model);
    setupGenerators(model);
    setupCapacitors(model);
    setupSwitchedShunts(model);
    setupLines(model);
    setupTransformers(model);
    setupInterties(model);
    			
		for (Bus bus : model.getBuses()) {
      ControlArea area = getArea(bus,model);
      Zone zone = getZone(bus,model);
			String line = getPFWBusLine(bus, area, zone);
			buffer.append(line);
			buffer.append(System.getProperty("line.separator"));
			ids.put(bus, bus.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class));
		}
	}

	/**
	 * Create the pfw data that we might need
	 * @param id
	 * @param bus
	 * @return
	 */
	private int fillWithPFWData(int id, Bus bus) {
//	  PFWBusFactory.getInstance().updateBus(bus, id);
	  tempModel.getBusFactory().updateBus(bus, id);
	  return Math.max(id, bus.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class) + 1);
	}

	/**
   * Create the pfw data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithPFWData(Bus id, ShuntCapacitorSwitch shunt) {
//    PFWSwitchedShuntFactory.getInstance().updateSwitchedShunt(shunt, id);
    tempModel.getShuntCapacitorSwitchFactory().updateSwitchedShunt(shunt, id);
  }
  
  /**
   * Create the pfw data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithPFWData(Bus id, ShuntCapacitor shunt) {
//    PFWShuntFactory.getInstance().updateShunt(shunt, id);
    tempModel.getShuntCapacitorFactory().updateShunt(shunt, id);
  }

  /**
   * Create the pfw data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithPFWData(Bus id1, Bus id2, Line line) {
//    PFWLineFactory.getInstance().updateLine(line, id1, id2, getCircuit(id1,id2));
    tempModel.getLineFactory().updateLine(line, id1, id2, getCircuit(id1,id2));
  }

  /**
   * Create the pfw data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithPFWData(Bus id1, Bus id2, Transformer line) {
//    PFWTransformerFactory.getInstance().updateTransformer(line, id1, id2, getCircuit(id1,id2));
    tempModel.getTransformerFactory().updateTransformer(line, id1, id2, getCircuit(id1,id2));
  }

  /**
   * Create the pfw data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithPFWData(Bus id1, Bus id2, Intertie intertie) {
//    PFWIntertieFactory.getInstance().updateIntertie(intertie, id1, id2, getCircuit(id1,id2));
    tempModel.getIntertieFactory().updateIntertie(intertie, id1, id2, getCircuit(id1,id2));
  }
  
  /**
   * Create the pfw data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithPFWData(Bus bus, Load load) {
//    PFWLoadFactory.getInstance().updateLoad(load, bus);
    tempModel.getLoadFactory().updateLoad(load, bus);
  }
  
  /**
   * Create the pfw data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithPFWData(int id, ControlArea area) {
//    PFWAreaFactory.getInstance().updateArea(area, id);
    tempModel.getControlAreaFactory().updateArea(area, id);
  }
  
  /**
   * Create the pfw data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithPFWData(int id, Zone zone) {
//    PFWZoneFactory.getInstance().updateZone(zone,id);
    tempModel.getZoneFactory().updateZone(zone,id);
  }
  
  /**
   * Create the pfw data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithPFWDataGenerator(Bus id, Collection<Generator> generators) {
//    PFWGeneratorFactory.getInstance().updateGenerators(generators, id);
    tempModel.getGeneratorFactory().updateGenerators(generators, id);
  }
 
  /**
   * Create the pfw data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithPFWDataBattery(Bus id, Collection<Battery> batteries) {
//    PFWBatteryFactory.getInstance().updateBatteries(batteries, id);
    tempModel.getBatteryFactory().updateBatteries(batteries, id);
  }
  
	@Override
	protected void writeAreas(ElectricPowerModel model, StringBuffer buffer) {
	  for (ControlArea controlArea : model.getControlAreas()) {
	    Bus sb = model.getSlackBus(controlArea) != null ? model.getSlackBus(controlArea) : slackBus; 
	    String line = getPFWAreaLine(controlArea, sb);      
	    buffer.append(line);
	    buffer.append(System.getProperty("line.separator"));
	  }	  
	}

	@Override
	protected void writeZones(ElectricPowerModel model, StringBuffer buffer) {
	  for (Zone zone : model.getZones()) {
	    String line = getPFWZoneLine(zone);      
	    buffer.append(line);
	    buffer.append(System.getProperty("line.separator"));
	  }   
	}

	@Override
	protected void writeGenerators(ElectricPowerModel model, StringBuffer buffer) {
		// write the generator of the buses
		for (Bus bus : model.getBuses()) {
			Collection<Generator> generators = (Collection<Generator>) model.getNode(bus).getComponents(Generator.class);
			if (generators.size() > 0) {
	      Generator generator = generators.iterator().next();
	      ControlArea area = getArea(generator, model);
	      Zone zone = getZone(generator, model);
	      Bus remoteBus = model.getControlBus(generator) != null ? model.getControlBus(generator) : bus;

				String line = getPFWGeneratorLine(new MultiGenerator(generators), bus, area, zone, remoteBus);
				buffer.append(line);
				buffer.append(System.getProperty("line.separator"));
			}
		}
		
		// write the battery information as generators so they show up in the computation
		
		for (Bus bus : model.getBuses()) {
			Collection<Battery> batteries = (Collection<Battery>) model.getNode(bus).getComponents(Battery.class);
			if (batteries.size() > 0) {
	      Battery battery = batteries.iterator().next();
	      ControlArea area = getArea(battery,model);
	      Zone zone = getZone(battery, model);
	      Bus remoteBus = model.getControlBus(battery) != null ? model.getControlBus(battery) : bus;

				String line = getPFWGeneratorLine(new MultiBattery(batteries), bus, area, zone, remoteBus);
				buffer.append(line);
				buffer.append(System.getProperty("line.separator"));
			}
		}

	}

	@Override
	protected void writeInterties(ElectricPowerModel model, StringBuffer buffer) {
		for (Intertie intertie : model.getInterties()) {
	    ControlArea meteredArea = model.getMeteredArea(intertie) != null ? model.getMeteredArea(intertie) : model.getControlAreas().iterator().next();
	    ControlArea nonMeteredArea = model.getNonMeteredArea(intertie) != null ? model.getNonMeteredArea(intertie) : model.getControlAreas().iterator().next();     
	    String line = getPFWIntertieLine(intertie, model.getFirstNode(intertie).getBus(), model.getSecondNode(intertie).getBus(), meteredArea, nonMeteredArea);
			buffer.append(line);
			buffer.append(System.getProperty("line.separator"));
		}
	}

	/**
	 * Create pseudo circuits
	 * @param model
	 * @param id1
	 * @param id2
	 * @return
	 */
	private String getCircuit(Bus id1, Bus id2) {
		if (circuits.get(id1) == null) {
			circuits.put(id1, new HashMap<Bus, Integer>());
		}
		if (circuits.get(id1).get(id2) == null) {
			circuits.get(id1).put(id2, 0);
		}
		int circuit = circuits.get(id1).get(id2);
		circuits.get(id1).put(id2, circuit + 1);
		String string = new Integer(circuit).toString();
		if (string.length() == 1) {
			string = "\" " + string + "\"";
		}
		else {
			string = "\" " + string.substring(0,1) + "\"";
		}
		
		return string;
	}

	@Override
	protected void writeLines(ElectricPowerModel model, StringBuffer buffer) {
		for (Line line : model.getLines()) {
      ControlArea area = getArea(line, model);
      Zone zone = getZone(line, model);
	    String string = getPFWLineLine(line, model.getFirstNode(line).getBus(), model.getSecondNode(line).getBus(), area, zone); 
			buffer.append(string);
			buffer.append(System.getProperty("line.separator"));
		}
	}

	@Override
	protected void writeTransformers(ElectricPowerModel model, StringBuffer buffer) {
		for (Transformer line : model.getTransformers()) {
      ControlArea area = getArea(line, model);
      Zone zone = getZone(line, model);
      Bus remoteBus = model.getControlBus(line) != null ? model.getControlBus(line) : model.getFirstNode(line).getBus();
      String string = getPFWTransformerLine(line, model.getFirstNode(line).getBus(), model.getSecondNode(line).getBus(), area, zone, remoteBus); 
			buffer.append(string);
			buffer.append(System.getProperty("line.separator"));
		}
	}

	@Override
	protected void writeLoads(ElectricPowerModel model, StringBuffer buffer) {
		for (Load load : model.getLoads()) {
      ControlArea area = getArea(load,model);
      Zone zone = getZone(load, model);
      String line = getPFWLoadLine(load, area, zone);      
			buffer.append(line);
			buffer.append(System.getProperty("line.separator"));
		}
	}

	@Override
	protected void writeShunts(ElectricPowerModel model, StringBuffer buffer) {
		for (ShuntCapacitor shunt : model.getShuntCapacitors()) {
      ControlArea area = getArea(shunt,model);
      Zone zone = getZone(shunt, model);
			String line = getPFWShuntLine(shunt, area, zone);
			buffer.append(line);
			buffer.append(System.getProperty("line.separator"));
		}
	}

	@Override
	protected void writeSwitchedShunts(ElectricPowerModel model, StringBuffer buffer) {
		for (ShuntCapacitorSwitch shunt : model.getShuntCapacitorSwitches()) {
		  Bus remoteBus = model.getControlBus(shunt) != null ? model.getControlBus(shunt) : model.getNode(shunt).getBus();
			String line = getPFWSwitchedShuntLine(shunt, getArea(shunt,model), getZone(shunt,model), remoteBus);
			buffer.append(line);
			buffer.append(System.getProperty("line.separator"));
		}
	}

	@Override
	protected PFWHeader getHeader(ElectricPowerModel model) {
		return DefaultPFWHeaderFactory.getInstance().createDefaultPFWFileHeader();
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
   * Sets up the areas with pfw data
   * @param model
   */
  private void setupAreas(ElectricPowerModel model) {
    if (model.getControlAreas().size() == 0) {
//      model.addArea(PFWAreaFactory.getInstance().createArea(area));
      model.addArea(tempModel.getControlAreaFactory().createArea(area));
    }
    
    int tempArea = area;
    for (ControlArea controlArea : model.getControlAreas()) {
      fillWithPFWData(tempArea, controlArea);
      ++tempArea;
    }
  }

  /**
   * Sets up the zones with pfw data
   * @param model
   */
  private void setupZones(ElectricPowerModel model) {
    if (model.getZones().size() == 0) {
//      model.addZone(PFWZoneFactory.getInstance().createZone(zone));
      model.addZone(tempModel.getZoneFactory().createZone(zone));
    }
    
    int tempZone = zone;
    for (Zone zone : model.getZones()) {
      fillWithPFWData(tempZone, zone);
      ++tempZone;
    }
  }
  
  /**
   * Set up the buses with pfw data
   * @param model
   */
  private void setupGenerators(ElectricPowerModel model) {
    double maxReal = 0;
    for (Generator generator : model.getGenerators()) {
      if (maxReal < generator.getRealGeneration().doubleValue()) {
        slackBus = model.getNode(generator).getBus();
        maxReal = generator.getRealGeneration().doubleValue();
      }
    }
    
    // generators
    for (Bus bus : model.getBuses()) {
      Collection<Generator> generators = (Collection<Generator>) model.getNode(bus).getComponents(Generator.class);
      if (generators.size() > 0) {
        Bus id = model.getNode(generators.iterator().next()).getBus();
        fillWithPFWDataGenerator(id, generators);
      }
    }
    
    // batteries   
    for (Bus bus : model.getBuses()) {
      Collection<Battery> batteries = (Collection<Battery>) model.getNode(bus).getComponents(Battery.class);
      if (batteries.size() > 0) {
        Bus id = model.getNode(batteries.iterator().next()).getBus();
        fillWithPFWDataBattery(id, batteries);
      }
    }
  }  
  
  
  /**
   * Set up the buses with pfw data
   * @param model
   */
  private void setupBuses(ElectricPowerModel model) {
    int id = 1; // can't start with 0 as t2000 ids must start with 1
    for (Bus bus : model.getBuses()) {
      id = fillWithPFWData(id,bus);
      ids.put(bus, bus.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class));
    }
  }

  /**
   * Set up the buses with pfw data
   * @param model
   */
  private void setupLoads(ElectricPowerModel model) {
    for (Load load : model.getLoads()) {
      Bus id = model.getNode(load).getBus();
      fillWithPFWData(id, load);
    }
  }

  /**
   * Set up the capacitors with pfw data
   * @param model
   */
  private void setupCapacitors(ElectricPowerModel model) {
    for (ShuntCapacitor capacitor : model.getShuntCapacitors()) {
      Bus id = model.getNode(capacitor).getBus();
      fillWithPFWData(id, capacitor);     
    }
  }

  /**
   * Set up the switched capacitors with pfw data
   * @param model
   */
  private void setupSwitchedShunts(ElectricPowerModel model) {
    for (ShuntCapacitorSwitch shunt : model.getShuntCapacitorSwitches()) {
      Bus id = model.getNode(shunt).getBus();
      fillWithPFWData(id, shunt);
    }
  }

  /**
   * Set up the lines with pfw data
   * @param model
   */
  private void setupLines(ElectricPowerModel model) {
    for (Line line : model.getLines()) {
      Bus id1 = model.getFirstNode(line).getBus();
      Bus id2 = model.getSecondNode(line).getBus();
      fillWithPFWData(id1, id2, line);      
    }
  }
  
  /**
   * Set up the transformers with pfw data
   * @param model
   */
  private void setupTransformers(ElectricPowerModel model) {
    for (Transformer line : model.getTransformers()) {
      Bus id1 = model.getFirstNode(line).getBus();
      Bus id2 = model.getSecondNode(line).getBus();
      fillWithPFWData(id1, id2, line);      
    }  
  }  

  /**
   * Set up the interties with pfw data
   * @param model
   */
  private void setupInterties(ElectricPowerModel model) {
    for (Intertie intertie : model.getInterties()) {
      Bus id1 = model.getFirstNode(intertie).getBus();
      Bus id2 = model.getSecondNode(intertie).getBus();
      fillWithPFWData(id1, id2, intertie);      
    }
  }  

  
}
