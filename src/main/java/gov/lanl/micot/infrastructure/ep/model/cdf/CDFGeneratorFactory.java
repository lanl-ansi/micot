package gov.lanl.micot.infrastructure.ep.model.cdf;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.GeneratorFactory;
import gov.lanl.micot.infrastructure.ep.model.GeneratorTypeEnum;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.PointImpl;

import java.util.Collection;

/**
 * Factory class for creating CDFGenerators an ensuring their uniqueness
 * @author Russell Bent
 */
public class CDFGeneratorFactory extends GeneratorFactory {

	private static final String LEGACY_TAG = "CDF";
	
	/**
	 * Constructor
	 */
	protected CDFGeneratorFactory() {
	}
	
	/**
	 * Creates a generator and its state from a PFW file line
	 * @param line
	 * @return
	 * @throws PFWModelException 
	 */
	public Generator createGenerator(String line, Point point) throws CDFModelException {		
		Generator generator = constructGenerator(line, point);
    int legacyid = generator.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, Integer.class);
		if (getLegacy(LEGACY_TAG, legacyid) != null) {
			if (point == null) {
  			generator.setCoordinate(getLegacy(LEGACY_TAG,legacyid).getCoordinate());
  		}
  	}
		return generator;
	}
	
	
	/**
	 * Construction of a generation
	 * @param line
	 * @return
	 */
	private Generator constructGenerator(String line, Point point) {
	  int legacyid = Integer.parseInt(line.substring(0,4).trim());
    String name = line.substring(5,17);
    boolean status = true;
    int type = Integer.parseInt(line.substring(25,26).trim());   
  	double realGeneration = Double.parseDouble(line.substring(60,67).trim());
  	double reactiveGeneration = Double.parseDouble(line.substring(68,75).trim());
  	
  	double q_or_v_max = Double.parseDouble(line.substring(91,98).trim());
  	double q_or_v_min = Double.parseDouble(line.substring(99,106).trim());
  	
  	double reactiveMax = type == 2 ? q_or_v_max : reactiveGeneration;
  	double reactiveMin = type == 2 ? q_or_v_min : reactiveGeneration;
  	      	
  	/*int remoteBus = */Integer.parseInt(line.substring(124,127).trim());
  	double realGenerationMax = realGeneration;
  	double realGenerationMin = realGeneration < 0 ? realGeneration : 0;
		
  	// check to see if the area already exists
  	GeneratorTypeEnum gType = GeneratorTypeEnum.getEnum(type);
  	Generator generator = registerGenerator(legacyid);
    generator.setAttribute(Generator.NAME_KEY, name);
    generator.setType(gType);    
    generator.setDesiredRealGeneration(realGeneration);
    generator.setDesiredReactiveGeneration(reactiveGeneration);
    generator.setDesiredReactiveMax(reactiveMax);
    generator.setReactiveMin(reactiveMin);
    generator.setStatus(status);
    generator.setDesiredRealGenerationMax(realGenerationMax);
    generator.setRealGenerationMin(realGenerationMin);
    generator.setActualRealGeneration(realGeneration);
    generator.setActualReactiveGeneration(reactiveGeneration);
    generator.setCoordinate(point == null ? new PointImpl(0,0) : point);
  	return generator;
	}

  /**
   * Creates some generators based on ids and the like
   * @param generators
   * @param id
   * @param area
   * @param zone
   * @return
   */
  public void updateGenerators(Collection<Generator> generators, Bus bus) {
    int legacyId = bus.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, Integer.class);
    
    for (Generator generator : generators) {
      if (generator.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY) == null) {
        generator.setAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, legacyId);
        if (getLegacy(LEGACY_TAG, legacyId) == null) {
          registerLegacy(LEGACY_TAG, legacyId, generator);
        }
      }
        
      if (generator.getAttribute(Generator.NAME_KEY) == null) {
        String name = generator.toString();
        if (name.length() > 10) {
          name = name.substring(name.length()-10,name.length());
        }   
        while (name.length() < 10) {
          name += " ";
        }
        generator.setAttribute(Generator.NAME_KEY, name);
      }
    }    
  }
  
  /**
   * Register the generator
   * @param legacyId
   * @param bus
   * @return
   */
  private Generator registerGenerator(int legacyId) {
    Generator generator = getLegacy(LEGACY_TAG, legacyId);
    if (generator == null) {
      generator = createNewGenerator();
      generator.setAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY,legacyId);
      generator.addOutputKey(CDFModelConstants.CDF_LEGACY_ID_KEY);
      registerLegacy(LEGACY_TAG, legacyId, generator);
    }
    return generator;
  }
}
