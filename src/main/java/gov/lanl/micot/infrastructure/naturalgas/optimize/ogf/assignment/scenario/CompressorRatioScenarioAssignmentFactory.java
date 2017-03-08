package gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.assignment.scenario;

import java.util.ArrayList;
import java.util.Collection;

import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.model.ScenarioAttribute;
import gov.lanl.micot.infrastructure.naturalgas.model.Compressor;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasNode;
import gov.lanl.micot.infrastructure.naturalgas.optimize.AssignmentFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.FlowScenarioVariableFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.PressureScenarioVariableFactory;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Assignments for compression ratios based on scenarios
 * @author Russell Bent
 */
public class CompressorRatioScenarioAssignmentFactory implements AssignmentFactory {
    
private Collection<Scenario> scenarios = new ArrayList<Scenario>();
  
  /**
   * Construtor
   * @param models
   */
  public CompressorRatioScenarioAssignmentFactory(Collection<Scenario> scenarios) {
    this.scenarios = scenarios;
  }
  
  @Override
  public void performAssignment(NaturalGasModel model, MathematicalProgram problem, Solution solution) throws VariableExistsException, NoVariableException {
    FlowScenarioVariableFactory flowFactory = new FlowScenarioVariableFactory(scenarios);
    PressureScenarioVariableFactory pressureFactory = new PressureScenarioVariableFactory(scenarios);
    
    for (Compressor connection : model.getCompressors()) {
      connection.setCompressionRatio(new ScenarioAttribute());
    }

    for (Scenario scenario : scenarios) {
      for (Compressor connection : model.getCompressors()) {
        ScenarioAttribute attribute = (ScenarioAttribute) connection.getCompressionRatio();
        
        if (!scenario.computeActualStatus(connection, connection.getActualStatus())) {          
          attribute.addEntry(scenario, 0.0);          
          continue;
        }

        Variable flowVariable = flowFactory.getVariable(problem, connection, scenario);
        double value = solution.getValueDouble(flowVariable);
        double flow = Math.sqrt(Math.abs(value));
        if (value < 0) {
          flow = -flow;
        }

        NaturalGasNode node1 = model.getFirstNode(connection);
        NaturalGasNode node2 = model.getSecondNode(connection);
        
        double p1 = Math.sqrt(solution.getValueDouble(pressureFactory.getVariable(problem, node1,scenario)));
        double p2 = Math.sqrt(solution.getValueDouble(pressureFactory.getVariable(problem, node2,scenario)));
        
        double ratio = 0;
        if (flow >= 0) {
          ratio = p2 / p1;
        }
        else {
          ratio = p1 / p2;
        }
                
        attribute.addEntry(scenario, ratio);          
      }
    }      
  }

 
}
