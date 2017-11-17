package gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.LoadTimeVariableFactory;
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
 * This constraint adds constraints to the load
 * @author Russell Bent
 */
public class LoadTimeConstraint implements ConstraintFactory {

  private int numberOfIncrements = -1;
  private double incrementSize = -1;
    
  /**
   * Constructor
   * @param numberOfIncrements
   * @param incrementSize
   */
  public LoadTimeConstraint(int numberOfIncrements, double incrementSize) {
    super();
    this.numberOfIncrements = numberOfIncrements;
    this.incrementSize = incrementSize;
  }
  
  /**
   * Function for create the variable name associated with a load
   * @param node
   * @return
   */
  private String getLoadUpperBoundConstraintName(Load load, double time) {
    return "LU" + load.toString()+"-"+time;
  }

  /**
   * Function for create the variable name associated with a load
   * @param node
   * @return
   */
  private String getLoadLowerBoundConstraintName(Load load, double time) {
    return "LL" + load.toString()+"-"+time;
  }
  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    double mva = model.getMVABase();    
    
    LoadTimeVariableFactory loadVariableFactory = new LoadTimeVariableFactory(numberOfIncrements, incrementSize);
            
    for (ElectricPowerNode node : model.getNodes()) {      
      Collection<Load> loads = node.getComponents(Load.class);
      
      for (int i = 0; i < numberOfIncrements; ++i) {
        double time = i * incrementSize;
        for (Load load : loads) {
          double totalDesiredLoad = load.getStatus() == true ? MathUtils.TO_FUNCTION(load.getRealLoadMax()).getValue(time).doubleValue() / mva : 0;
        
          if (totalDesiredLoad <= 0) {
            LinearConstraint constraint = new LinearConstraintEquals(getLoadUpperBoundConstraintName(load,time));
            constraint.setRightHandSide(totalDesiredLoad);  
            constraint.addVariable(loadVariableFactory.getVariable(problem, load, time), 1.0);
            problem.addLinearConstraint(constraint);
          }
          else {            
            LinearConstraint constraint = new LinearConstraintLessEq(getLoadUpperBoundConstraintName(load,time)); 
            constraint.setRightHandSide(totalDesiredLoad);
            constraint.addVariable(loadVariableFactory.getVariable(problem, load, time), 1.0);
            problem.addLinearConstraint(constraint);
        
            constraint = new LinearConstraintGreaterEq(getLoadLowerBoundConstraintName(load,time)); 
            constraint.setRightHandSide(0.0);  
            constraint.addVariable(loadVariableFactory.getVariable(problem, load, time), 1.0);
            problem.addLinearConstraint(constraint);        
          }
        }
      }
    }
    
  }

  /**
   * Get the upper bound constraint
   * @param problem
   * @param producer
   * @return
   */
  public LinearConstraint getUpperBoundConstraint(MathematicalProgram problem, Load load, double time) {
    return problem.getLinearConstraint(getLoadUpperBoundConstraintName(load, time));
  }
  
}
