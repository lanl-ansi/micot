package gov.lanl.micot.infrastructure.ep.model.opendss;


import gov.lanl.micot.infrastructure.ep.model.Battery;
import gov.lanl.micot.infrastructure.ep.model.BatteryFactory;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.util.io.dcom.ComObject;

/**
 * Factory for creating batteries
 * 
 * @author Russell Bent
 */
public class OpenDSSBatteryFactory extends BatteryFactory {

//  private static OpenDSSBatteryFactory INSTANCE           = null;
  private static final String LEGACY_TAG = "OpenDSS";
  
  //public static OpenDSSBatteryFactory getInstance() {
    //if (INSTANCE == null) {
      //INSTANCE = new OpenDSSBatteryFactory();
   // }
    //return INSTANCE;
  //}

  /**
   * Singleton constructor
   */
  protected OpenDSSBatteryFactory() {
  }

  /**
   * Creates an ieiss battery from another generator
   * 
   * @param generator
   * @param generatorData
   * @return
   */
  protected Battery createBattery(Battery battery, Bus bus) {
    throw new RuntimeException("OpenDSSBatteryFactory::createBattery");
    // TODO
    
    /*String legacyid = "Battery-" + battery.toString();
    if (battery.getAttribute(IeissModelConstants.IEISS_LEGACY_ID_KEY) == null) {
      battery.setAttribute(IeissModelConstants.IEISS_LEGACY_ID_KEY, legacyid);
      existingBatteries.put(legacyid, battery);
    }
   
    ieiss.domains.ep.entities.Generator ieissGenerator = new ieiss.domains.ep.entities.Generator();
    ieissGenerator.setId(legacyid);
    ieissGenerator.setName(legacyid);
    ieissGenerator.setLocation(new Point3d[] { new Point3d(battery.getCoordinate().getX(), battery.getCoordinate().getY(), 0) });
    ieissGenerator.setStatus(battery.getDesiredStatus() ? Status.operating : Status.off);
    ieissGenerator.setActive(battery.getDesiredStatus());
    ieissGenerator.setProductionRate(new ComplexNumber(battery.getDesiredProduction().doubleValue(), 0.0));
    ieissGenerator.setInitialProductionRate(new ComplexNumber(battery.getDesiredProduction().doubleValue(), 0.0));
    ieissGenerator.setMaximumProductionRate(new ComplexNumber(Math.min(battery.getMaximumProduction().doubleValue(), battery.getEnergyCapacity().doubleValue()-battery.getUsedEnergyCapacity().doubleValue()), 0.0));
    ieissGenerator.setMinimumProductionRate(new ComplexNumber(Math.max(battery.getMinimumProduction().doubleValue(),-battery.getUsedEnergyCapacity().doubleValue()), 0.0));
      
    battery.setAttribute(IeissModelConstants.IEISS_COMPONENT_KEY,ieissGenerator);
    battery.setAttribute(IeissModelConstants.IEISS_LEGACY_ID_KEY,ieissGenerator.getId());
    return battery;*/
  }

//	@Override
	//public Battery createBattery(Bus bus, double maxMW, double minMW, double capacity) {
	  //throw new RuntimeException("OpenDSSBatteryFactory::createBattery");
	/*		ieiss.domains.ep.entities.Generator generator = new ieiss.domains.ep.entities.Generator();
    String id = bus.getAttribute(IeissModelConstants.IEISS_COMPONENT_KEY, ieiss.domains.base.entities.Entity.class).getId().toString();
    generator.setId("Battery-" + id);
    
    Battery pair = createBattery(generator, bus);
    pair.setEnergyCapacity(capacity);
    pair.setMaximumProduction(maxMW);
    pair.setMinimumProduction(minMW);
    return pair;*/
//  }

	  /**
   * Creates a battery and data from a battery
   * 
   * @param line
   * @return
   */
  public Battery createBattery(ComObject iBattery, Bus bus, ComObject activeBattery) {
    throw new RuntimeException("OpenDSSBatteryFactory::createBattery");
    // TODO
/*    NodeAttachment attachment = (NodeAttachment) generator;
    String legacyid = attachment.getId().toString();    
    Battery battery= registerBattery(legacyid, bus);    
    fill(battery,generator);
    return battery;   */ 
  }

  /**
   * register the battery
   * @param legacyId
   * @param bus
   * @return
   */
  private Battery registerBattery(String legacyId, Bus bus) {
    Battery battery = getLegacy(LEGACY_TAG, legacyId);
    if (battery == null) {
      battery = createNewBattery();
      battery.setAttribute(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY,legacyId);
      battery.addOutputKey(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY); 
      registerLegacy(LEGACY_TAG, legacyId, battery);
    }
   return battery;
 }
	
  
}
