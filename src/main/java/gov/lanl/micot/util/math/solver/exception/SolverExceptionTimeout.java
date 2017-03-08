package gov.lanl.micot.util.math.solver.exception;

/**
 * Constructor
 * @author 210117
 *
 */
public class SolverExceptionTimeout extends SolverException{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public SolverExceptionTimeout(){
		super("The model could not be solved in the time specified");
	}
}
