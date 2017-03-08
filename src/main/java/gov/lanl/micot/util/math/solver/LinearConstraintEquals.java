package gov.lanl.micot.util.math.solver;

import gov.lanl.micot.util.math.MathUtils;

/**
 * Implementation of a constraint for equality
 * @author Carleton Coffrin
 */
public class LinearConstraintEquals extends LinearConstraint {

	/**
	 * Constructor
	 * @param name
	 */
	public LinearConstraintEquals(String name) {
		super(name);
	}
	
	@Override
	protected void visit(LinearConstraintVisitor v) {
		v.applyConstraintEquals(this);
	}
	
	protected String getRHSExpr(){
		return 	" = " + _rhs;
	}

  @Override
  public boolean isSatisfied(Solution solution) {
    double lhs = 0;
    for (Variable variable : getVariables()) {
      lhs += getCoefficient(variable) * solution.getValueDouble(variable);
    }    
    return MathUtils.DOUBLE_EQUAL(lhs,_rhs);
  } 
  
	
}
