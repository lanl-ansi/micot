package gov.lanl.micot.util.math.solver.mathprogram;

/**
 * Visitor for linear objective functions
 * @author Carleton Coffrin
 */
public class LinearObjectiveVisitor extends MathematicalProgramObjectiveVisitor {
	
	/**
	 * Do the visitation
	 * @param var
	 */
	public void doIt(LinearObjective var) { 
		var.visit(this);
	}
	
	/**
	 * visit based upon maximization
	 * @param o
	 */
	public void applyObjectiveMaximize(LinearObjectiveMaximize o) {		
	}
	
	/**
	 * visit based upon minimization
	 * @param o
	 */
	public void applyObjectiveMinimize(LinearObjectiveMinimize o) {
	}
}
