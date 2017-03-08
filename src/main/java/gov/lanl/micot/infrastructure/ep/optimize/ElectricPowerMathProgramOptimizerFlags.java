package gov.lanl.micot.infrastructure.ep.optimize;


import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;
import gov.lanl.micot.infrastructure.optimize.mathprogram.MathProgramOptimizerFlags;

/**
 * Flags used for opf DC solvers
 * @author Russell Bent
 */
public class ElectricPowerMathProgramOptimizerFlags extends MathProgramOptimizerFlags {
	
	private static final long serialVersionUID = 1L;
	

  /**
   * Constructor
   */
	public ElectricPowerMathProgramOptimizerFlags() {
		super();
	}
	
	/**
	 * Constructor
	 * @param flags
	 */
	public ElectricPowerMathProgramOptimizerFlags(OptimizerFlags flags) {
	  super(flags);
	}	
}
