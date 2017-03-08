package gov.lanl.micot.application.rdt.algorithm.ep.mip.assignment.scenario;

import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.model.ScenarioAttribute;
import gov.lanl.micot.infrastructure.optimize.mathprogram.assignment.ScenarioAssignmentFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioRealFlowPhaseVariableFactory;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Assignments of phase flows
 * 
 * @author Russell Bent
 */
public class ScenarioRealFlowPhaseAssignmentFactory extends ScenarioAssignmentFactory<ElectricPowerNode, ElectricPowerModel> {


  /**
   * Constructor
   */
  public ScenarioRealFlowPhaseAssignmentFactory(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  @Override
  public void performAssignment(ElectricPowerModel model, MathematicalProgram problem, Solution solution) throws VariableExistsException, NoVariableException {
    ScenarioRealFlowPhaseVariableFactory flowVariableFactory = new ScenarioRealFlowPhaseVariableFactory(getScenarios());
    double mvaBase = model.getMVABase();

    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_A_KEY) == null || !(edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_A_KEY) instanceof ScenarioAttribute)) {      
        edge.setAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_A_KEY, new ScenarioAttribute());
      }
      if (edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_B_KEY) == null || !(edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_B_KEY) instanceof ScenarioAttribute)) {      
        edge.setAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_B_KEY, new ScenarioAttribute());
      }
      if (edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_C_KEY) == null || !(edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_C_KEY) instanceof ScenarioAttribute)) {      
        edge.setAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_C_KEY, new ScenarioAttribute());
      }
      
      for (Scenario scenario : getScenarios()) {
        Variable variable = flowVariableFactory.getVariable(problem, edge, ScenarioRealFlowPhaseVariableFactory.PHASE_A, scenario);
        if (variable != null) {
          double flow = solution.getValueDouble(variable) * mvaBase;
          edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_A_KEY, ScenarioAttribute.class).addEntry(scenario, flow);
        }

        variable = flowVariableFactory.getVariable(problem, edge, ScenarioRealFlowPhaseVariableFactory.PHASE_B, scenario);
        if (variable != null) {
          double flow = solution.getValueDouble(variable) * mvaBase;
          edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_B_KEY, ScenarioAttribute.class).addEntry(scenario, flow);
        }

        variable = flowVariableFactory.getVariable(problem, edge, ScenarioRealFlowPhaseVariableFactory.PHASE_C, scenario);
        if (variable != null) {
          double flow = solution.getValueDouble(variable) * mvaBase;
          edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_C_KEY, ScenarioAttribute.class).addEntry(scenario, flow);
        }

      }
    }
  }
  
}
