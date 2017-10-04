package gov.lanl.micot.infrastructure.ep.simulate.powerworld;

import gov.lanl.micot.infrastructure.simulate.SimulatorFlags;

/**
 * Flags used for power world solvers
 * @author Russell Bent
 */
public class PowerworldSimulatorFlags extends SimulatorFlags {
	
	private static final long serialVersionUID = 1L;
	
	public static final String CHECK_PHASE_SHIFTERS_FLAG = "check phase shifters";
  public static final String CHECK_SHUNTS_FLAG         = "check shunts";
  public static final String CHECK_SVC_FLAG            = "check svc";
  public static final String CHECK_TAPS_FLAG           = "check taps";
  public static final String CHECK_AREA_FLAG           = "check area";
  public static final String CHECK_MVAR_LIMITS_FLAG    = "check mvar limits";
  public static final String CHECK_MW_LIMITS_FLAG      = "check mw limits";

	public static final boolean DEFAULT_PHASE_SHIFTERS = true;
  public static final boolean DEFAULT_SHUNTS         = true;
  public static final boolean DEFAULT_SVC            = true;
  public static final boolean DEFAULT_TAPS           = true;
  public static final boolean DEFAULT_AREA           = true;
  public static final boolean DEFAULT_MVAR_LIMITS    = false;
  public static final boolean DEFAULT_MW_LIMITS      = true;
	
  /**
   * Constructor
   */
	public PowerworldSimulatorFlags() {
		super();
		put(CHECK_PHASE_SHIFTERS_FLAG, DEFAULT_PHASE_SHIFTERS);
		put(CHECK_SHUNTS_FLAG, DEFAULT_SHUNTS);
		put(CHECK_SVC_FLAG, DEFAULT_SVC);
		put(CHECK_TAPS_FLAG,DEFAULT_TAPS);
		put(CHECK_AREA_FLAG, DEFAULT_AREA);
		put(CHECK_MVAR_LIMITS_FLAG, DEFAULT_MVAR_LIMITS);
		put(CHECK_MW_LIMITS_FLAG,DEFAULT_MW_LIMITS);
  }
	
	/**
	 * Constructor
	 * @param flags
	 */
	public PowerworldSimulatorFlags(SimulatorFlags flags) {
		this();
		fill(flags);
		
		if (get(CHECK_PHASE_SHIFTERS_FLAG) == null) {
		  put(CHECK_PHASE_SHIFTERS_FLAG, DEFAULT_PHASE_SHIFTERS);
		}

		if (get(CHECK_SHUNTS_FLAG) == null) {
		  put(CHECK_SHUNTS_FLAG, DEFAULT_SHUNTS);
		}
		
		if (get(CHECK_SVC_FLAG) == null) {
		  put(CHECK_SVC_FLAG, DEFAULT_SVC);
		}
		
		if (get(CHECK_TAPS_FLAG) == null) {
		  put(CHECK_TAPS_FLAG,DEFAULT_TAPS);
		}
		
		if (get(CHECK_AREA_FLAG) == null) {
		  put(CHECK_AREA_FLAG, DEFAULT_AREA);
		}
		
		if (get(CHECK_MVAR_LIMITS_FLAG) == null) {
		  put(CHECK_MVAR_LIMITS_FLAG, DEFAULT_MVAR_LIMITS);
		}
		
		if (get(CHECK_MW_LIMITS_FLAG) == null) {
		  put(CHECK_MW_LIMITS_FLAG,DEFAULT_MW_LIMITS);
		}
		
	}
}
