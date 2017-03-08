package gov.lanl.micot.infrastructure.ep.simulate.opendss;

import gov.lanl.micot.infrastructure.simulate.SimulatorFlags;

/**
 * Flags used for opendss solvers
 * @author Russell Bent
 */
public class OpenDSSSimulatorFlags extends SimulatorFlags {
	
	private static final long serialVersionUID = 1L;
 
  /**
   * Constructor
   */
	public OpenDSSSimulatorFlags() {
		super();
  }
	
	/**
	 * Constructor
	 * @param flags
	 */
	public OpenDSSSimulatorFlags(SimulatorFlags flags) {
		this();
		fill(flags);
	}
}
