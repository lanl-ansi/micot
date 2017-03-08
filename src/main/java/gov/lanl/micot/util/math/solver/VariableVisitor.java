package gov.lanl.micot.util.math.solver;

/**
 * A visitor class for variables
 * @author Carleton Coffrin
 * Slightly modified by Conrado to apply for binary variables, Oct 2014 
 */
public class VariableVisitor {
	
	public void doIt(Variable var) {
		var.visit(this);
	}

	// Apply a continous --> continuous variable: @param v
	public void applyContinuousVariable(ContinuousVariable v) {
	}
	
	// Apply a discrete variable: @param v
	public void applyDiscreteVariable(DiscreteVariable v) { 
	}
	
	// Apply a binary (boolean) variable: @param v
	public void applyBinaryVariable(BinaryVariable v) { 
	}
	
}
