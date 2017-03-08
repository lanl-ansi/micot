package gov.lanl.micot.util.math.solver.exception;

/**
 * Constructor
 * @author 210117
 *
 */
public class SolverExceptionNoConvergence extends SolverException{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public SolverExceptionNoConvergence(){
		super("The model did not converge");
	}
}
