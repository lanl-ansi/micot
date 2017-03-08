package gov.lanl.micot.util.math.solver.mathprogram;

import gov.lanl.micot.util.math.solver.LinearSummation;

/**
 * An objective function
 * @author Russell Bent
 */
public interface MathematicalProgramObjective extends LinearSummation  {

  /**
	 * Visit routine
	 * @param v
	 */
	public abstract void visit(MathematicalProgramObjectiveVisitor v);
}
