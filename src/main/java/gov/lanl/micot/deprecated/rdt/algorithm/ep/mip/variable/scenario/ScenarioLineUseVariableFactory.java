package gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.variable.ScenarioVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidVariableException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Creates a variable for whether or not a line is used in the scenario
 * 
 * @author Russell Bent
 */
public class ScenarioLineUseVariableFactory extends ScenarioVariableFactory<ElectricPowerNode, ElectricPowerModel> {

  /**
   * Constructor
   */
  public ScenarioLineUseVariableFactory(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  /**
   * Function for create the variable name associated with a harden
   * 
   * @param node
   * @return
   */
  private String getFlowVariableName(ElectricPowerFlowConnection edge, Scenario scenario) {
    return "x^s-" + edge.toString() + "." + scenario.getIndex();
  }

  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, ElectricPowerModel model) throws VariableExistsException, InvalidVariableException {
    ArrayList<Variable> variables = new ArrayList<Variable>();
    for (Scenario scenario : getScenarios()) {
      for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
        if (ScenarioVariableFactoryUtility.doCreateLineUseScenarioVariable(edge, scenario)) {
          variables.add(program.makeContinuousVariable(getFlowVariableName(edge, scenario)));
        }
      }
    }
    return variables;
  }

  @Override
  public Variable getVariable(MathematicalProgram program, Object asset) throws NoVariableException {
    return null;
  }

  /**
   * Get a variable
   * 
   * @param program
   * @param connection
   * @param scenario
   * @return
   * @throws NoVariableException
   */
  public Variable getVariable(MathematicalProgram program, ElectricPowerFlowConnection connection, Scenario scenario) throws NoVariableException {
    if (program.getVariable(getFlowVariableName(connection, scenario)) != null) {
      return program.getVariable(getFlowVariableName(connection, scenario));
    }
    return null;
  }

}
