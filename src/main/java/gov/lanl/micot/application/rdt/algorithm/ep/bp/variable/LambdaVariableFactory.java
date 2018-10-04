package gov.lanl.micot.application.rdt.algorithm.ep.bp.variable;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.variable.ScenarioVariableFactory;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * General class for creating lambda variables associated with each scenario
 * 
 * @author Russell Bent
 */
public class LambdaVariableFactory extends ScenarioVariableFactory<ElectricPowerNode, ElectricPowerModel> {

    HashMap<Scenario, ArrayList<Solution>> columns = null;
  
  /**
   * Constructor
   * 
   * @param scenarios
   */
  public LambdaVariableFactory(Collection<Scenario> scenarios, HashMap<Scenario, ArrayList<Solution>> columns) {
    super(scenarios);
    this.columns = columns;
  }

  /**
   * Function for create the variable name associated with a generator
   * 
   * @param node
   * @return
   */
  private String getLambdaVariableName(Scenario scenario, int column) {
    return "lambda." + scenario.getIndex() + "-" + column;
  }

  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, ElectricPowerModel model) throws VariableExistsException {
    ArrayList<Variable> variables = new ArrayList<Variable>();
    for (Scenario scenario : getScenarios()) {
        ArrayList<Solution> c = columns.get(scenario);
        for (int i = 0; i < c.size(); ++i) {
          variables.add(program.makeContinuousVariable(getLambdaVariableName(scenario, i)));                        
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
  public Variable getVariable(MathematicalProgram program, Scenario scenario, int column) throws NoVariableException {
    if (program.getVariable(getLambdaVariableName(scenario, column)) != null) {
      return program.getVariable(getLambdaVariableName(scenario, column));
    }
    return null;
  }

}
