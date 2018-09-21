package gov.lanl.micot.infrastructure.ep.model.opendss;

import gov.lanl.micot.infrastructure.ep.io.opendss.OpenDSSIOConstants;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.BusFactory;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.PointImpl;
import gov.lanl.micot.util.io.dcom.ComObject;

/**
 * Factory for create bus related information
 * @author Russell Bent
 */
public class OpenDSSBusFactory extends BusFactory {

  private static final String LEGACY_TAG = "OpenDSS";
	
	/**
	 * Singleton constructor
	 */
	protected OpenDSSBusFactory() {		
	}
	
  /**
   * Creates a bus and data from a bus
   * @param line
   * @return
   */
  public Bus createBus(ComObject bus, ComObject activeBus)  {
    String name = bus.getString(OpenDSSIOConstants.BUS_NAME, new Object[0]);    
    Bus openDSSBus = registerBus(name);   

    double x = bus.getDouble(OpenDSSIOConstants.BUS_XCOORD);
    double y = bus.getDouble(OpenDSSIOConstants.BUS_YCOORD);
        
    double kvbase = bus.getDouble(OpenDSSIOConstants.BUS_KVBASE);
    boolean status = activeBus.getBoolean(OpenDSSIOConstants.BUS_STATUS);
    
    openDSSBus.setCoordinate(new PointImpl(x,y));
    openDSSBus.setAttribute(Bus.NAME_KEY, name);
    
    if (!OpenDSSModelFactory.HACK_8500_NODE_SYSTEM) {
      openDSSBus.setMaximumVoltagePU(1.2); // some defaults.... loads will set their own limits
      openDSSBus.setMinimumVoltagePU(0.8);
    }
    else {
      openDSSBus.setMaximumVoltagePU(1.12); 
      openDSSBus.setMinimumVoltagePU(0.88);
    }
    openDSSBus.setAttribute(Bus.VOLTAGE_PU_A_KEY, 1.0);
    openDSSBus.setAttribute(Bus.VOLTAGE_PU_B_KEY, 1.0);
    openDSSBus.setAttribute(Bus.VOLTAGE_PU_C_KEY, 1.0);
    
    openDSSBus.setStatus(status);
    openDSSBus.setSystemVoltageKV(kvbase);
    return openDSSBus;    
  }
  
  @Override
  protected Bus createEmptyBus() {
    String id = findUnusedId();
    Bus bus = registerBus(id);
    return bus;
  }

  /**
  * Register the bus
  * @param legacyId
  * @param bus
  * @return
  */
  private Bus registerBus(String legacyId) {
    Bus bus = getLegacy(LEGACY_TAG, legacyId);
    if (bus == null) {
      bus = createNewBus();
      bus.setAttribute(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY,legacyId);
      bus.addOutputKey(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY);      
      registerLegacy(LEGACY_TAG, legacyId, bus);
    }
   return bus;
 }

  /**
   * Find an unused id number
   * @return
   */
  private String findUnusedId() {
    for (int i = 0; i < Integer.MAX_VALUE; ++i) {
      if (!doesLegacyExist(LEGACY_TAG,i+"")) {
        return i+"";
      }
    }
    throw new RuntimeException("Error: Cannot find an unused id");
  }

  /**
   * Create a bus from text data
   * @param id
   * @param busCoordData
   * @return
   */
  public Bus createBus(String name, Point busCoordData) {

	//System.err.println("Emre, pattern a bus creation routine like the function createBus(ComObject bus, ComObject activeBus)");
	  
    Bus openDSSBus = registerBus(name);   

    double x = busCoordData.getX();
    double y = busCoordData.getY();
        
    // TODO how to get these values, they are not passed with Point currently?
    double kvbase = 0.0; //bus.getDouble(OpenDSSIOConstants.BUS_KVBASE);
    boolean status = true; //activeBus.getBoolean(OpenDSSIOConstants.BUS_STATUS);
    
    openDSSBus.setCoordinate(new PointImpl(x,y));
    openDSSBus.setAttribute(Bus.NAME_KEY, name);
    
    // TODO need to get the voltage values for the bus
       
    openDSSBus.setStatus(status);
    openDSSBus.setSystemVoltageKV(kvbase);
	  
    return openDSSBus;
  }

	
}
