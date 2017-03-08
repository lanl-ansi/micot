package gov.lanl.micot.util.math.solver.scip;

import com.sun.jna.Pointer;

/**
 * A wrapper for a SCIP constraint
 * @author Russell Bent
 *
 */
public class ScipConstraint {

  private Pointer constraint;
  
  /**
   * Constructor
   * @param variable
   */
  protected ScipConstraint(Pointer constraint) {
    this.constraint = constraint;
  }
  
}
