package gov.lanl.micot.infrastructure.ep.model.pfw;

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
public class PFWBatteryFactory extends BatteryFactory {

//	private static PFWBatteryFactory instance = null;
  private static final String LEGACY_TAG = "PFW";
	
	//public static PFWBatteryFactory getInstance() {
		//if (instance == null) {
			//instance = new PFWBatteryFactory();
	//	}
		//return instance;
	//}
	
	/**
	 * Constructor
	 */
	protected PFWBatteryFactory() {
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
    int legacyid = bus.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);

    for (Battery battery: batteries) {
      if (battery.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY) == null) {
        battery.setAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, legacyid);
        if (getLegacy(LEGACY_TAG, legacyid) == null) {
          registerLegacy(LEGACY_TAG, legacyid, battery);
        }
      }
        
      if (battery.getAttribute(Generator.NAME_KEY) == null) {
        String name = battery.toString();
        if (name.length() > 12) {
          name = name.substring(name.length()-12,name.length());
        }   
        while (name.length() < 12) {
          name += " ";
        }
        name = "\"" + name + "\"";          
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
    Number mw    = generator.getDesiredRealGeneration();
    
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
      battery.setAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY,legacyId);
      battery.addOutputKey(PFWModelConstants.PFW_LEGACY_ID_KEY); 
      registerLegacy(LEGACY_TAG, legacyId, battery);
    }
    return battery;
  }

}
