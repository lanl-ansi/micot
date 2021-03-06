package gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.VariableFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidVariableException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Creates a variable for whether or not a line is active or not
 * These are the z_{ij} variables in the paper
 * 
 * @author Russell Bent
 */
public class LineActiveVariableFactory implements VariableFactory {

  private Scenario scenario = null;
  private Boolean isDiscrete = true;
  
  /**
   * Constructor
   */
  public LineActiveVariableFactory(Scenario scenario, Boolean isDiscrete) {
    this.scenario = scenario;
    this.isDiscrete = isDiscrete;
  }
  
  /**
   * Function for create the variable name associated with a switch
   * 
   * @param node
   * @return
   */
  private String getVariableName(ElectricPowerFlowConnection edge, Scenario scenario) {
    return "z-" + edge.toString() +"-" + scenario.getIndex();
  }

  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, ElectricPowerModel model) throws VariableExistsException, InvalidVariableException {
    ArrayList<Variable> variables = new ArrayList<Variable>();
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (hasVariable(edge)) {
        if (isDiscrete) {
          variables.add(program.makeDiscreteVariable(getVariableName(edge, scenario)));
        }
        else {
          variables.add(program.makeContinuousVariable(getVariableName(edge, scenario)));          
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
  public boolean hasVariable(ElectricPowerFlowConnection edge) {
    if (!edge.getStatus()) {
      return false;
    }
    return true;      
  }

  @Override
  public Variable getVariable(MathematicalProgram program, Object asset) throws NoVariableException {
    return null;
  }

  /**
   * Get the variable of this variable
   * @param program
   * @param connection
   * @param scenario
   * @return
   * @throws NoVariableException
   */
  public Variable getVariable(MathematicalProgram program, ElectricPowerFlowConnection connection) throws NoVariableException {
    return program.getVariable(getVariableName(connection, scenario));
  }
  
}
