package gov.lanl.micot.infrastructure.optimize.mathprogram;

import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;

/**
 * Flags used for math program solvers
 * @author Russell Bent
 */
public class MathProgramOptimizerFlags extends OptimizerFlags {
	
	private static final long serialVersionUID = 1L;
	
	public static final String MATH_PROGRAM_FACTORY_KEY         = "MathProgramFactory";
  
	private static final String DEFAULT_MATH_PROGRAM = "gov.lanl.micot.util.math.solver.linearprogram.cplex.CPLEXLinearProgramFactory";
  
			/**
   * Constructor
   */
  public MathProgramOptimizerFlags() {
		super();
		put(MATH_PROGRAM_FACTORY_KEY, getDefaultMathProgram());
	}
	
	/**
	 * Constructor
	 * @param flags
	 */
  public MathProgramOptimizerFlags(OptimizerFlags flags) {
	  super(flags);
	  
		if (get(MATH_PROGRAM_FACTORY_KEY) == null) {
	    put(MATH_PROGRAM_FACTORY_KEY,getDefaultMathProgram());		  
		}
		
	}	
	
  /**
   * Overrideable function to set the default Math program...
   * @return
   */
  protected String getDefaultMathProgram() {
    return DEFAULT_MATH_PROGRAM;
  }

	
}
