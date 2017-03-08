package gov.lanl.micot.infrastructure.naturalgas.simulate;

import gov.lanl.micot.infrastructure.simulate.SimulatorFlags;

/**
 * Flags used for opf DC solvers
 * @author Russell Bent
 */
public class OptimalGasFlowSimulatorFlags extends SimulatorFlags {
	
	private static final long serialVersionUID = 1L;
	
	public static final String OPTIMIZER_FACTORY_KEY = "OPTIMIZER_FACTORY_KEY";
	private static final String DEFAULT_OPTIMIZER_FACTORY = "gov.lanl.infrastructure.naturalgas.optimize.opf.DefaultOptimalPowerFlowFactory";

  /**
   * Constructor
   */
	public OptimalGasFlowSimulatorFlags() {
		super();
		put(OPTIMIZER_FACTORY_KEY,DEFAULT_OPTIMIZER_FACTORY);
	}
	
	/**
	 * Constructor
	 * @param flags
	 */
	public OptimalGasFlowSimulatorFlags(SimulatorFlags flags) {
		this();
		fill(flags);
	}	
}
