package gov.lanl.micot.infrastructure.ep.model.powerworld;

import java.util.ArrayList;

import gov.lanl.micot.infrastructure.ep.io.powerworld.PowerworldIOConstants;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.BusFactory;
import gov.lanl.micot.util.geometry.PointImpl;
import gov.lanl.micot.util.io.dcom.ComDataObject;
import gov.lanl.micot.util.io.dcom.ComObject;

/**
 * Factory for create bus related information from powerworld models
 * @author Russell Bent
 */
public class PowerworldBusFactory extends BusFactory {

  private static final String LEGACY_TAG = "Powerworld";
		
	/**
	 * Singleton constructor
	 */
	protected PowerworldBusFactory() {		
	}
	
  /**
   * Creates a bus and data from a bus
   * @param line
   * @return
   */
  public Bus createBus(ComObject powerworld, int id)  {
    
    String fields[] = new String[]{PowerworldIOConstants.BUS_NUM, PowerworldIOConstants.BUS_NAME, PowerworldIOConstants.BUS_PU_VOLTAGE, 
        PowerworldIOConstants.BUS_ANGLE, PowerworldIOConstants.BUS_LATITUDE, PowerworldIOConstants.BUS_LONGITUDE, 
        PowerworldIOConstants.BUS_KV, PowerworldIOConstants.BUS_MAX_VOLTAGE, PowerworldIOConstants.BUS_MIN_VOLTAGE, PowerworldIOConstants.BUS_CAT, PowerworldIOConstants.BUS_STATUS}; 
    String values[] = new String[] {id+"", "", "", "", "", "", "", "", "", "", ""};
        
    ComDataObject dataObject = powerworld.callData(PowerworldIOConstants.GET_PARAMETERS_SINGLE_ELEMENT, PowerworldIOConstants.BUS, fields, values);
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
    bus.setDesiredStatus(status);
    bus.setSystemVoltageKV(kvbase);
    bus.setActualStatus(status); 
    bus.setMaximumVoltagePU(maxPU);
    bus.setMinimumVoltagePU(minPU);
    bus.setPhaseAngle(angle);
    bus.setVoltagePU(pu); 
    
    bus.setAttribute(PowerworldModelConstants.POWERWORLD_BUS_CATEGORY_KEY, busCat);
    
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
