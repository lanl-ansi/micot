package gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.dual;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ScenarioConstraintFactory;

import java.util.Collection;

import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.dual.YGeneratorVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;


/**
 * Bounds on the Y dual variables
 * 
 * @author Russell Bent
 */
public class YGeneratorBoundConstraint extends ScenarioConstraintFactory<ElectricPowerNode, ElectricPowerModel> {

  /**
   * Constructor
   */
  public YGeneratorBoundConstraint(Collection<Scenario> scenarios) {
    super(scenarios);
  }
    
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException {
    YGeneratorVariableFactory generatorVariableFactory = new YGeneratorVariableFactory(getScenarios());
   
    for (Generator generator : model.getGenerators()) {
      for (Scenario scenario : getScenarios()) {
        if (generatorVariableFactory.hasVariable(generator)) {
          Variable variable = generatorVariableFactory.getVariable(problem, generator, scenario);
          problem.addBounds(variable, 0.0, Double.MAX_VALUE);        
        }
      }
    }
  }
  
}
