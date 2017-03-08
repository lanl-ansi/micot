package gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.MicrogridVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;


/**
 * Bounds on the microgrid variables
 * 
 * This is constraint 26 in the AAAI 2015 paper
 * 
 * @author Russell Bent
 */
public class MicrogridBoundConstraint implements ConstraintFactory {


  /**
   * Constraint
   */
  public MicrogridBoundConstraint() {    
  }
    
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException {
    MicrogridVariableFactory generatorVariableFactory = new MicrogridVariableFactory();
      
    for (ElectricPowerNode node : model.getNodes()) {
      for (Generator generator : node.getComponents(Generator.class)) {
        Variable variable = generatorVariableFactory.getVariable(problem, generator);
        if (variable != null) {
          problem.addBounds(variable, 0.0, 1.0);        
        }
      }
    }
  }

  
  
}
