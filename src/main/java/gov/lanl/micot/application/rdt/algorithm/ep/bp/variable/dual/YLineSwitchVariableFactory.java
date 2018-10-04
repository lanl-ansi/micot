package gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.dual;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.variable.ScenarioVariableFactory;
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
 * Creates dual variables for each w - \sum lambda constraint (where w is design variable)
 * 
 * @author Russell Bent
 */
public class YLineSwitchVariableFactory extends ScenarioVariableFactory<ElectricPowerNode, ElectricPowerModel> {

  /**
   * Constructor
   * 
   * @param scenarios
   */
   public YLineSwitchVariableFactory(Collection<Scenario> scenarios) {
     super(scenarios);
   }

  /**
   * Function for create the variable name associated with a switch
   * 
   * @param node
   * @return
   */
  private String getYVariableName(ElectricPowerFlowConnection edge, Scenario scenario) {
    return "y_switch." + edge.toString() + "-" + scenario.getIndex();
  }

  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, ElectricPowerModel model) throws VariableExistsException, InvalidVariableException {
    ArrayList<Variable> variables = new ArrayList<Variable>();
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (hasVariable(edge)) {
        for (Scenario scenario : getScenarios()) {
          variables.add(program.makeContinuousVariable(getYVariableName(edge, scenario)));
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
    
    boolean hasCost = edge.getAttribute(AlgorithmConstants.LINE_SWITCH_COST_KEY) == null ? false : true;
    boolean buildSwitch = edge.getAttribute(LPNormIOConstants.LINE_CAN_ADD_SWITCH) == null ? false : edge.getAttribute(LPNormIOConstants.LINE_CAN_ADD_SWITCH, Boolean.class);
    
    if (hasCost || buildSwitch) {
      return true;
    }
    return false;
  }
  
  @Override
  public Variable getVariable(MathematicalProgram program, Object asset) throws NoVariableException {
    return null;
  }
  
  /**
   * Get the variable
   * @param program
   * @param edge
   * @param scenario
   * @return
   * @throws NoVariableException
   */
  public Variable getVariable(MathematicalProgram program, ElectricPowerFlowConnection edge, Scenario scenario) throws NoVariableException {
    return program.getVariable(getYVariableName(edge, scenario));
  }

}
