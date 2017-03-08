package gov.lanl.micot.util.math.solver;

/**
 * An implementation of a discrete variable
 * @author Carleton Coffrin
 */
public class DiscreteVariable extends VariableImpl {

	/**
	 * Constructor
	 * @param name
	 */
	public DiscreteVariable(String name) {
		super(name);
	}

	
	@Override
	public void visit(VariableVisitor v) {
		v.applyDiscreteVariable(this);
	}
	
  @Override
  public int compareTo(Variable o) {
    return getName().compareTo(o.getName());
  }
}
