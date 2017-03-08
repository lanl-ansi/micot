package gov.lanl.micot.util.math.solver.quadraticprogram.bonmin;

import gov.lanl.micot.util.math.solver.ContinuousVariable;
import gov.lanl.micot.util.math.solver.DiscreteVariable;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.VariableVisitor;
import gov.lanl.micot.util.math.solver.bonmin.Bonmin;
import gov.lanl.micot.util.math.solver.bonmin.BonminVariable;

/**
 * Variable initialization class for Bonmin Quadratics
 * @author Russell Bent
 */
public class VariableInit extends VariableVisitor {
  private Bonmin bonmin;
  private BonminVariable  variable;
  private Number lb = null;
  private Number ub = null;

	public VariableInit(Bonmin bonmin) {
    this.bonmin = bonmin;
  }

	public BonminVariable initVar(Variable var, Number lb, Number ub) {
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
    variable = bonmin.createContinuousVariable(lb,ub, v.getName());
  }

  @Override
  public void applyDiscreteVariable(DiscreteVariable v) {
    int lb = this.lb == null ? Integer.MIN_VALUE : this.lb.intValue();
    int ub = this.ub == null ? Integer.MAX_VALUE : this.ub.intValue();
    variable = bonmin.createDiscreteVariable(lb,ub, v.getName());
  }
}
