package gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.constraint.scenario.tree;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ScenarioConstraintFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.tree.ScenarioVirtualFlowChoiceVariableFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.tree.ScenarioVirtualFlowVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.Collection;

/**
 * The flow into the virtual network that is bounded between 0 and |nodes|
 * 
 * @author Russell Bent
 */
public class ScenarioVirtualFlowBoundConstraint extends ScenarioConstraintFactory<ElectricPowerNode, ElectricPowerModel> {

  /**
   * Constraint
   */
  public ScenarioVirtualFlowBoundConstraint(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  /**
   * Get the lb counstraint name
   * 
   * @param node
   * @param scenario
   * @return
   */
  private String getLBConstraintName(ElectricPowerNode node, Scenario scenario) {
    return "VirtualFlowLB-" + node.toString() + "_" + scenario;
  }

  /**
   * get the ub constraint name
   * 
   * @param node
   * @param scenario
   * @return
   */
  private String getUBConstraintName(ElectricPowerNode node, Scenario scenario) {
    return "VirtualFlowUB-" + node.toString() + "_" + scenario;
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    ScenarioVirtualFlowVariableFactory vFactory = new ScenarioVirtualFlowVariableFactory(getScenarios());
    ScenarioVirtualFlowChoiceVariableFactory f = new ScenarioVirtualFlowChoiceVariableFactory(getScenarios());

    for (Scenario scenario : getScenarios()) {
      for (ElectricPowerNode node : model.getNodes()) {
        Variable variable = vFactory.getVariable(problem, node, scenario);
        LinearConstraint constraint = new LinearConstraintLessEq(getLBConstraintName(node, scenario));
        constraint.setRightHandSide(0.0);
        constraint.addVariable(variable, 1.0);
        constraint.addVariable(f.getVariable(problem, node, scenario), -model.getNodes().size());
        problem.addLinearConstraint(constraint);

        constraint = new LinearConstraintGreaterEq(getUBConstraintName(node, scenario));
        constraint.setRightHandSide(0.0);
        constraint.addVariable(variable, 1.0);
        problem.addLinearConstraint(constraint);
      }
    }
  }

}
