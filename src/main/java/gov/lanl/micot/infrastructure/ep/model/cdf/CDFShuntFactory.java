package gov.lanl.micot.infrastructure.ep.model.cdf;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitor;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitorFactory;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.PointImpl;

/**
 * Factory class for creating PFWShunts and ensuring their uniqueness
 * @author Russell Bent
 */
public class CDFShuntFactory extends ShuntCapacitorFactory {

  private static final String LEGACY_TAG = "CDF";
   	
	/**
	 * Constructor
	 */
	protected CDFShuntFactory() {
	}
	
	/**
	 * Creates a shunt and its state from a PFW file line
	 * @param line
	 * @return
	 * @throws PFWModelException 
	 */
	public ShuntCapacitor createShunt(String line, Point point) throws CDFModelException {		
		ShuntCapacitor capacitor = constructShunt(line, point);
		int legacyid = capacitor.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, Integer.class);
		if (getLegacy(LEGACY_TAG,legacyid) != null) {
			if (point == null) {
  			capacitor.setCoordinate(getLegacy(LEGACY_TAG,legacyid).getCoordinate());
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
	  int id = Integer.parseInt(line.substring(0,4).trim());
	  String name = line.substring(5,17);
		double generatorShunt = Double.parseDouble(line.substring(107,114).trim());
  	double busShunt = Double.parseDouble(line.substring(115,122).trim());  	 
		
  	// check to see if the area already exists
  	ShuntCapacitor shunt = registerCapacitor(id);  	
    shunt.setAttribute(ShuntCapacitor.SHUNT_NAME_KEY, name);
  	shunt.setRealCompensation(generatorShunt);
  	shunt.setReactiveCompensation(busShunt);
  	shunt.setCoordinate(point == null ? new PointImpl(0,0) : point);
  	shunt.setStatus(true);
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
    int legacyid = bus.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, Integer.class);
    if (shunt.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY) == null) {
      shunt.setAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, legacyid);
      registerLegacy(LEGACY_TAG, legacyid, shunt);
 //     existingShunts.put(legacyid, shunt);
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
      capacitor.setAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY,legacyId);
      capacitor.addOutputKey(CDFModelConstants.CDF_LEGACY_ID_KEY);
      registerLegacy(LEGACY_TAG, legacyId, capacitor);
    }
    return capacitor;
  }
  
}
