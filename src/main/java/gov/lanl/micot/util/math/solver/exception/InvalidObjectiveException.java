package gov.lanl.micot.util.math.solver.exception;

/**
 * Constructor
 * @author Russell Bent
 *
 */
public class InvalidObjectiveException extends SolverException{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public InvalidObjectiveException(String name){
		super(name);
	}
}
