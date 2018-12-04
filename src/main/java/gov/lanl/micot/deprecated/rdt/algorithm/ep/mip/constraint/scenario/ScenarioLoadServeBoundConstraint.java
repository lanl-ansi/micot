package gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.constraint.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ScenarioConstraintFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.ScenarioLoadServeVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.Collection;

/**
 * Load served binary variable
 * 
 * Constraint 14 from the AAAI 2014 paper
 * 
 * @author Russell Bent
 */
public class ScenarioLoadServeBoundConstraint extends ScenarioConstraintFactory<ElectricPowerNode, ElectricPowerModel> {

  /**
   * Constraint
   */
  public ScenarioLoadServeBoundConstraint(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException {
    ScenarioLoadServeVariableFactory loadServeVariableFactory = new ScenarioLoadServeVariableFactory(getScenarios());

    for (Scenario scenario : getScenarios()) {
      for (ElectricPowerNode node : model.getNodes()) {
        Variable variable = loadServeVariableFactory.getVariable(problem, node, scenario);
        if (variable != null) {
          problem.addBounds(variable, 0, 1);
        }
      }    
    }
  }  
  
}
