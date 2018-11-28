package gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario.cycle.flow;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.cycle.EdgeActiveVariable;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.cycle.flow.FlowVariable;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * This forces the tree edge to have capacity 0 when it is not selected
 * 
 * @author Russell Bent
 */
public class FlowCapacityConstraint implements ConstraintFactory {

  private Scenario scenario = null;

  /**
   * Constraint
   */
  public FlowCapacityConstraint(Scenario scenario) {
    this.scenario = scenario;
  }

  /**
   * Get the lower bound constraint name
   * 
   * @param edge
   * @return
   */
  private String getLowerBoundConstraintName(ElectricPowerFlowConnection edge) {
    return "TreeCapacityLB-" + edge.toString() + "_" + scenario;
  }

  /**
   * Get the upper bound constraint name
   * 
   * @param edge
   * @return
   */
  private String getUpperBoundConstraintName(ElectricPowerFlowConnection edge) {
    return "TreeCapacityUB-" + edge.toString() + "_" + scenario;
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    EdgeActiveVariable vFactory = new EdgeActiveVariable(scenario);
    FlowVariable fFactory = new FlowVariable(scenario);

    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      ElectricPowerNode node1 = model.getFirstNode(edge);
      ElectricPowerNode node2 = model.getSecondNode(edge);
      Variable variable = vFactory.getVariable(problem, node1, node2);

      if (variable != null) {
        Variable flowVariable = fFactory.getVariable(problem, node1, node2);

        String name = getLowerBoundConstraintName(edge);
        if (problem.getLinearConstraint(name) == null) {
          LinearConstraint constraint = new LinearConstraintLessEq(name);
          constraint.addVariable(flowVariable, 1.0);
          constraint.addVariable(variable, -model.getNodes().size());
          constraint.setRightHandSide(0.0);
          problem.addLinearConstraint(constraint);

          constraint = new LinearConstraintGreaterEq(getUpperBoundConstraintName(edge));
          constraint.addVariable(flowVariable, 1.0);
          constraint.addVariable(variable, model.getNodes().size());
          constraint.setRightHandSide(0.0);
          problem.addLinearConstraint(constraint);
        }
      }
    }
  }
}
