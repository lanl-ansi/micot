package gov.lanl.micot.infrastructure.optimize.jump;

import gov.lanl.micot.util.io.FlagsImpl;

/**
 * Flags used for interacting with optimization algorithms developed in Jump
 * @author Russell Bent
 */
public class JumpOptimizerFlags extends FlagsImpl {
	
	private static final long serialVersionUID = 1L;
	
	public static final String CPLEX_KEY  = "CPLEX"; 
  public static final String IPOPT_KEY  = "IPOPT"; 
  public static final String GUROBI_KEY = "GUROBI"; 
  public static final String BONMIN_KEY = "BONMIN"; 
  public static final String KNITRO_KEY = "KNITRO"; 
  public static final String MOSEK_KEY  = "MOSEK"; 
  
	
	public static final String SOLVER_KEY         = "solver";
  	
	private static final String DEFAULT_SOLVER = GUROBI_KEY;

  /**
   * Constructor
   */
  public JumpOptimizerFlags() {
		super();
		put(SOLVER_KEY,DEFAULT_SOLVER);
	}
	
	/**
	 * Constructor
	 * @param flags
	 */
  public JumpOptimizerFlags(FlagsImpl flags) {
    fill(flags);
		if (get(SOLVER_KEY) == null) {
	    put(SOLVER_KEY,DEFAULT_SOLVER);		  
		}		
	}	
		
}
