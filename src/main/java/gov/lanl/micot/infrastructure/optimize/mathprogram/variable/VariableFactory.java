package gov.lanl.micot.infrastructure.optimize.mathprogram.variable;

import java.util.Collection;

import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.model.Node;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidVariableException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Interface for factories that create variables
 * @author Russell Bent
 */
public interface VariableFactory<N extends Node, M extends Model> {

  /**
   * Function for creating the variables
   * @param program
   * @return
   * @throws VariableExistsException 
   * @throws InvalidVariableException 
   */
  public Collection<Variable> createVariables(MathematicalProgram program,  M model) throws VariableExistsException, InvalidVariableException;
  
  /**
   * Get the variable associated with an asset
   * @param asset
   * @return
   */
  public Variable getVariable(MathematicalProgram program, Object asset) throws NoVariableException;
    
}
