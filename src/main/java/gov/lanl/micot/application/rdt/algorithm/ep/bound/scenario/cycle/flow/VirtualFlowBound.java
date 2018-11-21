package gov.lanl.micot.application.rdt.algorithm.ep.bound.scenario.cycle.flow;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.cycle.flow.VirtualFlowVariable;
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
public class VirtualFlowBound implements ConstraintFactory {

  private Scenario scenario = null;
  
  /**
   * Constraint
   */
  public VirtualFlowBound(Scenario scenario) {
    this.scenario = scenario;
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    VirtualFlowVariable vFactory = new VirtualFlowVariable(scenario);

    for (ElectricPowerNode node : model.getNodes()) {
      Variable variable = vFactory.getVariable(problem, node);
      problem.addBounds(variable, 0.0, model.getNodes().size());      
    }
  }

}
