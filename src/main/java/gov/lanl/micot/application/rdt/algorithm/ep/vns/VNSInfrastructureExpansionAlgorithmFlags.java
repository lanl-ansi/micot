package gov.lanl.micot.application.rdt.algorithm.ep.vns;

import gov.lanl.micot.infrastructure.ep.optimize.ElectricPowerMathProgramOptimizerFlags;
import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;

/**
 * Flags used for expansion planning algorithms that are based on variable neighborhood search
 * @author Russell Bent
 */
public class VNSInfrastructureExpansionAlgorithmFlags extends ElectricPowerMathProgramOptimizerFlags  {
	
	private static final long serialVersionUID = 1L;

	public static final String MAX_TIME_KEY = "MAX_TIME";
	public static final String MAX_RESTARTS_KEY = "MAX_RESTARTS";
	public static final String MAX_ITERATIONS_KEY = "MAX_ITERATIONS";
	public static final String NEIGHBORHOOD_NORMALIZE_KEY = "NEIGHBORHOOD_NORMALIZE"; // d in the algorithm
	public static final String LINEAR_RELAXATION_MATH_PROGRAM_FACTORY_KEY = "LINEAR_RELAXATION_MATH_PROGRAM_FACTORY";
	
	private static final double DEFAULT_MAX_TIME = 172800.0; // 48 hours worth of cpu seconds
  public static final int DEFAULT_MAX_RESTARTS = 10;
  public static final int DEFAULT_MAX_ITERATIONS = 4;
  public static final double DEFAULT_NEIGHBORHOOD_NORMALIZE = 2.0;
  public static final String DEFAULT_LINEAR_RELAXATION_MATH_PROGRAM_FACTORY = "gov.lanl.micot.util.math.solver.linearprogram.cplex.CPLEXLinearProgramFactory";
	
  /**
   * Constructor
   */
	public VNSInfrastructureExpansionAlgorithmFlags() {
		super();   
		put(MAX_TIME_KEY,DEFAULT_MAX_TIME);
		put(MAX_RESTARTS_KEY, DEFAULT_MAX_RESTARTS);
		put(MAX_ITERATIONS_KEY, DEFAULT_MAX_ITERATIONS);
		put(NEIGHBORHOOD_NORMALIZE_KEY, DEFAULT_NEIGHBORHOOD_NORMALIZE);
		put(LINEAR_RELAXATION_MATH_PROGRAM_FACTORY_KEY,DEFAULT_LINEAR_RELAXATION_MATH_PROGRAM_FACTORY);
	}
	
	/**
	 * Constructor
	 * @param flags
	 */
	public VNSInfrastructureExpansionAlgorithmFlags(OptimizerFlags flags) {
		super(flags);

		if (get(MAX_TIME_KEY) == null) {
      put(MAX_TIME_KEY,DEFAULT_MAX_TIME);     
    }
		
		if (get(MAX_RESTARTS_KEY) == null) {
		  put(MAX_RESTARTS_KEY, DEFAULT_MAX_RESTARTS);
		}
		
		if (get(MAX_ITERATIONS_KEY) == null) {
		  put(MAX_ITERATIONS_KEY, DEFAULT_MAX_ITERATIONS);
		}
		
		if (get(NEIGHBORHOOD_NORMALIZE_KEY) == null) {
		  put(NEIGHBORHOOD_NORMALIZE_KEY, DEFAULT_NEIGHBORHOOD_NORMALIZE);
		}
		
		if (get(LINEAR_RELAXATION_MATH_PROGRAM_FACTORY_KEY) == null) {
		  put(LINEAR_RELAXATION_MATH_PROGRAM_FACTORY_KEY,DEFAULT_LINEAR_RELAXATION_MATH_PROGRAM_FACTORY);
		}

		
	}
	
}
