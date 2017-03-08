package gov.lanl.micot.util.math.solver.linearprogram.cplex;

import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFlags;
import ilog.cplex.IloCplex;


/**
 * Flags and parameters for setting up mathematical programs 
 * @author Russell Bent
 */
public class CPLEXLinearProgramFlags extends MathematicalProgramFlags {
	
	private static final long serialVersionUID = 1L;
  
	public static final String ROOT_ALGORITHM_FLAG     = "ROOT_ALGORITHM";
  public static final String PRESOLVE_DUAL_FLAG      = "PRESOLVE_DUAL";
  public static final String NUMERICAL_EMPHASIS_FLAG = "NUMERICAL_EMPHASIS";
  public static final String MAX_THREADS_FLAG          = "MAX_THREADS";

  
	private static final int DEFAULT_ROOT_ALGORITHM         = IloCplex.Algorithm.Auto;
  private static final int DEFAULT_PRESOLVE_DUAL          = 0;
  private static final boolean DEFAULT_NUMERICAL_EMPHASIS = false;
  private static final int DEFAULT_MAXTHREADS = 0;
	
	/**
	 * Constructor
	 */
	public CPLEXLinearProgramFlags() {
	  super();
	  put(MATHEMATICAL_PROGRAM_FACTORY_FLAG,"gov.lanl.util.math.solver.linearprogram.cplex.CPLEXLinearProgramFactory");
	  put(ROOT_ALGORITHM_FLAG,DEFAULT_ROOT_ALGORITHM);
    put(PRESOLVE_DUAL_FLAG,DEFAULT_PRESOLVE_DUAL);
    put(NUMERICAL_EMPHASIS_FLAG,DEFAULT_NUMERICAL_EMPHASIS);
    put(MAX_THREADS_FLAG, DEFAULT_MAXTHREADS);

	}
	
}
