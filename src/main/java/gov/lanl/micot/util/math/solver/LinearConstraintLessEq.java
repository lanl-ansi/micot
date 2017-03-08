package gov.lanl.micot.util.math.solver;

import gov.lanl.micot.util.math.MathUtils;

/**
 * Less than or equal constraint
 * @author Carleton Coffrin
 */
public class LinearConstraintLessEq extends LinearConstraint {

	/**
	 * Constructor
	 * @param name
	 */
	public LinearConstraintLessEq(String name) {
		super(name);
	}
	
	@Override
	protected void visit(LinearConstraintVisitor v) {
		v.applyConstraintLessEq(this);
	}

	protected String getRHSExpr(){
		return 	" <= " + _rhs;
	}

  @Override
  public boolean isSatisfied(Solution solution) {
    double lhs = 0;
    for (Variable variable : getVariables()) {
      lhs += getCoefficient(variable) * solution.getValueDouble(variable);
    }    
    return MathUtils.DOUBLE_LESS_EQUAL_THAN(lhs,_rhs);
  }

	
}
