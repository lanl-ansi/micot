package gov.lanl.micot.util.math.solver;

/**
 * Greater than constraint
 * @author Carleton Coffrin
 */
public class LinearConstraintGreater extends LinearConstraint {

	/**
	 * Constructor
	 * @param name
	 */
	protected LinearConstraintGreater(String name) {
		super(name);
	}
	
	@Override
	protected void visit(LinearConstraintVisitor v) {
		v.applyConstraintGreater(this);
	}


	protected String getRHSExpr(){
		return 	" > " + _rhs;
	}

  @Override
  public boolean isSatisfied(Solution solution) {
    double lhs = 0;
    for (Variable variable : getVariables()) {
      lhs += getCoefficient(variable) * solution.getValueDouble(variable);
    }    
    return lhs > _rhs;
  }


}
