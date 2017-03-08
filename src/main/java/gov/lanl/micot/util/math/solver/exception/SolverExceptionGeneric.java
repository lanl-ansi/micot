package gov.lanl.micot.util.math.solver.exception;

/**
 * This is a very generic exception and should only be used for rapid prototyping
 * Ideally each specific kind of expression has it's own specific class
 * @author 236322
 *
 */
public class SolverExceptionGeneric extends SolverException{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * @param msg
	 */
	public SolverExceptionGeneric(String msg){
		super(msg);
	}
}
