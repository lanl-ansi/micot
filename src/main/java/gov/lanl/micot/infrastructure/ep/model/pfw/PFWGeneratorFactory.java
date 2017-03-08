package gov.lanl.micot.infrastructure.ep.model.pfw;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.GeneratorFactory;
import gov.lanl.micot.infrastructure.ep.model.GeneratorTypeEnum;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.PointImpl;

import java.util.Collection;
import java.util.StringTokenizer;

/**
 * Factory class for creating PFWGenerators an ensuring their uniqueness
 * @author Russell Bent
 */
public class PFWGeneratorFactory extends GeneratorFactory {

//	private static PFWGeneratorFactory instance = null;
	private static final String LEGACY_TAG = "PFW";
	
//	public static PFWGeneratorFactory getInstance() {
	//	if (instance == null) {
		//	instance = new PFWGeneratorFactory();
		//}
		//return instance;
	//}
	
	/**
	 * Constructor
	 */
	protected PFWGeneratorFactory() {
	}
	
	/**
	 * Creates a generator and its state from a PFW file line
	 * @param line
	 * @return
	 * @throws PFWModelException 
	 */
	public Generator createGenerator(String line, Point point, Bus bus) throws PFWModelException {		
		Generator generator = constructGenerator(line, point, bus);
		int legacyid = generator.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);
		if (getLegacy(LEGACY_TAG, legacyid) != null) {
			if (point == null) {
  			generator.setCoordinate(getLegacy(LEGACY_TAG, legacyid).getCoordinate());
  		}
  	}
		return generator;
	}
	
  /**
	 * Construction of a generation
	 * @param line
	 * @return
	 */
	private Generator constructGenerator(String line, Point point, Bus bus) {
		StringTokenizer tokenizer = new StringTokenizer(line,",");
  	int id = Integer.parseInt(tokenizer.nextToken().trim());
  	String name = tokenizer.nextToken();
  	if (name.startsWith("\"")) {
  		while (!name.endsWith("\"")) {
  			name = name + "," + tokenizer.nextToken();
  		}
  	}
  	/*int area = */Integer.parseInt(tokenizer.nextToken().trim());
  	/*int zone = */Integer.parseInt(tokenizer.nextToken().trim());
  	int type = Integer.parseInt(tokenizer.nextToken().trim());
  	double realGeneration = Double.parseDouble(tokenizer.nextToken().trim());
  	double reactiveGeneration = Double.parseDouble(tokenizer.nextToken().trim());
  	double voltageSetPoint = Double.parseDouble(tokenizer.nextToken().trim());
  	double reactiveMaxRatio = Double.parseDouble(tokenizer.nextToken().trim());
  	double reactiveMinRatio = Double.parseDouble(tokenizer.nextToken().trim());
  	/*int remoteBus = */Integer.parseInt(tokenizer.nextToken().trim());
  	int status = Integer.parseInt(tokenizer.nextToken().trim());
  	double realGenerationMax = Double.parseDouble(tokenizer.nextToken().trim());
  	double realGenerationMin = Double.parseDouble(tokenizer.nextToken().trim());
  	double maxVoltage = Double.parseDouble(tokenizer.nextToken().trim());
  	double minVoltage = Double.parseDouble(tokenizer.nextToken().trim()); 
		
  	// check to see if the area already exists
  	GeneratorTypeEnum gType = GeneratorTypeEnum.getEnum(type);
  	Generator generator = registerGenerator(id);
    generator.setAttribute(Generator.NAME_KEY, name);
    generator.setType(gType);    
    generator.setDesiredRealGeneration(realGeneration);
    generator.setDesiredReactiveGeneration(reactiveGeneration);
    generator.setDesiredReactiveMax(reactiveMaxRatio);
    generator.setReactiveMin(reactiveMinRatio);
    generator.setDesiredStatus(status == 1 ? true : false);
    generator.setDesiredRealGenerationMax(realGenerationMax);
    generator.setRealGenerationMin(realGenerationMin);
    generator.setActualRealGeneration(realGeneration);
    generator.setActualReactiveGeneration(reactiveGeneration);
    generator.setActualStatus(status == 1 ? true : false);    
    generator.setCoordinate(point == null ? new PointImpl(0,0) : point);
    
    bus.setVoltagePU(voltageSetPoint); // backwards way of doing this, but this is the voltage for the bus...
    
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
    int legacyId = bus.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);
    
    for (Generator generator : generators) {
      if (generator.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY) == null) {
        generator.setAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, legacyId);
        if (getLegacy(LEGACY_TAG,legacyId) == null) {
          registerLegacy(LEGACY_TAG, legacyId, generator);
        }
      }
        
      if (generator.getAttribute(Generator.NAME_KEY) == null) {
        String name = generator.toString();
        if (name.length() > 12) {
          name = name.substring(name.length()-12,name.length());
        }   
        while (name.length() < 12) {
          name += " ";
        }
        name = "\"" + name + "\"";          
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
      generator.setAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY,legacyId);
      generator.addOutputKey(PFWModelConstants.PFW_LEGACY_ID_KEY);
      registerLegacy(LEGACY_TAG, legacyId, generator);
    }
    return generator;
  }


}
