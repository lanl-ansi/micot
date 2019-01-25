package gov.lanl.micot.application.rdt.algorithm.ep.objective;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.optimize.ObjectiveFunctionFactory;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.GeneratorConstructionVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramObjective;

/**
 * General class for creating generator construction objective coefficients
 * @author Russell Bent
 */
public class GeneratorObjectiveFunctionFactory implements ObjectiveFunctionFactory {

  @Override
  public void addCoefficients(MathematicalProgram program, ElectricPowerModel model) throws NoVariableException {
    GeneratorConstructionVariableFactory generatorVariableFactory = new GeneratorConstructionVariableFactory();
    MathematicalProgramObjective objective = program.getLinearObjective();

    for (Generator generator : model.getGenerators()) {
      if (generatorVariableFactory.hasVariable(generator)) {
        Variable variable = generatorVariableFactory.getVariable(program, generator);
        double cost = -generator.getAttribute(AlgorithmConstants.MICROGRID_COST_KEY, Number.class).doubleValue();       
        objective.addVariable(variable, cost);                  
      }      
    }     
  }


  
}
