package gov.lanl.micot.infrastructure.ep.model.dew;

import gov.lanl.micot.infrastructure.ep.model.Battery;
import gov.lanl.micot.infrastructure.ep.model.BatteryFactory;
import gov.lanl.micot.infrastructure.ep.model.Bus;

/**
 * Factory class for creating MatPowerBatteries an ensuring their uniqueness
 * @author Russell Bent
 */
public class DewBatteryFactory extends BatteryFactory {

//	 private static DewBatteryFactory instance = null;
	 private static final String LEGACY_TAG = "DEW";

	
	//public static DewBatteryFactory getInstance() {
		//if (instance == null) {
			//instance = new DewBatteryFactory();
		//}
		//return instance;
	//}
	
	/**
	 * Constructor
	 */
	protected DewBatteryFactory() {
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
      battery.setAttribute(DewVariables.DEW_LEGACY_ID_KEY,legacyId);
      battery.addOutputKey(DewVariables.DEW_LEGACY_ID_KEY);      
      registerLegacy(LEGACY_TAG,legacyId,battery);
    }
    return battery;
  }

}
