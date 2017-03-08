package gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.tree;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ScenarioConstraintFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.tree.ScenarioVirtualFlowVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.Collection;

/**
 * The flow into the virtual network that forces the network to operate as a
 * tree must be exactly the number of nodes
 * 
 * @author Russell Bent
 */
public class ScenarioVirtualFlowConstraint extends ScenarioConstraintFactory<ElectricPowerNode, ElectricPowerModel> {

  /**
   * Constraint
   */
  public ScenarioVirtualFlowConstraint(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  /**
   * Get the name of the constraint
   * 
   * @return
   */
  private String getConstraintName(Scenario scenario) {
    return "VirtualFlow_" + scenario;
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    ScenarioVirtualFlowVariableFactory vFactory = new ScenarioVirtualFlowVariableFactory(getScenarios());

    for (Scenario scenario : getScenarios()) {
      LinearConstraint constraint = new LinearConstraintEquals(getConstraintName(scenario));
      constraint.setRightHandSide(model.getNodes().size());

      for (ElectricPowerNode node : model.getNodes()) {
        Variable variable = vFactory.getVariable(problem, node, scenario);
        constraint.addVariable(variable, 1.0);
      }
      problem.addLinearConstraint(constraint);
    }

  }
}
