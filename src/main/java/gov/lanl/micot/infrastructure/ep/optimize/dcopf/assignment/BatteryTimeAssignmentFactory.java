package gov.lanl.micot.infrastructure.ep.optimize.dcopf.assignment;

import gov.lanl.micot.infrastructure.ep.model.Battery;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.optimize.AssignmentFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.BatteryTimeVariableFactory;
import gov.lanl.micot.util.math.LookupTableTimeDependentFunction;
import gov.lanl.micot.util.math.MathUtils;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Assignments when the generator is a variable
 * @author Russell Bent
 */
public class BatteryTimeAssignmentFactory implements AssignmentFactory {

  private int numberOfIncrements = -1;
  private double incrementSize = -1;
    
  /**
   * Constructor
   * @param numberOfIncrements
   * @param incrementSize
   */
  public BatteryTimeAssignmentFactory(int numberOfIncrements, double incrementSize) {
    super();
    this.numberOfIncrements = numberOfIncrements;
    this.incrementSize = incrementSize;
  }
  
  @Override
  public void performAssignment(ElectricPowerModel model, MathematicalProgram problem, Solution solution/*, Collection<ElectricPowerNode> nodes*/) throws VariableExistsException, NoVariableException {
    BatteryTimeVariableFactory batteryVariableFactory = new BatteryTimeVariableFactory(numberOfIncrements, incrementSize);
    double mva = model.getMVABase(); 
    
    for (ElectricPowerNode node : model.getNodes()) {
      for (Battery battery : node.getComponents(Battery.class)) {
        battery.setActualRealGeneration(new LookupTableTimeDependentFunction());
      }
      
      for (int i = 0; i < numberOfIncrements; ++i) {        
        double time = i * incrementSize;
        for (Battery battery : node.getComponents(Battery.class)) {
          Variable variable = batteryVariableFactory.getVariable(problem, battery, time);
          ((LookupTableTimeDependentFunction)battery.getActualRealGeneration()).addEntry(time, solution.getValueDouble(variable) * mva);          
        }        
      }
      
      for (Battery battery : node.getComponents(Battery.class)) {
        LookupTableTimeDependentFunction used = new LookupTableTimeDependentFunction();
        double charge = 0;
        double lastUsed = MathUtils.TO_FUNCTION(battery.getUsedEnergyCapacity()).getValue(0).doubleValue();        
        used.addEntry(0.0,lastUsed);
      
        for (int i = 0; i < numberOfIncrements; ++i) {
          double time = i * incrementSize;
          charge = charge - ((LookupTableTimeDependentFunction)battery.getActualRealGeneration()).getValue(time).doubleValue();
          double newUsed = lastUsed - ((LookupTableTimeDependentFunction)battery.getActualRealGeneration()).getValue(time).doubleValue();
          used.addEntry(time+incrementSize,newUsed);
          lastUsed = newUsed;
        }
        battery.setUsedEnergyCapacity(used);
      }     
    }
  }

 
}
