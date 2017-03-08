package gov.lanl.micot.infrastructure.ep.optimize.dcopf.objective;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerProducer;
import gov.lanl.micot.infrastructure.ep.model.GeneratorDefaults;
import gov.lanl.micot.infrastructure.ep.optimize.ObjectiveFunctionFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.GeneratorVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramObjective;

/**
 * General class for creating generators
 * @author Russell Bent
 */
public class GenerationObjectiveFunctionFactory implements ObjectiveFunctionFactory {

  @Override
  public void addCoefficients(MathematicalProgram program, ElectricPowerModel model) throws NoVariableException {
    GeneratorVariableFactory generatorVariableFactory = new GeneratorVariableFactory();
    GeneratorDefaults defaults = GeneratorDefaults.getInstance();    
    MathematicalProgramObjective objective = program.getLinearObjective();
        
    for (ElectricPowerNode node : model.getNodes()) {
      for (ElectricPowerProducer producer : node.getComponents(ElectricPowerProducer.class)) {
        double cost = defaults.convertToLinearCost(producer).doubleValue();
        Variable variable = generatorVariableFactory.getVariable(program, producer);
        cost = (cost == 0) ? 0 : -cost;       
        objective.addVariable(variable, cost);        
      }     
    }
  }


  
}
