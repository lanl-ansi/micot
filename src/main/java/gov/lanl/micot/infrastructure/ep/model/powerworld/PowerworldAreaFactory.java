package gov.lanl.micot.infrastructure.ep.model.powerworld;

import gov.lanl.micot.infrastructure.ep.io.powerworld.PowerworldIOConstants;
import gov.lanl.micot.infrastructure.ep.model.ControlArea;
import gov.lanl.micot.infrastructure.ep.model.ControlAreaFactory;
import gov.lanl.micot.util.io.dcom.ComDataObject;
import gov.lanl.micot.util.io.dcom.ComObject;

import java.util.ArrayList;

/**
 * Factory class for creating Powerworld an ensuring their uniqueness
 * @author Russell Bent
 */
public class PowerworldAreaFactory extends ControlAreaFactory {

	private static final String LEGACY_TAG = "POWERWORLD";
  	
	/**
	 * Constructor
	 */
	protected PowerworldAreaFactory() {
	}
	
	/**
	 * Constructs the area
	 * @param line
	 * @return
	 */
	protected ControlArea createArea(ComObject powerworld, int legacyid) {
    String fields[] = new String[]{PowerworldIOConstants.AREA_NUM, PowerworldIOConstants.AREA_NAME}; 
    String values[] = new String[] {legacyid+"", "",};
        
    ComDataObject dataObject = powerworld.callData(PowerworldIOConstants.GET_PARAMETERS_SINGLE_ELEMENT, PowerworldIOConstants.AREA, fields, values);
    ArrayList<ComDataObject> areaData = dataObject.getArrayValue();
    String errorString = areaData.get(0).getStringValue();
    if (!errorString.equals("")) {
      System.err.println("Error getting powerworld area data: " + errorString);                
    }
    
    ArrayList<ComDataObject> bData = areaData.get(1).getArrayValue();                       
    String name = bData.get(1).getStringValue();

    // check to see if the area already exists
    ControlArea area = registerArea(legacyid);
  	area.setStatus(true);
    area.setAttribute(ControlArea.AREA_NAME_KEY, name);
      	
  	return area;
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
      area.setAttribute(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY,legacyId);
      area.addOutputKey(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY);
      registerLegacy(LEGACY_TAG, legacyId, area);
    }
    return area;
  }

}
