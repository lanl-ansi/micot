package gov.lanl.micot.infrastructure.naturalgas.optimize;

import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;
import gov.lanl.micot.infrastructure.optimize.mathprogram.MathProgramOptimizerFlags;

/**
 * Flags used for optimal gas flow solvers
 * @author Russell Bent
 */
public class NaturalGasMathProgramOptimizerFlags extends MathProgramOptimizerFlags {
	
	private static final long serialVersionUID = 1L;
	

  /**
   * Constructor
   */
	public NaturalGasMathProgramOptimizerFlags() {
		super();
	}
	
	/**
	 * Constructor
	 * @param flags
	 */
	public NaturalGasMathProgramOptimizerFlags(OptimizerFlags flags) {
	  super(flags);
	}	
}
