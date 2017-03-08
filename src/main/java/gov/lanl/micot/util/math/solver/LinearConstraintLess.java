package gov.lanl.micot.util.math.solver;

/**
 * Less constraint
 * @author Carleton Coffrin
 */
public class LinearConstraintLess extends LinearConstraint {

	/**
	 * Constructor
	 * @param name
	 */
	public LinearConstraintLess(String name) {
		super(name);
	}
	
	@Override
	protected void visit(LinearConstraintVisitor v) {
		v.applyConstraintLess(this);
	}

	protected String getRHSExpr(){
		return 	" < " + _rhs;
  }

  @Override
  public boolean isSatisfied(Solution solution) {
    double lhs = 0;
    for (Variable variable : getVariables()) {
      lhs += getCoefficient(variable) * solution.getValueDouble(variable);
    }    
    return lhs < _rhs;
  }


}
