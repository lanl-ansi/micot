package gov.lanl.micot.application.lpnorm.model;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.model.LoadFactory;
import gov.lanl.micot.application.lpnorm.io.LPNormIOConstants;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.util.io.json.JSONArray;
import gov.lanl.micot.util.io.json.JSONObject;

/**
 * Factory class for creating MatPowerLoads an ensuring their uniqueness
 * @author Russell Bent
 */
public class LPNormLoadFactory extends LoadFactory {

	private static final String LEGACY_TAG = "LPNORM";
	
	/**
	 * Constructor
	 */
	protected LPNormLoadFactory() {
	}
	
	/**
	 * Creates a generator and its state from a PFW file line
	 * @param line
	 * @return
	 * @throws MatPowerModelException 
	 */
  public Load createLoad(JSONObject object, Bus bus)  {   
    Load load = constructLoad(object, bus);
    return load;
  }
		
	/**
	 * Construction of a generation
	 * @param line
	 * @return
	 */
	private Load constructLoad(JSONObject object, Bus bus) {
    String legacyid = object.getString(LPNormIOConstants.LOAD_ID_TAG);

    JSONArray hasPhase = object.getArray(LPNormIOConstants.LOAD_HAS_PHASE_TAG);
    JSONArray real = object.getArray(LPNormIOConstants.LOAD_REAL_TAG);
    JSONArray reactive = object.getArray(LPNormIOConstants.LOAD_REACTIVE_TAG);
    boolean isCritical = object.containsKey(LPNormIOConstants.LOAD_IS_CRITICAL_LOAD_TAG) ? object.getBoolean(LPNormIOConstants.LOAD_IS_CRITICAL_LOAD_TAG) : false;
		int phases = (hasPhase.getBoolean(0) ? 1 : 0) + (hasPhase.getBoolean(1) ? 1 : 0) +  (hasPhase.getBoolean(2) ? 1 : 0);
		boolean status = true;    
				
  	// check to see if the area already exists
    Load load = registerLoad(legacyid, bus);    

    load.setAttribute(Load.NUM_PHASE_KEY, phases);
    load.setAttribute(Load.ACTUAL_REACTIVE_LOAD_A_KEY, reactive.getDouble(0));
    load.setAttribute(Load.ACTUAL_REAL_LOAD_A_KEY, real.getDouble(0));
    load.setAttribute(Load.DESIRED_REACTIVE_LOAD_A_KEY, reactive.getDouble(0));
    load.setAttribute(Load.DESIRED_REAL_LOAD_A_KEY, real.getDouble(0));
    load.setAttribute(Load.HAS_PHASE_A_KEY, hasPhase.getBoolean(0));
    
    load.setAttribute(Load.ACTUAL_REACTIVE_LOAD_B_KEY, reactive.getDouble(1));
    load.setAttribute(Load.ACTUAL_REAL_LOAD_B_KEY, real.getDouble(1));
    load.setAttribute(Load.DESIRED_REACTIVE_LOAD_B_KEY, reactive.getDouble(1));
    load.setAttribute(Load.DESIRED_REAL_LOAD_B_KEY, real.getDouble(1));
    load.setAttribute(Load.HAS_PHASE_B_KEY, hasPhase.getBoolean(1));
    
    load.setAttribute(Load.ACTUAL_REACTIVE_LOAD_C_KEY, reactive.getDouble(2));
    load.setAttribute(Load.ACTUAL_REAL_LOAD_C_KEY, real.getDouble(2));
    load.setAttribute(Load.DESIRED_REACTIVE_LOAD_C_KEY, reactive.getDouble(2));
    load.setAttribute(Load.DESIRED_REAL_LOAD_C_KEY, real.getDouble(2));
    load.setAttribute(Load.HAS_PHASE_C_KEY, hasPhase.getBoolean(2));
    
    load.setDesiredStatus(status);
    load.setActualStatus(status);
    load.setDesiredReactiveLoad(reactive.getDouble(0) + reactive.getDouble(1) + reactive.getDouble(2));
    load.setDesiredRealLoad(real.getDouble(0) + real.getDouble(1) + real.getDouble(2));
    load.setActualReactiveLoad(load.getDesiredReactiveLoad());
    load.setActualRealLoad(load.getDesiredRealLoad());    
    load.setCoordinate(bus.getCoordinate());  

    load.setAttribute(AlgorithmConstants.IS_CRITICAL_LOAD_KEY, isCritical);
    
  	return load;
	}
		
  @Override
  protected Load createEmptyLoad(Bus bus) {
//    int legacyId = bus.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, Integer.class);
  //  Load load = registerLoad(legacyId, bus);    
   // return load;       
    return null;
  }    
  
  /**
   * Register the load
   * @param legacyId
   * @param bus
   * @return
   */
  private Load registerLoad(String legacyId, Bus bus) {
    Load load = getLegacy(LEGACY_TAG, legacyId);
    if (load == null) {
      load = createNewLoad();
      load.setAttribute(LPNormModelConstants.LPNORM_LEGACY_ID_KEY,legacyId);
      load.addOutputKey(LPNormModelConstants.LPNORM_LEGACY_ID_KEY);      
      registerLegacy(LEGACY_TAG, legacyId, load);
    }
    return load;
  }

  
}
