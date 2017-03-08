package gov.lanl.micot.util.math.solver.exception;

/**
 * Implementation of a solver exception
 * @author Carleton Coffin
 */
public class SolverException extends Exception {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor
	 * @param msg
	 */
	protected SolverException(String msg){
		super(msg);
	}
}
