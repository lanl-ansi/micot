package gov.lanl.micot.util.math.solver.exception;

/**
 * Constructor
 * @author 210117
 *
 */
public class InvalidVariableException extends SolverException{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public InvalidVariableException(String name){
		super(name);
	}
}
