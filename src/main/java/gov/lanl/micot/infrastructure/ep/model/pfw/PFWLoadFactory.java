package gov.lanl.micot.infrastructure.ep.model.pfw;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.model.LoadFactory;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.PointImpl;

import java.util.StringTokenizer;

/**
 * Factory class for creating PFWLoads an ensuring their uniqueness
 * @author Russell Bent
 */
public class PFWLoadFactory extends LoadFactory {

//	private static PFWLoadFactory instance = null;
	private static final String LEGACY_TAG = "PFW";
	
	//public static PFWLoadFactory getInstance() {
		//if (instance == null) {
		//	instance = new PFWLoadFactory();
		//}
		//return instance;
	//}
	
	/**
	 * Constructor
	 */
	protected PFWLoadFactory() {
	}
	
	/**
	 * Creates a generator and its state from a PFW file line
	 * @param line
	 * @return
	 * @throws PFWModelException 
	 */
	public Load createLoad(String line, Point point, Bus bus) throws PFWModelException {		
		Load load = constructLoad(line, point);
    int legacyid = load.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);
    if (getLegacy(LEGACY_TAG, legacyid) != null) {
			if (point == null) {
  			load.setCoordinate(getLegacy(LEGACY_TAG, legacyid).getCoordinate());
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
		StringTokenizer tokenizer = new StringTokenizer(line,",");
  	int legacyid = Integer.parseInt(tokenizer.nextToken().trim());
  	String name = tokenizer.nextToken();
  	if (name.startsWith("\"")) {
  		while (!name.endsWith("\"")) {
  			name = name + "," + tokenizer.nextToken();
  		}
  	}
  	/*int area = */Integer.parseInt(tokenizer.nextToken().trim());
  	/*int zone = */Integer.parseInt(tokenizer.nextToken().trim());
  	double realLoad = Double.parseDouble(tokenizer.nextToken().trim());
  	double reactiveLoad = Double.parseDouble(tokenizer.nextToken().trim());  
		
  	// check to see if the area already exists
    Load load = registerLoad(legacyid);    
  	load.setAttribute(Load.LOAD_NAME_KEY, name);
    load.setDesiredRealLoad(realLoad);
    load.setDesiredReactiveLoad(reactiveLoad);
    load.setDesiredStatus(true);
    load.setActualStatus(true);
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
    int legacyid = bus.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);
    if (load.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY) == null) {
      load.setAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, legacyid);
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
    int legacyId = bus.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);
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
      load.setAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY,legacyId);
      load.addOutputKey(PFWModelConstants.PFW_LEGACY_ID_KEY);      
      registerLegacy(LEGACY_TAG, legacyId, load);      
    }
    return load;
  }

  
}
