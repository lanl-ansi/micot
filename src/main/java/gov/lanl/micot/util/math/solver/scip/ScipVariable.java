package gov.lanl.micot.util.math.solver.scip;

import com.sun.jna.Pointer;

/**
 * A wrapper for a SCIP variables
 * @author Russell Bent
 *
 */
public class ScipVariable {

  private Pointer variable;
  
  /**
   * Constructor
   * @param variable
   */
  protected ScipVariable(Pointer variable) {
    this.variable = variable;
  }
  
  /**
   * gets the pointer to the variables
   */
  protected Pointer getPointer() {
    return variable;
  }
}
