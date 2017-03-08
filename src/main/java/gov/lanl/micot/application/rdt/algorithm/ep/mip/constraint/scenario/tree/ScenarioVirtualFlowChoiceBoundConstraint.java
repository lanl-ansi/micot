package gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.tree;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ScenarioConstraintFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.tree.ScenarioVirtualFlowChoiceVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.Collection;

/**
 * This forces the virtual edge choice variables to be 0,1
 * 
 * @author Russell Bent
 */
public class ScenarioVirtualFlowChoiceBoundConstraint extends ScenarioConstraintFactory<ElectricPowerNode, ElectricPowerModel> {

  /**
   * Constraint
   */
  public ScenarioVirtualFlowChoiceBoundConstraint(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException {
    ScenarioVirtualFlowChoiceVariableFactory vFactory = new ScenarioVirtualFlowChoiceVariableFactory(getScenarios());

    for (Scenario scenario : getScenarios()) {
      for (ElectricPowerNode node : model.getNodes()) {
        Variable variable = vFactory.getVariable(problem, node, scenario);
        problem.addBounds(variable, 0.0, 1.0);
      }
    }
  }
  
}
