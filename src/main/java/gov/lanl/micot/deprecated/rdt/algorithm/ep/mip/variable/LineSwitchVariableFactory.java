package gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.VariableFactory;
import gov.lanl.micot.application.lpnorm.io.LPNormIOConstants;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidVariableException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Creates a variable for construction of switches
 * 
 * These are the _\tau_{ij} variables from the paper
 * 
 * @author Russell Bent
 */
public class LineSwitchVariableFactory implements VariableFactory {

  /**
   * Constructor
   */
  public LineSwitchVariableFactory() {
  }

  /**
   * Function for create the variable name associated with a switch
   * 
   * @param node
   * @return
   */
  private String getFlowVariableName(ElectricPowerFlowConnection edge) {
    return "tau-" + edge.toString();
  }

  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, ElectricPowerModel model) throws VariableExistsException, InvalidVariableException {
    ArrayList<Variable> variables = new ArrayList<Variable>();
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      boolean isDisabled = !edge.getStatus();
      boolean hasCost = edge.getAttribute(AlgorithmConstants.LINE_SWITCH_COST_KEY) == null ? false : true;
      boolean buildSwitch = edge.getAttribute(LPNormIOConstants.LINE_CAN_ADD_SWITCH) == null ? false : edge.getAttribute(LPNormIOConstants.LINE_CAN_ADD_SWITCH, Boolean.class);
      boolean canBuild  = (isDisabled || (!hasCost && !buildSwitch)) ? false : true;             
      if (isDisabled || !canBuild) {
        continue;
      }
      variables.add(program.makeDiscreteVariable(getFlowVariableName(edge)));
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
    return null;
  }

}
