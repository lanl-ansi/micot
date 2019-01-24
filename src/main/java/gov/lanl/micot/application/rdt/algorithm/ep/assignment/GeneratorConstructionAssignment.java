package gov.lanl.micot.application.rdt.algorithm.ep.assignment;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.optimize.AssignmentFactory;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.GeneratorConstructionVariableFactory;
import gov.lanl.micot.util.math.solver.ContinuousVariable;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Assignments of microgrid construction
 * @author Russell Bent
 */
public class GeneratorConstructionAssignment implements AssignmentFactory {
    
  /**
   * Constructor
   */
  public GeneratorConstructionAssignment() {
    super();
  }
  
  @Override
  public void performAssignment(ElectricPowerModel model, MathematicalProgram problem, Solution solution) throws VariableExistsException, NoVariableException {
    GeneratorConstructionVariableFactory variableFactory = new GeneratorConstructionVariableFactory();
    
    for (Generator generator : model.getGenerators()) {
      Variable u = variableFactory.getVariable(problem, generator);
      if (u != null) {
        boolean built = false;
        if (u instanceof ContinuousVariable) {
          built = solution.getValueDouble(u) >= 1e-4 ? true : false;          
        }
        else {
          built = solution.getValueInt(u) == 1 ? true : false;
        }
        
        generator.setAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY, built);
      }      
    }
  }
}
