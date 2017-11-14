package gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint;

import gov.lanl.micot.infrastructure.ep.model.Battery;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.BatteryTimeVariableFactory;
import gov.lanl.micot.util.math.MathUtils;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.Collection;

/**
 * This constraint adds constraints to the battery
 * @author Russell Bent
 */
public class BatteryTimeConstraint implements ConstraintFactory {

  private int numberOfIncrements = -1;
  private double incrementSize = -1;
    
  /**
   * Constructor
   * @param numberOfIncrements
   * @param incrementSize
   */
  public BatteryTimeConstraint(int numberOfIncrements, double incrementSize) {
    super();
    this.numberOfIncrements = numberOfIncrements;
    this.incrementSize = incrementSize;
  }
  

  /**
   * Function for create the variable name associated with a load
   * @param node
   * @return
   */
  private String getBatteryCapacityLowerBoundConstraintName(Battery battery, double time) {
    return "BLC" + battery.toString()+"-"+time;
  }

  /**
   * Function for create the variable name associated with a load
   * @param node
   * @return
   */
  private String getBatteryCapacityUpperBoundConstraintName(Battery battery, double time) {
    return "BUC" + battery.toString()+"-"+time;
  }

  /**
   * Function for create the variable name associated with a load
   * @param node
   * @return
   */
  private String getBatteryLowerBoundConstraintName(Battery battery, double time) {
    return "BL" + battery.toString()+"-"+time;
  }
  
  /**
   * Function for create the variable name associated with a load
   * @param node
   * @return
   */
  private String getBatteryUpperBoundConstraintName(Battery battery, double time) {
    return "BU" + battery.toString()+"-"+time;
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, /*Collection<ElectricPowerNode> nodes,*/ ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    double mva = model.getMVABase();      

    BatteryTimeVariableFactory batteryVariableFactory = new BatteryTimeVariableFactory(numberOfIncrements, incrementSize);
            
    for (ElectricPowerNode node : model.getNodes()) {
      Collection<Battery> batteries = node.getComponents(Battery.class);
      
      double used0 = 0;
      for (Battery battery : batteries) {
        if (battery.getStatus()) {
          used0 += MathUtils.TO_FUNCTION(battery.getUsedEnergyCapacity()).getValue(0).doubleValue();
        }
      }
      
      double capacity0 = 0;
      for (Battery battery : batteries) {
        if (battery.getStatus()) {
          capacity0 += battery.getEnergyCapacity().doubleValue();
        }
      }      
      
      for (int i = 0; i < numberOfIncrements; ++i) {
        double time = i * incrementSize;
        for (Battery battery : batteries) {
          double totalMaxGeneration = battery.getStatus() == true ? MathUtils.TO_FUNCTION(battery.getDesiredRealGenerationMax()).getValue(time).doubleValue() / mva : 0;
          double totalMinGeneration = battery.getStatus() == true ? MathUtils.TO_FUNCTION(battery.getRealGenerationMin()).getValue(time).doubleValue() / mva : 0;      
          
          // constraints on actual production rates
          if (totalMaxGeneration == totalMinGeneration) {
            LinearConstraint constraint = new LinearConstraintEquals(getBatteryUpperBoundConstraintName(battery,time)); 
            constraint.setRightHandSide(totalMaxGeneration);  
            constraint.addVariable(batteryVariableFactory.getVariable(problem, battery, time), 1.0);
            problem.addLinearConstraint(constraint);
          }
          else {      
              
            LinearConstraint constraint = new LinearConstraintLessEq(getBatteryUpperBoundConstraintName(battery,time)); 
            constraint.setRightHandSide(totalMaxGeneration);  
            constraint.addVariable(batteryVariableFactory.getVariable(problem, battery, time), 1.0);
            problem.addLinearConstraint(constraint);

            constraint = new LinearConstraintGreaterEq(getBatteryLowerBoundConstraintName(battery,time)); 
            constraint.setRightHandSide(totalMinGeneration);  
            constraint.addVariable(batteryVariableFactory.getVariable(problem, battery, time), 1.0);
            problem.addLinearConstraint(constraint);        
          }
          
          // constraints based upon battery capacity
          if (time == 0 && used0 == 0) {
            LinearConstraint constraint = new LinearConstraintEquals(getBatteryCapacityUpperBoundConstraintName(battery,time));
            constraint.setRightHandSide(used0);  
            constraint.addVariable(batteryVariableFactory.getVariable(problem, battery, time), 1.0);
            problem.addLinearConstraint(constraint);            
          }
          else {
            LinearConstraint leConstraint = new LinearConstraintLessEq(getBatteryCapacityUpperBoundConstraintName(battery,time));
            LinearConstraint geConstraint = new LinearConstraintGreaterEq(getBatteryCapacityLowerBoundConstraintName(battery,time));
            
            leConstraint.addVariable(batteryVariableFactory.getVariable(problem, battery, time), 1.0);
            geConstraint.addVariable(batteryVariableFactory.getVariable(problem, battery, time), 1.0);
            
            for (int j = 0; j < i; ++j) {
              double time2 = j * incrementSize;
              leConstraint.setRightHandSide(batteryVariableFactory.getVariable(problem, battery, time2), -1.0);
              geConstraint.setRightHandSide(batteryVariableFactory.getVariable(problem, battery, time2), -1.0);
            }
            
            leConstraint.setRightHandSide(used0 / mva);
            geConstraint.setRightHandSide(-(capacity0-used0)/ mva);
            problem.addLinearConstraint(leConstraint);
            problem.addLinearConstraint(geConstraint);          
          }
        }
      }
    }

    
  }

  
}
