package gov.lanl.micot.util.math.solver.ipopt;

import com.sun.jna.Pointer;

/**
 * A wrapper for an IpOpt constraint
 * @author Russell Bent
 *
 */
public class IpOptConstraint {

  private Pointer constraint;
  
  /**
   * Constructor
   * @param variable
   */
  protected IpOptConstraint(Pointer constraint) {
    this.constraint = constraint;
  }
  
}
