package gov.lanl.micot.infrastructure.model;

/**
 * Exception type for throwing errors when the model cannot be changed
 * @author Russell Bent
 */
public class ModelChangeException extends RuntimeException {

	private static final long	serialVersionUID	= 1L;
	
	/**
	 * Constructor
	 * @param message
	 */
	public ModelChangeException(String message) {
		super(message);
	}

}
