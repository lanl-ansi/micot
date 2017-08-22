package gov.lanl.micot.application.rdt.algorithm.ep.mip.variable;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.optimize.VariableFactory;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

/**
 * Microgrid sizing variables
 * 
 * These are the z variables in the AAAI 2015 paper
 * 
 * @author Russell Bent
 */
public class MicrogridCapacityVariableFactory implements VariableFactory {

  public static final String PHASE_A = "a";
  public static final String PHASE_B = "b";
  public static final String PHASE_C = "c";  
  
  /**
   * Constructor
   */
  public MicrogridCapacityVariableFactory() {    
  }
  
  /**
   * Function for create the variable name associated with microgrid size
   * 
   * These are the u_i variables from the paper
   * 
   * 
   * @param node
   * @return
   */
  private String getGeneratorVariableName(Generator node, String phase) {
    return "z." + phase + "-" + node.toString();
  }

  
  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, ElectricPowerModel model) throws VariableExistsException {
    ArrayList<Variable> variables = new ArrayList<Variable>();
    
    for (ElectricPowerNode node : model.getNodes()) {
      TreeSet<Generator> sorted = new TreeSet<Generator>();
      sorted.addAll(node.getComponents(Generator.class));
      for (Generator producer : sorted) {
        boolean isNew = producer.getAttribute(AlgorithmConstants.IS_NEW_MICROGRID_KEY) != null && producer.getAttribute(AlgorithmConstants.IS_NEW_MICROGRID_KEY, Boolean.class);                 
        if (!producer.getActualStatus() || !isNew) {
          continue;
        }
        
        if (producer.getAttribute(Generator.HAS_PHASE_A_KEY, Boolean.class)) {
          variables.add(program.makeContinuousVariable(getGeneratorVariableName(producer, PHASE_A)));
        }
        if (producer.getAttribute(Generator.HAS_PHASE_B_KEY, Boolean.class)) {
          variables.add(program.makeContinuousVariable(getGeneratorVariableName(producer, PHASE_B)));
        }
        if (producer.getAttribute(Generator.HAS_PHASE_C_KEY, Boolean.class)) {
          variables.add(program.makeContinuousVariable(getGeneratorVariableName(producer, PHASE_C)));
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
  public Variable getVariable(MathematicalProgram program, Object asset, String phase) throws NoVariableException {
    if (asset instanceof Generator) {
      if (program.getVariable(getGeneratorVariableName((Generator) asset, phase)) != null) {
        return program.getVariable(getGeneratorVariableName((Generator) asset, phase));
      }
    }
    return null;
  }


}
