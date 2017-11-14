package gov.lanl.micot.application.rdt.algorithm.ep.mip.variable;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.VariableFactory;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidVariableException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Creates a variable for construction of lines
 * These are the x_{ij} variables in the paper
 * 
 * @author Russell Bent
 */
public class LineConstructionVariableFactory implements VariableFactory {

  /**
   * Constructor
   */
  public LineConstructionVariableFactory() {
  }

  /**
   * Function for create the variable name associated with a switch
   * 
   * @param node
   * @return
   */
  private String getFlowVariableName(ElectricPowerFlowConnection edge) {
    return "X-" + edge.toString();
  }

  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, ElectricPowerModel model) throws VariableExistsException, InvalidVariableException {
    ArrayList<Variable> variables = new ArrayList<Variable>();
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      boolean isDisabled = !edge.getStatus();
      boolean isNew = edge.getAttribute(AlgorithmConstants.IS_NEW_LINE_KEY) == null ? false : edge.getAttribute(AlgorithmConstants.IS_NEW_LINE_KEY, Boolean.class);
      if (isDisabled || !isNew) {
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
