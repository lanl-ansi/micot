package gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario.cycle.flow;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.cycle.flow.VirtualFlowVariable;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * The flow into the virtual network that forces the network to operate as a
 * tree must be exactly the number of nodes
 * 
 * @author Russell Bent
 */
public class TotalFlowConstraint implements ConstraintFactory {

  private Scenario scenario = null;
  
  /**
   * Constraint
   */
  public TotalFlowConstraint(Scenario scenario) {
    this.scenario = scenario;
  }

  /**
   * Get the name of the constraint
   * 
   * @return
   */
  private String getConstraintName() {
    return "VirtualFlow_" + scenario;
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
   VirtualFlowVariable vFactory = new VirtualFlowVariable(scenario);

   LinearConstraint constraint = new LinearConstraintEquals(getConstraintName());
   constraint.setRightHandSide(model.getNodes().size());

   for (ElectricPowerNode node : model.getNodes()) {
     Variable variable = vFactory.getVariable(problem, node);
     constraint.addVariable(variable, 1.0);
   }
   problem.addLinearConstraint(constraint);
 }

}
