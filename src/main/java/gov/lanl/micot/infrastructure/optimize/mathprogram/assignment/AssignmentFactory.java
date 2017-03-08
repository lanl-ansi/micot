package gov.lanl.micot.infrastructure.optimize.mathprogram.assignment;

import java.util.Collection;

import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.model.Node;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.exception.NoConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Interface for factories that create assignment factories
 * @author Russell Bent
 */
public interface AssignmentFactory<N extends Node, M extends Model> {

  /**
   * Function for performing assignments
   * @param program
   * @return
   * @throws VariableExistsException 
   * @throws NoVariableException 
   * @throws NoConstraintException 
   */
  public void performAssignment(M model, MathematicalProgram problem, Solution solution/*, Collection<N> nodes*/) throws VariableExistsException, NoVariableException, NoConstraintException;
  
}
