package gov.lanl.micot.infrastructure.ep.model.matpower;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.BusFactory;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.PointImpl;

import java.util.StringTokenizer;

/**
 * Factory class for creating MatPowerBuses an ensuring their uniqueness
 * @author Russell Bent
 */
public class MatPowerBusFactory extends BusFactory {

	private static final String LEGACY_TAG = "MATPOWER";
		
	/**
	 * Constructor
	 */
	protected MatPowerBusFactory() {
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
	  int legacyid = bus.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY,Integer.class);
	  
  	// check to see if the area already exists
  	if (doesLegacyExist(LEGACY_TAG,legacyid)) {  	     
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
	  StringTokenizer tokenizer = new StringTokenizer(line, "\t");
//	  int numTokens = tokenizer.countTokens();
	  
	  int id = Integer.parseInt(tokenizer.nextToken());
	  tokenizer.nextToken(); // bus type
	  tokenizer.nextToken(); // real load
	  tokenizer.nextToken(); // reactive load
	  tokenizer.nextToken(); // shunt conductance
	  tokenizer.nextToken(); // shunt susceptance
	  /*int area = */Integer.parseInt(tokenizer.nextToken().trim());
    double voltageMagnitude = Double.parseDouble(tokenizer.nextToken());
    double voltageAngle = Double.parseDouble(tokenizer.nextToken());
    String name = "";
    double baseKV = Double.parseDouble(tokenizer.nextToken());
    /*int zone = */Double.parseDouble(tokenizer.nextToken());
    double maxVoltage = Double.parseDouble(tokenizer.nextToken());
    String token = tokenizer.nextToken();
    if (token.endsWith(";")) {
      token = token.substring(0, token.length()-1); 
    }    
    double minVoltage = Double.parseDouble(token);
    	  
    boolean status = true;

    Bus bus = registerBus(id);
    bus.setAttribute(Bus.NAME_KEY,name);
    bus.setVoltagePU(voltageMagnitude);
    bus.setPhaseAngle(voltageAngle);
    bus.setSystemVoltageKV(baseKV);
    bus.setStatus(status);
  	bus.setCoordinate(point == null ? new PointImpl(0,0) : point);  	
  	bus.setMaximumVoltagePU(maxVoltage);
    bus.setMinimumVoltagePU(minVoltage);
    bus.setAttribute(Bus.NAME_KEY, id+"");

  	return bus;
	}
	
  /**
   * Creates a bus from another bus
   * @param bus
   * @return
   */
  public void updateBus(Bus bus, int id) {
    if (bus.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY) == null) {
      bus.setAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, id);
      registerLegacy(LEGACY_TAG,id,bus);
    }
        
    if (bus.getAttribute(Bus.NAME_KEY) == null) {
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
  public int findUnusedId() {
    for (int i = 0; i < Integer.MAX_VALUE; ++i) {
      if (!doesLegacyExist(LEGACY_TAG,i)) {
        return i;
      }
    }
    throw new RuntimeException("Error: Ran out of MatPower ids");    
  }

  /**
   * Register the bus
   * @param legacyId
   * @param bus
   * @return
   */
  private Bus registerBus(int legacyId) {
    Bus bus = getLegacy(LEGACY_TAG,legacyId);
    if (bus == null) {
      bus = createNewBus();
      bus.setAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, legacyId);
      bus.addOutputKey(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY);      
      registerLegacy(LEGACY_TAG,legacyId,bus);
    }
    return bus;
  }
  
}
