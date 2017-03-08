package gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.PhaseAngleVariableFactory;
import gov.lanl.micot.infrastructure.model.Node;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * The phase angle constraint bounds... essentially here for big M on some of the dual variables
 * @author Russell Bent
 */
public class PhaseAngleBoundConstraint implements ConstraintFactory {
  
  /**
   * Get the upper bound constraint name
   * 
   * @param edge
   * @return
   */
  private String getPhaseAngleUpperConstraintName(Node node) {
    return "\"PBU" + node.toString() + "\"";
  }
  
  /**
   * Get the lower bound constraint name
   * @param node
   * @return
   */
  private String getPhaseAngleLowerConstraintName(Node node) {
    return "\"PBL" + node.toString() + "\"";
  }
  
  /**
   * Constraint
   */
  public PhaseAngleBoundConstraint() {    
  }
    
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    PhaseAngleVariableFactory phaseAngleVariableFactory = new PhaseAngleVariableFactory();
        
    for (Node node : model.getNodes()) {      
      LinearConstraint constraint = new LinearConstraintLessEq(getPhaseAngleUpperConstraintName(node));
      constraint.setRightHandSide(model.getNodes().size());
      constraint.addVariable(phaseAngleVariableFactory.getVariable(problem, node), 1);
      problem.addLinearConstraint(constraint);
      
      constraint = new LinearConstraintGreaterEq(getPhaseAngleLowerConstraintName(node));
      constraint.setRightHandSide(-model.getNodes().size());
      constraint.addVariable(phaseAngleVariableFactory.getVariable(problem, node), 1);
      problem.addLinearConstraint(constraint);
      
    }
  }

  
  
}
