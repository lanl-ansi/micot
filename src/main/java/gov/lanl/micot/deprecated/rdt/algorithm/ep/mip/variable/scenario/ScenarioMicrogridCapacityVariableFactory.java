package gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.variable.ScenarioVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

/**
 * Microgrid sizing variables on a per scenario basis
 * 
 * This is the size of both the real and reactive power in the model
 * 
 * This is the z^s variables in the 2015 AAAI paper
 * 
 * @author Russell Bent
 */
public class ScenarioMicrogridCapacityVariableFactory extends ScenarioVariableFactory<ElectricPowerNode, ElectricPowerModel> {

  public static final String PHASE_A = "a";
  public static final String PHASE_B = "b";
  public static final String PHASE_C = "c";  
  
  /**
   * Constructor
   * @param scenarios
   */
  public ScenarioMicrogridCapacityVariableFactory(Collection<Scenario> scenarios) {
    super(scenarios);
  }
    
  /**
   * Function for create the variable name associated with microgrid size
   * 
   * 
   * 
   * @param node
   * @return
   */
  private String getGeneratorVariableName(Generator node, String phase, Scenario scenario) {
    return "z^s." + phase + "-" + node.toString() + "." + scenario.getIndex();
  }

  
  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, ElectricPowerModel model) throws VariableExistsException {
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
  
  /**
   * Get the variable by phase
   * @param program
   * @param asset
   * @param phase
   * @return
   * @throws NoVariableException
   */
  public Variable getVariable(MathematicalProgram program, Generator asset, String phase, Scenario scenario) throws NoVariableException {
    if (asset instanceof Generator) {
      if (program.getVariable(getGeneratorVariableName(asset, phase, scenario)) != null) {
        return program.getVariable(getGeneratorVariableName(asset, phase, scenario));
      }
    }
    return null;
  }

}
