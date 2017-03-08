package gov.lanl.micot.infrastructure.ep.optimize.dcopf.objective;

import gov.lanl.micot.infrastructure.ep.model.Battery;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.optimize.ObjectiveFunctionFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.BatteryTimeVariableFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.GeneratorTimeVariableFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.LoadTimeVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramObjective;

import java.util.Collection;

/**
 * General class for creating loads
 * @author Russell Bent
 */
public class LoadTimeObjectiveFunctionFactory implements ObjectiveFunctionFactory {

  private int numberOfIncrements = -1;
  private double incrementSize = -1;
    
  /**
   * Constructor
   * @param numberOfIncrements
   * @param incrementSize
   */
  public LoadTimeObjectiveFunctionFactory(int numberOfIncrements, double incrementSize) {
    super();
    this.numberOfIncrements = numberOfIncrements;
    this.incrementSize = incrementSize;
  }

  
  @Override
  public void addCoefficients(MathematicalProgram program, /*Collection<ElectricPowerNode> nodes,*/ ElectricPowerModel model) throws NoVariableException {
    GeneratorTimeVariableFactory generatorVariableFactory = new GeneratorTimeVariableFactory(numberOfIncrements, incrementSize);
    LoadTimeVariableFactory loadVariableFactory = new LoadTimeVariableFactory(numberOfIncrements, incrementSize);
    BatteryTimeVariableFactory batteryVariableFactory = new BatteryTimeVariableFactory(numberOfIncrements, incrementSize);
    MathematicalProgramObjective objective = program.getLinearObjective();
    
    double maxCost = 0;
    for (ElectricPowerNode node : model.getNodes()) {
      for (Generator producer : node.getComponents(Generator.class)) {
        for (int i = 0; i < numberOfIncrements; ++i) {
          double time = i * incrementSize;
          Variable variable = generatorVariableFactory.getVariable(program, producer, time);
          double cost = -objective.getCoefficient(variable);
          maxCost = Math.max(cost, maxCost);
        }
      }     
    }

    for (ElectricPowerNode node : model.getNodes()) {
      for (Battery producer : node.getComponents(Battery.class)) {
        for (int i = 0; i < numberOfIncrements; ++i) {
          double time = i * incrementSize;
          Variable variable = batteryVariableFactory.getVariable(program, producer, time);
          double cost = -objective.getCoefficient(variable);
          maxCost = Math.max(cost, maxCost);
        }
      }     
    }
    
    maxCost += 1;
       
    for (ElectricPowerNode node : model.getNodes()) {     
      for (int i = 0; i < numberOfIncrements; ++i) {
        double time = i * incrementSize;
        for (Load load : node.getComponents(Load.class)) {
          Variable variable = loadVariableFactory.getVariable(program, load, time);
          objective.addVariable(variable, maxCost * 2.0);
        }
      }      
    }
  }




  
}
