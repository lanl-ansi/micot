package gov.lanl.micot.application.rdt.algorithm.ep.mip.assignment.scenario;

import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.model.ScenarioAttribute;
import gov.lanl.micot.infrastructure.optimize.mathprogram.assignment.ScenarioAssignmentFactory;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioSwitchVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioVariableFactoryUtility;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Assignments the status of a switch on a per scenario basis
 * 
 * @author Russell Bent
 */
public class ScenarioLineSwitchStatusAssignmentFactory extends ScenarioAssignmentFactory<ElectricPowerNode, ElectricPowerModel> {

  /**
   * Constructor
   * 
   * @param numberOfIncrements
   * @param incrementSize
   */
  public ScenarioLineSwitchStatusAssignmentFactory(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  @Override
  public void performAssignment(ElectricPowerModel model, MathematicalProgram problem, Solution solution) throws VariableExistsException, NoVariableException {
    ScenarioSwitchVariableFactory switchVariableFactory = new ScenarioSwitchVariableFactory(getScenarios());

    
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (edge.getAttribute(AlgorithmConstants.IS_SWITCH_OPEN_KEY) == null || !(edge.getAttribute(AlgorithmConstants.IS_SWITCH_OPEN_KEY) instanceof ScenarioAttribute)) {
        edge.setAttribute(AlgorithmConstants.IS_SWITCH_OPEN_KEY, new ScenarioAttribute());
      }
      
      for (Scenario scenario : getScenarios()) {
        Variable variable = switchVariableFactory.getVariable(problem, edge, scenario);
        if (ScenarioVariableFactoryUtility.doCreateSwitchScenarioVariable(edge, scenario)) {
          int isOpen = solution.getValueInt(variable) == 1 ? 0 : 1;
          edge.getAttribute(AlgorithmConstants.IS_SWITCH_OPEN_KEY, ScenarioAttribute.class).addEntry(scenario, isOpen);
        }
        else {
          int isOpen = ScenarioVariableFactoryUtility.getSwitchScenarioConstant(edge, scenario) == 1 ? 0 : 1;
          edge.getAttribute(AlgorithmConstants.IS_SWITCH_OPEN_KEY, ScenarioAttribute.class).addEntry(scenario, isOpen);          
        }
      }
    }
  }
  
}
