package gov.lanl.micot.infrastructure.ep.model.cdf;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.model.LoadFactory;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.PointImpl;

/**
 * Factory class for creating PFWLoads an ensuring their uniqueness
 * @author Russell Bent
 */
public class CDFLoadFactory extends LoadFactory {

	private static final String LEGACY_TAG = "CDF";
	
	/**
	 * Constructor
	 */
	protected CDFLoadFactory() {
	}
	
	/**
	 * Creates a generator and its state from a PFW file line
	 * @param line
	 * @return
	 * @throws PFWModelException 
	 */
	public Load createLoad(String line, Point point) throws CDFModelException {		
		Load load = constructLoad(line, point);
    int legacyid = load.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, Integer.class);
		if (getLegacy(LEGACY_TAG,legacyid) != null) {
			if (point == null) {
  			load.setCoordinate(getLegacy(LEGACY_TAG,legacyid).getCoordinate());
  		}
  	}
		return load;
	}
		
	/**
	 * Construction of a generation
	 * @param line
	 * @return
	 */
	private Load constructLoad(String line, Point point) {
	  int legacyid = Integer.parseInt(line.substring(0,4).trim());
	  String name = line.substring(5,17);
		double realLoad = Double.parseDouble(line.substring(41,49).trim());
  	double reactiveLoad = Double.parseDouble(line.substring(50,59).trim());  
		
  	// check to see if the area already exists
    Load load = registerLoad(legacyid);    
  	load.setAttribute(Load.LOAD_NAME_KEY, name);
    load.setDesiredRealLoad(realLoad);
    load.setDesiredReactiveLoad(reactiveLoad);
    load.setStatus(true);
    load.setActualRealLoad(realLoad);
    load.setActualReactiveLoad(reactiveLoad);
  	load.setCoordinate(point == null ? new PointImpl(0,0) : point);
  	return load;
	}

  /**
   * Creates a bus from another bus
   * @param bus
   * @return
   */
  public void updateLoad(Load load, Bus bus) {
    int legacyid = bus.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, Integer.class);
    if (load.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY) == null) {
      load.setAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, legacyid);
      registerLegacy(LEGACY_TAG, legacyid, load);
    }
        
    if (load.getAttribute(Load.LOAD_NAME_KEY) == null) {
      String name = load.toString();
      if (name.length() > 12) {
        name = name.substring(name.length()-12,name.length());
      }   
      while (name.length() < 12) {
        name += " ";
      }
      name = "\"" + name + "\"";    
      load.setAttribute(Load.LOAD_NAME_KEY, name);
    }
  }
	
  @Override
  protected Load createEmptyLoad(Bus bus) {
    int legacyId = bus.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, Integer.class);
    Load load = registerLoad(legacyId);    
    return load;    
  }    

  
  /**
   * Register the load
   * @param legacyId
   * @param bus
   * @return
   */
  private Load registerLoad(int legacyId) {
    Load load = getLegacy(LEGACY_TAG, legacyId);
    if (load == null) {
      load = createNewLoad();
      load.setAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY,legacyId);
      load.addOutputKey(CDFModelConstants.CDF_LEGACY_ID_KEY);
      registerLegacy(LEGACY_TAG, legacyId, load);
    }
    return load;
  }

  
}
