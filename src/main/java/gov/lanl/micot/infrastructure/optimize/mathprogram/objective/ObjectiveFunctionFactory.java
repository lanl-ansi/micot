package gov.lanl.micot.infrastructure.optimize.mathprogram.objective;

import java.util.Collection;

import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.model.Node;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Interface for factories that create objective functions
 * @author Russell Bent
 */
public interface ObjectiveFunctionFactory<N extends Node, M extends Model> {
  
  /**
   * Get the variable associated with an asset
   * @param asset
   * @return
   */
  public void addCoefficients(MathematicalProgram program,  /*Collection<N> nodes,*/ M model) throws NoVariableException;

}
