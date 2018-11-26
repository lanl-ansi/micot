package gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.optimize.VariableFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

/**
 * General class for determining how much load is served per phase per scenario
 * This is the l^s variable in the AAAI 2015 paper
 * 
 * @author Russell Bent
 */
public class LoadVariableFactory implements VariableFactory {

  public static final String PHASE_A = "a";
  public static final String PHASE_B = "b";
  public static final String PHASE_C = "c";

  private Scenario scenario = null;
  
  /**
   * Constructor
   * 
   * @param scenario
   */
  public LoadVariableFactory(Scenario scenario) {
    this.scenario = scenario;
  }

  /**
   * Function for create the real variable name
   * 
   * @param node
   * @return
   */
  private String getRealVariableName(Load node, String phase) {
    return "L(p)." + phase + "-" + node.toString() + "." + scenario.getIndex();
  }
  
  /**
   * Function for create the reactive variable name
   * 
   * @param node
   * @return
   */
  private String getReactiveVariableName(Load node, String phase) {
    return "L(q)." + phase + "-" + node.toString() + "." + scenario.getIndex();
  }

  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, ElectricPowerModel model) throws VariableExistsException {
    ArrayList<Variable> variables = new ArrayList<Variable>();
    for (Load load : model.getLoads()) {
      if (!scenario.computeActualStatus(load, true)) {
        continue;
      }

      if (load.getAttribute(Load.HAS_PHASE_A_KEY, Boolean.class)) {
        variables.add(program.makeContinuousVariable(getRealVariableName(load, PHASE_A)));
        variables.add(program.makeContinuousVariable(getReactiveVariableName(load, PHASE_A)));        
      }
      if (load.getAttribute(Load.HAS_PHASE_B_KEY, Boolean.class)) {
        variables.add(program.makeContinuousVariable(getRealVariableName(load, PHASE_B)));
        variables.add(program.makeContinuousVariable(getReactiveVariableName(load, PHASE_B)));
      }
      if (load.getAttribute(Load.HAS_PHASE_C_KEY, Boolean.class)) {
        variables.add(program.makeContinuousVariable(getRealVariableName(load, PHASE_C)));
        variables.add(program.makeContinuousVariable(getReactiveVariableName(load, PHASE_C)));
      }
    }
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
  public Variable getRealVariable(MathematicalProgram program, Load asset, String phase) throws NoVariableException {
    return program.getVariable(getRealVariableName(asset, phase));
  }

 /**
  * Get the reactive variable
  * @param program
  * @param asset
  * @param phase
  * @return
  * @throws NoVariableException
  */
 public Variable getReactiveVariable(MathematicalProgram program, Load asset, String phase) throws NoVariableException {
   return program.getVariable(getReactiveVariableName(asset, phase));
 }


}
