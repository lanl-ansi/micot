package gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
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
 * General class for creating the reactive output of each generator on each phase
 * 
 * @author Russell Bent
 */
public class ScenarioGeneratorReactivePhaseVariableFactory extends ScenarioVariableFactory<ElectricPowerNode, ElectricPowerModel> {

  public static final String PHASE_A = "a";
  public static final String PHASE_B = "b";
  public static final String PHASE_C = "c";

  /**
   * Constructor
   * 
   * @param scenarios
   */
  public ScenarioGeneratorReactivePhaseVariableFactory(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  /**
   * Function for create the variable name associated with a generator
   * 
   * @param node
   * @return
   */
  private String getGeneratorVariableName(Generator node, String phase, Scenario scenario) {
    return "g^s(q)." + phase + "-" + node.toString() + "." + scenario.getIndex();
  }

  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, ElectricPowerModel model) throws VariableExistsException {
    ArrayList<Variable> variables = new ArrayList<Variable>();

    for (Scenario scenario : getScenarios()) {
      for (ElectricPowerNode node : model.getNodes()) {
        TreeSet<Generator> sorted = new TreeSet<Generator>();
        sorted.addAll(node.getComponents(Generator.class));
        for (Generator producer : sorted) {
          if (!scenario.computeActualStatus(producer, true)) {
            continue;
          }

          if (producer.getAttribute(Generator.HAS_PHASE_A_KEY, Boolean.class)) {
            variables.add(program.makeContinuousVariable(getGeneratorVariableName(producer, PHASE_A, scenario)));
          }
          if (producer.getAttribute(Generator.HAS_PHASE_B_KEY, Boolean.class)) {
            variables.add(program.makeContinuousVariable(getGeneratorVariableName(producer, PHASE_B, scenario)));
          }
          if (producer.getAttribute(Generator.HAS_PHASE_C_KEY, Boolean.class)) {
            variables.add(program.makeContinuousVariable(getGeneratorVariableName(producer, PHASE_C, scenario)));
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

  public Variable getVariable(MathematicalProgram program, Generator asset, String phase, Scenario scenario) throws NoVariableException {
    if (asset instanceof Generator) {
      if (program.getVariable(getGeneratorVariableName(asset, phase, scenario)) != null) {
        return program.getVariable(getGeneratorVariableName(asset, phase, scenario));
      }
    }
    return null;
  }

}
