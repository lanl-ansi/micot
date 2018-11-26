package gov.lanl.micot.application.rdt.algorithm.ep.bound;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.GeneratorConstructionVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Bounds on the generator variables
 * 
 * This is constraint 26 in the AAAI 2015 paper
 * 
 * @author Russell Bent
 */
public class GeneratorConstructionBound implements ConstraintFactory {

  double upperBound = 0;

  /**
   * Constraint
   */
  public GeneratorConstructionBound(double upperBound) {    
    this.upperBound = upperBound;
  }
    
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException {
    GeneratorConstructionVariableFactory generatorVariableFactory = new GeneratorConstructionVariableFactory();
   
    for (Generator generator : model.getGenerators()) {
      if (generatorVariableFactory.hasVariable(generator)) {
        Variable u = generatorVariableFactory.getVariable(problem, generator);
        problem.addBounds(u, 0.0, upperBound);        
      }
    }
  }
  
}
