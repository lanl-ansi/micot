package gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.assignment.scenario;

import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.model.ScenarioAttribute;
import gov.lanl.micot.infrastructure.optimize.mathprogram.assignment.ScenarioAssignmentFactory;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.ScenarioLineUseVariableFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.ScenarioVariableFactoryUtility;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Assignments for whether or not a line is used
 * 
 * @author Russell Bent
 */
public class ScenarioLineInUseAssignmentFactory extends ScenarioAssignmentFactory<ElectricPowerNode, ElectricPowerModel> {

  /**
   * Constructor
   * 
   * @param numberOfIncrements
   * @param incrementSize
   */
  public ScenarioLineInUseAssignmentFactory(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  @Override
  public void performAssignment(ElectricPowerModel model, MathematicalProgram problem, Solution solution) throws VariableExistsException, NoVariableException {
    ScenarioLineUseVariableFactory variableFactory = new ScenarioLineUseVariableFactory(getScenarios());

    
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (edge.getAttribute(AlgorithmConstants.IS_USED_KEY) == null || !(edge.getAttribute(AlgorithmConstants.IS_USED_KEY) instanceof ScenarioAttribute)) {
        edge.setAttribute(AlgorithmConstants.IS_USED_KEY, new ScenarioAttribute());
      }
      
      for (Scenario scenario : getScenarios()) {
        Variable variable = variableFactory.getVariable(problem, edge, scenario);
        if (ScenarioVariableFactoryUtility.doCreateLineUseScenarioVariable(edge, scenario)) {
          int isUsed = solution.getValueInt(variable);
          edge.getAttribute(AlgorithmConstants.IS_USED_KEY, ScenarioAttribute.class).addEntry(scenario, isUsed);
        }
        else {
          int isUsed = ScenarioVariableFactoryUtility.getLineUseScenarioConstant(edge, scenario);
          edge.getAttribute(AlgorithmConstants.IS_USED_KEY, ScenarioAttribute.class).addEntry(scenario, isUsed);          
        }
      }
    }
  }
  
}
