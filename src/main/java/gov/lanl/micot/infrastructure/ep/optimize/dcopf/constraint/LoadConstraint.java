package gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.LoadVariableFactory;
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
public class LoadConstraint implements ConstraintFactory {

  /**
   * Constraint
   */
  public LoadConstraint() {    
  }
  
  /**
   * Function for create the variable name associated with a load
   * 
   * @param node
   * @return
   */
  private String getLoadUpperBoundConstraintName(Load node) {
    return "LU" + node.toString();
  }

  /**
   * Function for create the variable name associated with a load
   * 
   * @param node
   * @return
   */
  private String getLoadLowerBoundConstraintName(Load node) {
    return "LL" + node.toString();
  }

  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    double mva = model.getMVABase();    
    LoadVariableFactory loadVariableFactory = new LoadVariableFactory();
        
    for (ElectricPowerNode node : model.getNodes()) {
      Collection<Load> loads = node.getComponents(Load.class);
      for (Load load : loads) {
        double totalDesiredLoad = load.getStatus() ? load.getDesiredRealLoad().doubleValue() / mva : 0;
        if (totalDesiredLoad <= 0) {
          LinearConstraint constraint = new LinearConstraintEquals(getLoadUpperBoundConstraintName(load));
          constraint.setRightHandSide(totalDesiredLoad);
          constraint.addVariable(loadVariableFactory.getVariable(problem,load), 1.0);
          problem.addLinearConstraint(constraint);
        }
        else {
          LinearConstraint constraint = new LinearConstraintLessEq(getLoadUpperBoundConstraintName(load));
          constraint.setRightHandSide(totalDesiredLoad);
          constraint.addVariable(loadVariableFactory.getVariable(problem,load), 1.0);
          problem.addLinearConstraint(constraint);

          constraint = new LinearConstraintGreaterEq(getLoadLowerBoundConstraintName(load));
          constraint.setRightHandSide(0.0);
          constraint.addVariable(loadVariableFactory.getVariable(problem,load), 1.0);
          problem.addLinearConstraint(constraint);
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
  public LinearConstraint getUpperBoundConstraint(MathematicalProgram problem, Load load) {
    return problem.getLinearConstraint(getLoadUpperBoundConstraintName(load));
  }
  
}
