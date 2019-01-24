package gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.VariableFactory;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidVariableException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Creates a variable for the existence of a line... gets tied to construction and hardening
 * These are the x_{ij} variables in the paper
 * 
 * @author Russell Bent
 */
public class LineExistVariableFactory implements VariableFactory {

  private Scenario scenario = null;
  private Boolean isDiscrete = true;
  
  /**
   * Constructor
   */
  public LineExistVariableFactory(Scenario scenario, Boolean isDiscrete) {
    this.scenario = scenario;
    this.isDiscrete = isDiscrete;
  }
  
  /**
   * Function for create the variable name associated with a switch
   * 
   * @param node
   * @return
   */
  private String getFlowVariableName(ElectricPowerFlowConnection edge, Scenario scenario) {
    return "x-" + edge.toString() +"-" + scenario.getIndex();
  }

  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, ElectricPowerModel model) throws VariableExistsException, InvalidVariableException {
    ArrayList<Variable> variables = new ArrayList<Variable>();
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (hasVariable(edge, scenario)) {
        if (isDiscrete) {
          variables.add(program.makeDiscreteVariable(getFlowVariableName(edge, scenario)));
        }
        else {
          variables.add(program.makeContinuousVariable(getFlowVariableName(edge, scenario)));          
        }
      }
    }
    return variables;
  }
  
  /**
   * Does this edge have a variable
   * @param edge
   * @return
   */
  public boolean hasVariable(ElectricPowerFlowConnection edge, Scenario scenario) {
    if (!edge.getStatus()) {
      return false;
    }
     
    boolean isNew = edge.getAttribute(AlgorithmConstants.IS_NEW_LINE_KEY) == null ? false : edge.getAttribute(AlgorithmConstants.IS_NEW_LINE_KEY, Boolean.class);
    if (isNew) {
      return true;
    }
    
    boolean isDamaged = scenario.getModification(edge, Asset.IS_FAILED_KEY, Boolean.class) != null ? scenario.getModification(edge, Asset.IS_FAILED_KEY, Boolean.class) : false;
    return isDamaged;
  }

  @Override
  public Variable getVariable(MathematicalProgram program, Object asset) throws NoVariableException {
    return null;
  }

  public Variable getVariable(MathematicalProgram program, ElectricPowerFlowConnection connection) throws NoVariableException {
    return program.getVariable(getFlowVariableName(connection, scenario));
  }
  
}
