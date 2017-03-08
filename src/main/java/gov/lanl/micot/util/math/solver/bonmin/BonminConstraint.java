package gov.lanl.micot.util.math.solver.bonmin;

import com.sun.jna.Pointer;

/**
 * A wrapper for a Bonmin constraint
 * @author Russell Bent
 *
 */
public class BonminConstraint {

  private Pointer constraint;
  
  /**
   * Constructor
   * @param variable
   */
  protected BonminConstraint(Pointer constraint) {
    this.constraint = constraint;
  }
  
}
