package gov.lanl.micot.infrastructure.ep.model.matpower;

import java.util.StringTokenizer;

import gov.lanl.micot.infrastructure.ep.model.Battery;
import gov.lanl.micot.infrastructure.ep.model.BatteryFactory;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.Generator;

/**
 * Factory class for creating MatPowerBatteries an ensuring their uniqueness
 * @author Russell Bent
 */
public class MatPowerBatteryFactory extends BatteryFactory {

	private static final String LEGACY_TAG = "MATPOWER";
	
	/**
	 * Constructor
	 */
	protected MatPowerBatteryFactory() {
	}
		
	/**
	 * Update the battery
	 * @param battery
	 * @param bus
	 * @return
	 */
  public void updateBattery(Battery battery, Bus bus) {
     int legacyId = bus.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, Integer.class);
     
     if (battery.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY) == null) {
       battery.setAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, legacyId);
       registerLegacy(LEGACY_TAG,legacyId,battery);
     }
         
     if (battery.getAttribute(Generator.NAME_KEY) == null) {
       String name = battery.toString();
       battery.setAttribute(Generator.NAME_KEY, name);
     }
   }


  /**
   * Create a battery from PFW file line
   * @param string
   * @param generator
   * @return
   */
	public Battery createBattery(String string, Generator generator, Bus bus) {
		StringTokenizer tokenizer = new StringTokenizer(string,",");
    int legacyid = Integer.parseInt(tokenizer.nextToken());    
    double capacity = Double.parseDouble(tokenizer.nextToken());
    double used = Double.parseDouble(tokenizer.nextToken());
    double cost = Double.parseDouble(tokenizer.nextToken());
    double maxMW = Double.parseDouble(tokenizer.nextToken());
    double minMW = Double.parseDouble(tokenizer.nextToken());    
    Number mw    = generator.getRealGeneration();
    
    Battery battery = registerBattery(legacyid, bus);
    initializeBattery(battery, bus, maxMW, minMW, mw, capacity, used, cost, generator.getCoordinate());
    return battery;    
	}

  /**
   * Register the battery
   * @param legacyId
   * @param bus
   * @return
   */
  private Battery registerBattery(int legacyId, Bus bus) {
    Battery battery = getLegacy(LEGACY_TAG,legacyId);
    if (battery == null) {
      battery = createNewBattery();
      battery.setAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY,legacyId);
      battery.addOutputKey(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY);      
      registerLegacy(LEGACY_TAG,legacyId,battery);
    }
    return battery;
  }

}
