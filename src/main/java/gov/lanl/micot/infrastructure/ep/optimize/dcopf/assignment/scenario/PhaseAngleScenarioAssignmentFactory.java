package gov.lanl.micot.infrastructure.ep.optimize.dcopf.assignment.scenario;

import java.util.ArrayList;
import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.model.Bus;
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
 * General class for creating phase angle variable assignments
 * @author Russell Bent
 */
public class PhaseAngleScenarioAssignmentFactory implements AssignmentFactory {

  private Collection<Scenario> scenarios = new ArrayList<Scenario>();
  
  /**
   * Construtor
   * @param models
   */
  public PhaseAngleScenarioAssignmentFactory(Collection<Scenario> scenarios) {
    this.scenarios = scenarios;
  }
      
  @Override
  public void performAssignment(ElectricPowerModel model, MathematicalProgram problem, Solution solution) throws VariableExistsException, NoVariableException {
    PhaseAngleScenarioVariableFactory phaseAngleVariableFactory = new PhaseAngleScenarioVariableFactory(scenarios);
    
    for (ElectricPowerNode n : model.getNodes()) {
      n.getBus().setPhaseAngle(new ScenarioAttribute());
    }
    
    for (Scenario scenario : scenarios) {
        for (ElectricPowerNode node : model.getNodes()) {
          Variable variable = phaseAngleVariableFactory.getVariable(problem, node, scenario);
          ScenarioAttribute attribute = node.getBus().getAttribute(Bus.PHASE_ANGLE_KEY, ScenarioAttribute.class);
          attribute.addEntry(scenario, Math.toDegrees(solution.getValueDouble(variable)));
          
        }    
    }
  }

 
}
