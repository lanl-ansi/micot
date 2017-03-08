package gov.lanl.micot.util.math.solver;

/**
 * A visitor for linear constraints
 * @author Carleton Coffrin
 * Revised by Conrado, Oct 2014
 */
public class LinearConstraintVisitor  {

	// Do the visiting: @param var
	public void doIt(LinearConstraint var) {
		var.visit(this);
	}

	// Equal constraint: @param c
	public void applyConstraintEquals(LinearConstraintEquals c) {		
	}
	
	// Greater visitor: @param c
	public void applyConstraintGreater(LinearConstraintGreater c) {
	}

	// Greater than or equal application: @param c
	public void applyConstraintGreaterEq(LinearConstraintGreaterEq c) {
	}

	// apply less appliaction --> Corrected: application, @param c
	public void applyConstraintLess(LinearConstraintLess c) {
	}
	
	// apply less han --> CorrecteD: than or equal: @param c
	public void applyConstraintLessEq(LinearConstraintLessEq c) {
	}
}
