package gov.lanl.micot.infrastructure.ep.model.pfw;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitor;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitorFactory;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.PointImpl;

import java.util.StringTokenizer;

/**
 * Factory class for creating PFWShunts and ensuring their uniqueness
 * @author Russell Bent
 */
public class PFWShuntFactory extends ShuntCapacitorFactory {

//	private static PFWShuntFactory instance = null;
	private static final String LEGACY_TAG = "PFW";
		
	//public static PFWShuntFactory getInstance() {
		//if (instance == null) {
			//instance = new PFWShuntFactory();
		//}
		//return instance;
	//}
	
	/**
	 * Constructor
	 */
	protected PFWShuntFactory() {
	}
	
	/**
	 * Creates a shunt and its state from a PFW file line
	 * @param line
	 * @return
	 * @throws PFWModelException 
	 */
	public ShuntCapacitor createShunt(String line, Point point) throws PFWModelException {		
		ShuntCapacitor capacitor = constructShunt(line, point);
		int id = capacitor.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);
		if (getLegacy(LEGACY_TAG, id) == null) {
			if (point == null) {
  			capacitor.setCoordinate(getLegacy(LEGACY_TAG, id).getCoordinate());
  		}
	  }
		return capacitor;
	}
		
	/**
	 * Construction of a shunt
	 * @param line
	 * @return
	 */
	private ShuntCapacitor constructShunt(String line, Point point) {
		StringTokenizer tokenizer = new StringTokenizer(line,",");
  	int id = Integer.parseInt(tokenizer.nextToken().trim());
  	String name = tokenizer.nextToken();
  	if (name.startsWith("\"")) {
  			while (!name.endsWith("\"")) {
  			name = name + "," + tokenizer.nextToken();
  		}
		}
  	/*int area = */Integer.parseInt(tokenizer.nextToken().trim());
  	/*int zone = */Integer.parseInt(tokenizer.nextToken().trim());
  	double generatorShunt = Double.parseDouble(tokenizer.nextToken().trim());
  	double busShunt = Double.parseDouble(tokenizer.nextToken().trim());  	 
		
  	// check to see if the area already exists
  	ShuntCapacitor shunt = registerCapacitor(id);
  	shunt.setAttribute(ShuntCapacitor.SHUNT_NAME_KEY, name);
  	shunt.setRealCompensation(generatorShunt);
  	shunt.setReactiveCompensation(busShunt);
  	shunt.setCoordinate(point == null ? new PointImpl(0,0) : point);
  	shunt.setDesiredStatus(true);
  	shunt.setActualStatus(true);
  	return shunt;
	}

  /**
   * Create a shunt
   * @param shunt
   * @param id
   * @param area
   * @param zone
   * @return
   */
  public void updateShunt(ShuntCapacitor shunt, Bus bus) {    
    int legacyid = bus.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);
    if (shunt.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY) == null) {
      shunt.setAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, legacyid);
      registerLegacy(LEGACY_TAG, legacyid, shunt);
    }
        
    if (shunt.getAttribute(ShuntCapacitor.SHUNT_NAME_KEY) == null) {
      String name = shunt.toString();
      if (name.length() > 12) {
        name = name.substring(name.length()-12,name.length());
      }   
      while (name.length() < 12) {
        name += " ";
      }
      name = "\"" + name + "\"";    
      shunt.setAttribute(ShuntCapacitor.SHUNT_NAME_KEY, name);
    }         
  }
  
  /**
   * Register the shunt capacitor
   * @param legacyId
   * @param bus
   * @return
   */
  private ShuntCapacitor registerCapacitor(int legacyId) {
    ShuntCapacitor capacitor = getLegacy(LEGACY_TAG, legacyId);
    if (capacitor == null) {
      capacitor = createNewShuntCapacitor();
      capacitor.setAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY,legacyId);
      capacitor.addOutputKey(PFWModelConstants.PFW_LEGACY_ID_KEY);
      registerLegacy(LEGACY_TAG, legacyId, capacitor);
    }
    return capacitor;
  }

  
}
