package gov.lanl.micot.util.math.solver;

/**
 * An implementation of the name
 * @author Russell Bent
 */
public abstract class VariableImpl implements Variable {

	private String name = null;

	/**
	 * Constructor
	 * @param name
	 */
	protected VariableImpl(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public boolean equals(Object v) {
		return toString().equals(v.toString());
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
}
