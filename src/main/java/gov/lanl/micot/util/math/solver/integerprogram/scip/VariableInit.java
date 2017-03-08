package gov.lanl.micot.util.math.solver.integerprogram.scip;

import gov.lanl.micot.util.math.solver.ContinuousVariable;
import gov.lanl.micot.util.math.solver.DiscreteVariable;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.VariableVisitor;
import gov.lanl.micot.util.math.solver.scip.Scip;
import gov.lanl.micot.util.math.solver.scip.ScipVariable;

/**
 * Variable initialization class
 * @author Russell Bent
 */
public class VariableInit extends VariableVisitor {
	private Scip	scip;
	private ScipVariable	variable;
	private Number lb = null;
	private Number ub = null;

	/**
	 * Consructor
	 * @param scip
	 */
	public VariableInit(Scip scip) {
		this.scip = scip;
	}

	public ScipVariable initVar(Variable var, Number lb, Number ub) {
		variable = null;
		this.lb = lb;
		this.ub = ub;
		doIt(var);
		return variable;
	}

	@Override
	public void applyContinuousVariable(ContinuousVariable v) {
	  double lb = this.lb == null ? -Double.MAX_VALUE : this.lb.doubleValue();
    double ub = this.ub == null ? Double.MAX_VALUE : this.ub.doubleValue();      
    variable = scip.createContinuousVariable(lb,ub, v.getName());
	}

	@Override
	public void applyDiscreteVariable(DiscreteVariable v) {
    int lb = this.lb == null ? Integer.MIN_VALUE : this.lb.intValue();
    int ub = this.ub == null ? Integer.MAX_VALUE : this.ub.intValue();
    if (lb >= 0 && ub <= 1) {
      variable = scip.createBinaryVariable(lb,ub, v.getName());
    }
    else {
      variable = scip.createDiscreteVariable(lb,ub, v.getName());
    }
	}
}
