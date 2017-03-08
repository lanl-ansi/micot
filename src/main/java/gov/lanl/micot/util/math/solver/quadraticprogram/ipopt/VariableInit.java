package gov.lanl.micot.util.math.solver.quadraticprogram.ipopt;

import gov.lanl.micot.util.math.solver.ContinuousVariable;
import gov.lanl.micot.util.math.solver.DiscreteVariable;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.VariableVisitor;
import gov.lanl.micot.util.math.solver.ipopt.IpOpt;
import gov.lanl.micot.util.math.solver.ipopt.IpOptVariable;

/**
 * Variable initialization class for IPOpt Quadratics
 * @author Russell Bent
 */
public class VariableInit extends VariableVisitor {
  private IpOpt ipopt;
  private IpOptVariable  variable;
  private Number lb = null;
  private Number ub = null;

	public VariableInit(IpOpt ipopt) {
    this.ipopt = ipopt;
  }

	public IpOptVariable initVar(Variable var, Number lb, Number ub) {
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
    variable = ipopt.createContinuousVariable(lb,ub, v.getName());
  }

  @Override
  public void applyDiscreteVariable(DiscreteVariable v) {
    int lb = this.lb == null ? Integer.MIN_VALUE : this.lb.intValue();
    int ub = this.ub == null ? Integer.MAX_VALUE : this.ub.intValue();
    System.err.println("Error: Ipopt does not support binary variables. Relaxing it to a continous variable");
    variable = ipopt.createContinuousVariable(lb,ub, v.getName());
  }
}
