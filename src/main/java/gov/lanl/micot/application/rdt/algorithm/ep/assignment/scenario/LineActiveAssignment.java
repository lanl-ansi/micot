package gov.lanl.micot.application.rdt.algorithm.ep.assignment.scenario;


import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.AssignmentFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.model.ScenarioAttribute;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineActiveVariableFactory;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Assignments for whether or not a line is active
 * 
 * @author Russell Bent
 */
public class LineActiveAssignment implements AssignmentFactory {

  private Scenario scenario = null;
  
  /**
   * Constructor
   * 
   * @param numberOfIncrements
   * @param incrementSize
   */
  public LineActiveAssignment(Scenario scenario) {
    this.scenario = scenario;
  }

  @Override
  public void performAssignment(ElectricPowerModel model, MathematicalProgram problem, Solution solution) throws VariableExistsException, NoVariableException {
    LineActiveVariableFactory variableFactory = new LineActiveVariableFactory(scenario);
    
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (edge.getAttribute(AlgorithmConstants.IS_USED_KEY) == null || !(edge.getAttribute(AlgorithmConstants.IS_USED_KEY) instanceof ScenarioAttribute)) {
        edge.setAttribute(AlgorithmConstants.IS_USED_KEY, new ScenarioAttribute());
      }
      
      Variable z_s = variableFactory.getVariable(problem, edge);
      if (z_s != null) {
        int isUsed = solution.getValueInt(z_s);
        edge.getAttribute(AlgorithmConstants.IS_USED_KEY, ScenarioAttribute.class).addEntry(scenario, isUsed);
      }
      else {
        edge.getAttribute(AlgorithmConstants.IS_USED_KEY, ScenarioAttribute.class).addEntry(scenario, 0);          
      }
    }
  }
  
}
