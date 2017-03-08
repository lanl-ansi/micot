package gov.lanl.micot.infrastructure.ep.simulate.powerworld;

import gov.lanl.micot.infrastructure.simulate.SimulatorFlags;

/**
 * Flags used for power world solvers
 * @author Russell Bent
 */
public class PowerworldSimulatorFlags extends SimulatorFlags {
	
	private static final long serialVersionUID = 1L;
 
  /**
   * Constructor
   */
	public PowerworldSimulatorFlags() {
		super();
  }
	
	/**
	 * Constructor
	 * @param flags
	 */
	public PowerworldSimulatorFlags(SimulatorFlags flags) {
		this();
		fill(flags);
	}
}
