package gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.VariableFactory;
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
 * Creates a variable for hardening lines
 * 
 * These are the t_{ij} variables from the paper
 * 
 * @author Russell Bent
 */
public class LineHardenVariableFactory implements VariableFactory {

  private Scenario scenario = null;
  
  /**
   * Constructor
   */
  public LineHardenVariableFactory(Scenario scenario) {
    this.scenario = scenario;
  }

  /**
   * Function for create the variable name associated with a switch
   * 
   * @param node
   * @return
   */
  private String getFlowVariableName(ElectricPowerFlowConnection edge, Scenario sceanario) {
    return "h-" + edge.toString() + "-" + scenario.getIndex();
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
   * Does the variable exist
   * @param edge
   * @return
   */
  public boolean hasVariable(ElectricPowerFlowConnection edge) {
    return edge.getAttribute(AlgorithmConstants.CAN_HARDEN_KEY) != null && edge.getAttribute(AlgorithmConstants.CAN_HARDEN_KEY, Boolean.class);
  }
  
  @Override
  public Variable getVariable(MathematicalProgram program, Object asset) throws NoVariableException {
    return null;
  }

  public Variable getVariable(MathematicalProgram program, ElectricPowerFlowConnection edge) throws NoVariableException {
    return program.getVariable(getFlowVariableName(edge, scenario));
  }
  
}
