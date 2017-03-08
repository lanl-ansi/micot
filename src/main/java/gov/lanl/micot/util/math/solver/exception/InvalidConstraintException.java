package gov.lanl.micot.util.math.solver.exception;

/**
 * Constructor
 * @author Russell Bent
 *
 */
public class InvalidConstraintException extends SolverException{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public InvalidConstraintException(String name){
		super(name);
	}
}
