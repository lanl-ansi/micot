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
 * Creates flow variables on a per phase basis and per scenario basis
 * This is the f^s_ij variable in the AAAI 2015 paper
 * 
 * @author Russell Bent
 */
public class ScenarioReactiveFlowPhaseVariableFactory extends ScenarioVariableFactory<ElectricPowerNode, ElectricPowerModel> {

  public static final String PHASE_A = "a";
  public static final String PHASE_B = "b";
  public static final String PHASE_C = "c";

  /**
   * Constructor
   */
  public ScenarioReactiveFlowPhaseVariableFactory(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  /**
   * Function for create the variable name associated with a load
   * 
   * @param node
   * @return
   */
  private String getFlowVariableName(ElectricPowerFlowConnection edge, String phase, Scenario scenario) {
    return "f(q)^s." + phase + "-" + edge.toString() + "." + scenario.getIndex();
  }

  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, ElectricPowerModel model) throws VariableExistsException {
    ArrayList<Variable> variables = new ArrayList<Variable>();
    for (Scenario scenario : getScenarios()) {
      for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
        
        boolean hasLineUseVariable = ScenarioVariableFactoryUtility.doCreateLineUseScenarioVariable(edge, scenario);
        Integer lineUseConstant = ScenarioVariableFactoryUtility.getLineUseScenarioConstant(edge, scenario);
        
        // the line is not being used, so don't even create the constraint
        if (!hasLineUseVariable && lineUseConstant == 0) {
          continue;
        }

        if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_A_KEY, Boolean.class)) {
          variables.add(program.makeContinuousVariable(getFlowVariableName(edge, PHASE_A, scenario)));
        }
        if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_B_KEY, Boolean.class)) {
          variables.add(program.makeContinuousVariable(getFlowVariableName(edge, PHASE_B, scenario)));
        }
        if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_C_KEY, Boolean.class)) {
          variables.add(program.makeContinuousVariable(getFlowVariableName(edge, PHASE_C, scenario)));
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
   * String phase
   * 
   * @param program
   * @param asset
   * @param phase
   * @return
   * @throws NoVariableException
   */
  public Variable getVariable(MathematicalProgram program, ElectricPowerFlowConnection asset, String phase, Scenario scenario) throws NoVariableException {
    if (program.getVariable(getFlowVariableName(asset, phase, scenario)) != null) {
      return program.getVariable(getFlowVariableName(asset, phase, scenario));
    }
    return null;
  }

}
