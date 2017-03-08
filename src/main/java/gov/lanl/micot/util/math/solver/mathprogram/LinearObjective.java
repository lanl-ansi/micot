package gov.lanl.micot.util.math.solver.mathprogram;

import gov.lanl.micot.util.math.solver.LinearSummation;

/**
 * A linear objective function
 * @author Carleton Coffrin
 */
public interface LinearObjective extends MathematicalProgramObjective, LinearSummation {

	/**
	 * Visit routine
	 * @param v
	 */
	public void visit(LinearObjectiveVisitor v);
	
}
