package gov.lanl.micot.infrastructure.ep.optimize.dcopf.objective;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.GeneratorDefaults;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.optimize.ObjectiveFunctionFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.LoadVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramObjective;

import java.util.Collection;

/**
 * General class for creating generators
 * @author Russell Bent
 */
public class LoadObjectiveFunctionFactory implements ObjectiveFunctionFactory {

  @Override
  public void addCoefficients(MathematicalProgram program, /*Collection<ElectricPowerNode> nodes,*/ ElectricPowerModel model) throws NoVariableException {
    LoadVariableFactory loadVariableFactory = new LoadVariableFactory();
    MathematicalProgramObjective objective = program.getLinearObjective();
    GeneratorDefaults defaults = GeneratorDefaults.getInstance();    
     
    // create the load variables - equally important to maximize load
    for (ElectricPowerNode node : model.getNodes()) {
      for (Load load : node.getComponents(Load.class)) {
        Variable variable = loadVariableFactory.getVariable(program, load);        
        objective.addVariable(variable, defaults.calculateLoadShedCost(load, model.getNodes()).doubleValue());
      }
    }
  }




  
}
