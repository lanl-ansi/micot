package gov.lanl.micot.infrastructure.ep.model.matpower;

import java.util.StringTokenizer;

import gov.lanl.micot.infrastructure.ep.model.ControlArea;
import gov.lanl.micot.infrastructure.ep.model.ControlAreaFactory;

/**
 * Factory class for creating MatPower Areas an ensuring their uniqueness
 * @author Russell Bent
 */
public class MatPowerAreaFactory extends ControlAreaFactory {

  private static final String LEGACY_TAG = "MATPOWER";
	
	/**
	 * Constructor
	 */
	protected MatPowerAreaFactory() {
	}
	
	/**
	 * Creates an area and its state from a PFW file line
	 * @param line
	 * @return
	 * @throws MatPowerModelException 
	 */
	public ControlArea createArea(String line) throws MatPowerModelException {		
		ControlArea area = constructArea(line);
		return area;		
	}

	
	/**
	 * Constructs the area
	 * @param line
	 * @return
	 */
	private ControlArea constructArea(String line) {
	  StringTokenizer tokenizer = new StringTokenizer(line, "\t");
	  int legacyId = Integer.parseInt(tokenizer.nextToken());
	  String temp = tokenizer.nextToken();
	  if (temp.endsWith(";")) {
	    temp = temp.substring(0,temp.length()-1);
	  }
	  /*int slackBus = */Integer.parseInt(temp.trim());
	  
  	// check to see if the area already exists
	  ControlArea area = registerArea(legacyId);
  	area.setStatus(true);
    return area;
	}
	
  /**
   * Create the area
   * @param area
   * @param slackBus
   * @return
   */
  public ControlArea createArea(int legacyid) {
    ControlArea area = registerArea(legacyid);
    area.setStatus(true);
    return area;
  }
  
  /**
   * Creates an area from another area
   * @param area
   * @return
   */
  public void updateArea(ControlArea area, int id) {
    if (area.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY) == null) {
      area.setAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, id);
      registerLegacy(LEGACY_TAG, id, area);
    }    
  }

  /**
   * Register the control area
   * @param legacyId
   * @param bus
   * @return
   */
  private ControlArea registerArea(int legacyId) {
    ControlArea area = getLegacy(LEGACY_TAG, legacyId);
    if (area == null) {
      area = createNewControlArea();
      area.setAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY,legacyId);
      area.addOutputKey(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY);
      registerLegacy(LEGACY_TAG, legacyId, area);
    }
    return area;
  }

  /**
   * get an unsed id number
   * 
   * @return
   */
  public int getUnusedId() {
    for (int i = 0; i < Integer.MAX_VALUE; ++i) {
      if (getLegacy(LEGACY_TAG, i) == null) {
        return i;
      }
    }   
    throw new RuntimeException("Error: Ran out of integers");
  }


}
