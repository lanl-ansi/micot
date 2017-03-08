package gov.lanl.micot.util.math.solver.exception;

/**
 * Constructor
 * @author 210117
 *
 */
public class SolverExceptionUnboundedSolution extends SolverException{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public SolverExceptionUnboundedSolution(){
		super("The model is unbounded");
	}
}
