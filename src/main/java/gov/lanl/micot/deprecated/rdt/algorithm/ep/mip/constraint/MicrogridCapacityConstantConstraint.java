package gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.constraint;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.MicrogridCapacityVariableFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.MicrogridVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;


/**
 * Bounds on the microgrid variables that are set to a specific value
 * Typically used for inner problems
 * @author Russell Bent
 */
public class MicrogridCapacityConstantConstraint implements ConstraintFactory {

  /**
   * Constraint
   */
  public MicrogridCapacityConstantConstraint() {    
  }
  
  /**
   * Get the constraint name
   * @param generator
   * @param phase
   * @return
   */
  protected String getConstraintName(Generator generator, String phase) {
    return "GeneratorUB."+ phase + "-" + generator; 
  }
    
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException {
    MicrogridCapacityVariableFactory generatorVariableFactory = new MicrogridCapacityVariableFactory();
    MicrogridVariableFactory variableFactory = new MicrogridVariableFactory();
    
    for (ElectricPowerNode node : model.getNodes()) {
      for (Generator generator : node.getComponents(Generator.class)) {
        Variable discrete = variableFactory.getVariable(problem, generator);
        if (discrete == null) {
          continue;
        }
      
        if (generator.getAttribute(Generator.HAS_PHASE_A_KEY, Boolean.class)) {          
          Variable variable = generatorVariableFactory.getVariable(problem, generator, MicrogridCapacityVariableFactory.PHASE_A);
          double value = generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_A_KEY, Double.class);
          problem.addBounds(variable, value, value);
        }

        if (generator.getAttribute(Generator.HAS_PHASE_B_KEY, Boolean.class)) {          
          Variable variable = generatorVariableFactory.getVariable(problem, generator, MicrogridCapacityVariableFactory.PHASE_B);
          double value = generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_B_KEY, Double.class);
          problem.addBounds(variable, value, value);
        }

        if (generator.getAttribute(Generator.HAS_PHASE_C_KEY, Boolean.class)) {          
          Variable variable = generatorVariableFactory.getVariable(problem, generator, MicrogridCapacityVariableFactory.PHASE_C);
          double value = generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_C_KEY, Double.class);
          problem.addBounds(variable, value, value);
        }
        
      }
    }
  }

  
  
}
