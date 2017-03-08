package gov.lanl.micot.infrastructure.optimize.mathprogram.constraint;

import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.model.Node;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * This is an interface for augmenting OPFDC simulators with additional constraints
 * @author Russell Bent
 *
 */
public interface ConstraintFactory<N extends Node, M extends Model> {

  /**
   * 
   * @param problem
   * @param nodes
   * @param model
   * @param loadModel
   * @throws VariableExistsException 
   * @throws NoVariableException 
   * @throws InvalidConstraintException 
   */
  public void constructConstraint(MathematicalProgram problem, M model) throws VariableExistsException, NoVariableException, InvalidConstraintException;
 
}
