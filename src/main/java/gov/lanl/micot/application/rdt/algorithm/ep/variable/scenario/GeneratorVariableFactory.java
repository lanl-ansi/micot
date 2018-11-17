package gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.optimize.VariableFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

/**
 * General class for creating the output of each generator on each phase
 * these variables are the q^s and p^s variables 
 * 
 * @author Russell Bent
 */
public class GeneratorVariableFactory implements VariableFactory {

  public static final String PHASE_A = "a";
  public static final String PHASE_B = "b";
  public static final String PHASE_C = "c";

  private Scenario scenario = null;
  
  /**
   * Constructor
   * 
   * @param scenarios
   */
  public GeneratorVariableFactory(Scenario scenario) {
    this.scenario = scenario;
  }

  /**
   * Function for create the real variable name 
   * 
   * @param node
   * @return
   */
  private String getRealVariableName(Generator node, String phase) {
    return "g^s(p)." + phase + "-" + node.toString() + "." + scenario.getIndex();
  }

  
  /**
   * Function for create the real variable name 
   * 
   * @param node
   * @return
   */
  private String getReactiveVariableName(Generator node, String phase) {
    return "g^s(q)." + phase + "-" + node.toString() + "." + scenario.getIndex();
  }
  
  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, ElectricPowerModel model) throws VariableExistsException {
    ArrayList<Variable> variables = new ArrayList<Variable>();
    for (Generator producer : model.getGenerators()) {
      if (!scenario.computeActualStatus(producer, true)) {
        continue;
      }

      if (producer.getAttribute(Generator.HAS_PHASE_A_KEY, Boolean.class)) {
        variables.add(program.makeContinuousVariable(getRealVariableName(producer, PHASE_A)));
        variables.add(program.makeContinuousVariable(getReactiveVariableName(producer, PHASE_A)));
      }

      if (producer.getAttribute(Generator.HAS_PHASE_B_KEY, Boolean.class)) {
        variables.add(program.makeContinuousVariable(getRealVariableName(producer, PHASE_B)));
        variables.add(program.makeContinuousVariable(getReactiveVariableName(producer, PHASE_B)));
      }

      if (producer.getAttribute(Generator.HAS_PHASE_C_KEY, Boolean.class)) {
        variables.add(program.makeContinuousVariable(getRealVariableName(producer, PHASE_C)));
        variables.add(program.makeContinuousVariable(getReactiveVariableName(producer, PHASE_C)));
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
  public Variable getRealVariable(MathematicalProgram program, Generator asset, String phase) throws NoVariableException {
    return program.getVariable(getRealVariableName(asset, phase));
  }
  
  /**
   * Get the real variable
   * @param program
   * @param asset
   * @param phase
   * @return
   * @throws NoVariableException
   */
  public Variable getReactiveVariable(MathematicalProgram program, Generator asset, String phase) throws NoVariableException {
    return program.getVariable(getReactiveVariableName(asset, phase));
  }

}
