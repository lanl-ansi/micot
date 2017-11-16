package gov.lanl.micot.infrastructure.ep.model.opendss;

import gov.lanl.micot.infrastructure.ep.io.opendss.OpenDSSIOConstants;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.GeneratorFactory;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.io.dcom.ComObject;

/**
 * Factory for creating generators
 * 
 * @author Russell Bent
 */
public class OpenDSSGeneratorFactory extends GeneratorFactory {

  private static final String LEGACY_TAG = "OpenDSS";
    
  /**
   * Singleton constructor
   */
  protected OpenDSSGeneratorFactory() {
  }

  public Generator createSourceGenerator(Bus bus) {
    String legacyid = "source-" + bus.getAttribute(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY, String.class); 
    Point point = bus.getCoordinate();
    double reactiveMax = Double.MAX_VALUE;
    double reactiveMin = 0;
    double realMax = Double.MAX_VALUE;
    double realMin = 0;
    
    Generator generator = registerGenerator(legacyid, bus);    
    initializeGenerator(generator, bus, realMax, reactiveMax, realMin, reactiveMin, point);
    
    generator.setAttribute(Generator.HAS_PHASE_A_KEY, true);
    generator.setAttribute(Generator.HAS_PHASE_B_KEY, true);
    generator.setAttribute(Generator.HAS_PHASE_C_KEY, true);

    generator.setAttribute(Generator.REACTIVE_GENERATION_A_KEY, Double.MAX_VALUE);
    generator.setAttribute(Generator.REAL_GENERATION_A_KEY, Double.MAX_VALUE);
    generator.setAttribute(Generator.REACTIVE_GENERATION_B_KEY, Double.MAX_VALUE);
    generator.setAttribute(Generator.REAL_GENERATION_B_KEY, Double.MAX_VALUE);
    generator.setAttribute(Generator.REACTIVE_GENERATION_C_KEY, Double.MAX_VALUE);
    generator.setAttribute(Generator.REAL_GENERATION_C_KEY, Double.MAX_VALUE);
    
    return generator;    
  }

  
  /**
   * Creates a generator and data from a generator
   * 
   * @param line
   * @return
   */
  public Generator createGenerator(ComObject iGenerator, Bus bus, ComObject activeGenerator) {
    String legacyid = iGenerator.getString(OpenDSSIOConstants.GENERATOR_NAME);     
    Point point = bus.getCoordinate();
    
    // everything gets coverted to MWs
    double reactiveMax = iGenerator.getDouble(OpenDSSIOConstants.GENERATOR_KVAR) / 1000.0;  // we want Maxkvar, but we cant get it
    double reactiveMin = 0; // we want minkvar, but the com objects don't have it
    double realMax = iGenerator.getDouble(OpenDSSIOConstants.GENERATOR_KW) / 1000.0; // no seperate field for max
    double realMin = 0.0;
    
    Generator generator = registerGenerator(legacyid, bus);    
    initializeGenerator(generator, bus, realMax, reactiveMax, realMin, reactiveMin, point);
    fill(generator,iGenerator, activeGenerator);
    return generator;    
  }

  /**
  * Register the load
  * @param legacyId
  * @param bus
  * @return
  */
  private Generator registerGenerator(String legacyId, Bus bus) {
    Generator generator = getLegacy(LEGACY_TAG, legacyId);
    if (generator == null) {
      generator = createNewGenerator();
      generator.setAttribute(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY,legacyId);
      generator.addOutputKey(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY);      
      registerLegacy(LEGACY_TAG, legacyId, generator);
    }
   return generator;
 }

  
  /**
   * Fill the generator with the ieiss generator
   * @param load
   * @param iLoad
   */
  private void fill(Generator generator, ComObject iGenerator, ComObject activeGenerator) {
    String legacyid = generator.getAttribute(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY, String.class); 
    
    double reactiveGeneration = iGenerator.getDouble(OpenDSSIOConstants.GENERATOR_KVAR) / 1000.0;
    double realGeneration = iGenerator.getDouble(OpenDSSIOConstants.GENERATOR_KW) / 1000.0;
    boolean status = activeGenerator.getBoolean(OpenDSSIOConstants.GENERATOR_STATUS);
        
    generator.setStatus(status);
    generator.setReactiveGeneration(reactiveGeneration);
    generator.setRealGeneration(realGeneration);    

    ComObject property = activeGenerator.call(OpenDSSIOConstants.PROPERTIES, OpenDSSIOConstants.GENERATOR_PHASES);
    int phases = Integer.parseInt(property.getString(OpenDSSIOConstants.PROPERTY_VALUE));
    double reactivePhaseA = 0;
    double realPhaseA = 0;
    double reactivePhaseB = 0;
    double realPhaseB = 0;
    double reactivePhaseC =0;
    double realPhaseC = 0;
    boolean hasPhaseA = false;
    boolean hasPhaseB = false;
    boolean hasPhaseC = false;
    
    if (phases == 3) {
      reactivePhaseA = reactiveGeneration / 3.0;
      realPhaseA = realGeneration / 3.0;
      reactivePhaseB = reactiveGeneration / 3.0;
      realPhaseB = realGeneration / 3.0;
      reactivePhaseC = reactiveGeneration / 3.0;
      realPhaseC = realGeneration / 3.0;
      hasPhaseA = hasPhaseB = hasPhaseC = true;
    }
    else if (phases == 1) {
      char phase = legacyid.charAt(legacyid.length()-1);
      if (phase == 'a') {
        reactivePhaseA = reactiveGeneration;
        realPhaseA = realGeneration;
        hasPhaseA = true;
      }
      else if (phase == 'b') {
        reactivePhaseB = reactiveGeneration;
        realPhaseB = realGeneration;
        hasPhaseB = true;
      }
      else if (phase == 'c') {
        reactivePhaseC = reactiveGeneration;
        realPhaseC = realGeneration;
        hasPhaseC = true;
      }
      else {
        throw new RuntimeException("Don't have code for phase " + phase);        
      }
      
    }
    else {
      throw new RuntimeException("Don't have code for phases " + phases);
    }
    
    generator.setAttribute(Generator.NUM_PHASE_KEY, phases);
    generator.setAttribute(Generator.REACTIVE_GENERATION_A_KEY, reactivePhaseA);
    generator.setAttribute(Generator.REAL_GENERATION_A_KEY, realPhaseA);
    generator.setAttribute(Generator.HAS_PHASE_A_KEY, hasPhaseA);
    
    generator.setAttribute(Generator.REACTIVE_GENERATION_B_KEY, reactivePhaseB);
    generator.setAttribute(Generator.REAL_GENERATION_B_KEY, realPhaseB);
    generator.setAttribute(Generator.HAS_PHASE_B_KEY, hasPhaseB);

    generator.setAttribute(Generator.REACTIVE_GENERATION_C_KEY, reactivePhaseC);
    generator.setAttribute(Generator.REAL_GENERATION_C_KEY, realPhaseC);
    generator.setAttribute(Generator.HAS_PHASE_C_KEY, hasPhaseC);

   }

