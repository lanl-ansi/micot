package gov.lanl.micot.application.rdt.algorithm.ep.bp.objective.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.optimize.ObjectiveFunctionFactory;
import gov.lanl.micot.infrastructure.model.Scenario;

import java.util.ArrayList;

import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.dual.YGeneratorVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.scenario.GeneratorVariableFactory;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramObjective;

/**
 * General class for creating generator construction objective coefficients
 * @author Russell Bent
 */
public class GeneratorObjectiveFunctionFactory implements ObjectiveFunctionFactory {

  private Scenario scenario = null;
  private Solution dualSolution = null;
  private MathematicalProgram dualProgram = null;
    
  public GeneratorObjectiveFunctionFactory(Scenario scenario, Solution dualSolution, MathematicalProgram dualProgram) {
    this.scenario = scenario;
    this.dualSolution = dualSolution;        
    this.dualProgram = dualProgram;
  }
    
  @Override
  public void addCoefficients(MathematicalProgram program, ElectricPowerModel model) throws NoVariableException {
    ArrayList<Scenario> scenarios = new ArrayList<Scenario>();
    scenarios.add(scenario);    

    YGeneratorVariableFactory yVariableFactory = new YGeneratorVariableFactory(scenarios);
    GeneratorVariableFactory generatorVariableFactory = new GeneratorVariableFactory(scenario);

    MathematicalProgramObjective objective = program.getLinearObjective();
    for (Generator generator : model.getGenerators()) {
      if (yVariableFactory.hasVariable(generator)) {        
        Variable variable = generatorVariableFactory.getVariable(program, generator, scenario); 
        Variable y = yVariableFactory.getVariable(dualProgram, generator, scenario);
        double cost = dualSolution.getValueDouble(y);       
        objective.addVariable(variable, cost);                      
      }     
    }
  }


  
}
