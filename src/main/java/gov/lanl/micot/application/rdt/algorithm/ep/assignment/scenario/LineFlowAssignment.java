package gov.lanl.micot.application.rdt.algorithm.ep.assignment.scenario;


import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.AssignmentFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.model.ScenarioAttribute;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineFlowVariableFactory;
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
public class LineFlowAssignment implements AssignmentFactory {

  private Scenario scenario = null;
  
  /**
   * Constructor
   */
  public LineFlowAssignment(Scenario scenario) {
    this.scenario = scenario;
  }

  @Override
  public void performAssignment(ElectricPowerModel model, MathematicalProgram problem, Solution solution) throws VariableExistsException, NoVariableException {
    LineFlowVariableFactory flowVariableFactory = new LineFlowVariableFactory(scenario);
    double mvaBase = model.getMVABase();

    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (edge.getAttribute(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_A_KEY) == null || !(edge.getAttribute(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_A_KEY) instanceof ScenarioAttribute)) {      
        edge.setAttribute(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_A_KEY, new ScenarioAttribute());
      }
      if (edge.getAttribute(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_B_KEY) == null || !(edge.getAttribute(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_B_KEY) instanceof ScenarioAttribute)) {      
        edge.setAttribute(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_B_KEY, new ScenarioAttribute());
      }
      if (edge.getAttribute(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_C_KEY) == null || !(edge.getAttribute(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_C_KEY) instanceof ScenarioAttribute)) {      
        edge.setAttribute(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_C_KEY, new ScenarioAttribute());
      }
      
      if (edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_A_KEY) == null || !(edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_A_KEY) instanceof ScenarioAttribute)) {      
        edge.setAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_A_KEY, new ScenarioAttribute());
      }
      if (edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_B_KEY) == null || !(edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_B_KEY) instanceof ScenarioAttribute)) {      
        edge.setAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_B_KEY, new ScenarioAttribute());
      }
      if (edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_C_KEY) == null || !(edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_C_KEY) instanceof ScenarioAttribute)) {      
        edge.setAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_C_KEY, new ScenarioAttribute());
      }
      
      
      Variable fp_a = flowVariableFactory.getRealVariable(problem, edge, LineFlowVariableFactory.PHASE_A);
      Variable fp_b = flowVariableFactory.getRealVariable(problem, edge, LineFlowVariableFactory.PHASE_B);
      Variable fp_c = flowVariableFactory.getRealVariable(problem, edge, LineFlowVariableFactory.PHASE_C);

      Variable fq_a = flowVariableFactory.getReactiveVariable(problem, edge, LineFlowVariableFactory.PHASE_A);
      Variable fq_b = flowVariableFactory.getReactiveVariable(problem, edge, LineFlowVariableFactory.PHASE_B);
      Variable fq_c = flowVariableFactory.getReactiveVariable(problem, edge, LineFlowVariableFactory.PHASE_C);
   
      if (fq_a != null) {          
        double flow = solution.getValueDouble(fq_a) * mvaBase;          
        edge.getAttribute(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_A_KEY, ScenarioAttribute.class).addEntry(scenario, flow);
      }

      if (fq_b != null) {
        double flow = solution.getValueDouble(fq_b) * mvaBase;
        edge.getAttribute(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_B_KEY, ScenarioAttribute.class).addEntry(scenario, flow);
      }

      if (fq_c != null) {
        double flow = solution.getValueDouble(fq_c) * mvaBase;
        edge.getAttribute(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_C_KEY, ScenarioAttribute.class).addEntry(scenario, flow);
      }
      
      if (fp_a != null) {          
        double flow = solution.getValueDouble(fq_a) * mvaBase;          
        edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_A_KEY, ScenarioAttribute.class).addEntry(scenario, flow);
      }

      if (fp_b != null) {
        double flow = solution.getValueDouble(fq_b) * mvaBase;
        edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_B_KEY, ScenarioAttribute.class).addEntry(scenario, flow);
      }

      if (fp_c != null) {
        double flow = solution.getValueDouble(fq_c) * mvaBase;
        edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_C_KEY, ScenarioAttribute.class).addEntry(scenario, flow);
      }

    }
  }
  
}
