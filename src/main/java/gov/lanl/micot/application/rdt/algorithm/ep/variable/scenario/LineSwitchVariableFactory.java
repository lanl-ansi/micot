package gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.VariableFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
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

  private Scenario scenario = null;
  
  /**
   * Constructor
   */
  public LineSwitchVariableFactory(Scenario scenario) {
    this.scenario = scenario;
  }

  /**
   * Function for create the variable name associated with a switch
   * 
   * @param node
   * @return
   */
  private String getFlowVariableName(ElectricPowerFlowConnection edge, Scenario scenario) {
    return "tau-" + edge.toString() + "-" + scenario.getIndex();
  }

  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, ElectricPowerModel model) throws VariableExistsException, InvalidVariableException {
    ArrayList<Variable> variables = new ArrayList<Variable>();
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (hasVariable(edge)) {
        variables.add(program.makeDiscreteVariable(getFlowVariableName(edge, scenario)));
      }
    }
    return variables;
  }

  /**
   * Does the edge have a variable or not.
   * @param edge
   * @return
   */
  public boolean hasVariable(ElectricPowerFlowConnection edge) {    
    
    // edge is completely gone
    if (!edge.getStatus()) {
      return false;
    }
    
    boolean hasCost = edge.getAttribute(AlgorithmConstants.LINE_SWITCH_COST_KEY) == null ? false : true;
    boolean buildSwitch = edge.getAttribute(LPNormIOConstants.LINE_CAN_ADD_SWITCH) == null ? false : edge.getAttribute(LPNormIOConstants.LINE_CAN_ADD_SWITCH, Boolean.class);
    
    // the switch is builable
    if (hasCost || buildSwitch) {
      return true;
    }
    
    // the line is new
    boolean isNew = edge.getAttribute(AlgorithmConstants.IS_NEW_LINE_KEY) == null ? false : edge.getAttribute(AlgorithmConstants.IS_NEW_LINE_KEY, Boolean.class);
    if (isNew) {
      return true;
    }
    
    // line already has a switch
    boolean hasSwitch = edge.getAttribute(LPNormIOConstants.LINE_HAS_SWITCH_TAG) == null ? false : edge.getAttribute(LPNormIOConstants.LINE_HAS_SWITCH_TAG, Boolean.class);
    if (hasSwitch) {
      return true;
    }

    return false;
  }
  
  @Override
  public Variable getVariable(MathematicalProgram program, Object asset) throws NoVariableException {
    return null;
  }

  public Variable getVariable(MathematicalProgram program, ElectricPowerFlowConnection edge) throws NoVariableException {
    return program.getVariable(getFlowVariableName(edge, scenario));
  }
  
}
