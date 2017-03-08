package gov.lanl.micot.application.rdt.algorithm.ep.mip.objective;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.optimize.ObjectiveFunctionFactory;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.MicrogridVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramObjective;

/**
 * General class for creating micogrid generator construction objective coefficients
 * @author Russell Bent
 */
public class MicrogridObjectiveFunctionFactory implements ObjectiveFunctionFactory {

  @Override
  public void addCoefficients(MathematicalProgram program, ElectricPowerModel model) throws NoVariableException {
    MicrogridVariableFactory generatorVariableFactory = new MicrogridVariableFactory();
    MathematicalProgramObjective objective = program.getLinearObjective();
  
    for (ElectricPowerNode node : model.getNodes()) {
      for (Generator generator : node.getComponents(Generator.class)) {
        Variable variable = generatorVariableFactory.getVariable(program, generator);
        if (variable != null) {
          double cost = -generator.getAttribute(AlgorithmConstants.MICROGRID_FIXED_COST_KEY, Number.class).doubleValue();       
          objective.addVariable(variable, cost);                  
        }      
      }     
    }
  }


  
}
