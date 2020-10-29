package gov.lanl.micot.application.lpnorm.model;

import java.util.ArrayList;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.BusFactory;
import gov.lanl.micot.application.lpnorm.io.LPNormIOConstants;
import gov.lanl.micot.util.geometry.PointImpl;
import gov.lanl.micot.util.io.json.JSONArray;
import gov.lanl.micot.util.io.json.JSONObject;

/**
 * Factory class for creating MatPowerBuses an ensuring their uniqueness
 * @author Russell Bent
 */
public class LPNormBusFactory extends BusFactory {

	private static final String LEGACY_TAG = "LPNORM";
	
	private static final double DEFAULT_MIN_VOLTAGE = .8;
  private static final double DEFAULT_MAX_VOLTAGE = 1.2;
	private static final double DEFAULT_VOLTAGE = 1.0;
  
	/**
	 * Constructor
	 */
	protected LPNormBusFactory() {
	}
	
	/**
	 * Creates a bus and its state from a PTI file line
	 * @param line
	 * @return
	 * @throws PFWModelException 
	 */
	public Bus createBus(JSONObject object) {		
		Bus bus = constructBus(object);
		return bus;
	}
		
	/**
	 * Construct a bus from a JSON Object
	 * @param line
	 * @return
	 */
	private Bus constructBus(JSONObject object) {
	  String id = object.getString(LPNormIOConstants.BUS_ID_TAG);
	  double minVoltage = object.containsKey(LPNormIOConstants.BUS_MIN_VOLTAGE_TAG) ? object.getDouble(LPNormIOConstants.BUS_MIN_VOLTAGE_TAG) : DEFAULT_MIN_VOLTAGE;
    double maxVoltage = object.containsKey(LPNormIOConstants.BUS_MAX_VOLTAGE_TAG) ? object.getDouble(LPNormIOConstants.BUS_MAX_VOLTAGE_TAG) : DEFAULT_MAX_VOLTAGE;
    
    ArrayList<Double> voltages = new ArrayList<Double>();
    voltages.add(DEFAULT_VOLTAGE);    
    voltages.add(DEFAULT_VOLTAGE);    
    voltages.add(DEFAULT_VOLTAGE);
    if (object.containsKey(LPNormIOConstants.BUS_VOLTAGE_TAG)) {
      JSONArray voltageArray =  object.getArray(LPNormIOConstants.BUS_VOLTAGE_TAG);
      voltages.set(0, voltageArray.getDouble(0));
      voltages.set(1, voltageArray.getDouble(1));
      voltages.set(2, voltageArray.getDouble(2));
    }
    boolean status = true;

    Bus bus = registerBus(id);
    bus.setVoltagePU(voltages.get(0));
    bus.setAttribute(Bus.VOLTAGE_PU_A_KEY, voltages.get(0));
    bus.setAttribute(Bus.VOLTAGE_PU_B_KEY, voltages.get(1));
    bus.setAttribute(Bus.VOLTAGE_PU_C_KEY, voltages.get(2));    
    bus.setStatus(status);
  	bus.setMaximumVoltagePU(maxVoltage);
    bus.setMinimumVoltagePU(minVoltage);

    double x = object.containsKey(LPNormIOConstants.BUS_LONGITUDE_TAG) ? object.getDouble(LPNormIOConstants.BUS_LONGITUDE_TAG) : 0.0;
    double y = object.containsKey(LPNormIOConstants.BUS_LATITUDE_TAG) ? object.getDouble(LPNormIOConstants.BUS_LATITUDE_TAG) : 0.0;
    
    bus.setCoordinate(new PointImpl(x,y));
    
	if (bus.getCoordinate().getY() == 0 && bus.getCoordinate().getX() == 0)
		System.out.println(bus.getCoordinate());
	

    
  	return bus;
	}
	
  /**
   * Register the bus
   * @param legacyId
   * @param bus
   * @return
   */
  private Bus registerBus(String legacyId) {
    Bus bus = getLegacy(LEGACY_TAG,legacyId);
    if (bus == null) {
      bus = createNewBus();
      bus.setAttribute(LPNormModelConstants.LPNORM_LEGACY_ID_KEY, legacyId);
      bus.addOutputKey(LPNormModelConstants.LPNORM_LEGACY_ID_KEY);      
      registerLegacy(LEGACY_TAG,legacyId,bus);
    }
    return bus;
  }
  
  @Override
  protected Bus createEmptyBus() {
    //int id = findUnusedId();
    //Bus bus = registerBus(id);
    //return bus; 
    return null;
  }

}
