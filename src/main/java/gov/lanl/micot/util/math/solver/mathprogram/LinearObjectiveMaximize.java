package gov.lanl.micot.util.math.solver.mathprogram;

import gov.lanl.micot.util.math.solver.Variable;

/**
 * A maximization objective function
 * @author Carleton Coffrin
 */
public class LinearObjectiveMaximize extends LinearObjectiveImpl {

	/**
	 * Constructor
	 */
	public LinearObjectiveMaximize() {
		super();
	}
	
	/**
	 * Constructor
	 * @param vars
	 * @param coefficients
	 */
	public LinearObjectiveMaximize(Variable[] vars, double[] coefficients){
		super(vars,coefficients);
	}

	@Override
	public void visit(LinearObjectiveVisitor v) {
		v.applyObjectiveMaximize(this);
	}
	
	@Override
	public String toString() {
	  return "max " + super.toString();
	}
}
