package gov.lanl.micot.util.math.solver.exception;

/**
 * Constructor
 * @author Russell Bent
 */
public class NoConstraintException extends SolverException{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public NoConstraintException(String name){
		super("Constraint " + name + " does not exist");
	}
}
