package gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.variable.ScenarioVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidVariableException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

/**
 * Microgrid existence variables
 * 
 * This is for both the real and raective power in the model
 * 
 * These are the u_i^s variables from the AAAI 2015 paper
 * 
 * @author Russell Bent
 */
public class ScenarioMicrogridVariableFactory extends ScenarioVariableFactory<ElectricPowerNode, ElectricPowerModel> {

  /**
   * Constructor
   * 
   * @param scenarios
   */
  public ScenarioMicrogridVariableFactory(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  /**
   * Function for create the variable name associated with microgrid size
   * 
   * @param node
   * @return
   */
  private String getGeneratorVariableName(Generator node, Scenario scenario) {
    return "u^s" + "-" + node.toString() + "." + scenario.getIndex();
  }

  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, ElectricPowerModel model) throws VariableExistsException, InvalidVariableException {
    ArrayList<Variable> variables = new ArrayList<Variable>();

    for (ElectricPowerNode node : model.getNodes()) {
      TreeSet<Generator> sorted = new TreeSet<Generator>();
      sorted.addAll(node.getComponents(Generator.class));
      for (Generator producer : sorted) {
        boolean hasCost = producer.getAttribute(AlgorithmConstants.MICROGRID_COST_KEY) == null ? false : true;
        if (!hasCost) {
          continue;
        }

        for (Scenario scenario : getScenarios()) {
          if (!scenario.computeActualStatus(producer, true)) {
            continue;
          }
          variables.add(program.makeDiscreteVariable(getGeneratorVariableName(producer, scenario)));
        }
      }
    }

    return variables;
  }

  @Override
  public Variable getVariable(MathematicalProgram program, Object asset) throws NoVariableException {
    return null;
  }

  public Variable getVariable(MathematicalProgram program, Generator generator, Scenario scenario) throws NoVariableException {
    if (program.getVariable(getGeneratorVariableName(generator, scenario)) != null) {
      return program.getVariable(getGeneratorVariableName(generator, scenario));
    }
    return null;
  }

}
