package gov.lanl.micot.infrastructure.ep.io.cdf;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import gov.lanl.micot.infrastructure.ep.model.Battery;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ControlArea;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.GeneratorTypeEnum;
import gov.lanl.micot.infrastructure.ep.model.Intertie;
import gov.lanl.micot.infrastructure.ep.model.Line;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitor;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.infrastructure.ep.model.Zone;
import gov.lanl.micot.infrastructure.ep.model.cdf.CDFHeader;
import gov.lanl.micot.infrastructure.ep.model.cdf.CDFModelConstants;
import gov.lanl.micot.infrastructure.ep.model.cdf.CDFModelImpl;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.model.Component;
import gov.lanl.micot.infrastructure.model.Connection;
import gov.lanl.micot.util.geometry.PointImpl;

/**
 * This class is intended to write CDF files for general non PFW models
 * 
 * @author Russell Bent
 */
public class GeneralCDFFileWriter extends CDFFileWriterImpl {

	private Map<Bus, Integer>										ids				= null;
	private Map<Bus, Map<Bus, Integer>>	circuits	        = null;
	private int																	area			= -1;
	private int																	zone			= -1;
	private Bus																	slackBus	= null;
	private CDFModelImpl  tempModel                       = null;
	
	/**
	 * Constructor
	 */
	protected GeneralCDFFileWriter() {
		area = 1;
		zone = 1;
		slackBus = null;
		tempModel = new CDFModelImpl();
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
    setupLines(model);
    setupTransformers(model);
    setupInterties(model);
		
		// write the bus data
		for (ElectricPowerNode node : model.getNodes()) {
		  Bus bus = node.getBus();
		  Load load = node.getLoad();
		  ShuntCapacitor shunt = node.getShunt();
	    Collection<Generator> generators = (Collection<Generator>) model.getNode(bus).getComponents(Generator.class);
	    Generator generator = null; // need to fix
	    
	    Bus remoteBus = model.getControlBus(generator) != null ? model.getControlBus(generator) : slackBus;
      String line = getCDFBusLine(bus, generator, load, shunt, getArea(bus,model), getZone(bus,model), remoteBus);
      buffer.append(line);
      buffer.append(System.getProperty("line.separator"));
		}		
	}

