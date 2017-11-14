package gov.lanl.micot.infrastructure.ep.optimize.dcopf.assignment.scenario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.optimize.AssignmentFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.scenario.PhaseAngleScenarioVariableFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.model.ScenarioAttribute;
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
  public void performAssignment(ElectricPowerModel model, MathematicalProgram problem, Solution solution) throws VariableExistsException, NoVariableException {
    PhaseAngleScenarioVariableFactory phaseAngleVariableFactory = new PhaseAngleScenarioVariableFactory(scenarios);
    
    for (ElectricPowerFlowConnection connection : model.getFlowConnections()) {
      connection.setMWFlow(new ScenarioAttribute());
    }

    for (Scenario scenario : scenarios) {
      HashMap<ElectricPowerNode,Double> phaseAngles = new HashMap<ElectricPowerNode,Double>();
      for (ElectricPowerNode node : model.getNodes()) {
        Variable variable = phaseAngleVariableFactory.getVariable(problem, node, scenario);
        phaseAngles.put(node,solution.getValueDouble(variable));              
      }
        
      for (ElectricPowerFlowConnection connection : model.getFlowConnections()) {
        if (!phaseAngles.containsKey(model.getFirstNode(connection)) || !phaseAngles.containsKey(model.getSecondNode(connection))) {
          continue;
        }

        ScenarioAttribute attribute = (ScenarioAttribute) connection.getMWFlow();

        if (!scenario.computeActualStatus(connection, connection.getStatus())) {          
          attribute.addEntry(scenario, 0.0);          
          continue;
        }

        double impedance = connection.getSusceptance();
        ElectricPowerNode firstNode = model.getFirstNode(connection);
        ElectricPowerNode secondNode = model.getSecondNode(connection);

        double realFlow = impedance * (phaseAngles.get(firstNode).doubleValue() - phaseAngles.get(secondNode).doubleValue());
        attribute.addEntry(scenario, realFlow * model.getMVABase());          
      }
    }
  }

 
}
