package gov.lanl.micot.infrastructure.coupled.simulate.test;

import gov.lanl.micot.infrastructure.simulate.SimulatorFlags;

/**
 * Flags used for a simple test solver... no flags really defined 
 * @author Russell Bent
 */
public class TestSimulatorFlags extends SimulatorFlags {
	
  private static final long serialVersionUID = 1L;

  /**
   * Constructor
   */
	public TestSimulatorFlags() {
		super();
  }
	
	/**
	 * Constructor
	 * @param flags
	 */
	public TestSimulatorFlags(SimulatorFlags flags) {
		this();
		fill(flags);
	}
}
