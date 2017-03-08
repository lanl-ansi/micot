package gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.optimize.VariableFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

/**
 * General class for creating phase angle variables associated with scenarios
 * 
 * @author Russell Bent
 */
public class PhaseAngleScenarioVariableFactory implements VariableFactory {

  private Collection<Scenario> scenarios = new ArrayList<Scenario>();

  /**
   * Construtor
   * 
   * @param models
   */
  public PhaseAngleScenarioVariableFactory(Collection<Scenario> scenarios) {
    this.scenarios = scenarios;
  }

  /**
   * Get the phase angle variable name
   * 
   * @param edge
   * @return
   */
  private String getPhaseAngleVariableName(int k, ElectricPowerNode node) {
    return "P" + node.toString() + "." + k;
  }

  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, ElectricPowerModel basemodel) throws VariableExistsException {
    ArrayList<Variable> variables = new ArrayList<Variable>();
    for (Scenario scenario : scenarios) {
      int k = scenario.getIndex();

        for (ElectricPowerNode node : basemodel.getNodes()) {
          Variable variable = program.makeContinuousVariable(getPhaseAngleVariableName(k, node));
          variables.add(variable);
        }
    }
    return variables;
  }

  @Override
  public Variable getVariable(MathematicalProgram program, Object asset) throws NoVariableException {
    throw new NoVariableException(asset.toString());
  }

  /**
   * Get a variable
   * 
   * @param program
   * @param asset
   * @param scenario
   * @return
   * @throws NoVariableException
   */
  public Variable getVariable(MathematicalProgram program, Object asset, Scenario scenario) throws NoVariableException {
    if (asset instanceof ElectricPowerNode) {
      Variable v = program.getVariable(getPhaseAngleVariableName(scenario.getIndex(), (ElectricPowerNode) asset));
      if (v != null) {
        return v;
      }
    }
    throw new NoVariableException(asset.toString() + " " + scenario);
  }

}
