package gov.lanl.micot.infrastructure.ep.model.cdf;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.BusFactory;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.PointImpl;

/**
 * Factory class for creating CDFBuses an ensuring their uniqueness
 * @author Russell Bent
 */
public class CDFBusFactory extends BusFactory {

	private static final String LEGACY_TAG = "CDF";
	
	/**
	 * Constructor
	 */
	protected CDFBusFactory() {
	}
	
	/**
	 * Creates a bus and its state from a PTI file line
	 * @param line
	 * @return
	 * @throws PFWModelException 
	 */
	public Bus createBus(String line, Point point) {		
		Bus bus = constructBus(line, point);
		return getBus(bus, point);
	}
	
	/**
	 * Do the filtering to determine if the bus exists
	 * @param bus
	 * @return
	 */
	private Bus getBus(Bus bus, Point point) {
	  int legacyid = bus.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY,Integer.class);	  
  	// check to see if the area already exists
  	if (doesLegacyExist(LEGACY_TAG, legacyid)) {
  		if (point == null) {
  			bus.setCoordinate(getLegacy(LEGACY_TAG,legacyid).getCoordinate());
  		}  		
  	}
		return bus;
	}
	
	/**
	 * Construct a bus from a line of text in version 23
	 * @param line
	 * @return
	 */
	private Bus constructBus(String line, Point point) {
	  int legacyid = Integer.parseInt(line.substring(0,4).trim());
    String name = line.substring(5,17);
    double voltageMagnitude = Double.parseDouble(line.substring(27,32).trim());
    double voltageAngle = Double.parseDouble(line.substring(33,39).trim());
    double baseKV = Double.parseDouble(line.substring(76,82).trim());
    double remoteVoltage = Double.parseDouble(line.substring(84,89).trim());
    boolean status = true;
  
    Bus bus = registerBus(legacyid);
    bus.setAttribute(Bus.NAME_KEY,name);
    bus.setVoltagePU(voltageMagnitude);
    bus.setPhaseAngle(voltageAngle);
    bus.setSystemVoltageKV(baseKV);
    bus.setRemoteVoltagePU(remoteVoltage);
    bus.setStatus(status);
  	bus.setCoordinate(point == null ? new PointImpl(0,0) : point);  	
  	return bus;
	}
	
  /**
   * Creates a bus from another bus
   * @param bus
   * @return
   */
  public void updateBus(Bus bus, int legacyid) {
    if (bus.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY) == null) {
      bus.setAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, legacyid);
      registerLegacy(LEGACY_TAG, legacyid, bus);
    }
        
    if (bus.getAttribute(Bus.NAME_KEY) == null || bus.getAttribute(Bus.NAME_KEY,String.class).length() == 0) {
      String name = bus.toString();
      bus.setAttribute(Bus.NAME_KEY, name);
    }
  } 
  
  @Override
  protected Bus createEmptyBus() {
    int id = findUnusedId();
    Bus bus = registerBus(id);
    return bus;
  }

  /**
   * Find an unused id number
   * @return
   */
  private int findUnusedId() {
    for (int i = 0; i < Integer.MAX_VALUE; ++i) {
      if (!doesLegacyExist(LEGACY_TAG, i)) {
        return i;
      }
    }
    throw new RuntimeException("Error: Cannot find an unsed id");
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
      bus.setAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY,legacyId);
      bus.addOutputKey(CDFModelConstants.CDF_LEGACY_ID_KEY);
      registerLegacy(LEGACY_TAG, legacyId, bus);
    }
    return bus;
  }
  
}
