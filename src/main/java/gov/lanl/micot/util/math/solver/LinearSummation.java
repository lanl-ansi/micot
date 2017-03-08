package gov.lanl.micot.util.math.solver;

/**
 * Interface for the Linear Summation
 * @author 210117
 *
 */
public interface LinearSummation {

  /**
   * Add a variable
   * @param var
   * @param coefficient
   */
  public abstract void addVariable(Variable var, double coefficient);

  /**
   * Add a variable
   * @param var
   */
  public abstract void addVariable(Variable var);

  /**
   * Increment a variable
   * @param var
   * @param coefficient
   */
  public abstract void sumVariable(Variable var, double coefficient);

  /**
   * Increment a variable
   * @param var
   */
  public abstract void sumVariable(Variable var);

  /**
   * Get the variables
   * @return
   */
  public abstract Iterable<Variable> getVariables();

  /**
   * Get number of variables
   * @return
   */
  public abstract int getNumberOfVariables();

  /**
   * Get the coefficient of a variable
   * @param var
   * @return
   */
  public abstract double getCoefficient(Variable var);

}