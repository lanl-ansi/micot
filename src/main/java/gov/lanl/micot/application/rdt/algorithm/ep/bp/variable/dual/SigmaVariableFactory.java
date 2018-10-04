package gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.dual;

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
 * General class for creating the dual sigma variables associated with each sum lamba = 1 constraint
 * 
 * @author Russell Bent
 */
public class SigmaVariableFactory extends ScenarioVariableFactory<ElectricPowerNode, ElectricPowerModel> {

  HashMap<Scenario, ArrayList<Solution>> columns = null;
  
  /**
   * Constructor
   * 
   * @param scenarios
   */
  public SigmaVariableFactory(Collection<Scenario> scenarios, HashMap<Scenario, ArrayList<Solution>> columns) {
    super(scenarios);
    this.columns = columns;
  }

  /**
   * Function for create the variable name associated with sum of scenarios <= 1
   * 
   * @param node
   * @return
   */
  private String getVariableName(Scenario scenario) {
    return "sigma." + scenario.getIndex();
  }
  
  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, ElectricPowerModel model) throws VariableExistsException {
    ArrayList<Variable> variables = new ArrayList<Variable>();
    for (Scenario scenario : getScenarios()) {
      if (hasVariable(scenario)) {      
        variables.add(program.makeContinuousVariable(getVariableName(scenario)));                        
      }
    }
    return variables;
  }
  
  /**
   * Determine if we should have the scenario or not
   * @param scenario
   * @return
   */
  public boolean hasVariable(Scenario scenario) {
    return columns.get(scenario).size() > 0;
  }
    
  @Override
  public Variable getVariable(MathematicalProgram program, Object asset) throws NoVariableException {
    throw new NoVariableException(asset.toString());
  }

  /**
   * Get the specific variable
   * @param program
   * @param asset
   * @param phase
   * @param scenario
   * @return
   * @throws NoVariableException
   */
  public Variable getVariable(MathematicalProgram program, Scenario scenario) throws NoVariableException {
    return program.getVariable(getVariableName(scenario));
  }

}
