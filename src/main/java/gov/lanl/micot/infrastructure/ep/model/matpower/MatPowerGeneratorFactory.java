package gov.lanl.micot.infrastructure.ep.model.matpower;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.GeneratorFactory;
import gov.lanl.micot.infrastructure.ep.model.GeneratorTypeEnum;
import gov.lanl.micot.util.collection.Pair;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.PointImpl;

import java.util.StringTokenizer;

/**
 * Factory class for creating MatPowerGenerators an ensuring their uniqueness
 * 
 * @author Russell Bent
 */
public class MatPowerGeneratorFactory extends GeneratorFactory {

	private static final String LEGACY_TAG                                                      = "MATPOWER";

	private static final double													DEFAULT_MVA_BASE												= 100;
	private static final double													DEFAULT_MIN_REAL_CURVE									= 0;
	private static final double													DEFAULT_MAX_REAL_CURVE									= 0;

	private static final double													DEFAULT_MIN_REACTIVE_CURVE_FOR_MIN_REAL	= 0;
	private static final double													DEFAULT_MAX_REACTIVE_CURVE_FOR_MIN_REAL	= 0;
	private static final double													DEFAULT_MIN_REACTIVE_CURVE_FOR_MAX_REAL	= 0;
	private static final double													DEFAULT_MAX_REACTIVE_CURVE_FOR_MAX_REAL	= 0;
	private static final double													DEFAULT_LOAD_RAMP_RATE									= 0;
	private static final double													DEFAULT_TEN_MINUTE_RESERVE_RATE					= 0;
	private static final double													DEFAULT_THIRTY_MINUTE_RESERVE_RATE			= 0;
	private static final double													DEFAULT_REACTIVE_RAMP_RATE							= 0;
	private static final double													DEFAULT_AREA_PARTICPATION_FACTOR_RATE		= 0;

	/**
	 * Constructor
	 */
	protected MatPowerGeneratorFactory() {
	}

