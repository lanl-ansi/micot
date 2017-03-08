package gov.lanl.micot.infrastructure.simulate;

import java.io.File;

import gov.lanl.micot.util.io.FlagsImpl;

/**
 * Flags and parameters for setting up simulators 
 * @author Russell Bent
 */
public class SimulatorFlags extends FlagsImpl {
	
	private static final long serialVersionUID = 1L;

	public static final String PATH_FLAG = "DIRECTORY_PATH";
	
	private static final String DEFAULT_DIRECTORY = System.getProperty("user.dir") + File.separatorChar;

  public static final String SCENARIOS_KEY = "Scenarios";
	
	/**
	 * Constructor
	 */
	public SimulatorFlags() {
	  super();
		put(PATH_FLAG, DEFAULT_DIRECTORY);
	}
	   
}
