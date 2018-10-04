package gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.dual;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ScenarioConstraintFactory;

import java.util.Collection;

import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.dual.YGeneratorVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;


/**
 * Constraints associated with the columns of the generator build variables
 * 
 * @author Russell Bent
 */
public class GeneratorConstraint extends ScenarioConstraintFactory<ElectricPowerNode, ElectricPowerModel> {

  /**
   * Constructor
   */
  public GeneratorConstraint(Collection<Scenario> scenarios) {
    super(scenarios);
  }
    
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    YGeneratorVariableFactory variableFactory = new YGeneratorVariableFactory(getScenarios());
        
    for (Generator generator : model.getGenerators()) {
      LinearConstraint constraint = new LinearConstraintGreaterEq(getName(generator));  
      double cost = -generator.getAttribute(AlgorithmConstants.MICROGRID_FIXED_COST_KEY, Number.class).doubleValue();       
      constraint.setRightHandSide(cost);
          
      for (Scenario scenario : getScenarios()) {
        if (variableFactory.hasVariable(generator)) {
          Variable variable = variableFactory.getVariable(problem, generator, scenario);
          constraint.addVariable(variable, -1);
        }
      }
      problem.addLinearConstraint(constraint);
    }
  }
    
  /**
   * Get the constraint name
   * 
   * @param edge
   * @return
   */
  private String getName(Generator generator) {
    return "GenDual." + generator.toString();
  }
  
}
