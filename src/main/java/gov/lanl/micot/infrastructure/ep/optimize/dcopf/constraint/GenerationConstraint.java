package gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint;

import gov.lanl.micot.infrastructure.ep.model.Battery;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerProducer;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.GeneratorVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * This constraint adds constraints to the generation
 * @author Russell Bent
 */
public class GenerationConstraint implements ConstraintFactory {

  /**
   * Constraint
   */
  public GenerationConstraint() {    
  }
  
  /**
   * Function for create the variable name associated with a load
   * 
   * @param node
   * @return
   */
  private String getGenerationUpperBoundConstraintName(ElectricPowerProducer node) {
    return "GU" + node.toString();
  }

  /**
   * Function for create the variable name associated with a load
   * 
   * @param node
   * @return
   */
  private String getGenerationLowerBoundConstraintName(ElectricPowerProducer node) {
    return "GL" + node.toString();
  }
  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    double mva = model.getMVABase();    
    GeneratorVariableFactory generatorVariableFactory = new GeneratorVariableFactory();
    
    for (ElectricPowerNode node : model.getNodes()) {
      for (ElectricPowerProducer producer : node.getComponents(ElectricPowerProducer.class)) {
        double totalMaxGeneration = 0;
        double totalMinGeneration = 0;

        if (producer.getStatus()) {
          if (producer instanceof Generator) {
            totalMaxGeneration = ((Generator)producer).computeActualRealGenerationMax().doubleValue(); // steady state model
            totalMinGeneration = ((Generator)producer).getRealGenerationMin();
          }
          else {
            totalMaxGeneration = ((Battery)producer).getAvailableMaximumRealProduction().doubleValue();
            totalMinGeneration = ((Battery)producer).getAvailableMinimumRealProduction().doubleValue();
          }
        }
        totalMaxGeneration = totalMaxGeneration / mva;
        totalMinGeneration = totalMinGeneration / mva;
      
        if (totalMaxGeneration == totalMinGeneration) {
          LinearConstraint constraint = new LinearConstraintEquals(getGenerationUpperBoundConstraintName(producer));
          constraint.setRightHandSide(totalMaxGeneration);
          constraint.addVariable(generatorVariableFactory.getVariable(problem, producer), 1.0);
          problem.addLinearConstraint(constraint);
        }
        else {
          LinearConstraint constraint = new LinearConstraintLessEq(getGenerationUpperBoundConstraintName(producer));
          constraint.setRightHandSide(totalMaxGeneration);
          constraint.addVariable(generatorVariableFactory.getVariable(problem, producer), 1.0);
          problem.addLinearConstraint(constraint);

          constraint = new LinearConstraintGreaterEq(getGenerationLowerBoundConstraintName(producer));
          constraint.setRightHandSide(totalMinGeneration);
          constraint.addVariable(generatorVariableFactory.getVariable(problem, producer), 1.0);
          problem.addLinearConstraint(constraint);
        }        
      }           
    }    
  }

  /**
   * Get the upper bound constraint
   * @param problem
   * @param producer
   * @return
   */
  public LinearConstraint getUpperBoundConstraint(MathematicalProgram problem, ElectricPowerProducer producer) {
    return problem.getLinearConstraint(getGenerationUpperBoundConstraintName(producer));
  }
  
}
