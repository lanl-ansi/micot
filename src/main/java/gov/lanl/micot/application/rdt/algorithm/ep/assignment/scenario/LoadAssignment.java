package gov.lanl.micot.application.rdt.algorithm.ep.assignment.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.optimize.AssignmentFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.model.ScenarioAttribute;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LoadVariableFactory;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Assignments of load values
 * 
 * @author Russell Bent
 */
public class LoadAssignment implements AssignmentFactory {

  private Scenario scenario = null;
  
  /**
   * Constructor
   */
  public LoadAssignment(Scenario scenario) {
    this.scenario = scenario;
  }

  @Override
  public void performAssignment(ElectricPowerModel model, MathematicalProgram problem, Solution solution) throws VariableExistsException, NoVariableException {
    LoadVariableFactory variableFactory = new LoadVariableFactory(scenario);
    double mvaBase = model.getMVABase();

    for (Load load : model.getLoads()) {
      
      if (load.getAttribute(Load.REACTIVE_LOAD_A_KEY) == null || !(load.getAttribute(Load.REACTIVE_LOAD_A_KEY) instanceof ScenarioAttribute)) {
        load.setAttribute(Load.REACTIVE_LOAD_A_KEY, new ScenarioAttribute());
      }

      if (load.getAttribute(Load.REACTIVE_LOAD_B_KEY) == null || !(load.getAttribute(Load.REACTIVE_LOAD_B_KEY) instanceof ScenarioAttribute)) {
        load.setAttribute(Load.REACTIVE_LOAD_B_KEY, new ScenarioAttribute());
      }
      
      if (load.getAttribute(Load.REACTIVE_LOAD_C_KEY) == null || !(load.getAttribute(Load.REACTIVE_LOAD_C_KEY) instanceof ScenarioAttribute)) {
        load.setAttribute(Load.REACTIVE_LOAD_C_KEY, new ScenarioAttribute());
      }
      
      if (load.getAttribute(Load.REAL_LOAD_A_KEY) == null || !(load.getAttribute(Load.REAL_LOAD_A_KEY) instanceof ScenarioAttribute)) {
        load.setAttribute(Load.REAL_LOAD_A_KEY, new ScenarioAttribute());
      }

      if (load.getAttribute(Load.REAL_LOAD_B_KEY) == null || !(load.getAttribute(Load.REAL_LOAD_B_KEY) instanceof ScenarioAttribute)) {
        load.setAttribute(Load.REAL_LOAD_B_KEY, new ScenarioAttribute());
      }
      
      if (load.getAttribute(Load.REAL_LOAD_C_KEY) == null || !(load.getAttribute(Load.REAL_LOAD_C_KEY) instanceof ScenarioAttribute)) {
        load.setAttribute(Load.REAL_LOAD_C_KEY, new ScenarioAttribute());
      }

      Variable dq_a = variableFactory.getReactiveVariable(problem, load, LoadVariableFactory.PHASE_A);
      Variable dq_b = variableFactory.getReactiveVariable(problem, load, LoadVariableFactory.PHASE_B);
      Variable dq_c = variableFactory.getReactiveVariable(problem, load, LoadVariableFactory.PHASE_C);

      Variable dp_a = variableFactory.getRealVariable(problem, load, LoadVariableFactory.PHASE_A);
      Variable dp_b = variableFactory.getRealVariable(problem, load, LoadVariableFactory.PHASE_B);
      Variable dp_c = variableFactory.getRealVariable(problem, load, LoadVariableFactory.PHASE_C);
      
      if (dq_a != null) {
        double demand = solution.getValueDouble(dq_a) * mvaBase;
        load.getAttribute(Load.REACTIVE_LOAD_A_KEY, ScenarioAttribute.class).addEntry(scenario, demand);
      }

      if (dq_b != null) {
        double demand = solution.getValueDouble(dq_b) * mvaBase;
        load.getAttribute(Load.REACTIVE_LOAD_B_KEY, ScenarioAttribute.class).addEntry(scenario, demand);
      }

      if (dq_c != null) {
        double demand = solution.getValueDouble(dq_c) * mvaBase;
        load.getAttribute(Load.REACTIVE_LOAD_C_KEY, ScenarioAttribute.class).addEntry(scenario, demand);
      }
     
      if (dp_a != null) {
        double demand = solution.getValueDouble(dq_a) * mvaBase;
        load.getAttribute(Load.REAL_LOAD_A_KEY, ScenarioAttribute.class).addEntry(scenario, demand);
      }

      if (dp_b != null) {
        double demand = solution.getValueDouble(dq_b) * mvaBase;
        load.getAttribute(Load.REAL_LOAD_B_KEY, ScenarioAttribute.class).addEntry(scenario, demand);
      }

      if (dp_c != null) {
        double demand = solution.getValueDouble(dq_c) * mvaBase;
        load.getAttribute(Load.REAL_LOAD_C_KEY, ScenarioAttribute.class).addEntry(scenario, demand);
      }
    }
  }
  
}
