package gov.lanl.micot.infrastructure.ep.optimize.dcopf.objective;

import gov.lanl.micot.infrastructure.ep.model.Battery;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.GeneratorDefaults;
import gov.lanl.micot.infrastructure.ep.optimize.ObjectiveFunctionFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.BatteryTimeVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramObjective;

import java.util.Collection;

/**
 * General class for creating battery related objective functions
 * @author Russell Bent
 */
public class BatteryTimeObjectiveFunctionFactory implements ObjectiveFunctionFactory {
  
  private int numberOfIncrements = -1;
  private double incrementSize = -1;
    
  /**
   * Constructor
   * @param numberOfIncrements
   * @param incrementSize
   */
  public BatteryTimeObjectiveFunctionFactory(int numberOfIncrements, double incrementSize) {
    super();
    this.numberOfIncrements = numberOfIncrements;
    this.incrementSize = incrementSize;
  }

  
  @Override
  public void addCoefficients(MathematicalProgram program, /*Collection<ElectricPowerNode> nodes,*/ ElectricPowerModel model) throws NoVariableException {
    BatteryTimeVariableFactory batteryVariableFactory = new BatteryTimeVariableFactory(numberOfIncrements, incrementSize);
    GeneratorDefaults defaults = GeneratorDefaults.getInstance();    
    MathematicalProgramObjective objective = program.getLinearObjective();
        
    for (ElectricPowerNode node : model.getNodes()) {
      for (int i = 0; i < numberOfIncrements; ++i) {
        double time = i * incrementSize;
        for (Battery battery : node.getComponents(Battery.class)) {         
          Number number = battery.getAttribute(Battery.ECONOMIC_COST_KEY, Number.class);
          double cost = (number == null) ? defaults.calculateCost(battery)  : number.doubleValue();        
          Variable variable = batteryVariableFactory.getVariable(program, battery, time);
          objective.addVariable(variable, cost == 0 ? 0 : -cost);
        }        
      }
    }    
  }


  
}
