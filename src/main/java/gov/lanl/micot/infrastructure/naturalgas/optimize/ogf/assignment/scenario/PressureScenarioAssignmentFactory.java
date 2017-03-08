package gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.assignment.scenario;

import java.util.ArrayList;
import java.util.Collection;

import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.model.ScenarioAttribute;
import gov.lanl.micot.infrastructure.naturalgas.model.Junction;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.optimize.AssignmentFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.PressureScenarioVariableFactory;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Assignments for pressure assignments based on scenarios
 * @author Russell Bent
 */
public class PressureScenarioAssignmentFactory implements AssignmentFactory {
    
private Collection<Scenario> scenarios = new ArrayList<Scenario>();
  
  /**
   * Construtor
   * @param models
   */
  public PressureScenarioAssignmentFactory(Collection<Scenario> scenarios) {
    this.scenarios = scenarios;
  }
  
  @Override
  public void performAssignment(NaturalGasModel model, MathematicalProgram problem, Solution solution) throws VariableExistsException, NoVariableException {
    PressureScenarioVariableFactory factory = new PressureScenarioVariableFactory(scenarios);
    
    for (Junction junction : model.getJunctions()) {
      junction.setPressure(new ScenarioAttribute());
    }

    for (Scenario scenario : scenarios) {
      for (Junction junction : model.getJunctions()) {
        ScenarioAttribute attribute = (ScenarioAttribute) junction.getPressure();
        
        if (!scenario.computeActualStatus(junction, junction.getActualStatus())) {          
          attribute.addEntry(scenario, 0.0);          
          continue;
        }

        Variable variable = factory.getVariable(problem, model.getNode(junction), scenario);        
        attribute.addEntry(scenario, Math.sqrt(solution.getValueDouble(variable)));          
      }
    }      
  }

 
}
