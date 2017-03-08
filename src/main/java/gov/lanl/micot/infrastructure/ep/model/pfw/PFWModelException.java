package gov.lanl.micot.infrastructure.ep.model.pfw;

/**
 * A custom exception for pfw model exceptions
 * @author Russell Bent
 */
public class PFWModelException extends RuntimeException {

	protected static final long serialVersionUID = 0;
	
	/**
	 * Constructor
	 * @param str
	 */
	public PFWModelException(String str) {
		super(str);
	}
	
}
