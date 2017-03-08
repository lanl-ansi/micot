package gov.lanl.micot.util.math.solver;

/**
 * Basic interface for constraints that go into mathematical programs
 * @author Russell Bent
 *
 */
public interface SummationConstraint extends LinearSummation, Comparable<SummationConstraint> {

  /**
   * Set the right hand side
   * @param rhs
   */
  public abstract void setRightHandSide(double rhs);

  /**
   * Set the right hand side
   * @param var
   * @param coefficient
   */
  public abstract void setRightHandSide(Variable var, double coefficient);

  /**
   * Set the right hand side
   * @param var
   */
  public abstract void setRightHandSide(Variable var);

  /**
   * Get the right hand side
   * @return
   */
  public abstract double getRightHandSide();

  /**
   * Get the name
   * @return
   */
  public abstract String getName();

  /**
   * Determine if the constraint is satisfied by a solution
   * @param solution
   * @return
   */
  public abstract boolean isSatisfied(Solution solution);
  

}