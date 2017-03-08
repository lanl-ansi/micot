package gov.lanl.micot.util.math.solver;

/**
 * A visitor for quadratic constraints
 * @author Russell Bent
 */
public class QuadraticConstraintVisitor extends LinearConstraintVisitor {

	/**
	 * Do the visiting
	 * @param var
	 */
	public void doIt(QuadraticConstraint var) {
		var.visit(this);
	}

	/**
	 * Equal constraint
	 * @param c
	 */
	public void applyConstraintEquals(QuadraticConstraintEquals c) {		
	}
	
	/**
	 * Greater visitor
	 * @param c
	 */
	public void applyConstraintGreater(QuadraticConstraintGreater c) {
	  
	}

	/**
	 * Greater than or equal application
	 * @param c
	 */
	public void applyConstraintGreaterEq(QuadraticConstraintGreaterEq c) {
	  
	}

	/**
	 * apply less appliaction
	 * @param c
	 */
	public void applyConstraintLess(QuadraticConstraintLess c) {
	  
	}
	
	/**
	 * apply less han or equal
	 * @param c
	 */
	public void applyConstraintLessEq(QuadraticConstraintLessEq c) {
	  
	}

}
