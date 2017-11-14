package gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.assignment.scenario;

import java.util.ArrayList;
import java.util.Collection;

import gov.lanl.micot.infrastructure.model.FlowConnection;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.model.ScenarioAttribute;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.optimize.AssignmentFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.FlowScenarioVariableFactory;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Assignments for flow assignments based on scenarios
 * @author Russell Bent
 */
public class FlowScenarioAssignmentFactory implements AssignmentFactory {
    
private Collection<Scenario> scenarios = new ArrayList<Scenario>();
  
  /**
   * Construtor
   * @param models
   */
  public FlowScenarioAssignmentFactory(Collection<Scenario> scenarios) {
    this.scenarios = scenarios;
  }
  
  @Override
  public void performAssignment(NaturalGasModel model, MathematicalProgram problem, Solution solution) throws VariableExistsException, NoVariableException {
    FlowScenarioVariableFactory factory = new FlowScenarioVariableFactory(scenarios);
    
    for (FlowConnection connection : model.getFlowConnections()) {
      connection.setFlow(new ScenarioAttribute());
    }

    for (Scenario scenario : scenarios) {
      for (FlowConnection connection : model.getFlowConnections()) {
        ScenarioAttribute attribute = (ScenarioAttribute) connection.getFlow();
        
        if (!scenario.computeActualStatus(connection, connection.getStatus())) {          
          attribute.addEntry(scenario, 0.0);          
          continue;
        }

        Variable variable = factory.getVariable(problem, connection, scenario);
        double value = solution.getValueDouble(variable);
        double flow = Math.sqrt(Math.abs(value));
        if (value < 0) {
          flow = -flow;
        }
        
        attribute.addEntry(scenario, flow);          
      }
    }      
  }

 
}
