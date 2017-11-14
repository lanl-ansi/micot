package gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.assignment.scenario;

import java.util.ArrayList;
import java.util.Collection;

import gov.lanl.micot.infrastructure.model.FlowConnection;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.model.ScenarioAttribute;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.optimize.AssignmentFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.Constants;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.FlowDirectionScenarioVariableFactory;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Assignments for flow direction based on variables
 * @author Russell Bent
 */
public class ComputedDirectionScenarioAssignmentFactory implements AssignmentFactory {
    
private Collection<Scenario> scenarios = new ArrayList<Scenario>();
  
  /**
   * Construtor
   * @param models
   */
  public ComputedDirectionScenarioAssignmentFactory(Collection<Scenario> scenarios) {
    this.scenarios = scenarios;
  }
  
  @Override
  public void performAssignment(NaturalGasModel model, MathematicalProgram problem, Solution solution) throws VariableExistsException, NoVariableException {
    FlowDirectionScenarioVariableFactory pipeFactory = new FlowDirectionScenarioVariableFactory(scenarios);
    
    for (FlowConnection connection : model.getFlowConnections()) {
      connection.setAttribute(Constants.IS_FLOW_FORWARD_KEY, new ScenarioAttribute());
      connection.setAttribute(Constants.IS_FLOW_REVERSE_KEY, new ScenarioAttribute());

    }

    for (Scenario scenario : scenarios) {
      for (FlowConnection connection : model.getFlowConnections()) {
        ScenarioAttribute fattribute = connection.getAttribute(Constants.IS_FLOW_FORWARD_KEY, ScenarioAttribute.class);
        ScenarioAttribute rattribute = connection.getAttribute(Constants.IS_FLOW_REVERSE_KEY, ScenarioAttribute.class);
        
        if (!scenario.computeActualStatus(connection, connection.getStatus())) {          
          fattribute.addEntry(scenario, 0);          
          rattribute.addEntry(scenario, 0);          
          continue;
        }

        Variable fvariable = pipeFactory.getVariable(problem, connection, scenario, FlowDirectionScenarioVariableFactory.FORWARD_PREFIX);
        if (fvariable == null) {
          continue;
        }
        
        int value = solution.getValueInt(fvariable);
        fattribute.addEntry(scenario, value);          
        
        Variable rvariable = pipeFactory.getVariable(problem, connection, scenario, FlowDirectionScenarioVariableFactory.REVERSE_PREFIX);
        value = solution.getValueInt(rvariable);
        rattribute.addEntry(scenario, value);          
      }
      
    }      
  }

 
}
