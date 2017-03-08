package gov.lanl.micot.util.math.solver.ipopt;

import com.sun.jna.Pointer;

/**
 * A wrapper for a IpOpt variables
 * @author Russell Bent
 *
 */
public class IpOptVariable {

  private Pointer variable;
  
  /**
   * Constructor
   * @param variable
   */
  protected IpOptVariable(Pointer variable) {
    this.variable = variable;
  }
  
  /**
   * gets the pointer to the variables
   */
  protected Pointer getPointer() {
    return variable;
  }
}
