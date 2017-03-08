package gov.lanl.micot.infrastructure.ep.model.cdf;

import gov.lanl.micot.infrastructure.ep.model.Zone;
import gov.lanl.micot.infrastructure.ep.model.ZoneFactory;

/**
 * Factory class for creating PFWAreas an ensuring their uniqueness
 * @author Russell Bent
 */
public class CDFZoneFactory extends ZoneFactory {

	//private static CDFZoneFactory instance      = null;
		
	private static final String LEGACY_TAG = "CDF";
	
	//public static CDFZoneFactory getInstance() {
		//if (instance == null) {
			//instance = new CDFZoneFactory();
	//	}
	//	return instance;
//	}
	
	/**
	 * Constructor
	 */
	protected CDFZoneFactory() {
	}
	
	/**
	 * Creates an area and its state from a PFW file line
	 * @param line
	 * @return
	 * @throws PFWModelException 
	 */
	public Zone createZone(String line) throws CDFModelException {		
		Zone pair = constructZone(line);
		return pair;
	}

	
	/**
	 * Constructs the area
	 * @param line
	 * @return
	 */
	private Zone constructZone(String line) {
	  int id = Integer.parseInt(line.substring(0,4).trim());
	  String name = line.substring(4,Math.min(16,line.length())).trim();
	  
  	// check to see if the area already exists
	  Zone zone = registerZone(id);
  	zone.setActualStatus(true);
  	zone.setDesiredStatus(true);
    zone.setAttribute(Zone.ZONE_NAME_KEY, name);
  	return zone;
	}
	
  /**
   * Create a zone from an id number
   * @param area
   * @param slackBus
   * @return
   */
  public Zone createZone(int id) {
    String name = new Integer(id).toString() + "";
    
    Zone zone = registerZone(id);
    zone.setActualStatus(true);
    zone.setDesiredStatus(true);
    zone.setAttribute(Zone.ZONE_NAME_KEY, name);
    return zone;  
  }
  
  /**
   * Creates a bus from another bus
   * @param bus
   * @return
   */
  public void updateZone(Zone zone, int id) {
    if (zone.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY) == null) {
      zone.setAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, id);
      registerLegacy(LEGACY_TAG, id, zone);
    }
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
      zone.setAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY,legacyId);
      zone.addOutputKey(CDFModelConstants.CDF_LEGACY_ID_KEY);
      registerLegacy(LEGACY_TAG, legacyId, zone);
    }
    return zone;
  }


}
