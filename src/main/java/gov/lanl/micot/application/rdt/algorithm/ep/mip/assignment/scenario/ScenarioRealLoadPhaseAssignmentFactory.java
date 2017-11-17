package gov.lanl.micot.application.rdt.algorithm.ep.mip.assignment.scenario;

import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.model.ScenarioAttribute;
import gov.lanl.micot.infrastructure.optimize.mathprogram.assignment.ScenarioAssignmentFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioRealLoadPhaseVariableFactory;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Assignments of load phase
 * 
 * @author Russell Bent
 */
public class ScenarioRealLoadPhaseAssignmentFactory extends ScenarioAssignmentFactory<ElectricPowerNode, ElectricPowerModel> {

  /**
   * Constructor
   */
  public ScenarioRealLoadPhaseAssignmentFactory(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  @Override
  public void performAssignment(ElectricPowerModel model, MathematicalProgram problem, Solution solution) throws VariableExistsException, NoVariableException {
    ScenarioRealLoadPhaseVariableFactory gridVariableFactory = new ScenarioRealLoadPhaseVariableFactory(getScenarios());
    double mvaBase = model.getMVABase();
    

    for (Load load : model.getLoads()) {
      
      if (load.getAttribute(Load.REAL_LOAD_A_KEY) == null || !(load.getAttribute(Load.REAL_LOAD_A_KEY) instanceof ScenarioAttribute)) {
        load.setAttribute(Load.REAL_LOAD_A_KEY, new ScenarioAttribute());
      }

      if (load.getAttribute(Load.REAL_LOAD_B_KEY) == null || !(load.getAttribute(Load.REAL_LOAD_B_KEY) instanceof ScenarioAttribute)) {
        load.setAttribute(Load.REAL_LOAD_B_KEY, new ScenarioAttribute());
      }
      
      if (load.getAttribute(Load.REAL_LOAD_C_KEY) == null || !(load.getAttribute(Load.REAL_LOAD_C_KEY) instanceof ScenarioAttribute)) {
        load.setAttribute(Load.REAL_LOAD_C_KEY, new ScenarioAttribute());
      }

      for (Scenario scenario : getScenarios()) {
        Variable variable = gridVariableFactory.getVariable(problem, load, ScenarioRealLoadPhaseVariableFactory.PHASE_A, scenario);
        if (variable != null) {
          double demand = solution.getValueDouble(variable) * mvaBase;
          load.getAttribute(Load.REAL_LOAD_A_KEY, ScenarioAttribute.class).addEntry(scenario, demand);
        }

        variable = gridVariableFactory.getVariable(problem, load, ScenarioRealLoadPhaseVariableFactory.PHASE_B, scenario);
        if (variable != null) {
          double demand = solution.getValueDouble(variable) * mvaBase;
          load.getAttribute(Load.REAL_LOAD_B_KEY, ScenarioAttribute.class).addEntry(scenario, demand);
        }

        variable = gridVariableFactory.getVariable(problem, load, ScenarioRealLoadPhaseVariableFactory.PHASE_C, scenario);
        if (variable != null) {
          double demand = solution.getValueDouble(variable) * mvaBase;
          load.getAttribute(Load.REAL_LOAD_C_KEY, ScenarioAttribute.class).addEntry(scenario, demand);
        }

      }
    }
  }
  
}
