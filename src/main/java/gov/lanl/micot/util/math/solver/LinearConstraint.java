package gov.lanl.micot.util.math.solver;

/**
 * Linear constraint implementation
 * @author Carleton Coffrin
 */
public abstract class LinearConstraint extends LinearSummationImpl implements SummationConstraint {
	
	protected double _rhs;
	protected String _name;
	
	/**
	 * Constructor
	 */
	public LinearConstraint(String name){
		super();
		_rhs = 0;
		_name = name;
	}
	
	/**
	 * Constructor
	 * @param vars
	 * @param coefficients
	 * @param rhs
	 */
	public LinearConstraint(String name, Variable[] vars, double[] coefficients, double rhs){
		super(vars,coefficients);
		if (rhs == -0.0) {
			rhs = 0;
		}
		_rhs = rhs;
		_name = name;
	}
	
	@Override
  public void setRightHandSide(double rhs){
		if (rhs == -0.0) {
			rhs = 0;
		}
		_rhs = rhs;
	}
	
	@Override
  public void setRightHandSide(Variable var, double coefficient){
		sumVariable(var, -coefficient);
		_rhs = 0.0;
	}
	
	@Override
  public void setRightHandSide(Variable var) {
		setRightHandSide(var,1.0);
	}
	
	@Override
  public double getRightHandSide() {
		return _rhs;
	}
	
	/**
	 * Allow the constraint to be visted
	 * @param v
	 */
	protected abstract void visit(LinearConstraintVisitor v);
	
	@Override
	public String toString(){		
		return super.toString() + getRHSExpr();
	}
	
	@Override
  public String getName() {
		return _name;
	}
	
	protected String getRHSExpr(){
		return 	" ? " + _rhs;
	}
	
  @Override
  public int compareTo(SummationConstraint arg0) {
    return this.getName().compareTo(arg0.getName());
  }


	
}
