package gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.MicrogridCapacityVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.MicrogridVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;


/**
 * Bounds on the microgrid variables
 * 
 * This is constraint 24 in the AAAI 2015 paper
 * 
 * @author Russell Bent
 */
public class MicrogridCapacityBoundConstraint implements ConstraintFactory {

  /**
   * Constraint
   */
  public MicrogridCapacityBoundConstraint() {    
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
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    MicrogridCapacityVariableFactory generatorVariableFactory = new MicrogridCapacityVariableFactory();
    MicrogridVariableFactory variableFactory = new MicrogridVariableFactory();
    
    for (ElectricPowerNode node : model.getNodes()) {
      for (Generator generator : node.getComponents(Generator.class)) {
        boolean isNew = generator.getAttribute(AlgorithmConstants.IS_NEW_MICROGRID_KEY) != null && generator.getAttribute(AlgorithmConstants.IS_NEW_MICROGRID_KEY, Boolean.class);                

        
        if (!generator.getStatus() || !isNew) {
          continue;
        }
      
        double upperBound = 10e6;
        if (generator.getAttribute(AlgorithmConstants.MAX_MICROGRID_KEY) != null) {
          upperBound = generator.getAttribute(AlgorithmConstants.MAX_MICROGRID_KEY, Number.class).doubleValue() / model.getMVABase();
        }
        Variable discrete = variableFactory.getVariable(problem, generator);
        
        // add simple bounds and fixed construction upper bound
        if (generator.getAttribute(Generator.HAS_PHASE_A_KEY, Boolean.class)) {
          Variable variable = generatorVariableFactory.getVariable(problem, generator, MicrogridCapacityVariableFactory.PHASE_A);
          problem.addBounds(variable, 0, upperBound);
          
          LinearConstraintLessEq constraint = new LinearConstraintLessEq(getConstraintName(generator, MicrogridCapacityVariableFactory.PHASE_A));
          constraint.addVariable(variable, 1.0);
          constraint.setRightHandSide(discrete, upperBound);
          problem.addLinearConstraint(constraint);
        }

        if (generator.getAttribute(Generator.HAS_PHASE_B_KEY, Boolean.class)) {
          Variable variable = generatorVariableFactory.getVariable(problem, generator, MicrogridCapacityVariableFactory.PHASE_B);
          problem.addBounds(variable, 0, upperBound);
          LinearConstraintLessEq constraint = new LinearConstraintLessEq(getConstraintName(generator, MicrogridCapacityVariableFactory.PHASE_B));
          constraint.addVariable(variable, 1.0);
          constraint.setRightHandSide(discrete, upperBound);
          problem.addLinearConstraint(constraint);

        }
        
        if (generator.getAttribute(Generator.HAS_PHASE_C_KEY, Boolean.class)) {
          Variable variable = generatorVariableFactory.getVariable(problem, generator, MicrogridCapacityVariableFactory.PHASE_C);
          problem.addBounds(variable, 0, upperBound);
          
          LinearConstraintLessEq constraint = new LinearConstraintLessEq(getConstraintName(generator, MicrogridCapacityVariableFactory.PHASE_C));
          constraint.addVariable(variable, 1.0);
          constraint.setRightHandSide(discrete, upperBound);
          problem.addLinearConstraint(constraint);

        }
        
        
      }
    }
  }

  
  
}
