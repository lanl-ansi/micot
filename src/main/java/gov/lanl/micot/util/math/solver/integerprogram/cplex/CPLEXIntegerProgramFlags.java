package gov.lanl.micot.util.math.solver.integerprogram.cplex;

import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFlags;


/**
 * Flags and parameters for setting up mathematical programs 
 * @author Russell Bent
 */
public class CPLEXIntegerProgramFlags extends MathematicalProgramFlags {
	
	private static final long serialVersionUID = 1L;

	public static final String NUMERICAL_EMPHASIS_FLAG   = "NUMERICAL_EMPHASIS";
	public static final String DO_REPEATED_PRESOLVE_FLAG = "DO_REPEATED_PRESOLVE";
  public static final String MAX_THREADS_FLAG          = "MAX_THREADS";
  public static final String DO_PRESOLVE_FLAG          = "DO_PRESOLVE";
  public static final String DO_EXPORT_FLAG            = "DO_EXPORT";
  public static final String SOLUTION_FILENAME_FLAG    = "SOLUTION_FILENAME";
  public static final String MODEL_FILENAME_FLAG       = "MODEL_FILENAME";
  
	private static final boolean DEFAULT_NUMERICAL_EMPHASIS = false;
	private static final boolean DEFAULT_DO_REPREATED_PRESOLVE = true;
  private static final int DEFAULT_MAXTHREADS = 0;
  private static final boolean DEFAULT_DO_PRESOLVE = true;
  private static final boolean DEFAULT_DO_EXPORT = false;
  private static final String DEFAULT_SOLUTION_FILENAME = "cplex_sol.txt";
  private static final String DEFAULT_MODEL_FILENAME = "cplex.lp";
 

	/**
	 * Constructor
	 */
	public CPLEXIntegerProgramFlags() {
	  super();
	  put(NUMERICAL_EMPHASIS_FLAG,DEFAULT_NUMERICAL_EMPHASIS);
	  put(DO_REPEATED_PRESOLVE_FLAG,DEFAULT_DO_REPREATED_PRESOLVE);
	  put(DO_PRESOLVE_FLAG,DEFAULT_DO_PRESOLVE);
	  put(MAX_THREADS_FLAG, DEFAULT_MAXTHREADS);
	  put(DO_EXPORT_FLAG, DEFAULT_DO_EXPORT);
	  put(SOLUTION_FILENAME_FLAG, DEFAULT_SOLUTION_FILENAME);
	  put(MODEL_FILENAME_FLAG, DEFAULT_MODEL_FILENAME);
	}
	
}
