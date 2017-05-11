package gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.variable.ScenarioVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

/**
 * General class for creating voltage offset variables associated with each bus in the network
 * 
 * @author Russell Bent
 */
public class ScenarioVoltageOnOffVariableFactory extends ScenarioVariableFactory<ElectricPowerNode, ElectricPowerModel> {

  public static final String PHASE_A = "a";
  public static final String PHASE_B = "b";
  public static final String PHASE_C = "c";

  /**
   * Constructor
   * 
   * @param scenarios
   */
  public ScenarioVoltageOnOffVariableFactory(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  /**
   * Function for create the variable name associated with a generator
   * 
   * @param node
   * @return
   */
  private String getVoltageVariableName(ElectricPowerFlowConnection edge, String phase, Scenario scenario) {
    return "v_offset^s." + phase + "-" + edge.toString() + "." + scenario.getIndex();
  }

  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, ElectricPowerModel model) throws VariableExistsException {
    ArrayList<Variable> variables = new ArrayList<Variable>();
    for (Scenario scenario : getScenarios()) {
      for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {        
        if (ScenarioVariableFactoryUtility.doCreateLineUseScenarioVariable(edge, scenario)) {
          if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_A_KEY, Boolean.class)) {
            variables.add(program.makeContinuousVariable(getVoltageVariableName(edge, PHASE_A, scenario)));        
          }
          if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_B_KEY, Boolean.class)) {
            variables.add(program.makeContinuousVariable(getVoltageVariableName(edge, PHASE_B, scenario)));        
          }
          if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_C_KEY, Boolean.class)) {
            variables.add(program.makeContinuousVariable(getVoltageVariableName(edge, PHASE_C, scenario)));                  
          }          
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
   * Get the specific voltage variable
   * @param program
   * @param asset
   * @param phase
   * @param scenario
   * @return
   * @throws NoVariableException
   */
  public Variable getVariable(MathematicalProgram program, ElectricPowerFlowConnection asset, String phase, Scenario scenario) throws NoVariableException {
    if (program.getVariable(getVoltageVariableName(asset, phase, scenario)) != null) {
      return program.getVariable(getVoltageVariableName(asset, phase, scenario));
    }
    return null;
  }

}
