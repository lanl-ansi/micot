package gov.lanl.micot.util.math.solver.mathprogram;

/**
 * Visitor for quadratic objective functions
 * @author Carleton Coffrin
 */
public class QuadraticObjectiveVisitor extends MathematicalProgramObjectiveVisitor {
	
	/**
	 * Do the visitation
	 * @param var
	 */
	public void doIt(QuadraticObjective var) { 
		var.visit(this);
	}
	
	/**
	 * visit based upon maximization
	 * @param o
	 */
	public void applyObjectiveMaximize(QuadraticObjectiveMaximize o) {		
	}
	
	/**
	 * visit based upon minimization
	 * @param o
	 */
	public void applyObjectiveMinimize(QuadraticObjectiveMinimize o) {
	}
}
