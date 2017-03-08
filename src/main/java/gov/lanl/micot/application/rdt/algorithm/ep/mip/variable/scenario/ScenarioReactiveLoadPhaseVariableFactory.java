package gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.variable.ScenarioVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

/**
 * General class for determining how much reactive load is served per phase per scenario
 * 
 * @author Russell Bent
 */
public class ScenarioReactiveLoadPhaseVariableFactory extends ScenarioVariableFactory<ElectricPowerNode, ElectricPowerModel> {

  public static final String PHASE_A = "a";
  public static final String PHASE_B = "b";
  public static final String PHASE_C = "c";

  /**
   * Constructor
   * 
   * @param scenarios
   */
  public ScenarioReactiveLoadPhaseVariableFactory(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  /**
   * Function for create the variable name associated with a load
   * 
   * @param node
   * @return
   */
  private String getLoadVariableName(Load node, String phase, Scenario scenario) {
    return "L(q)." + phase + "-" + node.toString() + "." + scenario.getIndex();
  }

  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, ElectricPowerModel model) throws VariableExistsException {
    ArrayList<Variable> variables = new ArrayList<Variable>();

    for (Scenario scenario : getScenarios()) {
      for (ElectricPowerNode node : model.getNodes()) {
        TreeSet<Load> sorted = new TreeSet<Load>();
        sorted.addAll(node.getComponents(Load.class));
        for (Load load : sorted) {
          if (!scenario.computeActualStatus(load, true)) {
            continue;
          }

          if (load.getAttribute(Load.HAS_PHASE_A_KEY, Boolean.class)) {
            variables.add(program.makeContinuousVariable(getLoadVariableName(load, PHASE_A, scenario)));
          }
          if (load.getAttribute(Load.HAS_PHASE_B_KEY, Boolean.class)) {
            variables.add(program.makeContinuousVariable(getLoadVariableName(load, PHASE_B, scenario)));
          }
          if (load.getAttribute(Load.HAS_PHASE_C_KEY, Boolean.class)) {
            variables.add(program.makeContinuousVariable(getLoadVariableName(load, PHASE_C, scenario)));
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

  public Variable getVariable(MathematicalProgram program, Load asset, String phase, Scenario scenario) throws NoVariableException {
    if (program.getVariable(getLoadVariableName(asset, phase, scenario)) != null) {
      return program.getVariable(getLoadVariableName(asset, phase, scenario));
    }
    return null;
  }

}
