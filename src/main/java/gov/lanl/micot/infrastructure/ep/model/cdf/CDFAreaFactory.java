package gov.lanl.micot.infrastructure.ep.model.cdf;

import gov.lanl.micot.infrastructure.ep.model.ControlArea;
import gov.lanl.micot.infrastructure.ep.model.ControlAreaFactory;

/**
 * Factory class for creating PFWAreas an ensuring their uniqueness
 * @author Russell Bent
 */
public class CDFAreaFactory extends ControlAreaFactory {

	private static final String LEGACY_TAG = "CDF";
	
	private static final double DEFAULT_EXPORT    = 0.0;
	private static final double DEFAULT_TOLERANCE = 5.0;
	
	/**
	 * Constructor
	 */
	protected CDFAreaFactory() {
	}
	
	/**
	 * Creates an area and its state from a PFW file line
	 * @param line
	 * @return
	 * @throws PFWModelException 
	 */
	public ControlArea createArea(String line) throws CDFModelException {		
		ControlArea area = constructArea(line);
		return area;		
	}

	
	/**
	 * Constructs the area
	 * @param line
	 * @return
	 */
	private ControlArea constructArea(String line) {
	  int legacyid = Integer.parseInt(line.substring(0,3).trim());
	  String name = line.substring(8,20).trim();
	  double scheduledExport = Double.parseDouble(line.substring(21,28).trim());
    double tolerance = Double.parseDouble(line.substring(29,35).trim());
    String areaCode = line.substring(37,43).trim();
    String areaName = line.substring(45,Math.min(75,line.length())).trim();
       
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
	
  public ControlArea createArea(int legacyid) {
    String areaName = new Integer(legacyid).toString() + " ";    
    String name = new Integer(legacyid).toString() + " ";    
    String areaCode = new Integer(legacyid).toString() + " ";
    
    ControlArea area = this.registerArea(legacyid);
    area.setStatus(true);
    area.setAttribute(ControlArea.AREA_BUS_NAME_KEY, name);
    area.setAttribute(ControlArea.AREA_SCHEDULED_EXPORT_KEY, DEFAULT_EXPORT);
    area.setAttribute(ControlArea.AREA_TOLERANCE_KEY, DEFAULT_TOLERANCE);
    area.setAttribute(ControlArea.AREA_CODE_KEY, areaCode);
    area.setAttribute(ControlArea.AREA_NAME_KEY, areaName);
    
    return area;
  }
  
  /**
   * Creates a bus from another bus
   * @param bus
   * @return
   */
  public void updateArea(ControlArea area, int legacyid) {
    if (area.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY) == null) {
      area.setAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, legacyid);
      registerLegacy(LEGACY_TAG, legacyid, area);
    }
    if (area.getAttribute(ControlArea.AREA_CODE_KEY) == null) {
      area.setAttribute(ControlArea.AREA_CODE_KEY, "0");
    }
    if (area.getAttribute(ControlArea.AREA_SCHEDULED_EXPORT_KEY) == null) {
      area.setAttribute(ControlArea.AREA_SCHEDULED_EXPORT_KEY, 0.0);
    } 
    if (area.getAttribute(ControlArea.AREA_TOLERANCE_KEY) == null) {
      area.setAttribute(ControlArea.AREA_TOLERANCE_KEY, 1000.0);
    }     
    if (area.getAttribute(ControlArea.AREA_NAME_KEY) == null) {
      area.setAttribute(ControlArea.AREA_NAME_KEY, legacyid+"");
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
      area.setAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY,legacyId);
      area.addOutputKey(CDFModelConstants.CDF_LEGACY_ID_KEY);
      registerLegacy(LEGACY_TAG, legacyId, area);
    }
    return area;
  }


}
