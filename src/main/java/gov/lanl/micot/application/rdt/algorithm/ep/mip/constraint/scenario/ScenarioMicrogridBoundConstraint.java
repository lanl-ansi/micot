package gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario;

import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ScenarioConstraintFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioMicrogridVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Bounds on the microgrid variables
 * 
 * Constraint 14 from the AAAI 2015 paper
 * 
 * @author Russell Bent
 */
public class ScenarioMicrogridBoundConstraint extends ScenarioConstraintFactory<ElectricPowerNode, ElectricPowerModel> {


  /**
   * Constraint
   */
  public ScenarioMicrogridBoundConstraint(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException {
    ScenarioMicrogridVariableFactory generatorVariableFactory = new ScenarioMicrogridVariableFactory(getScenarios());

    for (Scenario scenario : getScenarios()) {
      for (ElectricPowerNode node : model.getNodes()) {
        for (Generator generator : node.getComponents(Generator.class)) {
          Variable variable = generatorVariableFactory.getVariable(problem, generator, scenario);
          if (variable != null) {
            problem.addBounds(variable, 0.0, 1.0);
          }
        }
      }
    }
  }

}
