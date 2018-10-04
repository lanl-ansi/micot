package gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.dual;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ScenarioConstraintFactory;

import java.util.Collection;

import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.dual.YLineConstructionVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;


/**
 * Bounds on the line construction variables are 0,1
 * @author Russell Bent
 */
public class YLineConstructionBoundConstraint extends ScenarioConstraintFactory<ElectricPowerNode, ElectricPowerModel> {

  /**
   * Constructor
   */
  public YLineConstructionBoundConstraint(Collection<Scenario> scenarios) {
    super(scenarios);
  }
    
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException {
    YLineConstructionVariableFactory variableFactory = new YLineConstructionVariableFactory(getScenarios());
        
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      for (Scenario scenario : getScenarios()) {
        if (variableFactory.hasVariable(edge)) {
          Variable variable = variableFactory.getVariable(problem, edge, scenario);
          problem.addBounds(variable, 0.0, Double.MAX_VALUE);
        }
      }
    }
  }
}
