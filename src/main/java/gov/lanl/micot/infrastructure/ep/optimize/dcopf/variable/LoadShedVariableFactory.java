package gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.optimize.VariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

/**
 * General class for creating load variable factories
 * @author Russell Bent
 */
public class LoadShedVariableFactory implements VariableFactory {

  /**
   * Constructor
   */
  public LoadShedVariableFactory() {    
  }
  
  /**
   * Function for create the variable name associated with a load
   * 
   * @param node
   * @return
   */
  private String getLoadVariableName(Load node) {
    return "LS" + node.toString();
  }
  
  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, /*Collection<ElectricPowerNode> nodes,*/ ElectricPowerModel model) throws VariableExistsException {
    ArrayList<Variable> variables = new ArrayList<Variable>();    
    for (ElectricPowerNode node : model.getNodes()) {
      for (Load load : node.getComponents(Load.class)) {
        Variable variable = program.makeContinuousVariable(getLoadVariableName(load));
        variables.add(variable);
      }
    }
    
    return variables;
  }

  @Override
  public Variable getVariable(MathematicalProgram program, Object asset) throws NoVariableException {
    if (asset instanceof Load) {
      if (program.getVariable(getLoadVariableName((Load) asset)) != null) {
        return program.getVariable(getLoadVariableName((Load) asset));
      }
    }
    return null;
  }

}
