package gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario.cycle.flow;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.cycle.flow.VirtualEdgeActiveVariable;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.cycle.flow.VirtualFlowVariable;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * The flow into the virtual network that is bounded between 0 and |nodes|
 * 
 * @author Russell Bent
 */
public class VirtualFlowCapacityConstraint implements ConstraintFactory {

  private Scenario scenario = null;
  
  /**
   * Constraint
   */
  public VirtualFlowCapacityConstraint(Scenario scenario) {
    this.scenario = scenario;
  }

  /**
   * Get the lb counstraint name
   * 
   * @param node
   * @param scenario
   * @return
   */
  private String getLBConstraintName(ElectricPowerNode node) {
    return "VirtualFlow-" + node.toString() + "_" + scenario;
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    VirtualFlowVariable vFactory = new VirtualFlowVariable(scenario);
    VirtualEdgeActiveVariable f = new VirtualEdgeActiveVariable(scenario,null);

    for (ElectricPowerNode node : model.getNodes()) {
      Variable variable = vFactory.getVariable(problem, node);
      LinearConstraint constraint = new LinearConstraintLessEq(getLBConstraintName(node));
      constraint.setRightHandSide(0.0);
      constraint.addVariable(variable, 1.0);
      constraint.addVariable(f.getVariable(problem, node), -model.getNodes().size());
      problem.addLinearConstraint(constraint);
    }
  }

}
