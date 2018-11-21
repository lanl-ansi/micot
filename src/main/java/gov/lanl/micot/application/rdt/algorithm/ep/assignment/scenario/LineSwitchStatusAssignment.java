package gov.lanl.micot.application.rdt.algorithm.ep.assignment.scenario;


import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.AssignmentFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.model.ScenarioAttribute;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineSwitchVariableFactory;
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
public class LineSwitchStatusAssignment implements AssignmentFactory {

  private Scenario scenario = null;
  
  /**
   * Constructor
   * 
   */
  public LineSwitchStatusAssignment(Scenario scenario) {
    this.scenario = scenario;
  }

  @Override
  public void performAssignment(ElectricPowerModel model, MathematicalProgram problem, Solution solution) throws VariableExistsException, NoVariableException {
    LineSwitchVariableFactory switchVariableFactory = new LineSwitchVariableFactory(scenario);
    
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (edge.getAttribute(AlgorithmConstants.IS_SWITCH_OPEN_KEY) == null || !(edge.getAttribute(AlgorithmConstants.IS_SWITCH_OPEN_KEY) instanceof ScenarioAttribute)) {
        edge.setAttribute(AlgorithmConstants.IS_SWITCH_OPEN_KEY, new ScenarioAttribute());
      }
      
      Variable variable = switchVariableFactory.getVariable(problem, edge);
      if (variable != null) {
        int isOpen = solution.getValueInt(variable) == 1 ? 0 : 1;
        edge.getAttribute(AlgorithmConstants.IS_SWITCH_OPEN_KEY, ScenarioAttribute.class).addEntry(scenario, isOpen);
      }
      else {
        edge.getAttribute(AlgorithmConstants.IS_SWITCH_OPEN_KEY, ScenarioAttribute.class).addEntry(scenario, -1);          
      }
    }
  }
  
}
