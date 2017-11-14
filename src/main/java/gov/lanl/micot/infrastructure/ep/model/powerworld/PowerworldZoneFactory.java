package gov.lanl.micot.infrastructure.ep.model.powerworld;

import gov.lanl.micot.infrastructure.ep.io.powerworld.PowerworldIOConstants;
import gov.lanl.micot.infrastructure.ep.model.Zone;
import gov.lanl.micot.infrastructure.ep.model.ZoneFactory;
import gov.lanl.micot.util.io.dcom.ComDataObject;
import gov.lanl.micot.util.io.dcom.ComObject;

import java.util.ArrayList;

/**
 * Factory class for creating power world zones an ensuring their uniqueness
 * @author Russell Bent
 */
public class PowerworldZoneFactory extends ZoneFactory {

	private static final String LEGACY_TAG = "POWERWORLD";
			
	/**
	 * Constructor
	 */
	protected PowerworldZoneFactory() {
	}
	
	/**
	 * Constructs the area
	 * @param line
	 * @return
	 */
	protected Zone createZone(ComObject powerworld, int legacyid) {
    String fields[] = new String[]{PowerworldIOConstants.ZONE_NUM, PowerworldIOConstants.ZONE_NAME}; 
    String values[] = new String[] {legacyid+"", "",};
        
    ComDataObject dataObject = powerworld.callData(PowerworldIOConstants.GET_PARAMETERS_SINGLE_ELEMENT, PowerworldIOConstants.ZONE, fields, values);
    ArrayList<ComDataObject> zoneData = dataObject.getArrayValue();
    String errorString = zoneData.get(0).getStringValue();
    if (!errorString.equals("")) {
      System.err.println("Error getting powerworld zone data: " + errorString);                
    }
    
    ArrayList<ComDataObject> bData = zoneData.get(1).getArrayValue();                       
    String name = bData.get(1).getStringValue();

    // check to see if the area already exists
    Zone zone = registerZone(legacyid);
    zone.setStatus(true);
    zone.setAttribute(Zone.ZONE_NAME_KEY, name);        
    return zone;
	}

  /**
   * Register the zone
   * @param legacyId
   * @param bus
   * @return
   */
  private Zone registerZone(int legacyId) {
    Zone zone = getLegacy(LEGACY_TAG, legacyId);
    if (zone == null) {
      zone = createNewZone();
      zone.setAttribute(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY,legacyId);
      zone.addOutputKey(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY);
      registerLegacy(LEGACY_TAG, legacyId, zone);
    }
    return zone;
  }

  
}