  /**
   * Create generator data
   * @param generatorData
   * @param bus
   * @return
   */
  public Generator createGenerator(String generatorData, Bus bus) {
	generatorData = generatorData.replace("\"", "");
    String legacyid =  OpenDSSIOConstants.getData(generatorData,"Generator", ".");
    Point point = bus.getCoordinate();
    double reactiveMax = Double.parseDouble(OpenDSSIOConstants.getData(generatorData,"Maxkvar", "="));
    double reactiveMin = Double.parseDouble(OpenDSSIOConstants.getData(generatorData,"Minkvar", "="));
    double realMax = Double.parseDouble(OpenDSSIOConstants.getData(generatorData,"kW", "="));
    double realMin = 0; 
        
    Generator generator = registerGenerator(legacyid, bus); 
    initializeGenerator(generator, bus, realMax, reactiveMax, realMin, reactiveMin, point);

    generator.setAttribute(Generator.HAS_PHASE_A_KEY, true);
    generator.setAttribute(Generator.HAS_PHASE_B_KEY, true);
    generator.setAttribute(Generator.HAS_PHASE_C_KEY, true);

    generator.setAttribute(Generator.REACTIVE_GENERATION_A_KEY, Double.MAX_VALUE);
    generator.setAttribute(Generator.REAL_GENERATION_A_KEY, Double.MAX_VALUE);
    generator.setAttribute(Generator.REACTIVE_GENERATION_B_KEY, Double.MAX_VALUE);
    generator.setAttribute(Generator.REAL_GENERATION_B_KEY, Double.MAX_VALUE);
    generator.setAttribute(Generator.REACTIVE_GENERATION_C_KEY, Double.MAX_VALUE);
    generator.setAttribute(Generator.REAL_GENERATION_C_KEY, Double.MAX_VALUE);
    return generator;	  
  }

  public Generator createSourceGenerator(Bus bus, String generatorData) {
	
    //System.err.println("Emre, pattern getting the data like the function Generator createSourceGenerator(Bus bus)");    

	// TODO following is hardcoded for linux OpenDSS!
    String legacyid = "source-" + bus.getAttribute("OPENDSS_LEGACY_ID", String.class);
    Point point = bus.getCoordinate();
    double reactiveMax = Double.MAX_VALUE;
    double reactiveMin = 0;
    double realMax = Double.MAX_VALUE;
    double realMin = 0;
        
    Generator generator = registerGenerator(legacyid, bus); 
    initializeGenerator(generator, bus, realMax, reactiveMax, realMin, reactiveMin, point);

    generator.setAttribute(Generator.HAS_PHASE_A_KEY, true);
    generator.setAttribute(Generator.HAS_PHASE_B_KEY, true);
    generator.setAttribute(Generator.HAS_PHASE_C_KEY, true);

    generator.setAttribute(Generator.REACTIVE_GENERATION_A_KEY, Double.MAX_VALUE);
    generator.setAttribute(Generator.REAL_GENERATION_A_KEY, Double.MAX_VALUE);
    generator.setAttribute(Generator.REACTIVE_GENERATION_B_KEY, Double.MAX_VALUE);
    generator.setAttribute(Generator.REAL_GENERATION_B_KEY, Double.MAX_VALUE);
    generator.setAttribute(Generator.REACTIVE_GENERATION_C_KEY, Double.MAX_VALUE);
    generator.setAttribute(Generator.REAL_GENERATION_C_KEY, Double.MAX_VALUE);
    
    return generator;
  }

  
  
}
