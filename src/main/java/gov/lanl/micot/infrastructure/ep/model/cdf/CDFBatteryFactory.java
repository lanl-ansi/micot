package gov.lanl.micot.infrastructure.ep.model.cdf;

import java.util.Collection;
import java.util.StringTokenizer;

import gov.lanl.micot.infrastructure.ep.model.Battery;
import gov.lanl.micot.infrastructure.ep.model.BatteryFactory;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.Generator;

/**
 * Factory class for creating PFWBatteries an ensuring their uniqueness
 * @author Russell Bent
 */
public class CDFBatteryFactory extends BatteryFactory {

//	private static CDFBatteryFactory instance = null;
	private static final String LEGACY_TAG = "CDF";
	
//	public static CDFBatteryFactory getInstance() {
	//	if (instance == null) {
		//	instance = new CDFBatteryFactory();
	//	}
	//	return instance;
	//}
	
	/**
	 * Constructor
	 */
	protected CDFBatteryFactory() {
	}
	
	/**
   * Creates some batteries based on ids and the like
   * @param generators
   * @param id
   * @param area
   * @param zone
   * @return
   */
  public void updateBatteries(Collection<Battery> batteries, Bus bus) {
    int legacyid = bus.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, Integer.class);

    
    for (Battery battery: batteries) {
      if (battery.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY) == null) {
        battery.setAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, legacyid);
        if (getLegacy(LEGACY_TAG, legacyid) == null) {
          registerLegacy(LEGACY_TAG, legacyid, battery);
        }
      }
        
      if (battery.getAttribute(Generator.NAME_KEY) == null) {
        String name = battery.toString();
        battery.setAttribute(Generator.NAME_KEY, name);
      }
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
    String name = tokenizer.nextToken();
    double capacity = Double.parseDouble(tokenizer.nextToken());
    double used = Double.parseDouble(tokenizer.nextToken());
    double cost = Double.parseDouble(tokenizer.nextToken());
    double maxMW = Double.parseDouble(tokenizer.nextToken());
    double minMW = Double.parseDouble(tokenizer.nextToken());    
    Number mw    = generator.getRealGeneration();
    
    Battery battery = registerBattery(legacyid, bus);
    initializeBattery(battery, bus, maxMW, minMW, mw, capacity, used, cost, generator.getCoordinate());
    battery.setAttribute(Generator.NAME_KEY, name);
    return battery;    
	}

  /**
   * Register the battery
   * @param legacyId
   * @param bus
   * @return
   */
  private Battery registerBattery(int legacyId, Bus bus) {
    Battery battery = getLegacy(LEGACY_TAG, legacyId);    
    if (battery == null) {  
      battery = createNewBattery();
      battery.setAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY,legacyId);
      battery.addOutputKey(CDFModelConstants.CDF_LEGACY_ID_KEY);    
      registerLegacy(LEGACY_TAG, legacyId, battery);      
    }
    return battery;
  }

}
