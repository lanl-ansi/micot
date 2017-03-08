package gov.lanl.micot.infrastructure.ep.simulate.dew;

import gov.lanl.micot.infrastructure.simulate.SimulatorFlags;

/**
 * Flags used for t2000 solvers
 * @author Russell Bent
 */
public class DewSimulatorFlags extends SimulatorFlags {
	
	private static final long serialVersionUID = 1L;
 
  /**
   * Constructor
   */
	public DewSimulatorFlags() {
		super();
  }
	
	/**
	 * Constructor
	 * @param flags
	 */
	public DewSimulatorFlags(SimulatorFlags flags) {
		this();
		fill(flags);
	}
}
