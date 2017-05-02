package gov.lanl.micot.infrastructure.ep.model.dew;

import java.util.Map;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.GeneratorFactory;
import gov.lanl.micot.infrastructure.ep.model.GeneratorTypeEnum;
import gov.lanl.micot.infrastructure.ep.model.LineInstallationTypeEnum;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.util.geometry.PointImpl;

/**
 * Factory class for creating MatPowerGenerators an ensuring their uniqueness
 * 
 * @author Russell Bent
 */
public class DewGeneratorFactory extends GeneratorFactory {

//	private static DewGeneratorFactory							instance																= null;
	private static final String LEGACY_TAG = "DEW";

	//public static DewGeneratorFactory getInstance() {
	//	if (instance == null) {
		//	instance = new DewGeneratorFactory();
		//}
		//return instance;
	//}

	/**
	 * Constructor
	 */
	protected DewGeneratorFactory() {
	}

	/**
	  * Register the generator
	  * @param legacyId
	  * @param bus
	  * @return
	  */
	 private Generator registerGenerator(DewLegacyId legacyId) {
	   Generator generator = getLegacy(LEGACY_TAG, legacyId);
	   if (generator == null) {
	     generator = createNewGenerator();
	     generator.setAttribute(DewVariables.DEW_LEGACY_ID_KEY,legacyId);
	     generator.addOutputKey(DewVariables.DEW_LEGACY_ID_KEY);
	     registerLegacy(LEGACY_TAG, legacyId, generator);
	   }
	   return generator;
	 }
	 
	 /**
	   * Construct a generator from a substation block
	   * @param line
	   * @return
	 * @throws DewException 
	   */
	  public Generator constructGeneratorFromSubstationData(DewLegacyId legacyid, Dew dew, Map<Integer,DewPtlinespcData> lineData) throws DewException {  
	    Object n = dew.getComponentData(Generator.NAME_KEY, legacyid, null);
	    String name = n +"";
	    boolean isFailed = Integer.parseInt(dew.getComponentData(Asset.IS_FAILED_KEY, legacyid, name).toString()) > 0;
	    boolean status = !isFailed && Integer.parseInt(dew.getComponentData(Asset.DESIRED_STATUS_KEY, legacyid, name).toString()) == 1;
	    double x = Double.parseDouble(dew.getComponentData(DewVariables.DEW_X_KEY, legacyid, name).toString());
	    double y = Double.parseDouble(dew.getComponentData(DewVariables.DEW_Y_KEY, legacyid, name).toString());
	 	  int dewType = DewVariables.NO_DEW_TYPE;
	    int numPhases = Integer.parseInt(dew.getComponentData(Generator.NUM_PHASE_KEY, legacyid, name).toString());
      int phases = Integer.parseInt(dew.getComponentData(DewVariables.DEW_PHASES_KEY, legacyid, name).toString());
	    boolean hasPhaseA = false;
	    boolean hasPhaseB = false;
	    boolean hasPhaseC = false;
	    
	    if (phases == 1 || phases == 3 || phases == 5 || phases == 7) {
	      hasPhaseA = true;
	    }
      if (phases == 2 || phases == 3 || phases == 6 || phases == 7) {
        hasPhaseB = true;
      }
      if (phases == 4 || phases == 5 || phases == 6 || phases == 7) {
        hasPhaseC = true;
      }
	    
	    double realGeneration = 0;
	    double reactiveGeneration = 0;
	    double reactiveMax = Double.MAX_VALUE;
	    double reactiveMin = Double.MIN_VALUE;
      double realMax = Double.MAX_VALUE;
      double realMin = Double.MIN_VALUE;
	    	        
	    GeneratorTypeEnum gType = GeneratorTypeEnum.REFERENCE_BUS_TYPE;
	    Generator generator = registerGenerator(legacyid);
	    generator.setAttribute(Generator.NAME_KEY, name);
	    generator.setType(gType);    
	    generator.setDesiredRealGeneration(realGeneration);
	    generator.setDesiredReactiveGeneration(reactiveGeneration);
	    generator.setDesiredReactiveMax(reactiveMax);
	    generator.setReactiveMin(reactiveMin);
	    generator.setDesiredStatus(status);
	    generator.setDesiredRealGenerationMax(realMax);
	    generator.setRealGenerationMin(realMin);
	    generator.setActualRealGeneration(realGeneration);
	    generator.setActualReactiveGeneration(reactiveGeneration);
	    generator.setActualStatus(status);    
	    generator.setCoordinate(new PointImpl(x,y));
	    generator.setAttribute(Generator.IS_FAILED_KEY, isFailed);
	    generator.setAttribute(DewVariables.DEW_COMPONENT_TYPE_KEY, dewType);
	    generator.setAttribute(Generator.NUM_PHASE_KEY, numPhases);
	    generator.setAttribute(Generator.HAS_PHASE_A_KEY, hasPhaseA);
      generator.setAttribute(Generator.HAS_PHASE_B_KEY, hasPhaseB);
      generator.setAttribute(Generator.HAS_PHASE_C_KEY, hasPhaseC);
      
      int subId = Integer.parseInt(dew.getComponentData(DewVariables.DEW_SUBSTATION_KEY, legacyid, name).toString());
      System.out.println("Substation ID: " + subId);    
      generator.setAttribute(DewVariables.DEW_SUBSTATION_KEY, subId);  
	    
      int ptrow = Integer.parseInt(dew.getComponentData(DewVariables.DEW_DATABASE_PTROW_KEY, legacyid, name).toString());
      DewPtlinespcData data = lineData.get(ptrow);
      String lineType = data.getStnam();
      LineInstallationTypeEnum installType = LineInstallationTypeEnum.getEnum(data.getSoverhead());
      String lineDesc = data.getStdesc();
      generator.setAttribute(ElectricPowerFlowConnection.LINE_DESCRIPTION_KEY, lineDesc);
      generator.setAttribute(ElectricPowerFlowConnection.LINE_TYPE_KEY, lineType);
      generator.setAttribute(ElectricPowerFlowConnection.INSTALLATION_TYPE_KEY, installType);
      
      
	    return generator;
	  }

