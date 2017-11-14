package gov.lanl.micot.infrastructure.ep.model.powerworld;

import java.util.ArrayList;

import gov.lanl.micot.infrastructure.ep.io.powerworld.PowerworldIOConstants;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.BusFactory;
import gov.lanl.micot.util.collection.Pair;
import gov.lanl.micot.util.geometry.PointImpl;
import gov.lanl.micot.util.io.dcom.ComDataObject;
import gov.lanl.micot.util.io.dcom.ComObject;

/**
 * Factory for create bus related information from powerworld models
 * @author Russell Bent
 */
public class PowerworldBusFactory extends BusFactory {

  private static final String LEGACY_TAG = "Powerworld";
	
  protected static final String DATA_FIELDS[] = new String[] { 
      PowerworldIOConstants.BUS_NUM, PowerworldIOConstants.BUS_NAME, 
      PowerworldIOConstants.BUS_PU_VOLTAGE, PowerworldIOConstants.BUS_ANGLE, 
      PowerworldIOConstants.BUS_LATITUDE, PowerworldIOConstants.BUS_LONGITUDE, 
      PowerworldIOConstants.BUS_KV_NOMINAL_VOLTAGE, PowerworldIOConstants.BUS_MAX_VOLTAGE, 
      PowerworldIOConstants.BUS_MIN_VOLTAGE, PowerworldIOConstants.BUS_CAT, 
      PowerworldIOConstants.BUS_STATUS, PowerworldIOConstants.BUS_OWNER_NAME,
      PowerworldIOConstants.BUS_AREA, PowerworldIOConstants.BUS_ZONE, 
      PowerworldIOConstants.BUS_SLACK
  }; 
  
  /**
	 * Singleton constructor
	 */
	protected PowerworldBusFactory() {		
	}
	

  public Bus createBus(ComObject powerworld, int id)  {  
    String values[] = new String[] {id+"", "", "", "", "", "", "", "", "", "", "", "","","",""};
        
    ComDataObject dataObject = powerworld.callData(PowerworldIOConstants.GET_PARAMETERS_SINGLE_ELEMENT, PowerworldIOConstants.BUS, DATA_FIELDS, values);
    ArrayList<ComDataObject> busData = dataObject.getArrayValue();
    String errorString = busData.get(0).getStringValue();
    if (!errorString.equals("")) {
      System.err.println("Error getting powerworld bus data: " + errorString);                
    }
    
    ArrayList<ComDataObject> bData = busData.get(1).getArrayValue();                       
    String name = bData.get(1).getStringValue();
    String puString = bData.get(2).getStringValue();
    String angleString = bData.get(3).getStringValue();
    String latitude = bData.get(4).getStringValue();
    String longitude = bData.get(5).getStringValue();
    String kvString = bData.get(6).getStringValue();
    String maxPUString = bData.get(7).getStringValue();
    String minPUString = bData.get(8).getStringValue();
    String busCat = bData.get(9).getStringValue();
    String statusString = bData.get(10).getStringValue();
    String ownerName = bData.get(11).getStringValue();
    
    double pu = Double.parseDouble(puString.trim());
    double angle = Double.parseDouble(angleString.trim());
    double x = longitude == null ? 0.0 : Double.parseDouble(longitude.trim());
    double y = latitude == null ? 0.0 : Double.parseDouble(latitude.trim());
    boolean status = statusString.equalsIgnoreCase(PowerworldIOConstants.BUS_CONNECTED) && !busCat.equalsIgnoreCase(PowerworldIOConstants.BUS_DEAD);
    double kvbase = Double.parseDouble(kvString.trim());
    double maxPU = Double.parseDouble(maxPUString.trim());
    double minPU = Double.parseDouble(minPUString.trim());
    
    Bus bus = registerBus(id);   
    bus.setCoordinate(new PointImpl(x,y));
    bus.setAttribute(Bus.NAME_KEY, name);
    bus.setStatus(status);
    bus.setSystemVoltageKV(kvbase);
    bus.setMaximumVoltagePU(maxPU);
    bus.setMinimumVoltagePU(minPU);
    bus.setPhaseAngle(angle);
    bus.setVoltagePU(pu); 
    bus.setOwnerName(ownerName);
    
    bus.setAttribute(PowerworldModelConstants.POWERWORLD_BUS_CATEGORY_KEY, busCat);
    
    return bus;    
  }
  
  /**
   * Creates a dc bus and data from a bus
   * @param line
   * @return
   */
  public Bus createDCBus(ComObject powerworld, int id, int id2)  {
    
    String fields[] = new String[]{PowerworldIOConstants.DC_BUS_NUM, PowerworldIOConstants.DC_RECORD_NUM, PowerworldIOConstants.DC_BUS_NAME, PowerworldIOConstants.DC_BUS_VOLTAGE_KV}; 
    String values[] = new String[] {id+"", id2+"", "", ""};
        
    ComDataObject dataObject = powerworld.callData(PowerworldIOConstants.GET_PARAMETERS_SINGLE_ELEMENT, PowerworldIOConstants.DC_BUS, fields, values);
    ArrayList<ComDataObject> busData = dataObject.getArrayValue();
    String errorString = busData.get(0).getStringValue();
    if (!errorString.equals("")) {
      System.err.println("Error getting powerworld bus data: " + errorString);                
    }
    
    ArrayList<ComDataObject> bData = busData.get(1).getArrayValue();                       
    String name = bData.get(1).getStringValue();
    String kvString = bData.get(2).getStringValue();
    
    double x = 0.0;
    double y = 0.0;
    boolean status = true;
    double kv = Double.parseDouble(kvString.trim());
    
    Bus bus = registerDCBus(new Pair<Integer, Integer>(id, id2));   
    bus.setCoordinate(new PointImpl(x,y));
    bus.setAttribute(Bus.NAME_KEY, name);
    bus.setStatus(status);
    bus.setVoltagePU(kv);
    
    bus.setAttribute(PowerworldModelConstants.POWERWORLD_BUS_CATEGORY_KEY, PowerworldModelConstants.POWER_WORLD_DC_BUS_CAT);
    
    return bus;    
  }
  
  @Override
  protected Bus createEmptyBus() {
    int id = findUnusedId();
    Bus bus = registerBus(id);
    return bus;
  }

  /**
  * Register the bus
  * @param legacyId
  * @param bus
  * @return
  */
  private Bus registerBus(int legacyId) {
    Bus bus = getLegacy(LEGACY_TAG, legacyId);
    if (bus == null) {
      bus = createNewBus();
      bus.setAttribute(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY,legacyId);
      bus.addOutputKey(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY);      
      registerLegacy(LEGACY_TAG, legacyId, bus);
    }
   return bus;
 }

  /**
  * Register the bus
  * @param legacyId
  * @param bus
  * @return
  */
  private Bus registerDCBus(Pair<Integer, Integer> legacyId) {
    Bus bus = getLegacy(LEGACY_TAG, legacyId);
    if (bus == null) {
      bus = createNewBus();
      bus.setAttribute(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY,legacyId);
      bus.addOutputKey(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY);      
      registerLegacy(LEGACY_TAG, legacyId, bus);
    }
   return bus;
 }

  
  /**
   * Find an unused id number
   * @return
   */
  private int findUnusedId() {
    for (int i = 0; i < Integer.MAX_VALUE; ++i) {
      if (!doesLegacyExist(LEGACY_TAG,i+"")) {
        return i;
      }
    }
    throw new RuntimeException("Error: Cannot find an unused id");
  }

	
}
