package gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.cycle;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ScenarioConstraintFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.cycle.ScenarioLineCycleVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.Collection;

/**
 * Creates an auxiliary variable for whether or not a corridor is active or not (for cycle considerations
 * These are the \bar{x}_{ij}^s variables in the paper
 * 
 * Part of constraint 14, implictly, in the AAAI 2015 paper.
 * 
 * @author Russell Bent
 */
public class ScenarioLineCycleBoundConstraint extends ScenarioConstraintFactory<ElectricPowerNode, ElectricPowerModel> {

  /**
   * Constructor
   */
  public ScenarioLineCycleBoundConstraint(Collection<Scenario> scenarios) {
    super(scenarios);
  }


  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException {
    ScenarioLineCycleVariableFactory lineVariableFactory = new ScenarioLineCycleVariableFactory(getScenarios());

    for (Scenario scenario : getScenarios()) {
      for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
        Variable variable = lineVariableFactory.getVariable(problem, model.getFirstNode(edge), model.getSecondNode(edge), scenario);
        if (variable != null) {
          problem.addBounds(variable, 0.0, 1.0);
        }
      }
    }
  }

}
