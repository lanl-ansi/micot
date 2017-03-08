package gov.lanl.micot.util.math.solver;

import gov.lanl.micot.util.collection.Pair;

/**
 * Interface for a sum of quadratics
 * @author 210117
 *
 */
public interface QuadraticSummation {

  /**
   * Add a coefficient to a variable pair
   * @param var
   * @param coefficient
   */
  public abstract void addVariables(Variable var1, Variable var2, double coefficient);

  /**
   * Add a variable
   * @param var
   */
  public abstract void addVariables(Variable var1, Variable var2);

  /**
   * Increment a variable
   * @param var
   * @param coefficient
   */
  public abstract void sumVariables(Variable var1, Variable var2, double coefficient);

  /**
   * Increment a variable
   * @param var
   */
  public abstract void sumVariables(Variable var1, Variable var2);

  /**
   * Get the variables
   * @return
   */
  public abstract Iterable<Pair<Variable, Variable>> getVariablePairs();

  /**
   * Get number of variables
   * @return
   */
  public abstract int getNumberOfVariablePairs();

  /**
   * Get the coefficient of a variable
   * @param var
   * @return
   */
  public abstract double getCoefficient(Variable var1, Variable var2);

}