	  /**
	   * Creates a generator from a set of lines
	   * @param lines
	   * @return
	   * @throws DewException 
	   * @throws NumberFormatException 
	   */
  public Generator constructGenerator(DewLegacyId legacyid, Dew dew, Map<Integer,DewPtlinespcData> lineData) throws NumberFormatException, DewException {
    Object temp = dew.getComponentData(Generator.NAME_KEY, legacyid, null);
    if (temp == null) {
      temp = dew.getComponentData(DewVariables.DEW_GENERATOR_NAME_KEY, legacyid, null);
    }
    
    String name = temp.toString();
    
    boolean isFailed = Integer.parseInt(dew.getComponentData(Asset.IS_FAILED_KEY, legacyid, name).toString()) > 0;
    boolean status = !isFailed && Integer.parseInt(dew.getComponentData(Asset.DESIRED_STATUS_KEY, legacyid, name).toString()) == 1;
    double x = Double.parseDouble(dew.getComponentData(DewVariables.DEW_X_KEY, legacyid, name).toString());
    double y = Double.parseDouble(dew.getComponentData(DewVariables.DEW_Y_KEY, legacyid, name).toString());
    int dewType = Integer.parseInt(dew.getComponentData(DewVariables.DEW_COMPONENT_TYPE_KEY, legacyid, name).toString());
    int numPhases = Integer.parseInt(dew.getComponentData(Generator.NUM_PHASE_KEY, legacyid, name).toString());
    int phases = Integer.parseInt(dew.getComponentData(DewVariables.DEW_PHASES_KEY, legacyid, name).toString());
    boolean hasPhaseA = false;
    boolean hasPhaseB = false;
    boolean hasPhaseC = false;

    if (phases == 1 || phases == 3 || phases == 5 || phases == 7) {
      hasPhaseA = true;
    }
    if (phases == 2 || phases == 3 || phases == 6 || phases == 7) {
      hasPhaseB = true;
    }
    if (phases == 4 || phases == 5 || phases == 6 || phases == 7) {
      hasPhaseC = true;
    }

 
    double realGeneration = dew.getComponentData(Generator.DESIRED_REAL_GENERATION_KEY, legacyid, name) == null ? 0.0 : Double.parseDouble(dew.getComponentData(Generator.DESIRED_REAL_GENERATION_KEY, legacyid, name).toString());
    double reactiveGeneration = dew.getComponentData(Generator.DESIRED_REACTIVE_GENERATION_KEY, legacyid, name) == null ? 0.0 : Double.parseDouble(dew.getComponentData(Generator.DESIRED_REACTIVE_GENERATION_KEY, legacyid, name).toString());
    double reactiveMax = dew.getComponentData(Generator.DESIRED_REACTIVE_MAX_KEY, legacyid, name) == null ? 0.0 : Double.parseDouble(dew.getComponentData(Generator.DESIRED_REACTIVE_MAX_KEY, legacyid, name).toString());
    double reactiveMin = dew.getComponentData(Generator.REACTIVE_MIN_KEY, legacyid, name) == null ? 0.0 : Double.parseDouble(dew.getComponentData(Generator.REACTIVE_MIN_KEY, legacyid, name).toString());
    double realMax = realGeneration;
    double realMin = realGeneration;
               
    GeneratorTypeEnum gType = GeneratorTypeEnum.HOLD_VOLTAGE_TYPE;
    Generator generator = registerGenerator(legacyid);
    generator.setAttribute(Generator.NAME_KEY, name);
    generator.setType(gType);    
    generator.setDesiredRealGeneration(realGeneration);
    generator.setDesiredReactiveGeneration(reactiveGeneration);
    generator.setDesiredReactiveMax(reactiveMax);
    generator.setReactiveMin(reactiveMin);
    generator.setDesiredStatus(status);
    generator.setDesiredRealGenerationMax(realMax);
    generator.setRealGenerationMin(realMin);
    generator.setActualRealGeneration(realGeneration);
    generator.setActualReactiveGeneration(reactiveGeneration);
    generator.setActualStatus(status);    
    generator.setCoordinate(new PointImpl(x,y));
    generator.setAttribute(Generator.IS_FAILED_KEY, isFailed);
    generator.setAttribute(DewVariables.DEW_COMPONENT_TYPE_KEY, dewType);
    generator.setAttribute(Generator.NUM_PHASE_KEY, numPhases);
    generator.setAttribute(Generator.HAS_PHASE_A_KEY, hasPhaseA);
    generator.setAttribute(Generator.HAS_PHASE_B_KEY, hasPhaseB);
    generator.setAttribute(Generator.HAS_PHASE_C_KEY, hasPhaseC);
    
    int subId = Integer.parseInt(dew.getComponentData(DewVariables.DEW_SUBSTATION_KEY, legacyid, name).toString());
    System.out.println("Substation ID: " + subId);    
    generator.setAttribute(DewVariables.DEW_SUBSTATION_KEY, subId);       
    
    int ptrow = Integer.parseInt(dew.getComponentData(DewVariables.DEW_DATABASE_PTROW_KEY, legacyid, name).toString());
    DewPtlinespcData data = lineData.get(ptrow);
    String lineType = data.getStnam();
    LineInstallationTypeEnum installType = LineInstallationTypeEnum.getEnum(data.getSoverhead());
    String lineDesc = data.getStdesc();
    generator.setAttribute(ElectricPowerFlowConnection.LINE_DESCRIPTION_KEY, lineDesc);
    generator.setAttribute(ElectricPowerFlowConnection.LINE_TYPE_KEY, lineType);
    generator.setAttribute(ElectricPowerFlowConnection.INSTALLATION_TYPE_KEY, installType);
        
    return generator;
  }

}
