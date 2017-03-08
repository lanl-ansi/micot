package gov.lanl.micot.util.math.solver.exception;

/**
 * Constructor
 * @author Russell Bent
 */
public class VariableExistsException extends SolverException{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public VariableExistsException(String name){
		super("Variable " + name + " already exists");
	}
}
