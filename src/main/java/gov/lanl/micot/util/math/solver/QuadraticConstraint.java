package gov.lanl.micot.util.math.solver;

import gov.lanl.micot.util.collection.Pair;

/**
 * Quadratic constraint implementation
 * @author Russell Bent
 */
public abstract class QuadraticConstraint extends QuadraticSummationImpl implements SummationConstraint, LinearSummation {
	
	protected double _rhs;
	protected String _name;
	
	private LinearSummation linearTerms = null;
	
	/**
	 * Constructor
	 */
	public QuadraticConstraint(String name){
		super();
		_rhs = 0;
		_name = name;
		linearTerms = new LinearSummationImpl();
	}
	
	/**
	 * Constructor
	 * @param vars
	 * @param coefficients
	 * @param rhs
	 */
	public QuadraticConstraint(String name, Pair<Variable,Variable>[] vars, double[] coefficients, double rhs){
		super(vars,coefficients);
		if (rhs == -0.0) {
			rhs = 0;
		}
		_rhs = rhs;
		_name = name;
		linearTerms = new LinearSummationImpl();
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
	 * Allow the constraint to be vistted
	 * @param v
	 */
	protected abstract void visit(QuadraticConstraintVisitor v);
	
	@Override
	public String toString(){	
	  String str = super.toString();
	  if (str.length() > 0 && linearTerms.toString().length() > 0) {
	    str += " + ";
	  }
	  str += linearTerms.toString() + getRHSExpr();
	  return str;
	}
	
	@Override
  public String getName() {
		return _name;
	}
	
	protected String getRHSExpr(){
		return 	" ? " + _rhs;
	}
	
  @Override
  public void addVariable(Variable var, double coefficient) {
    linearTerms.addVariable(var, coefficient);
  }

  @Override
  public void addVariable(Variable var) {
    linearTerms.addVariable(var);
  }

  @Override
  public void sumVariable(Variable var, double coefficient) {
    linearTerms.sumVariable(var, coefficient);
  }

  @Override
  public void sumVariable(Variable var) {
    linearTerms.sumVariable(var);
  }

  @Override
  public Iterable<Variable> getVariables() {
    return linearTerms.getVariables();
  }

  @Override
  public int getNumberOfVariables() {
    return linearTerms.getNumberOfVariables();
  }

  @Override
  public double getCoefficient(Variable var) {
    return linearTerms.getCoefficient(var);
  }
	
  @Override
  public int compareTo(SummationConstraint arg0) {
    return this.getName().compareTo(arg0.getName());
  }

}
