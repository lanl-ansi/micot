package gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.dual;

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

/**
 * Creates dual variables for each w - \sum lambda constraint (where w is design variable)
 * 
 * @author Russell Bent
 */
public class YGeneratorVariableFactory extends ScenarioVariableFactory<ElectricPowerNode, ElectricPowerModel> {

  /**
  * Constructor
  * 
  * @param scenarios
  */
  public YGeneratorVariableFactory(Collection<Scenario> scenarios) {
    super(scenarios);
  }
  
  /**
   * Function for create the variable name associated with microgrid size
   * 
   * @param node
   * @return
   */
  private String getYVariableName(Generator node, Scenario scenario) {
    return "y_gen." + node.toString() + "-" + scenario.getIndex();
  }
  
  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, ElectricPowerModel model) throws VariableExistsException, InvalidVariableException {
    ArrayList<Variable> variables = new ArrayList<Variable>();
    
    for (Generator producer : model.getGenerators()) {
      for (Scenario scenario : getScenarios()) {
        if (hasVariable(producer)) {
          variables.add(program.makeContinuousVariable(getYVariableName(producer, scenario)));
        }
      }     
    }
    return variables;
  }

  /**
   * Determine if a generator has a variable or not
   * @param producer
   * @return
   */
  public boolean hasVariable(Generator producer) {
    boolean isNew = producer.getAttribute(AlgorithmConstants.IS_NEW_MICROGRID_KEY) != null && producer.getAttribute(AlgorithmConstants.IS_NEW_MICROGRID_KEY, Boolean.class);                 
    return producer.getStatus() && isNew;
  }
  
  /**
   * Get the variable
   * @param program
   * @param generator
   * @param scenario
   * @return
   */
  public Variable getVariable(MathematicalProgram program, Generator generator, Scenario scenario) {
    return program.getVariable(getYVariableName(generator, scenario));
  }

  @Override
  public Variable getVariable(MathematicalProgram program, Object asset) throws NoVariableException {
    return null;
  }
  

}
