package gov.lanl.micot.util.math.solver.mathprogram;

import gov.lanl.micot.util.math.solver.Variable;

/**
 * Implementation of a minimization function
 * @author Carleton Coffrin
 */
public class LinearObjectiveMinimize extends LinearObjectiveImpl {

	/**
	 * Constructor
	 */
	public LinearObjectiveMinimize() {
		super();
	}
	
	/**
	 * Constructor
	 * @param vars
	 * @param coefficients
	 */
	public LinearObjectiveMinimize(Variable[] vars, double[] coefficients){
		super(vars,coefficients);
	}

	@Override
	public void visit(LinearObjectiveVisitor v) {
		v.applyObjectiveMinimize(this);
	}
	
	 @Override
	 public String toString() {
	   return "min " + super.toString();
	 }

}
