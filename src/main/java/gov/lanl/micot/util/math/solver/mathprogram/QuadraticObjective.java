package gov.lanl.micot.util.math.solver.mathprogram;

import gov.lanl.micot.util.collection.Pair;
import gov.lanl.micot.util.math.solver.QuadraticSummationImpl;
import gov.lanl.micot.util.math.solver.Variable;

/**
 * A quadratic objective function.  It includes linear terms as wel
 * @author Russell Bent
 */
public abstract class QuadraticObjective extends QuadraticSummationImpl implements LinearObjective, MathematicalProgramObjective  {

  private LinearObjective linearObjective = null;
  
	/**
	 * Constructor
	 */
	protected QuadraticObjective(LinearObjective linearObjective) {
		super();
		this.linearObjective = linearObjective;
	}
	
	/**
	 * Constructor
	 * @param vars
	 * @param coefficients
	 */
	protected QuadraticObjective(Pair<Variable,Variable>[] vars, double[] coefficients, LinearObjective linearObjective){
		super(vars,coefficients);
    this.linearObjective = linearObjective;
	}

	/**
	 * Visit routine
	 * @param v
	 */
	protected abstract void visit(QuadraticObjectiveVisitor v);
		
	/**
   * Add a variable
   * @param var
   * @param coefficient
   */
  public void addVariable(Variable var, double coefficient) {
    linearObjective.addVariable(var, coefficient);
  }
  
  /**
   * Add a variable
   * @param var
   */
  public void addVariable(Variable var) { 
    addVariable(var,1.0);
  }
  
  /**
   * Increment a variable
   * @param var
   * @param coefficient
   */
  public void sumVariable(Variable var, double coefficient){
    linearObjective.addVariable(var, coefficient);
  }
  
  /**
   * Increment a variable
   * @param var
   */
  public void sumVariable(Variable var) { 
    sumVariable(var,1.0);
  }
  
  /**
   * Get the variables
   * @return
   */
  public Iterable<Variable> getVariables() {
    return linearObjective.getVariables();
  }
  
  /**
   * Get number of variables
   * @return
   */
  public int getNumberOfVariables() {
    return linearObjective.getNumberOfVariables();
  }
  
  /**
   * Get the coefficient of a variable
   * @param var
   * @return
   */
  public double getCoefficient(Variable var) {
    return linearObjective.getCoefficient(var);
  }
  
  @Override
  public void visit(MathematicalProgramObjectiveVisitor v) {
    visit((QuadraticObjectiveVisitor)v);
  }
  
  /**
   * get the linear objective
   * @return
   */
  public void setLinearObjective(LinearObjective linearObjective) {
    this.linearObjective = linearObjective;
  }
  
  @Override
  public String toString() {
    String quadratic = super.toString();
    
    StringBuffer buff = new StringBuffer();
    boolean first = true;
    for(Variable v : this.getVariables()){
      if(first){first = false;}
      else {buff.append(" + ");}      
      buff.append(getCoefficient(v)).append("*").append(v.getName());      
    }
    
    return quadratic + " + " + buff.toString();
  }
}