	/**
	 * Create the pfw data that we might need
	 * @param id
	 * @param bus
	 * @return
	 */
	private int fillWithCDFData(int id, Bus bus) {
	  tempModel.getBusFactory().updateBus(bus, id);
//	  CDFBusFactory.getInstance().updateBus(bus, id);
	  return Math.max(id, bus.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, Integer.class) + 1);
	}

  /**
   * Create the pfw data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithCDFData(Bus id, ShuntCapacitor shunt) {
    //CDFShuntFactory2.getInstance().updateShunt(shunt, id);
    tempModel.getShuntCapacitorFactory().updateShunt(shunt, id);
  }

  /**
   * Create the pfw data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithCDFData(Bus id1, Bus id2, Line line) {
    //CDFLineFactory.getInstance().updateLine(line, id1, id2, getCircuit(id1,id2));    
    tempModel.getLineFactory().updateLine(line, id1, id2, getCircuit(id1,id2));        
  }

  /**
   * Create the pfw data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithCDFData(Bus fromBus, Bus toBus, Transformer line) {
//    CDFTransformerFactory2.getInstance().updateTransformer(line, fromBus, toBus, getCircuit(fromBus,toBus));
    tempModel.getTransformerFactory().updateTransformer(line, fromBus, toBus, getCircuit(fromBus,toBus));

  }

  /**
   * Create the pfw data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithCDFData(Bus fromBus, Bus toBus, Intertie intertie) {
//    CDFIntertieFactory.getInstance().updateIntertie(intertie, fromBus, toBus, getCircuit(fromBus,toBus));
    tempModel.getIntertieFactory().updateIntertie(intertie, fromBus, toBus, getCircuit(fromBus,toBus));
  }
  
  /**
   * Create the pfw data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithCDFData(Bus bus, Load load) {
//    CDFLoadFactory2.getInstance().updateLoad(load, bus);
    tempModel.getLoadFactory().updateLoad(load, bus);
  }
  
  /**
   * Create the pfw data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithCDFData(int id, ControlArea area) {
//    CDFAreaFactory.getInstance().updateArea(area, id);
    tempModel.getControlAreaFactory().updateArea(area, id);
  }
  
  /**
   * Create the pfw data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithCDFData(int id, Zone zone) {
//    CDFZoneFactory2.getInstance().updateZone(zone,id);
    tempModel.getZoneFactory().updateZone(zone,id);
  }
  
  /**
   * Create the pfw data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithCDFDataGenerator(Bus id, Collection<Generator> generators) {
//    CDFGeneratorFactory.getInstance().updateGenerators(generators, id);
    tempModel.getGeneratorFactory().updateGenerators(generators, id);

  }
 
  /**
   * Create the pfw data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithCDFDataBattery(Bus id, Collection<Battery> batteries) {
//    CDFBatteryFactory.getInstance().updateBatteries(batteries, id);
    tempModel.getBatteryFactory().updateBatteries(batteries, id);
  }
  
	@Override
	protected void writeAreas(ElectricPowerModel model, StringBuffer buffer) {
	  for (ControlArea controlArea : model.getControlAreas()) {
	    String line = getCDFAreaLine(controlArea, model.getSlackBus(controlArea));      
	    buffer.append(line);
	    buffer.append(System.getProperty("line.separator"));
	  }	  
	}

	@Override
	protected void writeZones(ElectricPowerModel model, StringBuffer buffer) {
	  for (Zone zone : model.getZones()) {
	    String line = getCDFZoneLine(zone);      
	    buffer.append(line);
	    buffer.append(System.getProperty("line.separator"));
	  }   
	}


	@Override
	protected void writeInterties(ElectricPowerModel model, StringBuffer buffer) {
		for (Intertie intertie : model.getInterties()) {
      ControlArea meteredArea = model.getMeteredArea(intertie) != null ? model.getMeteredArea(intertie) : model.getControlAreas().iterator().next();
      ControlArea nonMeteredArea = model.getNonMeteredArea(intertie) != null ? model.getNonMeteredArea(intertie) : model.getControlAreas().iterator().next();     
			String line = getCDFIntertieLine(intertie, model.getFirstNode(intertie).getBus(), model.getSecondNode(intertie).getBus(), meteredArea, nonMeteredArea);
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
		return string;
	}

	@Override
	protected void writeFlowLines(ElectricPowerModel model, StringBuffer buffer) {
		for (ElectricPowerFlowConnection line : model.getFlowConnections()) {
		  Bus fromBus = model.getFirstNode(line).getBus();
      Bus toBus = model.getSecondNode(line).getBus();		  
      String string = null;
      if (line instanceof Line) {
	      string = getCDFLineLine((Line)line, fromBus, toBus, getArea(line,model), getZone(line,model)); 
      }
      else {
        Bus remoteBus = model.getControlBus(line) != null ? model.getControlBus(line) : model.getFirstNode(line).getBus();
        string = getCDFTransformerLine((Transformer)line, fromBus, toBus,getArea(line,model), getZone(line,model), remoteBus); 
      }
			buffer.append(string);
			buffer.append(System.getProperty("line.separator"));
		}
	}

	@Override
	protected CDFHeader getHeader(ElectricPowerModel model) {
		return DefaultCDFHeaderFactory.getInstance().createDefaultCDFFileHeader(model.getMVABase());
	}

	@Override
	protected void writeCoordinates(ElectricPowerModel model, StringBuffer buffer) {
		for (Bus bus : model.getBuses()) {
		  if (bus.getCoordinate() == null) {
		    bus.setCoordinate(new PointImpl(0,0));
		  }		  
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
   * Set up the buses with cdf data
   * @param model
   */
  private void setupBuses(ElectricPowerModel model) {
    // find the slack bus and index the buses
    int id = 1; // can't start with 0 as cdf ids must start with 1
    for (ElectricPowerNode node : model.getNodes()) {
      Bus bus = node.getBus();
      id = fillWithCDFData(id,bus);
      ids.put(bus, bus.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, Integer.class));
      for (Generator generator : node.getComponents(Generator.class)) {
        if (generator.getType().equals(GeneratorTypeEnum.REFERENCE_BUS_TYPE)) {
          slackBus = bus;
        }
      }     
    }
    
    if (slackBus == null) {
      double maxReal = 0;
      for (Generator generator : model.getGenerators()) {
        if (maxReal < generator.getRealGeneration().doubleValue()) {
          slackBus = model.getNode(generator).getBus();
          maxReal = generator.getRealGeneration().doubleValue();
        }
      }
    }
  }

  /**
   * Set up the buses with cdf data
   * @param model
   */
  private void setupLoads(ElectricPowerModel model) {
    for (Load load : model.getLoads()) {
      Bus id = model.getNode(load).getBus();
      fillWithCDFData(id, load);
    }
  }

  /**
   * Set up the capacitors with cdf data
   * @param model
   */
  private void setupCapacitors(ElectricPowerModel model) {
    for (ShuntCapacitor capacitor : model.getShuntCapacitors()) {
      Bus id = model.getNode(capacitor).getBus();
      fillWithCDFData(id, capacitor);     
    }
  }

  /**
   * Set up the buses with cdf data
   * @param model
   */
  private void setupGenerators(ElectricPowerModel model) {
    // generators
    for (Bus bus : model.getBuses()) {
      Collection<Generator> generators = (Collection<Generator>) model.getNode(bus).getComponents(Generator.class);
      if (generators.size() > 0) {
        Bus id = model.getNode(generators.iterator().next()).getBus();
        fillWithCDFDataGenerator(id, generators);
      }
    }
    
    // batteries   
    for (Bus bus : model.getBuses()) {
      Collection<Battery> batteries = (Collection<Battery>) model.getNode(bus).getComponents(Battery.class);
      if (batteries.size() > 0) {
        Bus id = model.getNode(batteries.iterator().next()).getBus();
        fillWithCDFDataBattery(id, batteries);
      }
    }
  }  

  /**
   * Sets up the areas with cdf data
   * @param model
   */
  private void setupAreas(ElectricPowerModel model) {
    if (model.getControlAreas().size() == 0) {
//      model.addArea(CDFAreaFactory.getInstance().createArea(area));
      model.addArea(tempModel.getControlAreaFactory().createArea(area));
    }
    
    int tempArea = area;
    for (ControlArea controlArea : model.getControlAreas()) {
      fillWithCDFData(tempArea, controlArea);
    }
  }

  /**
   * Sets up the zones with cdf data
   * @param model
   */
  private void setupZones(ElectricPowerModel model) {
    if (model.getZones().size() == 0) {
//      model.addZone(CDFZoneFactory2.getInstance().createZone(zone));
      model.addZone(tempModel.getZoneFactory().createZone(zone));
    }
    
    int tempZone = zone;
    for (Zone zone : model.getZones()) {
      fillWithCDFData(tempZone, zone);
      ++tempZone;
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
      fillWithCDFData(id1, id2, intertie);      
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
      fillWithCDFData(id1, id2, line);      
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
      fillWithCDFData(id1, id2, line);      
    }  
  }  
  
}
