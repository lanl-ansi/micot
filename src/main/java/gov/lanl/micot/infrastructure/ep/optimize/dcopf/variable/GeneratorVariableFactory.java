package gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerProducer;
import gov.lanl.micot.infrastructure.ep.optimize.VariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

/**
 * General class for creating generators
 * @author Russell Bent
 */
public class GeneratorVariableFactory implements VariableFactory {

  /**
   * Function for create the variable name associated with a load
   * 
   * @param node
   * @return
   */
  private String getGeneratorVariableName(ElectricPowerProducer node) {
    return "G" + node.toString();
  }

  
  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, /*Collection<ElectricPowerNode> nodes,*/ ElectricPowerModel model) throws VariableExistsException {
    ArrayList<Variable> variables = new ArrayList<Variable>();
    
    for (ElectricPowerNode node : model.getNodes()) {
      TreeSet<ElectricPowerProducer> sorted = new TreeSet<ElectricPowerProducer>();
      sorted.addAll(node.getComponents(ElectricPowerProducer.class));
      for (ElectricPowerProducer producer : sorted) {
        variables.add(program.makeContinuousVariable(getGeneratorVariableName(producer)));
      }     
    }
    
    return variables;
  }

  @Override
  public Variable getVariable(MathematicalProgram program, Object asset) throws NoVariableException {
    if (asset instanceof ElectricPowerProducer) {
      if (program.getVariable(getGeneratorVariableName((ElectricPowerProducer) asset)) != null) {
        return program.getVariable(getGeneratorVariableName((ElectricPowerProducer) asset));
      }
    }
    throw new NoVariableException(asset.toString());
  }

}
