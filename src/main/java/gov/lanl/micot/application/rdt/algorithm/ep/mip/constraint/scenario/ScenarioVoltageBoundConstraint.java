package gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ScenarioConstraintFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioVoltageVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.Collection;

/**
 * Voltage binary variables (modeled as the square of voltage)
 * 
 * @author Russell Bent
 */
public class ScenarioVoltageBoundConstraint extends ScenarioConstraintFactory<ElectricPowerNode, ElectricPowerModel> {

  /**
   * Constraint
   */
  public ScenarioVoltageBoundConstraint(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException {
    ScenarioVoltageVariableFactory variableFactory = new ScenarioVoltageVariableFactory(getScenarios());

    for (Scenario scenario : getScenarios()) {
      for (ElectricPowerNode node : model.getNodes()) {
        Bus bus = node.getBus();
        
        Variable variableA = variableFactory.getVariable(problem, node, ScenarioVoltageVariableFactory.PHASE_A, scenario);
        Variable variableB = variableFactory.getVariable(problem, node, ScenarioVoltageVariableFactory.PHASE_B, scenario);
        Variable variableC = variableFactory.getVariable(problem, node, ScenarioVoltageVariableFactory.PHASE_C, scenario);

        if (variableA != null) {
          problem.addBounds(variableA, bus.getMinimumVoltagePU() * bus.getMinimumVoltagePU(), bus.getMaximumVoltagePU() * bus.getMaximumVoltagePU());
        }
        if (variableB != null) {
          problem.addBounds(variableB, bus.getMinimumVoltagePU() * bus.getMinimumVoltagePU(), bus.getMaximumVoltagePU() * bus.getMaximumVoltagePU());
        }
        if (variableC != null) {
          problem.addBounds(variableC, bus.getMinimumVoltagePU() * bus.getMinimumVoltagePU(), bus.getMaximumVoltagePU() * bus.getMaximumVoltagePU());
        }
      }    
    }
  }  
  
}
