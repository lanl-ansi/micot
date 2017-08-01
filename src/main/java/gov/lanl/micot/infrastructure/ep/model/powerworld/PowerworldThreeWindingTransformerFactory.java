package gov.lanl.micot.infrastructure.ep.model.powerworld;

import gov.lanl.micot.infrastructure.ep.io.powerworld.PowerworldIOConstants;
import gov.lanl.micot.infrastructure.ep.model.ThreeWindingTransformer;
import gov.lanl.micot.infrastructure.ep.model.ThreeWindingTransformerFactory;
import gov.lanl.micot.util.collection.Quintuple;
import gov.lanl.micot.util.io.dcom.ComDataObject;

import java.util.ArrayList;

/**
 * Factory class for creating Powerworld three winding transformers
 * @author Russell Bent
 */
public class PowerworldThreeWindingTransformerFactory extends ThreeWindingTransformerFactory {

	private static final String LEGACY_TAG = "POWERWORLD";
  	
	protected static final String DATA_FIELDS[] = new String[] { PowerworldIOConstants.THREE_WINDING_PRIMARY_BUS_NUM, PowerworldIOConstants.THREE_WINDING_SECONDARY_BUS_NUM, PowerworldIOConstants.THREE_WINDING_TERTIARY_BUS_NUM, PowerworldIOConstants.THREE_WINDING_STAR_BUS_NUM, PowerworldIOConstants.THREE_WINDING_CIRCUIT_NUM }; 
  
	/**
	 * Constructor
	 */
	protected PowerworldThreeWindingTransformerFactory() {
	}
	
	/**
	 * Constructs the three winding transformer
	 * @param line
	 * @return
	 */
	protected ThreeWindingTransformer createTransformer(ArrayList<ComDataObject> data) {
	  // this is something of a hack
    int primary = Integer.parseInt(data.get(0).toString().trim());
    int secondary = Integer.parseInt(data.get(1).toString().trim());
    int tertiary = Integer.parseInt(data.get(2).toString().trim());
    int star = Integer.parseInt(data.get(3).toString().trim());
    String temp = data.get(4).toString();
    String legacyid = temp.substring(1,temp.length());
        
    // check to see if the area already exists
    ThreeWindingTransformer transformer = registerTransformer(primary, secondary, tertiary, star, legacyid);      	
  	return transformer;
	}
	
  /**
   * Register the transformer
   * @return
   */
  private ThreeWindingTransformer registerTransformer(int primary, int secondary, int tertiary, int star, String id) {
    Quintuple<Integer, Integer, Integer, Integer, String> legacyId = createId(primary, secondary, tertiary, star, id);    
    ThreeWindingTransformer transformer = getLegacy(LEGACY_TAG, legacyId);
    if (transformer == null) {
      transformer = createNewThreeWindingTransformer();
      transformer.setAttribute(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY,legacyId);
      transformer.addOutputKey(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY);
      registerLegacy(LEGACY_TAG, legacyId, transformer);
    }
    return transformer;
  }

  /**
   * create the unique id
   * @param primary
   * @param secondary
   * @param tertiary
   * @param star
   * @param legacyId
   * @return
   */
  private Quintuple<Integer, Integer, Integer, Integer, String> createId(int primary, int secondary, int tertiary, int star, String legacyId) {
    return new Quintuple<Integer, Integer, Integer, Integer, String>(primary,secondary,tertiary,star,legacyId);
  }
  
}
