package gov.lanl.micot.util.math.solver;

import gov.lanl.micot.util.math.MathUtils;

/**
 * Greater than of equal constraint
 * @author Carleton Coffrin
 *
 */
public class LinearConstraintGreaterEq extends LinearConstraint {

	/**
	 * Constructor
	 * @param name
	 */
	public LinearConstraintGreaterEq(String name) {
		super(name);
	}
	
	@Override
	protected void visit(LinearConstraintVisitor v) {
		v.applyConstraintGreaterEq(this);
	}

	protected String getRHSExpr(){
		return 	" >= " + _rhs;
	}

  @Override
  public boolean isSatisfied(Solution solution) {
    double lhs = 0;
    for (Variable variable : getVariables()) {
      lhs += getCoefficient(variable) * solution.getValueDouble(variable);
    }    
    return MathUtils.DOUBLE_GREATER_EQUAL_THAN(lhs,_rhs);
  }


}
