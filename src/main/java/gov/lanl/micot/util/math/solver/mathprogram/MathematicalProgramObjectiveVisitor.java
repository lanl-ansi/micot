package gov.lanl.micot.util.math.solver.mathprogram;

/**
 * Visitor for  objective functions
 * @author Carleton Coffrin
 */
public class MathematicalProgramObjectiveVisitor {
	
	/**
	 * Do the visitation
	 * @param var
	 */
	public void doIt(MathematicalProgramObjective var) { 
		var.visit(this);
	}
	
	/**
	 * visit based upon maximization
	 * @param o
	 */
	public void applyObjectiveMaximize(MathematicalProgramObjective o) {		
	}
	
	/**
	 * visit based upon minimization
	 * @param o
	 */
	public void applyObjectiveMinimize(MathematicalProgramObjective o) {
	}
}
