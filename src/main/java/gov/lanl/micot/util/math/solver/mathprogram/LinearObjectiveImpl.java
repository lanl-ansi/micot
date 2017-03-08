package gov.lanl.micot.util.math.solver.mathprogram;

import gov.lanl.micot.util.math.solver.LinearSummationImpl;
import gov.lanl.micot.util.math.solver.Variable;

/**
 * A linear objective function
 * @author Carleton Coffrin
 */
public abstract class LinearObjectiveImpl extends LinearSummationImpl implements LinearObjective {

	/**
	 * Constructor
	 */
	public LinearObjectiveImpl(){
		super();
	}
	
	/**
	 * Constructor
	 * @param vars
	 * @param coefficients
	 */
	public LinearObjectiveImpl(Variable[] vars, double[] coefficients){
		super(vars,coefficients);
	}

	@Override
	public void visit(MathematicalProgramObjectiveVisitor v) {
	  visit((LinearObjectiveVisitor)v);
	}
}
