package gov.lanl.micot.util.math.solver;

/**
 * An implementation of a continuous variable
 * @author Carleton Coffrin
 */
public class ContinuousVariable extends VariableImpl {

	/**
	 * Constructor
	 * @param name
	 */
	public ContinuousVariable(String name) {
		super(name);
	}
	
	@Override
	public void visit(VariableVisitor v) {
		v.applyContinuousVariable(this);
	}

  @Override
  public int compareTo(Variable o) {
    return getName().compareTo(o.getName());
  }	
}
