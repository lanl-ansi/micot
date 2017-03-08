package gov.lanl.micot.infrastructure.ep.model.matpower;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitor;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitorFactory;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.PointImpl;

import java.util.StringTokenizer;

/**
 * Factory class for creating MatPowerShunts and ensuring their uniqueness
 * @author Russell Bent
 */
public class MatPowerShuntFactory extends ShuntCapacitorFactory {

	private static final String LEGACY_TAG = "MATPOWER";
	
	/**
	 * Constructor
	 */
	protected MatPowerShuntFactory() {
	}
	
	/**
	 * Creates a shunt and its state from a PFW file line
	 * @param line
	 * @return
	 * @throws MatPowerModelException 
	 */
	public ShuntCapacitor createShunt(String line, Point point) throws MatPowerModelException {		
		ShuntCapacitor capacitor = constructShunt(line, point);
		int id = capacitor.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, Integer.class);
		if (getLegacy(LEGACY_TAG,id) != null) {
			if (point == null) {
  			capacitor.setCoordinate(getLegacy(LEGACY_TAG,id).getCoordinate());
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
    StringTokenizer tokenizer = new StringTokenizer(line, "\t");
    int numTokens = tokenizer.countTokens();
    
    int id = Integer.parseInt(tokenizer.nextToken());
    tokenizer.nextToken(); // bus type
    tokenizer.nextToken(); // real load
    tokenizer.nextToken(); // reactive load
    double generatorShunt = Double.parseDouble(tokenizer.nextToken());
    double busShunt = Double.parseDouble(tokenizer.nextToken());
    /*int area = */Integer.parseInt(tokenizer.nextToken());
    tokenizer.nextToken();
    tokenizer.nextToken();
    String name = numTokens <= 13 ? "" : tokenizer.nextToken();
    tokenizer.nextToken();
    /*int zone = */Double.parseDouble(tokenizer.nextToken());
	  
  	// check to see if the area already exists
    ShuntCapacitor shunt = registerCapacitor(id);
    shunt.setAttribute(ShuntCapacitor.SHUNT_NAME_KEY, name);
  	shunt.setRealCompensation(generatorShunt);
  	shunt.setReactiveCompensation(busShunt);
  	shunt.setCoordinate(point == null ? new PointImpl(0,0) : point);
  	shunt.setDesiredStatus(generatorShunt > 0 || busShunt > 0);
  	shunt.setActualStatus(generatorShunt > 0 || busShunt > 0);
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
    int legacyid = bus.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, Integer.class);
    if (shunt.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY) == null) {
      shunt.setAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, legacyid);
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
      capacitor.setAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY,legacyId);
      capacitor.addOutputKey(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY);
      registerLegacy(LEGACY_TAG, legacyId, capacitor);
    }
    return capacitor;
  }

  
}
