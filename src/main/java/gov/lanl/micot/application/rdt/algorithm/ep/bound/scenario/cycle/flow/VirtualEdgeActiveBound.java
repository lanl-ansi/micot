package gov.lanl.micot.application.rdt.algorithm.ep.bound.scenario.cycle.flow;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.cycle.flow.VirtualEdgeActiveVariable;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * This forces the virtual edge choice variables to be 0,1
 * 
 * @author Russell Bent
 */
public class VirtualEdgeActiveBound implements ConstraintFactory {

  private Scenario scenario = null;
  
  /**
   * Constraint
   */
  public VirtualEdgeActiveBound(Scenario scenario) {
    this.scenario = scenario;
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException {
    VirtualEdgeActiveVariable vFactory = new VirtualEdgeActiveVariable(scenario);
    for (ElectricPowerNode node : model.getNodes()) {
      Variable variable = vFactory.getVariable(problem, node);
      problem.addBounds(variable, 0.0, 1.0);
    }
  }
  
}
