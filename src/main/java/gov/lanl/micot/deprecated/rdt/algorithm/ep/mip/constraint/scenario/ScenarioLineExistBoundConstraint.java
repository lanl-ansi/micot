package gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.constraint.scenario;

import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ScenarioConstraintFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.ScenarioLineExistVariableFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.ScenarioVariableFactoryUtility;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Bounds on the line variables are 0,1
 * 
 * Constraint 14 from AAAI 2015 paper
 * 
 * @author Russell Bent
 */
public class ScenarioLineExistBoundConstraint extends ScenarioConstraintFactory<ElectricPowerNode, ElectricPowerModel> {

  /**
   * Constructor
   * 
   * @param scenarios
   */
  public ScenarioLineExistBoundConstraint(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException {
    ScenarioLineExistVariableFactory lineVariableFactory = new ScenarioLineExistVariableFactory(getScenarios());

    for (Scenario scenario : getScenarios()) {
      for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
        Variable variable = lineVariableFactory.getVariable(problem, edge, scenario);
        if (ScenarioVariableFactoryUtility.doCreateLineExistScenarioVariable(edge, scenario)) {
          problem.addBounds(variable, 0.0, 1.0);
        }
      }
    }
  }

}
