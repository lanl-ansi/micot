package gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.MicrogridVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;


/**
 * Bounds on the microgrid variables that are set to a specific value.  Typically
 * used for an inner problem of a decomposition algorithm
 * @author Russell Bent
 */
public class MicrogridConstantConstraint implements ConstraintFactory {


  /**
   * Constraint
   */
  public MicrogridConstantConstraint() {    
  }
    
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException {
    MicrogridVariableFactory generatorVariableFactory = new MicrogridVariableFactory();
      
    for (ElectricPowerNode node : model.getNodes()) {
      for (Generator generator : node.getComponents(Generator.class)) {
        Variable variable = generatorVariableFactory.getVariable(problem, generator);
        if (variable != null) {
          int value = generator.getAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY, Boolean.class) ? 1 : 0;          
          problem.addBounds(variable, value, value);        
        }
      }
    }
  }

  
  
}
