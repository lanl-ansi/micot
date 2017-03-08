package gov.lanl.micot.util.math.solver.mathprogram;

import gov.lanl.micot.util.io.FlagsImpl;

/**
 * Flags and parameters for setting up mathematical programs
 * 
 * @author Russell Bent Slightly modified and more comments added by Conrado,
 *         Nov 2014
 */
public class MathematicalProgramFlags extends FlagsImpl {

	private static final long serialVersionUID = 1L;

	public static final String MATHEMATICAL_PROGRAM_FACTORY_FLAG = "MATHEMATICAL_PROGRAM_FACTORY_FLAG";
	public static final String DEBUG_ON_FLAG = "DEBUG_ON_FLAG";
	public static final String TIMEOUT_FLAG = "TIMEOUT_FLAG"; // usually CPU seconds
	public static final String DYNAMIC_CONSTRAINTS_FLAG = "DYNAMIC_CONSTRAINTS_FLAG";
	public static final String DYNAMIC_OBJECTIVES_FLAG = "DYNAMIC_OBJECTIVES_FLAG";
	public static final String MIP_GAP_TOLERANCE_FLAG = "MIP_GAP_TOLERANCE";
	public static final String FEASABILITY_GAP_TOLERANCE_FLAG = "FEASABILITY_GAP_TOLERANCE";

	private static final String DEFAULT_MATHEMATICAL_PROGRAM_FACTORY = "gov.lanl.util.math.solver.linearprogram.drasys.DrasysLinearProgramFactory";
	private static final boolean DEFAULT_DEBUG = false;
	private static final boolean DEFAULT_DYNAMIC_CONSTRAINTS = false;
	private static final boolean DEFAULT_DYNAMIC_OBJECTIVES = false;

	private static final Double DEFAULT_TIMEOUT = Double.POSITIVE_INFINITY;
	private static final Double DEFAULT_OPTIMALITY_GAP = 1e-4;
	private static final Double DEFAULT_FEASABILITY_GAP = 1e-4;

	/**
	 * Constructor
	 */
	public MathematicalProgramFlags() {
		super();
		put(MATHEMATICAL_PROGRAM_FACTORY_FLAG,
				DEFAULT_MATHEMATICAL_PROGRAM_FACTORY);
		put(DEBUG_ON_FLAG, DEFAULT_DEBUG);
		put(TIMEOUT_FLAG, DEFAULT_TIMEOUT);
		put(DYNAMIC_CONSTRAINTS_FLAG, DEFAULT_DYNAMIC_CONSTRAINTS);
		put(DYNAMIC_OBJECTIVES_FLAG, DEFAULT_DYNAMIC_OBJECTIVES);
		put(MIP_GAP_TOLERANCE_FLAG, DEFAULT_OPTIMALITY_GAP);
		put(FEASABILITY_GAP_TOLERANCE_FLAG, DEFAULT_FEASABILITY_GAP);
	}

}
