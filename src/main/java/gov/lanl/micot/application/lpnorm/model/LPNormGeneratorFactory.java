package gov.lanl.micot.application.lpnorm.model;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.GeneratorFactory;
import gov.lanl.micot.application.lpnorm.io.LPNormIOConstants;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.util.io.json.JSONArray;
import gov.lanl.micot.util.io.json.JSONObject;

/**
 * Factory class for creating LPNormGenerators an ensuring their uniqueness
 * 
 * @author Russell Bent
 */
public class LPNormGeneratorFactory extends GeneratorFactory {

	private static final String LEGACY_TAG  = "LPNORM";

	/**
	 * Constructor
	 */
	protected LPNormGeneratorFactory() {
	}

	/**
	 * Creates a generator and its state from a PFW file line
	 * 
	 * @param line
	 * @return
	 * @throws MatPowerModelException
	 */
	public Generator createGenerator(JSONObject object, Bus bus)  {
		Generator gen = constructGenerator(object, bus);
		return gen;
	}

	/**
	 * Construction of a generation
	 * 
	 * @param line
	 * @return
	 */
	private Generator constructGenerator(JSONObject object, Bus bus) {
	  String id = object.getString(LPNormIOConstants.GEN_ID_TAG);

	  JSONArray hasPhase = object.getArray(LPNormIOConstants.GEN_HAS_PHASE_TAG);
    JSONArray real = object.getArray(LPNormIOConstants.GEN_REAL_TAG);
    JSONArray reactive = object.getArray(LPNormIOConstants.GEN_REACTIVE_TAG);
    
    Double microgridCost = object.containsKey(LPNormIOConstants.GEN_MICROGRID_COST_TAG) ? object.getDouble(LPNormIOConstants.GEN_MICROGRID_COST_TAG) : null;
   // Double microgridFixedCost = object.containsKey(LPNormIOConstants.GEN_MICROGRID_FIXED_COST_TAG) ? object.getDouble(LPNormIOConstants.GEN_MICROGRID_FIXED_COST_TAG) : null;
  //  Double maxMicrogrid = object.containsKey(LPNormIOConstants.GEN_MICROGRID_MAX_TAG) ? object.getDouble(LPNormIOConstants.GEN_MICROGRID_MAX_TAG) : null;
    boolean newMicrogrid = object.containsKey(LPNormIOConstants.GEN_MICROGRID_IS_NEW_TAG) ? object.getBoolean(LPNormIOConstants.GEN_MICROGRID_IS_NEW_TAG) : false;
    
		Generator generator = registerGenerator(id);
    
    initializeGenerator(generator, bus, real.getDouble(0) + real.getDouble(1) + real.getDouble(2), reactive.getDouble(0) + reactive.getDouble(1) + reactive.getDouble(2), 0.0, 0.0, bus.getCoordinate());
    
    generator.setAttribute(Generator.HAS_PHASE_A_KEY, hasPhase.getBoolean(0));
    generator.setAttribute(Generator.HAS_PHASE_B_KEY, hasPhase.getBoolean(1));
    generator.setAttribute(Generator.HAS_PHASE_C_KEY, hasPhase.getBoolean(2));

    generator.setAttribute(Generator.REACTIVE_GENERATION_A_KEY, reactive.getDouble(0));
    generator.setAttribute(Generator.REAL_GENERATION_A_KEY, real.getDouble(0));
    generator.setAttribute(Generator.REACTIVE_GENERATION_B_KEY,reactive.getDouble(1));
    generator.setAttribute(Generator.REAL_GENERATION_B_KEY, real.getDouble(1));
    generator.setAttribute(Generator.REACTIVE_GENERATION_C_KEY, reactive.getDouble(2));
    generator.setAttribute(Generator.REAL_GENERATION_C_KEY, real.getDouble(2));

//    generator.setAttribute(Generator.DESIRED_REACTIVE_GENERATION_A_KEY, reactive.getDouble(0));
 //   generator.setAttribute(Generator.DESIRED_REAL_GENERATION_A_KEY, real.getDouble(0));
  //  generator.setAttribute(Generator.DESIRED_REACTIVE_GENERATION_B_KEY, reactive.getDouble(1));
   // generator.setAttribute(Generator.DESIRED_REAL_GENERATION_B_KEY, real.getDouble(1));
    // generator.setAttribute(Generator.DESIRED_REACTIVE_GENERATION_C_KEY, reactive.getDouble(2));
    //generator.setAttribute(Generator.DESIRED_REAL_GENERATION_C_KEY, real.getDouble(2));

    //generator.setAttribute(AlgorithmConstants.MICROGRID_FIXED_COST_KEY, microgridFixedCost);
    generator.setAttribute(AlgorithmConstants.MICROGRID_COST_KEY, microgridCost);
    //generator.setAttribute(AlgorithmConstants.MAX_MICROGRID_KEY, maxMicrogrid);
    generator.setAttribute(AlgorithmConstants.IS_NEW_MICROGRID_KEY, newMicrogrid);

		return generator;
	}

	/**
	  * Register the generator
	  * @param legacyId
	  * @param bus
	  * @return
	  */
	 private Generator registerGenerator(String legacyId) {
	   Generator generator = getLegacy(LEGACY_TAG, legacyId);
	   if (generator == null) {
	     generator = createNewGenerator();
	     generator.setAttribute(LPNormModelConstants.LPNORM_LEGACY_ID_KEY,legacyId);
	     generator.addOutputKey(LPNormModelConstants.LPNORM_LEGACY_ID_KEY);
	     registerLegacy(LEGACY_TAG, legacyId, generator);
	   }
	   return generator;
	 }
}
