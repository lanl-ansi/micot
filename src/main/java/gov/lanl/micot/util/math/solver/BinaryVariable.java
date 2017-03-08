package gov.lanl.micot.util.math.solver;

/**
 * An implementation of a binary variable
 * @author Conrado Borraz, Oct 2014
 */
public class BinaryVariable extends VariableImpl {
	// Constructor: @param name
	public BinaryVariable(String name) {
		super(name);
	}

	@Override
	public void visit(VariableVisitor v) {
		v.applyBinaryVariable(this);
	}
	
	@Override
	public int compareTo(Variable o) {
		return getName().compareTo(o.getName());
	}
}
