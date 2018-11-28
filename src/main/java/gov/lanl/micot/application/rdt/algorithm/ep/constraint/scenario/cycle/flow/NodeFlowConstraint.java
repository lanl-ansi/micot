package gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario.cycle.flow;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.cycle.flow.FlowVariable;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.cycle.flow.VirtualFlowVariable;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.Collection;

/**
 * This forces the sum of incoming flow = sum of outgoing flow -1. It basically
 * counts when a node is reachable by flow, and it consumes one unit of flow
 * 
 * @author Russell Bent
 */
public class NodeFlowConstraint implements ConstraintFactory {

  private Scenario scenario = null;
  
  /**
   * Constraint
   */
  public NodeFlowConstraint(Scenario scenario) {
    this.scenario = scenario;
  }

  /**
   * Get the lower bound constraint name
   * 
   * @param edge
   * @return
   */
  private String getConstraintName(ElectricPowerNode node) {
    return "NodeFlow-" + node.toString() + "_" + scenario;
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {

    FlowVariable fFactory = new FlowVariable(scenario);
    VirtualFlowVariable vFactory = new VirtualFlowVariable(scenario);

    for (ElectricPowerNode node : model.getNodes()) {
      LinearConstraint constraint = new LinearConstraintEquals(getConstraintName(node));
      constraint.setRightHandSide(1.0);
      constraint.addVariable(vFactory.getVariable(problem, node), 1.0);

      Collection<ElectricPowerFlowConnection> edges = model.getFlowEdges(node);
      for (ElectricPowerFlowConnection edge : edges) {
        ElectricPowerNode firstNode = model.getFirstNode(edge);
        Variable variable = fFactory.getVariable(problem, firstNode, model.getSecondNode(edge));

        if (constraint.getCoefficient(variable) != 0) {
          continue;
        }

        // outgoing edge
        if (firstNode.equals(node)) {
          constraint.addVariable(variable, -1.0);
        }
        // incoming edge
        else {
          constraint.addVariable(variable, 1.0);
        }
      }
      problem.addLinearConstraint(constraint);
    }
  }

}
