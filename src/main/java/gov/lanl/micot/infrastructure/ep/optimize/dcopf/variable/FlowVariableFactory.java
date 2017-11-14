package gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.optimize.VariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

/**
 * General class for creating flow variables
 * 
 * @author Russell Bent
 */
public class FlowVariableFactory implements VariableFactory {

  /**
   * Constructor
   */
  public FlowVariableFactory() {    
  }
  
  /**
   * Function for create the variable name associated with a load
   * 
   * @param node
   * @return
   */
  private String getFlowVariableName(ElectricPowerFlowConnection edge) {
    return "F" + edge.toString();
  }
    
  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, /*Collection<ElectricPowerNode> nodes,*/ ElectricPowerModel model) throws VariableExistsException {
    ArrayList<Variable> variables = new ArrayList<Variable>();   
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      ElectricPowerNode node1 = model.getFirstNode(edge);
      ElectricPowerNode node2 = model.getSecondNode(edge);
      if (/*nodes.contains(node1) && nodes.contains(node2) && */edge.getStatus()) {
        variables.add(program.makeContinuousVariable(getFlowVariableName(edge)));        
      }
    }
    return variables;
  }

  @Override
  public Variable getVariable(MathematicalProgram program, Object asset) throws NoVariableException {
    if (asset instanceof ElectricPowerFlowConnection) {
      if (program.getVariable(getFlowVariableName((ElectricPowerFlowConnection) asset)) != null) {
        return program.getVariable(getFlowVariableName((ElectricPowerFlowConnection) asset));
      }
    }
    throw new NoVariableException(asset.toString());
  }

}
