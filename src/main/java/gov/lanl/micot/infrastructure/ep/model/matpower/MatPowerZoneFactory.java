package gov.lanl.micot.infrastructure.ep.model.matpower;

import java.util.StringTokenizer;

import gov.lanl.micot.infrastructure.ep.model.Zone;
import gov.lanl.micot.infrastructure.ep.model.ZoneFactory;

/**
 * Factory class for creating PFWAreas an ensuring their uniqueness
 * @author Russell Bent
 */
public class MatPowerZoneFactory extends ZoneFactory {

	private static final String LEGACY_TAG = "MATPOWER";
	
  /**
	 * Constructor
	 */
	protected MatPowerZoneFactory() {
	}
	
	/**
	 * Creates an area and its state from a PFW file line
	 * @param line
	 * @return
	 * @throws PFWModelException 
	 */
	public Zone createZone(String line) throws MatPowerModelException {		
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
    StringTokenizer tokenizer = new StringTokenizer(line, "\t");
    int numTokens = tokenizer.countTokens();
    
    tokenizer.nextToken();
    tokenizer.nextToken(); // bus type
    tokenizer.nextToken(); // real load
    tokenizer.nextToken(); // reactive load
    tokenizer.nextToken(); // shunt conductance
    tokenizer.nextToken(); // shunt susceptance
    tokenizer.nextToken(); // area
    tokenizer.nextToken();
    tokenizer.nextToken();
    tokenizer.nextToken();
    if (numTokens > 13) {
      tokenizer.nextToken();
    }
    int legacyid = (int)Double.parseDouble(tokenizer.nextToken());

    // check to see if the area already exists
    Zone zone = createZone(legacyid);    
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
  public void updateZone(Zone zone, int legacyid) {    
    if (zone.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY) == null) {
      zone.setAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, legacyid);
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
      zone.setAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY,legacyId);
      zone.addOutputKey(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY);
      registerLegacy(LEGACY_TAG, legacyId, zone);
    }
    return zone;
  }

  /**
   * Get an used id
   * @return
   */
  public int findUnusedId() {
    for (int i = 0; i < Integer.MAX_VALUE; ++i) {
      if (getLegacy(LEGACY_TAG, i) == null) {
        return i;
      }
    }
    throw new RuntimeException("Ran out of integers");    
//    return existingZones.size() == 0 ? 1 : existingZones.lastKey() + 1;
  }

}