	/**
	 * Get the id of a generator
	 * 
	 * @param generator
	 * @return
	 */
	private Pair<Integer, Integer> getId(Generator generator) {
		int id = generator.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, Integer.class);
		int machine = generator.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_MACHINE_ID_KEY, Integer.class);
		return new Pair<Integer, Integer>(id, machine);
	}

	/**
	 * Creates a generator and its state from a PFW file line
	 * 
	 * @param line
	 * @return
	 * @throws MatPowerModelException
	 */
	public Generator createGenerator(String busline, String line, int machineNum, Point point, Bus bus) throws MatPowerModelException {
		Generator pair = constructGenerator(busline, line, machineNum, point, bus);
		if (getLegacy(LEGACY_TAG,getId(pair)) != null) {
			if (point == null) {
				pair.setCoordinate(getLegacy(LEGACY_TAG,getId(pair)).getCoordinate());
			}
		}
		return pair;
	}

	/**
	 * Construction of a generation
	 * 
	 * @param line
	 * @return
	 */
	private Generator constructGenerator(String busline, String line, int machineNum, Point point, Bus bus) {
		StringTokenizer tokenizer = new StringTokenizer(line, "\t");
		int id = Integer.parseInt(tokenizer.nextToken());
		double realGeneration = Double.parseDouble(tokenizer.nextToken());
		double reactiveGeneration = Double.parseDouble(tokenizer.nextToken());
		double reactiveMax = Double.parseDouble(tokenizer.nextToken());
		double reactiveMin = Double.parseDouble(tokenizer.nextToken());
		double desiredVoltage = Double.parseDouble(tokenizer.nextToken());
		double mBase = Double.parseDouble(tokenizer.nextToken());
		boolean status = Integer.parseInt(tokenizer.nextToken().trim()) > 0 ? true : false;
		double realGenerationMax = Double.parseDouble(tokenizer.nextToken());
		double realGenerationMin = Double.parseDouble(tokenizer.nextToken());
		double pc1 = Double.parseDouble(tokenizer.nextToken());
		double pc2 = Double.parseDouble(tokenizer.nextToken());
		double qc1Min = Double.parseDouble(tokenizer.nextToken());
		double qc1Max = Double.parseDouble(tokenizer.nextToken());
		double qc2Min = Double.parseDouble(tokenizer.nextToken());
		double qc2Max = Double.parseDouble(tokenizer.nextToken());
		double loadRampRate = Double.parseDouble(tokenizer.nextToken());
		double tenMinReserveRampRate = Double.parseDouble(tokenizer.nextToken());
		double thirtyMinReserveRampRate = Double.parseDouble(tokenizer.nextToken());
		double reactiveRampRate = Double.parseDouble(tokenizer.nextToken());
		String temp = tokenizer.nextToken();
		double areaParticipationFactor = 0;
		if (temp.endsWith(";")) {
		  areaParticipationFactor = Double.parseDouble(temp.substring(0, temp.length()-1));
		}
		else {
		  areaParticipationFactor = Double.parseDouble(temp.substring(0, temp.length()));
		}

		tokenizer = new StringTokenizer(busline, "\t");
		int numTokens = tokenizer.countTokens();

		tokenizer.nextToken(); // id
		int type = Integer.parseInt(tokenizer.nextToken().trim()); // bus type
		tokenizer.nextToken(); // real load
		tokenizer.nextToken(); // reactive load
		tokenizer.nextToken(); // shunt conductance
		tokenizer.nextToken(); // shunt susceptance
		/*int area = */Integer.parseInt(tokenizer.nextToken().trim());
		tokenizer.nextToken(); // voltageMagnitude
		tokenizer.nextToken(); // voltageAngle
		String name = numTokens <= 13 ? "" : tokenizer.nextToken();
		tokenizer.nextToken();
		/*int zone =*/ Double.parseDouble(tokenizer.nextToken());
		/*double maxVoltage =*/ Double.parseDouble(tokenizer.nextToken());
		String token = tokenizer.nextToken();
		if (token.endsWith(";")) {
			token = token.substring(0, token.length() - 1);
		}
		/*double minVoltage =*/ Double.parseDouble(token);

		if (type == 4) {
			type = 0;
		}

		// check to see if the area already exists
		GeneratorTypeEnum gType = GeneratorTypeEnum.getEnum(type);
		Generator generator = registerGenerator(id, machineNum);
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
		generator.setCoordinate(point == null ? new PointImpl(0, 0) : point);
		generator.setAttribute(Generator.MVA_BASE_KEY, mBase);
		generator.setAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MIN_REAL_CURVE_KEY, pc1);
		generator.setAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MAX_REAL_CURVE_KEY, pc2);
		generator.setAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MIN_REACTIVE_CURVE_FOR_MIN_REAL_KEY, qc1Min);
		generator.setAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MAX_REACTIVE_CURVE_FOR_MIN_REAL_KEY, qc1Max);
		generator.setAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MIN_REACTIVE_CURVE_FOR_MAX_REAL_KEY, qc2Min);
		generator.setAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MAX_REACTIVE_CURVE_FOR_MAX_REAL_KEY, qc2Max);
		generator.setAttribute(MatPowerModelConstants.MATPOWER_LOAD_RAMP_RATE_KEY, loadRampRate);
		generator.setAttribute(MatPowerModelConstants.MATPOWER_TEN_MINUTE_RESERVE_RAMP_RATE_KEY, tenMinReserveRampRate);
		generator.setAttribute(MatPowerModelConstants.MATPOWER_THIRTY_MINUTE_RESERVE_RAMP_RATE_KEY, thirtyMinReserveRampRate);
		generator.setAttribute(MatPowerModelConstants.MATPOWER_REACTIVE_RAMP_RATE_KEY, reactiveRampRate);
		generator.setAttribute(MatPowerModelConstants.MATPOWER_AREA_PARTICIPATION_FACTOR_KEY, areaParticipationFactor);
		
    generator.setAttribute(Generator.HAS_PHASE_A_KEY, true);
    generator.setAttribute(Generator.HAS_PHASE_B_KEY, false);
    generator.setAttribute(Generator.HAS_PHASE_C_KEY, false);
    generator.setAttribute(Generator.DESIRED_REAL_GENERATION_A_KEY, realGenerationMax);
    generator.setAttribute(Generator.DESIRED_REAL_GENERATION_B_KEY, realGenerationMax);
    generator.setAttribute(Generator.DESIRED_REAL_GENERATION_C_KEY, realGenerationMax);
    generator.setAttribute(Generator.DESIRED_REACTIVE_GENERATION_A_KEY, reactiveMax);
    generator.setAttribute(Generator.DESIRED_REACTIVE_GENERATION_B_KEY, reactiveMax);
    generator.setAttribute(Generator.DESIRED_REACTIVE_GENERATION_C_KEY, reactiveMax);

    bus.setVoltagePU(desiredVoltage); // this should be the voltage set point based on the generator data
    
		return generator;
	}

	/**
	 * Creates some generators based on ids and the like
	 * 
	 * @param generators
	 * @param id
	 * @param area
	 * @param zone
	 * @return
	 */
	public void updateGenerator(Generator generator, Bus bus) {
	  int legacyId = bus.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, Integer.class);
	  
		if (generator.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY) == null) {
			generator.setAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, legacyId);
			for (int i = 0; i < Integer.MAX_VALUE; ++i) {
			  Pair<Integer,Integer> completeId = new Pair<Integer,Integer>(legacyId,i);
			  if (getLegacy(LEGACY_TAG,completeId) == null) {		       
			    generator.setAttribute(MatPowerModelConstants.MATPOWER_LEGACY_MACHINE_ID_KEY, i);
		      break;
			  }
			}
			registerLegacy(LEGACY_TAG, getId(generator), generator);
		}

		if (generator.getAttribute(Generator.MVA_BASE_KEY) == null) {
			generator.setAttribute(Generator.MVA_BASE_KEY, DEFAULT_MVA_BASE);
		}
		if (generator.getAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MIN_REAL_CURVE_KEY) == null) {
			generator.setAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MIN_REAL_CURVE_KEY, DEFAULT_MIN_REAL_CURVE);
		}
		if (generator.getAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MAX_REAL_CURVE_KEY) == null) {
			generator.setAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MAX_REAL_CURVE_KEY, DEFAULT_MAX_REAL_CURVE);
		}
		if (generator.getAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MIN_REACTIVE_CURVE_FOR_MIN_REAL_KEY) == null) {
			generator.setAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MIN_REACTIVE_CURVE_FOR_MIN_REAL_KEY, DEFAULT_MIN_REACTIVE_CURVE_FOR_MIN_REAL);
		}
		if (generator.getAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MAX_REACTIVE_CURVE_FOR_MIN_REAL_KEY) == null) {
			generator.setAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MAX_REACTIVE_CURVE_FOR_MIN_REAL_KEY, DEFAULT_MAX_REACTIVE_CURVE_FOR_MIN_REAL);
		}
		if (generator.getAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MIN_REACTIVE_CURVE_FOR_MAX_REAL_KEY) == null) {
			generator.setAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MIN_REACTIVE_CURVE_FOR_MAX_REAL_KEY, DEFAULT_MIN_REACTIVE_CURVE_FOR_MAX_REAL);
		}
		if (generator.getAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MAX_REACTIVE_CURVE_FOR_MAX_REAL_KEY) == null) {
			generator.setAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MAX_REACTIVE_CURVE_FOR_MAX_REAL_KEY, DEFAULT_MAX_REACTIVE_CURVE_FOR_MAX_REAL);
		}
		if (generator.getAttribute(MatPowerModelConstants.MATPOWER_LOAD_RAMP_RATE_KEY) == null) {
			generator.setAttribute(MatPowerModelConstants.MATPOWER_LOAD_RAMP_RATE_KEY, DEFAULT_LOAD_RAMP_RATE);
		}
		if (generator.getAttribute(MatPowerModelConstants.MATPOWER_TEN_MINUTE_RESERVE_RAMP_RATE_KEY) == null) {
			generator.setAttribute(MatPowerModelConstants.MATPOWER_TEN_MINUTE_RESERVE_RAMP_RATE_KEY, DEFAULT_TEN_MINUTE_RESERVE_RATE);
		}
		if (generator.getAttribute(MatPowerModelConstants.MATPOWER_THIRTY_MINUTE_RESERVE_RAMP_RATE_KEY) == null) {
			generator.setAttribute(MatPowerModelConstants.MATPOWER_THIRTY_MINUTE_RESERVE_RAMP_RATE_KEY, DEFAULT_THIRTY_MINUTE_RESERVE_RATE);
		}
		if (generator.getAttribute(MatPowerModelConstants.MATPOWER_REACTIVE_RAMP_RATE_KEY) == null) {
			generator.setAttribute(MatPowerModelConstants.MATPOWER_REACTIVE_RAMP_RATE_KEY, DEFAULT_REACTIVE_RAMP_RATE);
		}
		if (generator.getAttribute(MatPowerModelConstants.MATPOWER_AREA_PARTICIPATION_FACTOR_KEY) == null) {
			generator.setAttribute(MatPowerModelConstants.MATPOWER_AREA_PARTICIPATION_FACTOR_KEY, DEFAULT_AREA_PARTICPATION_FACTOR_RATE);
		}
		
		if (generator.getAttribute(Generator.STARTUP_COST_KEY) == null) {
		  generator.setAttribute(Generator.STARTUP_COST_KEY, DEFAULT_STARTUP_COST);
		}

    if (generator.getAttribute(Generator.SHUTDOWN_COST_KEY) == null) {
      generator.setAttribute(Generator.SHUTDOWN_COST_KEY, DEFAULT_SHUTDOWN_COST);
    }
	}

	/**
	  * Register the generator
	  * @param legacyId
	  * @param bus
	  * @return
	  */
	 private Generator registerGenerator(int id, int machineId) {
	   Pair<Integer,Integer> legacyId = new Pair<Integer,Integer>(id,machineId);
	   Generator generator = getLegacy(LEGACY_TAG, legacyId);
	   if (generator == null) {
	     generator = createNewGenerator();
	     generator.setAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY,id);
	     generator.setAttribute(MatPowerModelConstants.MATPOWER_LEGACY_MACHINE_ID_KEY,machineId);
	     generator.addOutputKey(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY);
	     generator.addOutputKey(MatPowerModelConstants.MATPOWER_LEGACY_MACHINE_ID_KEY);
	     registerLegacy(LEGACY_TAG, legacyId, generator);
	   }
	   return generator;
	 }
}
