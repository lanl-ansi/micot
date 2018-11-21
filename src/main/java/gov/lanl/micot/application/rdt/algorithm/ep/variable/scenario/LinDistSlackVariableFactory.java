package gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.VariableFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

/**
 * General class for a slack variable associated
 * with each LinDistFlow constraint so that we can model slack
 * 
 * @author Russell Bent
 */
public class LinDistSlackVariableFactory implements VariableFactory {

  public static final String PHASE_A = "a";
  public static final String PHASE_B = "b";
  public static final String PHASE_C = "c";

  private Scenario scenario = null;
  
  /**
   * Constructor
   * 
   * @param scenarios
   */
  public LinDistSlackVariableFactory(Scenario scenario) {
    this.scenario = scenario;
  }

  /**
   * Function for create the variable name associated with a generator
   * 
   * @param node
   * @return
   */
  private String getVariableName(ElectricPowerFlowConnection edge, String phase) {
    return "v_slack^s." + phase + "-" + edge.toString() + "." + scenario.getIndex();
  }

  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, ElectricPowerModel model) throws VariableExistsException {
    ArrayList<Variable> variables = new ArrayList<Variable>();
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {        
      if (hasVariable(edge)) {
        if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_A_KEY, Boolean.class)) {
          variables.add(program.makeContinuousVariable(getVariableName(edge, PHASE_A)));        
        }
        if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_B_KEY, Boolean.class)) {
          variables.add(program.makeContinuousVariable(getVariableName(edge, PHASE_B)));        
        }
        if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_C_KEY, Boolean.class)) {
          variables.add(program.makeContinuousVariable(getVariableName(edge, PHASE_C)));                  
        }          
      }
    }
    return variables;
  }

  @Override
  public Variable getVariable(MathematicalProgram program, Object asset) throws NoVariableException {
    throw new NoVariableException(asset.toString());
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
  
  /**
   * Get the specific voltage variable
   * @param program
   * @param asset
   * @param phase
   * @param scenario
   * @return
   * @throws NoVariableException
   */
  public Variable getVariable(MathematicalProgram program, ElectricPowerFlowConnection asset, String phase) throws NoVariableException {
    return program.getVariable(getVariableName(asset, phase));
  }

}
