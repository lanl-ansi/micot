package gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable;

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
 * General class for creating phase angle variables
 * @author Russell Bent
 */
public class PhaseAngleVariableFactory implements VariableFactory {

  /**
   * Function for create the variable name associated with a load
   * 
   * @param node
   * @return
   */
  private String getPhaseAngleVariableName(ElectricPowerNode node) {
    return "P" + node.toString();
  }
    
  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, /*Collection<ElectricPowerNode> nodes,*/ ElectricPowerModel model) throws VariableExistsException {
    ArrayList<Variable> variables = new ArrayList<Variable>();    
    for (ElectricPowerNode node : model.getNodes()) {
      variables.add(program.makeContinuousVariable(getPhaseAngleVariableName(node)));
    }
    return variables;
  }

  @Override
  public Variable getVariable(MathematicalProgram program, Object asset) throws NoVariableException {
    if (asset instanceof ElectricPowerNode) {
      if (program.getVariable(getPhaseAngleVariableName((ElectricPowerNode) asset)) != null) {
        return program.getVariable(getPhaseAngleVariableName((ElectricPowerNode) asset));
      }
    }
    throw new NoVariableException(asset.toString());
  }

}
