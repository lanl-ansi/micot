package gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.LoadShedVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.Collection;

/**
 * This constraint adds constraints to the load shedding... has to be greater than 0
 * @author Russell Bent
 */
public class LoadShedConstraint implements ConstraintFactory {

  /**
   * Constraint
   */
  public LoadShedConstraint() {    
  }
  
  /**
   * Function for create the variable name associated with a load
   * 
   * @param node
   * @return
   */
  private String getLoadLowerBoundConstraintName(Load node) {
    return "LSL" + node.toString();
  }

  /**
   * Function for create the variable name associated with a load
   * 
   * @param node
   * @return
   */
  private String getLoadUpperBoundConstraintName(Load node) {
    return "LSU" + node.toString();
  }
  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    LoadShedVariableFactory loadVariableFactory = new LoadShedVariableFactory(); 
    double mva = model.getMVABase();
    for (ElectricPowerNode node : model.getNodes()) {
      Collection<Load> loads = node.getComponents(Load.class);
      for (Load load : loads) {
        if (load.getStatus()) {
          LinearConstraint constraint = new LinearConstraintGreaterEq(getLoadLowerBoundConstraintName(load));
          constraint.setRightHandSide(Math.min(0.0,load.getDesiredRealLoad().doubleValue() / mva));
          constraint.addVariable(loadVariableFactory.getVariable(problem,load), 1.0);
          problem.addLinearConstraint(constraint);
        
          constraint = new LinearConstraintLessEq(getLoadUpperBoundConstraintName(load));
          constraint.setRightHandSide(load.getDesiredRealLoad().doubleValue() / mva);
          constraint.addVariable(loadVariableFactory.getVariable(problem,load), 1.0);
          problem.addLinearConstraint(constraint);
        }
      }   
    }    
  }

  /**
   * Get the lower bound constraint
   * @param problem
   * @param load
   * @return
   */
  public LinearConstraint getUpperBoundConstraint(MathematicalProgram problem, Load load) {
    return problem.getLinearConstraint( getLoadUpperBoundConstraintName(load));
  }
}
