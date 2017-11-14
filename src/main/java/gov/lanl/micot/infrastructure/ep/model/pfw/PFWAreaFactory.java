package gov.lanl.micot.infrastructure.ep.model.pfw;

import java.util.StringTokenizer;

import gov.lanl.micot.infrastructure.ep.model.ControlArea;
import gov.lanl.micot.infrastructure.ep.model.ControlAreaFactory;

/**
 * Factory class for creating PFWAreas an ensuring their uniqueness
 * @author Russell Bent
 */
public class PFWAreaFactory extends ControlAreaFactory {

	private static final double DEFAULT_EXPORT    = 0.0;
	private static final double DEFAULT_TOLERANCE = 5.0;
	
	private static final String LEGACY_TAG = "PFW";
  	
	/**
	 * Constructor
	 */
	protected PFWAreaFactory() {
	}
	
	/**
	 * Creates an area and its state from a PFW file line
	 * @param line
	 * @return
	 * @throws PFWModelException 
	 */
	public ControlArea createArea(String line) throws PFWModelException {		
		ControlArea area = constructArea(line);
		return area;
	}

	
	/**
	 * Constructs the area
	 * @param line
	 * @return
	 */
	private ControlArea constructArea(String line) {
	// parse the information
		StringTokenizer tokenizer = new StringTokenizer(line,",");
  	int legacyid = Integer.parseInt(tokenizer.nextToken().trim());
  	/*int slackBus = */Integer.parseInt(tokenizer.nextToken().trim());
  	String name = tokenizer.nextToken();
  	while (!name.endsWith("\"")) {
  		name = name + "," + tokenizer.nextToken();
  	}
  	double scheduledExport = Double.parseDouble(tokenizer.nextToken().trim());
  	double tolerance = Double.parseDouble(tokenizer.nextToken().trim());
  	String areaCode = tokenizer.nextToken();
  	while (!areaCode.endsWith("\"")) {
  		areaCode = areaCode + "," + tokenizer.nextToken();
  	}
  	String areaName = tokenizer.nextToken();
  	while (!areaName.endsWith("\"")) {
  		areaName = areaName + "," + tokenizer.nextToken();
  	}
		
  	// check to see if the area already exists
    ControlArea area = registerArea(legacyid);
  	area.setStatus(true);
    area.setAttribute(ControlArea.AREA_BUS_NAME_KEY, name);
    area.setAttribute(ControlArea.AREA_SCHEDULED_EXPORT_KEY, scheduledExport);
    area.setAttribute(ControlArea.AREA_TOLERANCE_KEY, tolerance);
    area.setAttribute(ControlArea.AREA_CODE_KEY, areaCode);
    area.setAttribute(ControlArea.AREA_NAME_KEY, areaName);
      	
  	return area;
	}
	
  public ControlArea createArea(int area/*, int slackBus*/) {
    String areaName = new Integer(area).toString() + " ";
    if (areaName.length() > 28) {
      areaName = areaName.substring(0,28);
    }   
    while (areaName.length() < 28) {
      areaName += " ";
    }
    areaName = "\"" + areaName + "\"";  
    
    String name = new Integer(area).toString() + " ";
    if (name.length() > 12) {
      name = name.substring(0,12);
    }   
    while (name.length() < 12) {
      name += " ";
    }
    name = "\"" + name + "\"";    
    
    String areaCode = new Integer(area).toString() + " ";
    if (areaCode.length() > 6) {
      areaCode = areaCode.substring(0,6);
    }   
    while (areaCode.length() < 6) {
      areaCode += " ";
    }
    areaCode = "\"" + areaCode + "\"";    

    ControlArea pair = registerArea(area);
    pair.setStatus(true);
    pair.setAttribute(ControlArea.AREA_BUS_NAME_KEY, name);
    pair.setAttribute(ControlArea.AREA_SCHEDULED_EXPORT_KEY, DEFAULT_EXPORT);
    pair.setAttribute(ControlArea.AREA_TOLERANCE_KEY, DEFAULT_TOLERANCE);
    pair.setAttribute(ControlArea.AREA_CODE_KEY, areaCode);
    pair.setAttribute(ControlArea.AREA_NAME_KEY, areaName);
    
    return pair;
  }
  
  /**
   * Creates a bus from another bus
   * @param bus
   * @return
   */
  public void updateArea(ControlArea area, int legacyid) {
    if (area.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY) == null) {
      area.setAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, legacyid);
      registerLegacy(LEGACY_TAG, legacyid, area);
    }
    
    if (area.getAttribute(ControlArea.AREA_SCHEDULED_EXPORT_KEY) == null) {
      area.setAttribute(ControlArea.AREA_SCHEDULED_EXPORT_KEY, 0.0);
    }

    if (area.getAttribute(ControlArea.AREA_TOLERANCE_KEY) == null) {
      area.setAttribute(ControlArea.AREA_TOLERANCE_KEY, 0.0);
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
      area.setAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY,legacyId);
      area.addOutputKey(PFWModelConstants.PFW_LEGACY_ID_KEY);
      registerLegacy(LEGACY_TAG, legacyId, area);
    }
    return area;
  }

}
