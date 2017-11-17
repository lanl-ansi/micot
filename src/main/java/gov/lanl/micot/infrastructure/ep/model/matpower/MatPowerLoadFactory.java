package gov.lanl.micot.infrastructure.ep.model.matpower;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.model.LoadFactory;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.PointImpl;

import java.util.StringTokenizer;

/**
 * Factory class for creating MatPowerLoads an ensuring their uniqueness
 * @author Russell Bent
 */
public class MatPowerLoadFactory extends LoadFactory {

	private static final String LEGACY_TAG = "MATPOWER";
		
	/**
	 * Constructor
	 */
	protected MatPowerLoadFactory() {
	}
	
	/**
	 * Creates a generator and its state from a PFW file line
	 * @param line
	 * @return
	 * @throws MatPowerModelException 
	 */
  public Load createLoad(String line, Point point, Bus bus) throws MatPowerModelException {   
    Load load = constructLoad(line, point, bus);
    int legacyid = load.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, Integer.class);
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
	private Load constructLoad(String line, Point point, Bus bus) {
    StringTokenizer tokenizer = new StringTokenizer(line, "\t");
    int numTokens = tokenizer.countTokens();
    
    int legacyid = Integer.parseInt(tokenizer.nextToken());
    tokenizer.nextToken(); // bus type
    double realLoad = Double.parseDouble(tokenizer.nextToken());
    double reactiveLoad = Double.parseDouble(tokenizer.nextToken());
    tokenizer.nextToken(); // shunt conductance
    tokenizer.nextToken(); // shunt susceptance
    /*int area = */Integer.parseInt(tokenizer.nextToken().trim());
    tokenizer.nextToken(); // voltage magnitude
    tokenizer.nextToken(); // voltage angle
    String name = numTokens <= 13 ? "" : tokenizer.nextToken();
    tokenizer.nextToken(); // baseKV
    /*int zone =*/ Double.parseDouble(tokenizer.nextToken());
	  		
  	// check to see if the area already exists
    Load load = registerLoad(legacyid, bus);    
  	load.setAttribute(Load.LOAD_NAME_KEY, name);
    load.setRealLoad(realLoad);
    load.setReactiveLoad(reactiveLoad);
    load.setStatus(true);
  	load.setCoordinate(point == null ? new PointImpl(0,0) : point);
  	load.setAttribute(Load.HAS_PHASE_A_KEY, true);
    load.setAttribute(Load.HAS_PHASE_B_KEY, false);
    load.setAttribute(Load.HAS_PHASE_C_KEY, false);
    load.setAttribute(Load.REAL_LOAD_A_MAX_KEY, realLoad);
    load.setAttribute(Load.REACTIVE_LOAD_A_MAX_KEY, reactiveLoad);
  	
  	return load;
	}
	
  /**
   * Creates a bus from another bus
   * @param bus
   * @return
   */
  public void updateLoad(Load load, Bus bus) {
    int legacyid = bus.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, Integer.class);    
    if (load.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY) == null) {
      load.setAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, legacyid);
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
    int legacyId = bus.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, Integer.class);
    Load load = registerLoad(legacyId, bus);    
    return load;        
  }    

  
  /**
   * Register the load
   * @param legacyId
   * @param bus
   * @return
   */
  private Load registerLoad(int legacyId, Bus bus) {
    Load load = getLegacy(LEGACY_TAG, legacyId);
    if (load == null) {
      load = createNewLoad();
      load.setAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY,legacyId);
      load.addOutputKey(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY);      
      registerLegacy(LEGACY_TAG, legacyId, load);
    }
    return load;
  }

  
}
