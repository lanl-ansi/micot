package gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint;

import gov.lanl.micot.infrastructure.ep.model.Battery;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.BatteryTimeVariableFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.GeneratorTimeVariableFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.LoadTimeVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.Collection;

/**
 * This constraint adds balance of loads and generation
 * @author Russell Bent
 */
public class BalanceTimeConstraint implements ConstraintFactory {

  private int numberOfIncrements = -1;
  private double incrementSize = -1;
    
  /**
   * Constructor
   * @param numberOfIncrements
   * @param incrementSize
   */
  public BalanceTimeConstraint(int numberOfIncrements, double incrementSize) {
    super();
    this.numberOfIncrements = numberOfIncrements;
    this.incrementSize = incrementSize;
  }
  
  /**
   * Get the balancing constraint name 
   * @return
   */
  private String getBalanceConstraintName(double time) {
    return "Balance"+"-"+time;
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    BatteryTimeVariableFactory batteryVariableFactory = new BatteryTimeVariableFactory(numberOfIncrements, incrementSize);
    GeneratorTimeVariableFactory generatorVariableFactory = new GeneratorTimeVariableFactory(numberOfIncrements, incrementSize);
    LoadTimeVariableFactory loadVariableFactory = new LoadTimeVariableFactory(numberOfIncrements, incrementSize);
    
    for (int i = 0; i < numberOfIncrements; ++i) {
      double time = i * incrementSize;
      LinearConstraint constraint = new LinearConstraintEquals(getBalanceConstraintName(time));
      constraint.setRightHandSide(0);  
      problem.addLinearConstraint(constraint);
      for (ElectricPowerNode node : model.getNodes()) {
        for (Generator gen : node.getComponents(Generator.class)) {
          constraint.addVariable(generatorVariableFactory.getVariable(problem, gen, time), 1.0);
        }
        for (Battery battery : node.getComponents(Battery.class)) {
          constraint.addVariable(batteryVariableFactory.getVariable(problem, battery, time), 1.0);
        }
        for (Load load : node.getComponents(Load.class)) {
          constraint.addVariable(loadVariableFactory.getVariable(problem, load, time), -1.0);
        }
      }
    }    
  }

  
}
