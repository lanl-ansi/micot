package gov.lanl.micot.application.rdt.algorithm.ep.sbd.variable;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.VariableFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

/**
 * General class for tracking load served up to the maximum amount
 * that needs to be served
 * 
 * @author Russell Bent
 */
public class LoadSlackVariableFactory implements VariableFactory {

  private Scenario scenario = null;
  
  /**
   * Constructor
   * 
   * @param scenario
   */
  public LoadSlackVariableFactory(Scenario scenario) {
    this.scenario = scenario;
  }

  /**
   * Function for create the real variable name
   * 
   * @param node
   * @return
   */
  private String getVariableName() {
    return "L(slack)." + scenario.getIndex();
  }
  
  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, ElectricPowerModel model) throws VariableExistsException {
    ArrayList<Variable> variables = new ArrayList<Variable>();
    variables.add(program.makeContinuousVariable(getVariableName()));
    return variables;
  }

  @Override
  public Variable getVariable(MathematicalProgram program, Object asset) throws NoVariableException {
    throw new NoVariableException(asset.toString());
  }

  /**
   * Get the real variable
   * @param program
   * @param asset
   * @param phase
   * @return
   * @throws NoVariableException
   */
  public Variable getVariable(MathematicalProgram program) throws NoVariableException {
    return program.getVariable(getVariableName());
  }
}
