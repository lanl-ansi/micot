package gov.lanl.micot.infrastructure.ep.optimize.dcopf.assignment;

import java.util.Collection;
import java.util.HashMap;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.optimize.AssignmentFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.PhaseAngleVariableFactory;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Assignments for flow assignments
 * @author Russell Bent
 */
public class FlowAssignmentFactory implements AssignmentFactory {
    
  /**
   * Constructor
   * @param numberOfIncrements
   * @param incrementSize
   */
  public FlowAssignmentFactory() {
    super();
  }
  
  @Override
  public void performAssignment(ElectricPowerModel model, MathematicalProgram problem, Solution solution/*, Collection<ElectricPowerNode> nodes*/) throws VariableExistsException, NoVariableException {
    PhaseAngleVariableFactory phaseAngleVariableFactory = new PhaseAngleVariableFactory();
    HashMap<ElectricPowerNode,Double> phaseAngles = new HashMap<ElectricPowerNode,Double>();
    
    for (ElectricPowerNode node : model.getNodes()) {
      Variable variable = phaseAngleVariableFactory.getVariable(problem, node);
      phaseAngles.put(node,solution.getValueDouble(variable));              
    }
        
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (!phaseAngles.containsKey(model.getFirstNode(edge)) || !phaseAngles.containsKey(model.getSecondNode(edge))) {
        continue;
      }
      
      if (edge.getStatus() == false) {
        edge.setMWFlow(0.0);
        continue;
      }

      double impedance = edge.getSusceptance();
      ElectricPowerNode firstNode = model.getFirstNode(edge);
      ElectricPowerNode secondNode = model.getSecondNode(edge);

      double realFlow = impedance * (phaseAngles.get(firstNode).doubleValue() - phaseAngles.get(secondNode).doubleValue());
      edge.setMWFlow(realFlow * model.getMVABase());
    }
  }

 
}
