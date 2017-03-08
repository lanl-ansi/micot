package gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.tree;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ScenarioConstraintFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.tree.ScenarioTreeFlowChoiceVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.tree.ScenarioVirtualFlowChoiceVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.Collection;

/**
 * This forces the edges to be a tree, in the sense that there can only be n-1
 * edges
 * 
 * @author Russell Bent
 */
public class ScenarioTreeConstraint extends ScenarioConstraintFactory<ElectricPowerNode, ElectricPowerModel> {

  /**
   * Constraint
   */
  public ScenarioTreeConstraint(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  /**
   * Get the constraint name
   * 
   * @return
   */
  private String getConstraintName(Scenario scenario) {
    return "TreeConstraint_" + scenario;
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    ScenarioTreeFlowChoiceVariableFactory vaFactory = new ScenarioTreeFlowChoiceVariableFactory(getScenarios());
    ScenarioVirtualFlowChoiceVariableFactory viFactory = new ScenarioVirtualFlowChoiceVariableFactory(getScenarios());

    for (Scenario scenario : getScenarios()) {
      LinearConstraint constraint = new LinearConstraintEquals(getConstraintName(scenario));

      // new version of the constraint
      constraint.setRightHandSide(model.getNodes().size()); // there is the equal pseudo node
      for (ElectricPowerNode node : model.getNodes()) {
        constraint.addVariable(viFactory.getVariable(problem, node, scenario), 1.0);
      }
      for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
        ElectricPowerNode node1 = model.getFirstNode(edge);
        ElectricPowerNode node2 = model.getSecondNode(edge);

        Variable variable = vaFactory.getVariable(problem, node1, node2, scenario);
        if (constraint.getCoefficient(variable) == 0) {
          constraint.addVariable(variable,1.0);
        }
      }
      
      problem.addLinearConstraint(constraint);
    }
  }

}
