package gov.lanl.micot.infrastructure.ep.model.pfw;

import java.util.StringTokenizer;

import gov.lanl.micot.infrastructure.ep.model.Zone;
import gov.lanl.micot.infrastructure.ep.model.ZoneFactory;

/**
 * Factory class for creating PFWAreas an ensuring their uniqueness
 * @author Russell Bent
 */
public class PFWZoneFactory extends ZoneFactory {

//	private static PFWZoneFactory instance      = null;
	private static final String LEGACY_TAG = "PFW";
		
	//public static PFWZoneFactory getInstance() {
		//if (instance == null) {
			//instance = new PFWZoneFactory();
		//}
		//return instance;
	//}
	
	/**
	 * Constructor
	 */
	protected PFWZoneFactory() {
	}
	
	/**
	 * Creates an area and its state from a PFW file line
	 * @param line
	 * @return
	 * @throws PFWModelException 
	 */
	public Zone createZone(String line) throws PFWModelException {		
		Zone pair = constructZone(line);
		return pair;		
	}

	
	/**
	 * Constructs the area
	 * @param line
	 * @return
	 */
	private Zone constructZone(String line) {
	  // parse the information
	  StringTokenizer tokenizer = new StringTokenizer(line,",");
	  int id = Integer.parseInt(tokenizer.nextToken().trim());
	  String name = tokenizer.nextToken();
	  while (!name.endsWith("\"")) {
	    name = name + "," + tokenizer.nextToken();
	  }

  	// check to see if the area already exists
	  Zone zone = registerZone(id);
  	zone.setStatus(true);
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
    if (name.length() > 12) {
      name = name.substring(0,12);
    }   
    while (name.length() < 12) {
      name += " ";
    }
    name = "\"" + name + "\"";  
    
    Zone zone = registerZone(id);
    zone.setStatus(true);
    zone.setAttribute(Zone.ZONE_NAME_KEY, name);
    return zone;  
  }
  
  /**
   * Creates a bus from another bus
   * @param bus
   * @return
   */
  public void updateZone(Zone zone, int legacyid) {
    if (zone.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY) == null) {
      zone.setAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, legacyid);
      registerLegacy(LEGACY_TAG, legacyid, zone);
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
      zone.setAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY,legacyId);
      zone.addOutputKey(PFWModelConstants.PFW_LEGACY_ID_KEY);
      registerLegacy(LEGACY_TAG, legacyId, zone);
    }
    return zone;
  }

  
}
