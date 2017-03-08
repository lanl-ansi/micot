package gov.lanl.micot.util.math.solver.exception;

/**
 * Constructor
 * @author 210117
 *
 */
public class SolverExceptionNoSolution extends SolverException {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public SolverExceptionNoSolution() {
		super("The model is infeasable");
	}
}
