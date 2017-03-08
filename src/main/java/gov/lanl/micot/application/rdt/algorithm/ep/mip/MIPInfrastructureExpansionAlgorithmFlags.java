package gov.lanl.micot.application.rdt.algorithm.ep.mip;

import gov.lanl.micot.infrastructure.ep.optimize.ElectricPowerMathProgramOptimizerFlags;
import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;

/**
 * Flags used for expansion planning algorithms
 * @author Russell Bent
 */
public class MIPInfrastructureExpansionAlgorithmFlags extends ElectricPowerMathProgramOptimizerFlags  {
	
	private static final long serialVersionUID = 1L;

  /**
   * Constructor
   */
	public MIPInfrastructureExpansionAlgorithmFlags() {
		super();    
	}
	
	/**
	 * Constructor
	 * @param flags
	 */
	public MIPInfrastructureExpansionAlgorithmFlags(OptimizerFlags flags) {
		super(flags);
	}
	
}
