package gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.constraint.scenario.tree;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ScenarioConstraintFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.tree.ScenarioTreeFlowChoiceVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.Collection;

/**
 * This forces the tree edge choice variables to be 0,1
 * 
 * @author Russell Bent
 */
public class ScenarioTreeFlowChoiceBoundConstraint extends ScenarioConstraintFactory<ElectricPowerNode, ElectricPowerModel> {

  /**
   * Constraint
   */
  public ScenarioTreeFlowChoiceBoundConstraint(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException {
    ScenarioTreeFlowChoiceVariableFactory vFactory = new ScenarioTreeFlowChoiceVariableFactory(getScenarios());

    for (Scenario scenario : getScenarios()) {
      for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
        ElectricPowerNode node1 = model.getFirstNode(edge);
        ElectricPowerNode node2 = model.getSecondNode(edge);
        Variable variable = vFactory.getVariable(problem, node1, node2, scenario);
        problem.addBounds(variable, 0.0, 1.0);
      }
    }
  }
}
