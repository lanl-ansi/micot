package gov.lanl.micot.application.rdt.algorithm.ep.sbd;

import gov.lanl.micot.application.rdt.algorithm.ep.ResilienceExpansionFlags;
import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFlags;

/**
 * Flags used for expansion planning algorithms based on scenario based decomposition
 * 
 * @author Russell Bent
 */
public class SBDResilienceFlags extends OptimizerFlags {

	private static final long serialVersionUID = 1L;

  public static final String OUTER_PREFIX = "Outer";
  public static final String INNER_PREFIX = "Inner";
	
	public static final String OUTER_MATH_PROGRAM_FACTORY_KEY = OUTER_PREFIX + MathematicalProgramFlags.MATHEMATICAL_PROGRAM_FACTORY_FLAG;
  public static final String INNER_MATH_PROGRAM_FACTORY_KEY = INNER_PREFIX + MathematicalProgramFlags.MATHEMATICAL_PROGRAM_FACTORY_FLAG;
  
	/**
	 * Constructor
	 */
	public SBDResilienceFlags() {
		super();
		init(new ResilienceExpansionFlags());
	}
	
	/**
	 * Constructor
	 * 
	 * @param flags
	 */
	public SBDResilienceFlags(OptimizerFlags flags) {
		super(flags);
		init(new ResilienceExpansionFlags(flags));
	}

	/**
	 * Initialize everything we need to run the inner and outer problem
	 * @param defaults
	 */
  private void init(ResilienceExpansionFlags defaults) {
	  // initialize inner and outer math program flag
    if (get(OUTER_MATH_PROGRAM_FACTORY_KEY) == null) {
      put(OUTER_MATH_PROGRAM_FACTORY_KEY,defaults.get(ResilienceExpansionFlags.MATH_PROGRAM_FACTORY_KEY));
    }
    if (get(INNER_MATH_PROGRAM_FACTORY_KEY) == null) {
      put(INNER_MATH_PROGRAM_FACTORY_KEY,defaults.get(ResilienceExpansionFlags.MATH_PROGRAM_FACTORY_KEY));
    }
	      
  }
	
	  
}
