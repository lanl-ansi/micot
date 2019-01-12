package gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario.cycle.flow;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.cycle.EdgeActiveVariable;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.cycle.flow.VirtualEdgeActiveVariable;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * This forces the edges to be a tree, in the sense that there can only be n-1
 * edges
 * 
 * @author Russell Bent
 */
public class TreeConstraint implements ConstraintFactory {

  private Scenario scenario = null;
  
  /**
   * Constraint
   */
  public TreeConstraint(Scenario scenario) {
    this.scenario = scenario;
  }

  /**
   * Get the constraint name
   * 
   * @return
   */
  private String getConstraintName() {
    return "TreeConstraint_" + scenario;
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    EdgeActiveVariable vaFactory = new EdgeActiveVariable(scenario,null);
    VirtualEdgeActiveVariable viFactory = new VirtualEdgeActiveVariable(scenario,null);

    LinearConstraint constraint = new LinearConstraintEquals(getConstraintName());

    constraint.setRightHandSide(model.getNodes().size()); // there is the equal pseudo node
    for (ElectricPowerNode node : model.getNodes()) {
      constraint.addVariable(viFactory.getVariable(problem, node), 1.0);
    }
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      ElectricPowerNode node1 = model.getFirstNode(edge);
      ElectricPowerNode node2 = model.getSecondNode(edge);

      Variable variable = vaFactory.getVariable(problem, node1, node2);
      if (constraint.getCoefficient(variable) == 0) {
        constraint.addVariable(variable,1.0);
      }
    }
      
    problem.addLinearConstraint(constraint);
  }

}
