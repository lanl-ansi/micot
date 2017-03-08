package gov.lanl.micot.infrastructure.optimize.mathprogram.initialsolution;


import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.exception.NoConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Interface for factories that creating initial solution factories
 * @author Russell Bent
 */
public interface InitialSolutionFactory<M extends Model> {

  /**
   * Function for performing assignments
   * @param program
   * @return
   * @throws VariableExistsException 
   * @throws NoVariableException 
   * @throws NoConstraintException 
   */
  public void updateInitialSolution(M model, MathematicalProgram problem) throws VariableExistsException, NoVariableException, NoConstraintException;
  
}
