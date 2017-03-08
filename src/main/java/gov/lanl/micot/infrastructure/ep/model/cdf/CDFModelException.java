package gov.lanl.micot.infrastructure.ep.model.cdf;

/**
 * A custom exception for cdf model exceptions
 * @author Russell Bent
 */
public class CDFModelException extends RuntimeException {

	protected static final long serialVersionUID = 0;
	
	/**
	 * Constructor
	 * @param str
	 */
	public CDFModelException(String str) {
		super(str);
	}
	
}
