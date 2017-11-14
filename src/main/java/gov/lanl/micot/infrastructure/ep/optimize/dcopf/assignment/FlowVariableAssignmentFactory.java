package gov.lanl.micot.infrastructure.ep.optimize.dcopf.assignment;


import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.AssignmentFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.FlowVariableFactory;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Assignments for flow assignments for actual flow variables
 * @author Russell Bent
 */
public class FlowVariableAssignmentFactory implements AssignmentFactory {
    
  /**
   * Constructor
   * @param numberOfIncrements
   * @param incrementSize
   */
  public FlowVariableAssignmentFactory() {
    super();
  }
  
  @Override
  public void performAssignment(ElectricPowerModel model, MathematicalProgram problem, Solution solution/*, Collection<ElectricPowerNode> nodes*/) throws VariableExistsException, NoVariableException {
    FlowVariableFactory flowVariableFactory = new FlowVariableFactory();
    
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
//      ElectricPowerNode node1 = model.getFirstNode(edge);
   //   ElectricPowerNode node2 = model.getSecondNode(edge);
      if (edge.getStatus()/* && nodes.contains(node1) && nodes.contains(node2)*/) {
        Variable variable = flowVariableFactory.getVariable(problem, edge);      
        double realFlow = solution.getValueDouble(variable);
        edge.setMWFlow(realFlow * model.getMVABase());
      }
    }
  }
}
