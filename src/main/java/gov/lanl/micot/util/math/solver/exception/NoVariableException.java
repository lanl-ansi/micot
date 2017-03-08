package gov.lanl.micot.util.math.solver.exception;

/**
 * Constructor
 * @author Russell Bent
 */
public class NoVariableException extends SolverException{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public NoVariableException(String name){
		super("Variable " + name + " does not exist");
	}
}
