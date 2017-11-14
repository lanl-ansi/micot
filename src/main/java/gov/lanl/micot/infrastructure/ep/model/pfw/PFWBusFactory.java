package gov.lanl.micot.infrastructure.ep.model.pfw;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.BusFactory;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.PointImpl;

import java.util.StringTokenizer;

/**
 * Factory class for creating PFWBuses an ensuring their uniqueness
 * @author Russell Bent
 */
public class PFWBusFactory extends BusFactory {

	private static final String LEGACY_TAG = "PFW";
		
	/**
	 * Constructor
	 */
	protected PFWBusFactory() {
	}
	
	/**
	 * Creates a bus and its state from a PFW file line
	 * @param line
	 * @return
	 * @throws PFWModelException 
	 */
	public Bus createBus(String line, Point point) throws PFWModelException {		
		Bus bus = constructBus(line, point);				
    int legacyid = bus.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY,Integer.class);

  	// check to see if the area already exists
  	if (doesLegacyExist(LEGACY_TAG,legacyid)) {
  		if (point == null) {
        bus.setCoordinate(getLegacy(LEGACY_TAG,legacyid).getCoordinate());
        
  		}  		
  	}
  	return bus;
	}
	
	/**
	 * Construct a bus from a line
	 * @param line
	 * @return
	 */
	private Bus constructBus(String line, Point point) {
		// parse the information
		StringTokenizer tokenizer = new StringTokenizer(line,",");
  	int id = Integer.parseInt(tokenizer.nextToken().trim());
  	String name = tokenizer.nextToken();
  	if (name.startsWith("\"")) {
  		while (!name.endsWith("\"")) {
  			name = name + "," + tokenizer.nextToken();
  		}
  	}
  	/*int area = */Integer.parseInt(tokenizer.nextToken().trim());
  	/*int zone = */Integer.parseInt(tokenizer.nextToken().trim());
  	double voltageMagnitude = Double.parseDouble(tokenizer.nextToken().trim());
  	double voltageAngle = Double.parseDouble(tokenizer.nextToken().trim());
  	double baseVoltageKiloVolts = Double.parseDouble(tokenizer.nextToken().trim());
  	double remoteVoltage = Double.parseDouble(tokenizer.nextToken().trim());
  	int status = Integer.parseInt(tokenizer.nextToken().trim());
		
  	Bus bus = registerBus(id);
    bus.setAttribute(Bus.NAME_KEY,name);
    bus.setVoltagePU(voltageMagnitude);
    bus.setPhaseAngle(voltageAngle);
    bus.setSystemVoltageKV(baseVoltageKiloVolts);
    bus.setRemoteVoltagePU(remoteVoltage);
    bus.setStatus(status == 1 ? true : false);
  	bus.setCoordinate(point == null ? new PointImpl(0,0) : point);  	
  	return bus;		
	}

	
  /**
   * Creates a bus from another bus
   * @param bus
   * @return
   */
  public void updateBus(Bus bus, int id) {
        
    if (bus.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY) == null) {
      bus.setAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, id);
      registerLegacy(LEGACY_TAG,id,bus);
    }
        
    if (bus.getAttribute(Bus.NAME_KEY) == null) {
      String name = bus.toString();
      if (name.length() > 12) {
        name = name.substring(name.length()-12,name.length());
      }   
      while (name.length() < 12) {
        name += " ";
      }
      name = "\"" + name + "\"";    
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
      if (!doesLegacyExist(LEGACY_TAG,i)) {
        return i;
      }
    }
    throw new RuntimeException("Error: Cannot find an unused id");
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
      bus.setAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY,legacyId);
      bus.addOutputKey(PFWModelConstants.PFW_LEGACY_ID_KEY);      
      registerLegacy(LEGACY_TAG, legacyId, bus);
    }
    return bus;
  }
  
}
