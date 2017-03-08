package gov.lanl.micot.util.math.solver.bonmin;

import com.sun.jna.Pointer;

/**
 * A wrapper for a Bonmin variables
 * @author Russell Bent
 *
 */
public class BonminVariable {

  private Pointer variable;
  
  /**
   * Constructor
   * @param variable
   */
  protected BonminVariable(Pointer variable) {
    this.variable = variable;
  }
  
  /**
   * gets the pointer to the variables
   */
  protected Pointer getPointer() {
    return variable;
  }
}
