package gov.lanl.micot.util.math.solver;

import gov.lanl.micot.util.collection.Pair;
import gov.lanl.micot.util.math.MathUtils;

/**
 * Less than or equal constraint
 * @author Russell Bent
 */
public class QuadraticConstraintLessEq extends QuadraticConstraint {

	/**
	 * Constructor
	 * @param name
	 */
	public QuadraticConstraintLessEq(String name) {
		super(name);
	}
	
	@Override
	protected void visit(QuadraticConstraintVisitor v) {
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
    for (Pair<Variable,Variable> vars : getVariablePairs()) {
      lhs += getCoefficient(vars.getOne(), vars.getTwo()) * solution.getValueDouble(vars.getOne()) * solution.getValueDouble(vars.getTwo());
    }
    return MathUtils.DOUBLE_LESS_EQUAL_THAN(lhs,_rhs);
  }

	
}
