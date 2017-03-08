package gov.lanl.micot.infrastructure.ep.model.matpower;

/**
 * A custom exception for matpower model exceptions
 * @author Russell Bent
 */
public class MatPowerModelException extends RuntimeException {

	protected static final long serialVersionUID = 0;
	
	/**
	 * Constructor
	 * @param str
	 */
	public MatPowerModelException(String str) {
		super(str);
	}
	
}
