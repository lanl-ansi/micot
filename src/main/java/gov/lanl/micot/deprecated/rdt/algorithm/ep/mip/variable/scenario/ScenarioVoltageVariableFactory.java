package gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario;

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
 * General class for creating voltage variables associated with each bus in the netewor
 * 
 * @author Russell Bent
 */
public class ScenarioVoltageVariableFactory extends ScenarioVariableFactory<ElectricPowerNode, ElectricPowerModel> {

  public static final String PHASE_A = "a";
  public static final String PHASE_B = "b";
  public static final String PHASE_C = "c";

  /**
   * Constructor
   * 
   * @param scenarios
   */
  public ScenarioVoltageVariableFactory(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  /**
   * Function for create the variable name associated with a generator
   * 
   * @param node
   * @return
   */
  private String getVoltageVariableName(ElectricPowerNode node, String phase, Scenario scenario) {
    return "v^s." + phase + "-" + node.toString() + "." + scenario.getIndex();
  }

  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, ElectricPowerModel model) throws VariableExistsException {
    ArrayList<Variable> variables = new ArrayList<Variable>();
    for (Scenario scenario : getScenarios()) {
      for (ElectricPowerNode node : model.getNodes()) {
        variables.add(program.makeContinuousVariable(getVoltageVariableName(node, PHASE_A, scenario)));        
        variables.add(program.makeContinuousVariable(getVoltageVariableName(node, PHASE_B, scenario)));        
        variables.add(program.makeContinuousVariable(getVoltageVariableName(node, PHASE_C, scenario)));        
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
  public Variable getVariable(MathematicalProgram program, ElectricPowerNode asset, String phase, Scenario scenario) throws NoVariableException {
    if (program.getVariable(getVoltageVariableName(asset, phase, scenario)) != null) {
      return program.getVariable(getVoltageVariableName(asset, phase, scenario));
    }
    return null;
  }

}
